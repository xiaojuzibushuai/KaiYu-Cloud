package com.kaiyu.learning.config;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * @program: kai-yu-cloud
 * @description: 消费者回调
 * @author: xiaojuzi
 * @create: 2024-04-18 10:46
 **/
public class MqttConsumerCallBack implements MqttCallback{

        /**
         * 客户端断开连接的回调
         */
        @Override
        public void connectionLost(Throwable throwable) {
            System.out.println("与服务器断开连接，可重连");
        }

        /**
         * 消息到达的回调
         */
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            System.out.println(String.format("接收消息主题 : %s",topic));
            System.out.println(String.format("接收消息Qos : %d",message.getQos()));
            System.out.println(String.format("接收消息内容 : %s",new String(message.getPayload())));
            System.out.println(String.format("接收消息retained : %b",message.isRetained()));
        }

        /**
         * 消息发布成功的回调
         */
        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

        }
}
