package com.kaiyu.order.controller;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.kaiyu.order.domain.RestResponse;
import com.kaiyu.order.domain.dto.AddOrderDto;
import com.kaiyu.order.domain.dto.PayRecordDto;
import com.kaiyu.order.domain.dto.PayStatusDto;
import com.kaiyu.order.service.OrderService;
import com.kaiyu.order.util.HttpUtils;
import com.kaiyu.order.util.WechatPay2ValidatorForRequest;
import com.ruoyi.common.core.utils.JwtUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.security.annotation.Logical;
import com.ruoyi.common.security.annotation.RequiresRoles;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @program: KaiYu-Cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-06-17 15:07
 **/

@RestController
@RequestMapping("/order")
@Api(value = "订单支付接口",tags = "订单支付接口")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    OrderService orderService;

    @Resource
    private Verifier verifier;


    /**
     * 创建订单
     * @return
     */

    //TODO 未加权限
    @ApiOperation("创建订单")
//    @RequiresRoles(value = {"common"}, logical = Logical.OR)
    @PostMapping("/wxpay/jsapi/requestPay")
    public RestResponse requestPay(@RequestBody AddOrderDto addOrderDto){
        if (StringUtils.isNotEmpty(addOrderDto.getOpenId())){
            Map<String, Object> result = orderService.createOrder(addOrderDto);
            if (result == null){
                return RestResponse.validfail("请求支付API失败!请稍后重试！");
            }
            return RestResponse.success(result);
        }
        return RestResponse.validfail("参数非法，没有指定付款人！");
    }

    //TODO 未加权限
    @ApiOperation("查询微信支付结果")
    //    @RequiresRoles(value = {"common"}, logical = Logical.OR)
    @GetMapping("/wxpay/payResult/{payNo}")
    public RestResponse payresult(@PathVariable("payNo")String payNo) {
        PayStatusDto payStatusDto = orderService.queryVxPayResult(payNo);
        return RestResponse.success(payStatusDto);
    }


    //TODO 未加权限
    @ApiOperation("取消微信支付")
    //    @RequiresRoles(value = {"common"}, logical = Logical.OR)
    @GetMapping("/wxpay/cancelPay/{payNo}")
    public RestResponse cancelVxPay(@PathVariable("payNo")String payNo) {

        Map map = orderService.cancelVxPay(payNo);

        return RestResponse.success("订单已取消");
    }

    /**
     * 支付通知
     * 微信支付通过支付通知接口将用户支付成功消息通知给商户
     *    TODO 未加权限
     */
    @ApiOperation("微信支付异步通知")
    //    @RequiresRoles(value = {"common"}, logical = Logical.OR)
    @PostMapping("/wxpay/jsapi/notify")
    public String wxPayjsApiNotify(HttpServletRequest request, HttpServletResponse response){

        Map<String, String> params = new HashMap<>();//应答对象

        try {
            //处理通知参数
            String body = HttpUtils.readData(request);
            Map<String, Object> bodyMap = (Map) JSON.parseObject(body, Map.class);
            String requestId = (String)bodyMap.get("id");
            log.info("wxPayjsApiNotify id ===> {}", requestId);
            //log.info("支付通知的完整数据 ===> {}", body);

            //签名的验证
            WechatPay2ValidatorForRequest wechatPay2ValidatorForRequest
                    = new WechatPay2ValidatorForRequest(verifier, requestId, body);

            if(!wechatPay2ValidatorForRequest.validate(request)){

                log.error("wxPayjsApiNotify 通知验签失败");
                //失败应答
                response.setStatus(500);
                params.put("code", "ERROR");
                params.put("message", "微信支付异步通知接口通知验签失败");
                return (JSON.toJSONString(params));
            }

            log.info("wxPayjsApiNotify 通知验签成功");

            //回调通知 处理订单
            orderService.processOrder(bodyMap);


            //成功应答
            response.setStatus(200);
            params.put("code", "SUCCESS");
            params.put("message", "微信支付异步通知接口成功");
            return (JSON.toJSONString(params));

        } catch (Exception e) {
            e.printStackTrace();
            //失败应答
            response.setStatus(500);
            params.put("code", "ERROR");
            params.put("message", "微信支付异步通知接口异常造成失败！");
            return (JSON.toJSONString(params));
        }

    }


    @ApiOperation("生成支付二维码")
    @RequiresRoles(value = {"common"}, logical = Logical.OR)
    @PostMapping("/generatePayCode")
    public RestResponse<PayRecordDto> generatePayCode(HttpServletRequest request, @RequestBody AddOrderDto addOrderDto) {
        String token = SecurityUtils.getToken(request);
        if (StringUtils.isNotEmpty(token))
        {
            String register_phone = JwtUtils.getPhone(token);
            if (StringUtils.isNotEmpty(register_phone))
            {
                PayRecordDto order = null;
                if (order == null){
                    return RestResponse.validfail("订单二维码生成失败！请刷新重试！");
                }
                return RestResponse.success(order);
            }
        }
        return RestResponse.validfail("令牌解析非法，请重新登录支付！");
    }




}
