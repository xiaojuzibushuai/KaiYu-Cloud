package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ruoyi.common.core.exception.KaiYuEducationException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.system.api.domain.AdminUser;
import com.ruoyi.system.api.domain.AuditUser;
import com.ruoyi.system.api.domain.User;
import com.ruoyi.system.api.model.RegisterDto;
import com.ruoyi.system.mapper.AdminUserMapper;
import com.ruoyi.system.mapper.AuditUserMapper;
import com.ruoyi.system.mapper.UserMapper;
import com.ruoyi.system.service.UserLoginService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-11 20:38
 **/
@Service
public class UserLoginServiceImpl implements UserLoginService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    AdminUserMapper adminUserMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    AuditUserMapper auditUserMapper;



    @Override
    public User selectUserByregisterPhone(String register_phone, String password) {
//        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>().eq(User::getRegister_phone, register_phone);
//        User user = userMapper.selectOne(queryWrapper);
        User user = getUserInfo(register_phone);
        if(user == null || StringUtils.isEmpty(user.getPassword())){
            user = null;

            return user;
        }

        if (!SecurityUtils.matchesPassword(password,user.getPassword())) {

            user = null;
        }

        return user;
    }

    @Override
    public AdminUser selectAdminUserByUserName(String username, String password) {
        LambdaQueryWrapper<AdminUser> queryWrapper = new LambdaQueryWrapper<AdminUser>().eq(AdminUser::getUsername, username);
        AdminUser adminUser = adminUserMapper.selectOne(queryWrapper);

        if(adminUser == null || StringUtils.isEmpty(adminUser.getPassword())){
            adminUser = null;

            return adminUser;
        }

        if (!SecurityUtils.matchesPassword(password,adminUser.getPassword())) {

            adminUser = null;
        }

        return adminUser;
    }

    @Override
    public AdminUser getAdminUserInfo(String username) {
        AdminUser adminUserInfo = adminUserMapper.getAdminUserInfo(username);
        return adminUserInfo;
    }

    @Override
    public User getUserInfo(String register_phone) {
        User user = userMapper.getUserInfo(register_phone);
        return user;
    }

    @Override
    public User smsLogin(String register_phone, String code) {
        // 判断验证码是否正确
        String smsCode = (String) redisTemplate.opsForValue().get("SMS_WEB_LOGIN_CODE_" + register_phone);
        if (!code.equals(smsCode)) {
            return null;
        }
        User user = getUserInfo(register_phone);
        if (user == null ){
            return null;
        }
        return user;
    }

    @Override
    public User register(RegisterDto registerDto) {

        if (registerDto.getRegister_phone() == null) {
            return null;
        }
        String register_phone = registerDto.getRegister_phone();

        //先校验验证码是否正常
        String smsCode = (String)  redisTemplate.opsForValue().get("SMS_WEB_REGISTER_CODE_" + register_phone);

        if (registerDto.getCode() == null) {
            return null;
        }

        if (!registerDto.getCode().equals(smsCode)) {
            return null;
        }


        User userInfo = getUserInfo(registerDto.getRegister_phone());
        if (userInfo != null) {
            return null;
        }

        User user = new User();
        user.setRegister_phone(registerDto.getRegister_phone());
        user.setPassword(SecurityUtils.encryptPassword(registerDto.getPassword()));
        user.setNickname(registerDto.getUsername());

        int insert = userMapper.insert(user);
        if (insert <= 0) {
            return null;
        }

        return user;
    }

    @Override
    public List<AuditUser> getAllAuditUser() {

        return auditUserMapper.getAllAuditUser();
    }

    @Override
//    @Transactional(rollbackFor = Exception.class)
    public void updateAuditUserStatus(Long auditUserId) {

        AuditUser auditUser = auditUserMapper.selectOne(new LambdaQueryWrapper<AuditUser>().eq(AuditUser::getId,auditUserId));
        if (auditUser == null) {
            KaiYuEducationException.cast("审核用户不存在");
        }
        User user = new User();
        BeanUtils.copyProperties(auditUser,user);
        user.setRegister_phone(auditUser.getRegisterPhone());
        user.setRole_id(auditUser.getRoleId());
        user.setTrue_name(auditUser.getTrueName());
        user.setLogin_count(auditUser.getLoginCount());

        User user1 = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getRegister_phone, user.getRegister_phone()));
        if (user1 != null) {
            KaiYuEducationException.cast("该用户已注册");
        }
        //审核通过添加到用户表
        int insert = userMapper.insert(user);
        if (insert <= 0) {
            KaiYuEducationException.cast("新增用户失败");
        }
        //删除审核表记录
        int i = auditUserMapper.deleteById(auditUserId);
        if (i <= 0) {
            KaiYuEducationException.cast("删除审核用户失败");
        }

    }

    @Override
    public void deleteAuditUserStatus(Long auditUserId) {
        AuditUser auditUser = auditUserMapper.selectOne(new LambdaQueryWrapper<AuditUser>().eq(AuditUser::getId,auditUserId));
        if (auditUser == null) {
            KaiYuEducationException.cast("审核用户不存在");
        }
        //删除审核表记录
        int i = auditUserMapper.deleteById(auditUserId);
        if (i <= 0) {
            KaiYuEducationException.cast("删除审核用户失败");
        }
    }


}
