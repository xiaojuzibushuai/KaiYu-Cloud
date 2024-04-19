package com.ruoyi.auth.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-15 12:04
 **/
@Data
@ToString
public class SendSmsDto {

    @NotBlank
    private String register_phone;

    @NotBlank(message = "REGISTER:注册业务  LOGIN:登录业务")
    @ApiModelProperty(value = "REGISTER:注册业务  LOGIN:登录业务", required = true)
    private String sendType;

}
