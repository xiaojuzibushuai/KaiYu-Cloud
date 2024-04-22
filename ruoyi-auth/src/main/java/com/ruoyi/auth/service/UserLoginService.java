package com.ruoyi.auth.service;

import com.ruoyi.auth.util.SMSValidation;
import com.ruoyi.auth.util.SmsUtil;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.system.api.RemoteUserService;
import com.ruoyi.system.api.model.LoginUser;
import com.ruoyi.system.api.model.RegisterDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-11 20:25
 **/
@Component
public class UserLoginService {
    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    public RedisTemplate redisTemplate;

    @Autowired
    SmsUtil smsUtil;

    public LoginUser login(String register_phone,String password) {

//        return remoteUserService.getUserInfo(username, SecurityConstants.INNER).getData();
        return remoteUserService.info(register_phone, password).getData();

    }

    public LoginUser smsInfo(String register_phone,String code) {
        return remoteUserService.smsInfo(register_phone, code).getData();
    }


    public LoginUser adminLogin(String username,String password) {
        return remoteUserService.adminLogin(username, password).getData();
    }


    public String sendSms(String phone,String sendType){

        String code = null;

        if (sendType.equals("LOGIN")){
            //校验是否5分钟内发送过
            if (redisTemplate.hasKey("SMS_WEB_LOGIN_CODE_"+ phone)){
                return "5分钟内不能重复发送";
            }
            //生成随机code
            code = SMSValidation.generateVerificationCode();
            redisTemplate.opsForValue().set("SMS_WEB_LOGIN_CODE_"+ phone,code, 5,TimeUnit.MINUTES);

        } else if (sendType.equals("REGISTER")) {
            //校验是否5分钟内发送过
            if (redisTemplate.hasKey("SMS_WEB_REGISTER_CODE_"+ phone)){
                return "5分钟内不能重复发送";
            }
            //生成随机code
            code = SMSValidation.generateVerificationCode();
            redisTemplate.opsForValue().set("SMS_WEB_REGISTER_CODE_"+ phone,code, 5,TimeUnit.MINUTES);

        }
        if (code  == null){
            return "验证码生成失败";
        }

        try {

            String result = smsUtil.sendSms(phone, code);

            if (result != "success"){
                //发送失败 清除cache
                if (sendType.equals("LOGIN")){
                    redisTemplate.delete("SMS_WEB_LOGIN_CODE_"+ phone);
                }else if (sendType.equals("REGISTER")){
                    redisTemplate.delete("SMS_WEB_REGISTER_CODE_"+ phone);
                }
            }

            return result;

        } catch (Exception e) {
            //异常清除cache
            if (sendType.equals("LOGIN")){
                redisTemplate.delete("SMS_WEB_LOGIN_CODE_"+ phone);
            }else if (sendType.equals("REGISTER")){
                redisTemplate.delete("SMS_WEB_REGISTER_CODE_"+ phone);
            }
            return e.getMessage();
        }

    }


    public LoginUser register(RegisterDto registerDto) {
       return remoteUserService.register(registerDto).getData();
    }
}
