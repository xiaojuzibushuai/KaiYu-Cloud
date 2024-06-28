package com.kaiyu.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaiyu.order.config.PayNotifyConfig;
import com.kaiyu.order.config.WxPayConfig;
import com.kaiyu.order.domain.MqMessage;
import com.kaiyu.order.domain.Orders;
import com.kaiyu.order.domain.OrdersGoods;
import com.kaiyu.order.domain.PayRecord;
import com.kaiyu.order.domain.dto.AddOrderDto;
import com.kaiyu.order.domain.dto.PayStatusDto;
import com.kaiyu.order.enums.wxpay.WxApiType;
import com.kaiyu.order.enums.wxpay.WxNotifyType;
import com.kaiyu.order.enums.wxpay.WxTradeState;
import com.kaiyu.order.feignclient.RemoteSystemService;
import com.kaiyu.order.mapper.OrdersGoodsMapper;
import com.kaiyu.order.mapper.OrdersMapper;
import com.kaiyu.order.mapper.PayRecordMapper;
import com.kaiyu.order.service.MqMessageService;
import com.kaiyu.order.service.OrderService;
import com.kaiyu.order.util.IdWorkerUtils;
import com.kaiyu.order.util.NonceUtil;
import com.ruoyi.common.core.exception.KaiYuEducationException;
import com.ruoyi.common.core.utils.StringUtils;
import com.wechat.pay.contrib.apache.httpclient.auth.Signer;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Signature;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @program: KaiYu-Cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-06-17 17:09
 **/
