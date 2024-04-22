package com.ruoyi.system.domain.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-15 11:32
 **/
@Data
@ToString
public class RegisterDto {

    private String username; //用户名
    private String password; //域  用于扩展
    private String register_phone;//手机号

    private String code;//注册手机验证码


}
