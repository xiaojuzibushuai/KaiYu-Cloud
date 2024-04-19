package com.kaiyu.learning.controller;

import com.kaiyu.learning.config.MqttConsumerConfig;
import com.kaiyu.learning.config.MqttProviderConfig;
import com.kaiyu.learning.domain.RestResponse;
import com.kaiyu.learning.domain.dto.MqttSendMessageDto;
import com.kaiyu.learning.service.LearningService;
import com.ruoyi.common.security.annotation.Logical;
import com.ruoyi.common.security.annotation.RequiresRoles;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-18 10:44
 **/
@RestController
@RequestMapping("/learning/mqtt")
@Api(value = "MQTT消息管理接口", tags = "MQTT消息管理接口")
public class MqttSendController {

    @Autowired
    private MqttProviderConfig providerClient;

//    @Autowired
//    private MqttConsumerConfig client;

//    @Value("${spring.mqtt.client.id}")
//    private String clientId;

    @Autowired
    LearningService learningService;


//    @PostMapping("/pushDat")
//    @ApiOperation("预制课发送接口")
//    @RequiresRoles(value = {"admin", "common"}, logical = Logical.OR)
    public RestResponse<Object> videoAutoPushDatToDevice(){

        return RestResponse.success();
    }

//    @PostMapping("/pushAction")
//    @ApiOperation("web端对场景下设备进行运行控制")
//    @RequiresRoles(value = {"admin", "common"}, logical = Logical.OR)
    public RestResponse<Object> videoAutoPushActionToDevice(@RequestParam("operate")String operate,@RequestParam("sceneid")String sceneid){

        return  learningService.videoAutoPushActionToDevice(operate,sceneid);
    }






    @PostMapping("/sendMessage")
    @ApiOperation("mqtt发送消息测试接口")
    @RequiresRoles(value = {"admin", "common"}, logical = Logical.OR)
    public String sendMessage(@RequestBody MqttSendMessageDto message){
        try {
            providerClient.publish(message.getQos(), message.getRetained(), message.getTopic(), message.getMessage());
            return "发送成功";
        } catch (Exception e) {
//            e.printStackTrace();
            return "发送失败";
        }
    }

//    @RequestMapping("/connect")
//    @ResponseBody
//    public String connect(){
//        client.connect();
//        return clientId + "连接到服务器";
//    }
//
//    @RequestMapping("/disConnect")
//    @ResponseBody
//    public String disConnect(){
//        client.disConnect();
//        return clientId + "与服务器断开连接";
//    }
}