@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private RemoteSystemService remoteSystemService;

    @Autowired
    OrdersMapper ordersMapper;

    @Autowired
    PayRecordMapper payRecordMapper;

    @Autowired
    OrdersGoodsMapper ordersGoodsMapper;

    @Value("${pay.qrcodeurl}")
    String qrcodeurl;

    @Resource
    private WxPayConfig wxPayConfig;

    @Resource
    private CloseableHttpClient wxPayClient;

    @Autowired
    MqMessageService mqMessageService;

    @Autowired
    RabbitTemplate rabbitTemplate;


    @Autowired
    private RedissonClient redissonClient;

    private static final String LOCK_KEY_PREFIX = "vx_pay_callback_lock:";

    @Override
    public Map<String, Object> createOrder(AddOrderDto addOrderDto,String tradeType) {

        //1、创建订单
        Orders orders = saveOrders(addOrderDto);
        //2、创建支付记录
        PayRecord payRecord = createPayRecord(orders);
        //3、请求支付API
        Map map= null;
        try {
            Map<String, Object> attach = (Map<String, Object>) addOrderDto.getAttach();
            map = requestPayApi(payRecord,tradeType,attach);
        }catch (Exception var1){
            log.error("请求支付API失败",var1);
            KaiYuEducationException.cast("请求支付API失败");
        }
        return map;
    }


    /**
     * 保存订单信息，保存订单表和订单明细表
     */
    @Transactional(rollbackFor = Exception.class)
    public Orders saveOrders(AddOrderDto addOrderDto) {

        //查找已存在但未支付的订单 幂等性判断
        Orders order = getOrderByBusinessId(addOrderDto.getOutBusinessId(), addOrderDto.getOrderType());
        if (order != null){
            return order;
        }

        //插入订单表
        order = new Orders();
        BeanUtils.copyProperties(addOrderDto, order);
        order.setId(IdWorkerUtils.getInstance().nextId());
        order.setCreateDate(LocalDateTime.now());
        order.setUserId(addOrderDto.getOpenId());
        order.setStatus("500001");
        int insert = ordersMapper.insert(order);
        if (insert <= 0) {
            KaiYuEducationException.cast("插入订单记录失败");
        }

        //插入订单明细表
        Long orderId = order.getId();
        String orderDetail = addOrderDto.getOrderDetail();
        List<OrdersGoods> xcOrdersGoodsList = JSON.parseArray(orderDetail, OrdersGoods.class);
        xcOrdersGoodsList.forEach(goods -> {
            goods.setOrderId(orderId);
            int insert1 = ordersGoodsMapper.insert(goods);
            if (insert1 <= 0) {
                KaiYuEducationException.cast("插入订单明细失败");
            }
        });
        return order;
    }

    public Orders getOrderByBusinessId(String businessId,String paymentType) {
        return ordersMapper.selectOne(new LambdaQueryWrapper<Orders>()
                .eq(Orders::getOutBusinessId, businessId)
                .eq(Orders::getOrderType, paymentType)
                .eq(Orders::getStatus, "500001")
        );
    }

    public PayRecord createPayRecord(Orders orders) {
        if (orders == null) {
            KaiYuEducationException.cast("订单不存在");
        }
        if ("500002".equals(orders.getStatus())) {
            KaiYuEducationException.cast("订单已支付");
        }

        PayRecord payRecord = new PayRecord();
        payRecord.setPayNo(IdWorkerUtils.getInstance().nextId());
        payRecord.setOrderId(orders.getId());
        payRecord.setOrderName(orders.getOrderName());
        payRecord.setTotalPrice(orders.getTotalPrice());
        payRecord.setCurrency("CNY");
        payRecord.setCreateDate(LocalDateTime.now());
        payRecord.setStatus("501001");
        payRecord.setUserId(orders.getUserId());
        int insert = payRecordMapper.insert(payRecord);
        if (insert <= 0) {
            KaiYuEducationException.cast("插入支付交易记录失败");
        }
        return payRecord;
    }

    public Map requestPayApi(PayRecord payRecord,String tradeType,Map<String, Object> attach) throws Exception{

        if (payRecord == null) {
            KaiYuEducationException.cast("订单支付记录不存在");
        }
        if ("501002".equals(payRecord.getStatus())) {
            KaiYuEducationException.cast("订单已支付");
        }

        HttpPost httpPost = null;

        if ("jsapi".equals(tradeType)){
            httpPost = new HttpPost(wxPayConfig.getDomain().concat(WxApiType.JSAPI_PAY.getType()));
        }else if ("native".equals(tradeType)){
            httpPost = new HttpPost(wxPayConfig.getDomain().concat(WxApiType.NATIVE_PAY.getType()));
        }else {
            httpPost = new HttpPost(wxPayConfig.getDomain().concat(WxApiType.JSAPI_PAY.getType()));
        }

        // 请求body参数
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("appid", wxPayConfig.getAppid());
        paramsMap.put("mchid", wxPayConfig.getMchId());
        paramsMap.put("description", payRecord.getOrderName());
        paramsMap.put("out_trade_no", payRecord.getPayNo().toString());
        paramsMap.put("notify_url", wxPayConfig.getNotifyDomain().concat(WxNotifyType.JSAPI_NOTIFY.getType()));

        paramsMap.put("amount", new HashMap<String, Object>(){{
            put("total", payRecord.getTotalPrice());
            put("currency", "CNY");

        }});


        if ("jsapi".equals(tradeType)){
            paramsMap.put("payer", new HashMap<String, Object>() {{
                put("openid", payRecord.getUserId());
            }});
        }

        if (!attach.isEmpty()){
            paramsMap.put("attach", JSON.toJSONString(attach));
        }

        //将参数转换成json字符串
        String jsonParams = JSON.toJSONString(paramsMap);

        log.info("requestVxPayApi ===> {}" + jsonParams);

        StringEntity entity = new StringEntity(jsonParams,"utf-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");

        //完成签名并执行请求
        CloseableHttpResponse response = wxPayClient.execute(httpPost);

        Map result = null;

        try {
            String bodyAsString = EntityUtils.toString(response.getEntity());//响应体
            int statusCode = response.getStatusLine().getStatusCode();//响应状态码
            if (statusCode == 200) { //处理成功
                log.info("requestVxPayApi success,return body = " + bodyAsString);
                result = (Map)JSON.parseObject(bodyAsString, Map.class);

                if ("jsapi".equals(tradeType)){
                    //返回前端参数
                    long timestamp = Instant.now().getEpochSecond();
                    String nonceStr = NonceUtil.createNonce(32);
                    String prepayId = result.get("prepay_id").toString();
                    String packageVal = "prepay_id=" + prepayId;
                    String sign = wxPayConfig.generateSign(timestamp,nonceStr,packageVal);

                    ((Map)result).put("timeStamp", timestamp);
                    ((Map)result).put("nonceStr", nonceStr);
                    ((Map)result).put("package", packageVal);
                    ((Map)result).put("signType", "RSA");
                    ((Map)result).put("paySign", sign);
                }

                ((Map)result).put("code", "SUCCESS");
            } else if (statusCode == 204) { //处理成功，无返回Body
                log.info("requestPayApi success");
                result = new HashMap<String, Object>() {
                    {
                        put("code", "SUCCESS");
                    }
                };
            } else {
                log.error("requestPayApi failed,resp code = " + statusCode + ",return body = " + bodyAsString);
                final Map<String, String> map = (Map)JSON.parseObject(bodyAsString, Map.class);
                result = new HashMap<String, Object>() {
                    {
                        put("code", "FAIL");
                        put("message", map.get("message"));
                    }
                };
            }

        }catch (Exception var2){
                log.error("requestVxPayApi exception", var2);
                result = new HashMap<String, Object>() {
                    {
                        put("code", "FAIL");
                        put("message", var2.getMessage());
                    }
                };
            }
        finally {
            try {
                response.close();
            } catch (Exception var3) {
                var3.printStackTrace();
                throw new RuntimeException("资源回收出错");
            }
            return (Map)result;
        }
    }

    public PayRecord getPayRecordByPayNo(String payNo) {
        return payRecordMapper.selectOne(new LambdaQueryWrapper<PayRecord>().eq(PayRecord::getPayNo, payNo));
    }

    /**
     * 查询vx支付结果状态
     * @param payNo
     * @return
     */
    @Override
    public PayStatusDto queryVxPayResult(String payNo) {
        if (StringUtils.isEmpty(payNo)) {
            KaiYuEducationException.cast("支付单号不能为空");
        }
        //1、调用支付查单接口查询订单支付状态
        Map map = null;
        try {
            map = queryPayResultFromVxPay(payNo);
        }catch (Exception e){
            log.error("queryPayResultFromVxPay exception", e);
            KaiYuEducationException.cast("查询支付结果失败");
        }
        if (map == null) {
            KaiYuEducationException.cast("查询支付结果失败");
        }

        PayStatusDto payStatusDto = new PayStatusDto();
        payStatusDto.setOut_trade_no(payNo);
        payStatusDto.setTrade_status(map.get("trade_state").toString());
        payStatusDto.setApp_id(map.get("appid").toString());


        if (WxTradeState.SUCCESS.getType().equals(map.get("trade_state").toString())) {
            payStatusDto.setTrade_type(map.get("trade_type").toString());
            payStatusDto.setTrade_no(map.get("transaction_id").toString());

            payStatusDto.setTotal_amount(((Map)map.get("amount")).get("total").toString());

            payStatusDto.setAttach(JSON.toJSONString(map.get("attach")));

            //2、成功才更新支付记录表和订单表状态
            saveVxPayStatus(payStatusDto);
        }

        return payStatusDto;
    }

    @Override
    public Map cancelVxPay(String payNo) {
        //调用微信支付关单接口
        Map map = null;
        try {
            map = closeOrderByPayNo(payNo);
        }catch (Exception e){
            log.error("cancelVxPay exception", e);
            KaiYuEducationException.cast("订单关闭异常，请重试！");
        }

        //更新支付记录状态 订单状态

        PayRecord payRecord = getPayRecordByPayNo(payNo);
        if (payRecord == null) {
            KaiYuEducationException.cast("未找到支付记录");
        }
        Orders orders = ordersMapper.selectById(payRecord.getOrderId());
        if (orders == null) {
            KaiYuEducationException.cast("未找到订单");
        }
        String statusFromDB = payRecord.getStatus();
        //已支付，直接返回
        if ("501002".equals(statusFromDB)) {
            return null;
        }

        payRecord.setStatus("501003");
        int updateRecord = payRecordMapper.updateById(payRecord);
        if (updateRecord <= 0) {
            log.debug("更新支付交易表失败");
            KaiYuEducationException.cast("更新支付交易表失败");
        }

        String statusFromDB1 = orders.getStatus();
        //已支付，直接返回
        if ("500002".equals(statusFromDB1)) {
            return null;
        }

        // 更新订单表
        orders.setStatus("500003");
        int updateOrder = ordersMapper.updateById(orders);
        if (updateOrder <= 0) {
            log.debug("更新订单表失败");
            KaiYuEducationException.cast("更新订单表失败");
        }

        return map;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void processOrder(Map<String, Object> bodyMap) throws GeneralSecurityException {
        log.info("processOrder ===> {}", bodyMap);

        //解密报文
        String plainText = decryptFromResource(bodyMap);

        Map plainTextMap = (Map) JSON.parseObject(plainText, Map.class);

        String orderNo = (String) plainTextMap.get("out_trade_no");

        if (StringUtils.isEmpty(orderNo)) {
            KaiYuEducationException.cast("processOrder订单号解密异常！");
        }
        //加锁
        RLock lock = redissonClient.getLock(LOCK_KEY_PREFIX + orderNo);
        try {
            boolean isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS); // 尝试获取锁，等待5秒，自动释放时间为10秒
            if (!isLocked) {
                log.warn("Another thread is processing orderNo: {}, skipping.", orderNo);
                return;
            }
            //获取锁成功
            PayStatusDto payStatusDto = new PayStatusDto();
            payStatusDto.setOut_trade_no(orderNo);
            payStatusDto.setTrade_status(plainTextMap.get("trade_state").toString());
            payStatusDto.setTrade_no(plainTextMap.get("transaction_id").toString());
            payStatusDto.setAttach(JSON.toJSONString(plainTextMap.get("attach")));

            //更新支付记录表和订单表状态
            saveVxPayStatus(payStatusDto);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error acquiring lock for orderNo: {}", orderNo, e);
            throw new RuntimeException("获取锁时发生中断异常");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock(); // 释放锁
            }
        }
    }

    public Map queryPayResultFromVxPay(String payNo) throws Exception {

        log.info("queryPayResultFromVxPay ===> {}", payNo);

        String url = String.format(WxApiType.ORDER_QUERY_BY_NO.getType(), payNo);

        url = wxPayConfig.getDomain().concat(url).concat("?mchid=").concat(wxPayConfig.getMchId());

        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");

        //完成签名并执行请求
        CloseableHttpResponse response = wxPayClient.execute(httpGet);

        Map result = null;
        try {
            String bodyAsString = EntityUtils.toString(response.getEntity());//响应体
            int statusCode = response.getStatusLine().getStatusCode();//响应状态码

            if (statusCode == 200) { //处理成功

                log.info("queryPayResultFromVxPay success,return body = " + bodyAsString);

                result = (Map)JSON.parseObject(bodyAsString, Map.class);
                ((Map)result).put("code", "SUCCESS");

            } else if (statusCode == 204) { //处理成功，无返回Body
                log.info("queryPayResultFromVxPay success");
                result = new HashMap<String, Object>() {
                    {
                        put("code", "SUCCESS");
                    }
                };
            } else {
                log.error("queryPayResultFromVxPay failed,resp code = " + statusCode + ",return body = " + bodyAsString);
                final Map<String, String> map = (Map)JSON.parseObject(bodyAsString, Map.class);
                result = new HashMap<String, Object>() {
                    {
                        put("code", "FAIL");
                        put("message", map.get("message"));
                    }
                };
            }
        }catch (final Exception var4){
            result = new HashMap<String, Object>() {
                {
                    put("code", "FAIL");
                    put("message", var4.getMessage());
                }
            };
        }
        finally {
            try {
                response.close();
            } catch (Exception var5) {
                var5.printStackTrace();
                throw new RuntimeException("资源回收出错");
            }
            return (Map)result;
        }
    }

    /**
     * 保存支付结果信息
     * @param payStatusDto
     */
    public void saveVxPayStatus(PayStatusDto payStatusDto) {
        // 1. 获取支付流水号
        String payNo = payStatusDto.getOut_trade_no();
        // 2. 查询数据库订单状态
        PayRecord payRecord = getPayRecordByPayNo(payNo);
        if (payRecord == null) {
            KaiYuEducationException.cast("未找到支付记录");
        }
        Orders orders = ordersMapper.selectById(payRecord.getOrderId());
        if (orders == null) {
            KaiYuEducationException.cast("未找到订单");
        }
        String statusFromDB = payRecord.getStatus();
        // 已支付，直接返回
        if ("501002".equals(statusFromDB)) {
            return;
        }

        String statusFromDB1 = orders.getStatus();
        // 已支付，直接返回
        if ("500002".equals(statusFromDB1)) {
            return;
        }

        String tradeStatus = payStatusDto.getTrade_status();
        if (WxTradeState.SUCCESS.getType().equals(tradeStatus)) {
            //支付成功
            payRecord.setStatus("501002");
            payRecord.setOutPayChannel("VxPay");
            payRecord.setOutPayNo(payStatusDto.getTrade_no());
            payRecord.setPaySuccessTime(LocalDateTime.now());

            int updateRecord = payRecordMapper.updateById(payRecord);
            if (updateRecord <= 0) {
                log.debug("更新支付交易表失败");
                KaiYuEducationException.cast("更新支付交易表失败");
            }

            // 更新订单表
            orders.setStatus("500002");
            int updateOrder = ordersMapper.updateById(orders);
            if (updateOrder <= 0) {
                log.debug("更新订单表失败");
                KaiYuEducationException.cast("更新订单表失败");
            }

            //发送mq执行相关支付成功业务
            //保存消息 在发送
//            Map attachMap = null;
//            if(StringUtils.isNotBlank(payStatusDto.getAttach())){
//                attachMap = JSON.parseObject(payStatusDto.getAttach(), Map.class);
//            }

            MqMessage mqMessage = mqMessageService.addMessage("payresult_notify", orders.getId().toString(),payStatusDto.getAttach() ,null );
            notifyPayResult(mqMessage);
        }

    }
    public void notifyPayResult(MqMessage mqMessage) {
        // 1. 将消息体转为Json
        String jsonMsg = JSON.toJSONString(mqMessage);
        // 2. 设消息的持久化方式为PERSISTENT，即消息会被持久化到磁盘上，确保即使在RabbitMQ服务器重启后也能够恢复消息。
        Message msgObj = MessageBuilder.withBody(jsonMsg.getBytes()).setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
        // 3. 封装CorrelationData，
        CorrelationData correlationData = new CorrelationData(mqMessage.getId().toString());
        correlationData.getFuture().addCallback(result -> {
            if (result.isAck()) {
                // 3.1 消息发送成功，删除消息表中的记录
                log.debug("notifyPayResult 消息发送成功：{}", jsonMsg);
                mqMessageService.completed(mqMessage.getId());
            } else {
                // 3.2 消息发送失败
                log.error("notifyPayResult 消息发送失败，id：{}，原因：{}", mqMessage.getId(), result.getReason());

            }
        }, ex -> {
            // 3.3 消息异常
            log.error("notifyPayResult 消息发送异常，id：{}，原因：{}", mqMessage.getId(), ex.getMessage());

        });
        // 4. 发送消息
        rabbitTemplate.convertAndSend(PayNotifyConfig.PAYNOTIFY_EXCHANGE_FANOUT, "", msgObj, correlationData);
    }

    private Map closeOrderByPayNo(String payNo) throws Exception {
        log.info("closeOrderByPayNO ===> {}", payNo);

        //创建远程请求对象
        String url = String.format(WxApiType.CLOSE_ORDER_BY_NO.getType(), payNo);
        url = wxPayConfig.getDomain().concat(url);
        HttpPost httpPost = new HttpPost(url);

        //组装json请求体
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("mchid", wxPayConfig.getMchId());

        String jsonParams = JSON.toJSONString(paramsMap);

        log.info("请求参数 ===> {}", jsonParams);

        //将请求参数设置到请求对象中
        StringEntity entity = new StringEntity(jsonParams,"utf-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");

        //完成签名并执行请求
        CloseableHttpResponse response = wxPayClient.execute(httpPost);

        Map result = null;

        try {
            int statusCode = response.getStatusLine().getStatusCode();//响应状态码
            if (statusCode == 200) { //处理成功
                log.info("closeOrderByPayNO 200");
                result = new HashMap<String, Object>() {
                    {
                        put("code", "SUCCESS");
                    }
                };
            } else if (statusCode == 204) { //处理成功，无返回Body
                log.info("closeOrderByPayNO 204");
                result = new HashMap<String, Object>() {
                    {
                        put("code", "SUCCESS");
                    }
                };
            } else {
                log.info("closeOrderByPayNO 失败,响应码 = " + statusCode);
                result = new HashMap<String, Object>() {
                    {
                        put("code", "FAIL");
                        put("statusCode", statusCode);
                    }
                };
            }

        }catch (final Exception var6){
            result = new HashMap<String, Object>() {
                {
                    put("code", "FAIL");
                    put("message", var6.getMessage());
                }
            };
        } finally {
            try {
                response.close();
            } catch (Exception var7) {
                var7.printStackTrace();
                throw new RuntimeException("资源回收出错");
            }
            return (Map)result;
        }


    }

    /**
     * 对称解密 微信支付回调
     * @param bodyMap
     * @return
     */
    private String decryptFromResource(Map<String, Object> bodyMap) throws GeneralSecurityException {

        log.info("processOrder的Resource密文解密");

        //通知数据
        Map<String, String> resourceMap = (Map) bodyMap.get("resource");
        //数据密文
        String ciphertext = resourceMap.get("ciphertext");
        //随机串
        String nonce = resourceMap.get("nonce");
        //附加数据
        String associatedData = resourceMap.get("associated_data");

        log.info("processOrder的Resource密文 ===> {}", ciphertext);
        AesUtil aesUtil = new AesUtil(wxPayConfig.getApiV3Key().getBytes(StandardCharsets.UTF_8));
        String plainText = aesUtil.decryptToString(associatedData.getBytes(StandardCharsets.UTF_8),
                nonce.getBytes(StandardCharsets.UTF_8),
                ciphertext);

        log.info("processOrder的Resource明文 ===> {}", plainText);

        return plainText;
    }


}
