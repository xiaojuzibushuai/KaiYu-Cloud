package com.ruoyi.auth.util;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.tea.*;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.Common;
import com.aliyun.teautil.models.RuntimeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-15 10:23
 **/
@Component
public class SmsUtil {

    private static final Logger log = LoggerFactory.getLogger(SmsUtil.class);

    @Value("${aliyun.sms.accessKeyId}")
    String accessKeyId;

    @Value("${aliyun.sms.accessKeySecret}")
    String accessKeySecret;

    @Value("${aliyun.sms.smsSignName}")
    String signName;

    @Value("${aliyun.sms.smsLoginTemplateCode}")
    String templateCode;


    /**
     * 发送短信
     * xiaojuzi
     * @param phoneNumber
     * @param code
     * @return
     * @throws Exception
     */

    public String sendSms(String phoneNumber,String code) throws Exception {

        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        Client client = new Client(config);

        com.aliyun.dysmsapi20170525.models.SendSmsRequest sendSmsRequest = new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
                .setPhoneNumbers(phoneNumber)
                .setSignName(signName)
                .setTemplateCode(templateCode)
                .setTemplateParam("{\"code\":\""+code+"\"}");
        SendSmsResponse sendSmsResponse = client.sendSms(sendSmsRequest);
            try {

//                System.out.println(new Gson().toJson(response.Body));
                if (sendSmsResponse.body.code.equals("OK")) {
                    // 短信发送成功
                    log.info("发送短信成功：{}",phoneNumber);
                    return "success";
                } else {
                    // 短信发送失败
                    log.error("发送短信失败{}",phoneNumber);
                    return sendSmsResponse.body.message;
                }
            }catch (TeaException error){
                String s = Common.assertAsString(error.message);
                log.error("发送短信失败:{}",s);
                return sendSmsResponse.body.message;
            }
    }

}
