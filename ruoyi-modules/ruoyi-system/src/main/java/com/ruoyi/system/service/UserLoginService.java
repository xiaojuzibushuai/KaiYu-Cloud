package com.ruoyi.system.service;


import com.ruoyi.system.api.domain.AdminUser;
import com.ruoyi.system.api.domain.User;
import com.ruoyi.system.api.model.RegisterDto;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-11 20:32
 **/
public interface UserLoginService {


    User selectUserByregisterPhone(String register_phone, String password);

    AdminUser selectAdminUserByUserName(String username, String password);

    AdminUser getAdminUserInfo(String username);

    User getUserInfo(String register_phone);

    User smsLogin(String register_phone, String code);

    User register(RegisterDto registerDto);
}
