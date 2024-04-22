package com.ruoyi.system.api.model;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-15 11:32
 **/
@Data
@ToString
public class RegisterDto {

    @NotBlank
    private String username; //用户名
    @NotBlank
    private String password; //域  用于扩展
    @NotBlank
    private String register_phone;//手机号

    @NotBlank
    private String code;//注册手机验证码



}
