package com.kaiyu.learning.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kaiyu.learning.config.PayNotifyConfig;
import com.kaiyu.learning.domain.CourseTables;
import com.kaiyu.learning.domain.MqMessage;
import com.kaiyu.learning.domain.RestResponse;
import com.kaiyu.learning.feignclient.RemoteContentService;
import com.kaiyu.learning.feignclient.RemoteOrderService;
import com.kaiyu.learning.feignclient.RemoteSystemService;
import com.kaiyu.learning.mapper.CourseTablesMapper;
import com.ruoyi.common.core.exception.KaiYuEducationException;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReceivePayNotifyServiceImpl {

    @Autowired
    CourseTablesMapper courseTablesMapper;

    @Autowired
    RemoteOrderService remoteOrderService;

    @Autowired
    RemoteContentService remoteContentService;

    private static final Logger log = LoggerFactory.getLogger(ReceivePayNotifyServiceImpl.class);


    @RabbitListener(queues = PayNotifyConfig.PAYNOTIFY_QUEUE)
    @Transactional(rollbackFor = Exception.class)
    public void receive(Message message) {
        // 1. 获取消息
        MqMessage mqMessage = JSON.parseObject(message.getBody(), MqMessage.class);
        // 2. 进行解析
        String messageType = mqMessage.getMessageType();

        // 3. 处理支付结果的通知
        if (PayNotifyConfig.MESSAGE_TYPE.equals(messageType)) {

            String orderId = mqMessage.getBusinessKey1();
            String attach = mqMessage.getBusinessKey2();
            String userId = mqMessage.getBusinessKey3();

            Object parse = JSON.parse(attach);
            JSONObject jsonObject = JSON.parseObject(parse.toString());
            JSONArray phoneArray = (JSONArray) jsonObject.get("toRechargePhone");
            List<String> phoneList = phoneArray.toJavaList(String.class);


            RestResponse response = remoteOrderService.getUserOrderGoodsList(orderId);
            if (response.getCode() != 200){
                log.info("获取订单商品列表失败");
                KaiYuEducationException.cast("获取订单商品列表失败");
            }
            List<LinkedHashMap<String, Object>> OrderGoodList = (List<LinkedHashMap<String, Object>>) response.getData();

            List<Long> goodsId = OrderGoodList.stream().map(goods -> Long.parseLong((String) goods.get("goodsId"))).collect(Collectors.toList());
            //远程调用查询商品详情
            RestResponse goodResponse = remoteContentService.getGoodsListByIds(goodsId);
            if (goodResponse.getCode() != 200){
                log.info("获取商品详情列表失败");
                KaiYuEducationException.cast("获取商品详情列表失败");
            }
            List<LinkedHashMap<String, Object>> GoodDetailList = (List<LinkedHashMap<String, Object>>) goodResponse.getData();

            //组成公共属性的课程表对象
            List<CourseTables> courseTablesList = new ArrayList<>();
            for(LinkedHashMap<String, Object>  GoodDetail : GoodDetailList) {
                for (LinkedHashMap<String, Object> goods : OrderGoodList) {
                    if (((String) goods.get("goodsId")).equals(String.valueOf(GoodDetail.get("id")))) {
                        CourseTables courseTables = new CourseTables();
                        courseTables.setCourseId(Long.parseLong((String) GoodDetail.get("outBusinessId")));
                        courseTables.setCourseName((String) GoodDetail.get("goodName"));
                        courseTables.setCourseType((String) GoodDetail.get("goodsType"));
                        courseTables.setValidDays(Integer.parseInt(String.valueOf(goods.get("validDays"))));
                        courseTables.setCreateDate(LocalDateTime.now());
                        courseTables.setValidtimeStart(LocalDateTime.now());
                        courseTables.setValidtimeEnd(LocalDateTime.now().plusDays(Long.parseLong(String.valueOf(goods.get("validDays")))));
                        courseTablesList.add(courseTables);
                        break;
                    }
                }
            }

            if(phoneList.isEmpty()){
                log.info("没有需要充值的手机号");
                //给自己账号充值
                courseTablesList.forEach(courseTables -> {
                    courseTables.setUserId(userId);
                    courseTables.setRemarks("USER_VX_OPENID");
                });

                //批量插入课程表
                int insert = courseTablesMapper.insertCourseTablesBatch(courseTablesList);
                log.info("批量保存课程表{}条",insert);
                if (insert < courseTablesList.size()){
                    log.info("批量保存课程表失败");
                    KaiYuEducationException.cast("批量保存课程表失败");
                }


            }else {
                log.info("手机号列表：{}",phoneList);
                //给手机号充值
                List<CourseTables> newCourseTablesList = new ArrayList<>();
                phoneList.forEach(phone -> {
                    courseTablesList.forEach(courseTables -> {
                        courseTables.setUserId(phone);
                        courseTables.setRemarks("USER_PHONE");
                        newCourseTablesList.add(courseTables);
                    });
                });

                //批量插入课程表
                int insert = courseTablesMapper.insertCourseTablesBatch(newCourseTablesList);
                log.info("批量保存课程表{}条",insert);
                if (insert < (phoneList.size()*courseTablesList.size())){
                    log.info("批量保存课程表失败");
                    KaiYuEducationException.cast("批量保存课程表失败");
                }
            }

        }
    }


}
