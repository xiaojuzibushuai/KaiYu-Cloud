package com.kaiyu.learning.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kaiyu.learning.config.PayNotifyConfig;
import com.kaiyu.learning.domain.MqMessage;
import com.kaiyu.learning.feignclient.RemoteSystemService;
import com.kaiyu.learning.mapper.CourseTablesMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ReceivePayNotifyServiceImpl {

    @Autowired
    CourseTablesMapper courseTablesMapper;

    @Autowired
    RemoteSystemService remoteSystemService;

    private static final Logger log = LoggerFactory.getLogger(ReceivePayNotifyServiceImpl.class);


    @RabbitListener(queues = PayNotifyConfig.PAYNOTIFY_QUEUE)
    public void receive(Message message) {
        // 1. 获取消息
        MqMessage mqMessage = JSON.parseObject(message.getBody(), MqMessage.class);
        // 2. 进行解析
        String messageType = mqMessage.getMessageType();
        String orderId = mqMessage.getBusinessKey1();
        String attach = mqMessage.getBusinessKey2();

        Object parse = JSON.parse(attach);
        JSONObject jsonObject = JSON.parseObject(parse.toString());
        JSONArray phoneArray = (JSONArray) jsonObject.get("toRechargePhone");
        List<String> phoneList = phoneArray.toJavaList(String.class);

        // 3. 处理支付结果的通知
        if (PayNotifyConfig.MESSAGE_TYPE.equals(messageType)) {
            if(phoneList.isEmpty()){
                log.info("没有需要充值的手机号");
                //给自己账号充值

            }else {
                log.info("手机号列表：{}",phoneList);
                for (String phone : phoneList) {
                    //给手机号充值
                }
            }

        }
            //进行购买课程类订单结果处理
//            if ("60201".equals(orderType)){
//                boolean flag = tablesService.saveChooseCourseStatus(chooseCourseId);
//                if (!flag){
//                    XueChengPlusException.cast("保存选课记录失败");
//                }
//            }
    }

}
