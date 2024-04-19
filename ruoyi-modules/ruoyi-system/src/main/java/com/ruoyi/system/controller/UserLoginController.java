package com.ruoyi.system.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.JwtUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.security.annotation.InnerAuth;
import com.ruoyi.common.security.annotation.Logical;
import com.ruoyi.common.security.annotation.RequiresRoles;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.system.api.domain.AdminUser;
import com.ruoyi.system.api.domain.User;
import com.ruoyi.system.api.model.LoginUser;
import com.ruoyi.system.api.model.RegisterDto;
import com.ruoyi.system.domain.vo.UserVo;
import com.ruoyi.system.service.UserLoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-11 19:55
 **/
@RestController
@RequestMapping("/system")
@Api(value = "登录注册相关接口",tags = "登录注册相关接口")
public class UserLoginController {

    @Autowired
    private UserLoginService userLoginService;


    /**
     * 用户登录
     */
//    @InnerAuth
    @PostMapping("/userinfo")
    @ApiOperation(value = "认证中心远程调用用户登录接口")
    public R<LoginUser> info(@RequestParam("register_phone") String register_phone,@RequestParam("password") String password)
    {
//        SysUser sysUser = userService.selectUserByregisterPhoned(username);
//        if (StringUtils.isNull(sysUser))
//        {
//            return R.fail("用户名或密码错误");
//        }
        // 角色集合
//        Set<String> roles = permissionService.getRolePermission(sysUser);
        // 权限集合
//        Set<String> permissions = permissionService.getMenuPermission(sysUser);
        User user = userLoginService.selectUserByregisterPhone(register_phone, password);
        if (StringUtils.isNull(user))
        {
            return R.fail("手机号或密码错误");
        }
        Set<String> roles = new HashSet<String>();
        //普通用户
        roles.add("common");

        LoginUser userVo = new LoginUser();
        userVo.setUser(user);
//        sysUserVo.setSysUser(sysUser);
        userVo.setRoles(roles);
//        sysUserVo.setPermissions(permissions);
        return R.ok(userVo);
    }

    @PostMapping("/smsinfo")
    @ApiOperation(value = "认证中心远程调用短信登录接口")
    public R<LoginUser> smsInfo(@RequestParam("register_phone") String register_phone,@RequestParam("code") String code)
    {
        User user = userLoginService.smsLogin(register_phone, code);
        if (StringUtils.isNull(user))
        {
            return R.fail("手机号验证码错误或过期");
        }
        Set<String> roles = new HashSet<String>();
        //普通用户
        roles.add("common");

        LoginUser userVo = new LoginUser();
        userVo.setUser(user);
        userVo.setRoles(roles);

        return R.ok(userVo);
    }


    /**
     * 管理员后台登录
     */
//    @InnerAuth
    @PostMapping("/admininfo")
    @ApiOperation(value = "认证中心远程调用管理员登录接口")
    public R<LoginUser> adminLogin(@RequestParam("username") String username,@RequestParam("password") String password)
    {
//        SysUser sysUser = userService.selectUserByregisterPhoned(username);
//        if (StringUtils.isNull(sysUser))
//        {
//            return R.fail("用户名或密码错误");
//        }
        // 角色集合
//        Set<String> roles = permissionService.getRolePermission(sysUser);
        // 权限集合
//        Set<String> permissions = permissionService.getMenuPermission(sysUser);
        AdminUser user = userLoginService.selectAdminUserByUserName(username, password);
        if (StringUtils.isNull(user))
        {
            return R.fail("用户名或密码错误");
        }
        Set<String> roles = new HashSet<String>();
        //管理员用户
        roles.add("admin");

        LoginUser userVo = new LoginUser();
        userVo.setAdminUser(user);
//        sysUserVo.setSysUser(sysUser);
        userVo.setRoles(roles);
//        sysUserVo.setPermissions(permissions);
        return R.ok(userVo);
    }


    /**
     * 获取用户详细信息
     */
    @PostMapping("/getUserInfo")
    @RequiresRoles(value = {"common"}, logical = Logical.OR)
    @ApiOperation(value = "获取用户信息")
    public R<UserVo> getUserInfo(HttpServletRequest request){

        String token = SecurityUtils.getToken(request);

        if (StringUtils.isNotEmpty(token))
        {
            String register_phone = JwtUtils.getPhone(token);
            if (StringUtils.isNotEmpty(register_phone))
            {
                User userInfo = userLoginService.getUserInfo(register_phone);
                UserVo userVo = new UserVo();
                userVo.setUserid(userInfo.getOpenid());
                userVo.setLoginTime(userInfo.getUptime());
                userVo.setRegister_phone(userInfo.getRegister_phone());
                userVo.setIpaddr(userInfo.getIp());
                userVo.setUsername(userInfo.getNickname());
                userVo.setAvatar(userInfo.getAvatar());

                return R.ok(userVo);
            }

        }
        return R.ok();
    }


    @PostMapping("/register")
    @ApiOperation(value = "认证中心远程调用用户注册接口")
    public R<LoginUser> register(@RequestBody RegisterDto registerDto)
    {
        User register = userLoginService.register(registerDto);
        if (StringUtils.isNull(register))
        {
            return R.fail("用户手机号已存在或验证码已失效，注册失败");
        }
        LoginUser userVo = new LoginUser();
        userVo.setUsername(register.getNickname());
        userVo.setRegister_phone(register.getRegister_phone());

        return R.ok(userVo);
    }


}
