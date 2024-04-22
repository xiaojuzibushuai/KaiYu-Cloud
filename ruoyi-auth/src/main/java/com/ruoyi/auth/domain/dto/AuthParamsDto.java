package com.ruoyi.auth.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: kai-yu-cloud
 * @description: 统一认证入口后统一提交的数据
 * @author: xiaojuzi
 * @create: 2024-04-11 15:10
 **/
@Data
@ToString
public class AuthParamsDto {

    private String username; //用户名
    private String password; //域  用于扩展
    private String register_phone;//手机号
    private String checkcode;//验证码
    private String checkcodekey;//验证码key
    @NotBlank(message = "password:用户名密码模式类型    sms:短信模式类型")
    @ApiModelProperty(value = "password:用户名密码模式类型    sms:短信模式类型", required = true)
    private String authType; // 认证的类型   password:用户名密码模式类型    sms:短信模式类型
    private Map<String, Object> payload = new HashMap<>();//附加数据，作为扩展，不同认证类型可拥有不同的附加数据。如认证类型为短信时包含smsKey : sms:3d21042d054548b08477142bbca95cfa; 所有情况下都包含clientId

}
