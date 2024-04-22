package com.ruoyi.system.api;

import com.ruoyi.system.api.model.RegisterDto;
import com.ruoyi.system.api.model.UserVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.constant.ServiceNameConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.api.domain.SysUser;
import com.ruoyi.system.api.factory.RemoteUserFallbackFactory;
import com.ruoyi.system.api.model.LoginUser;

/**
 * 用户服务
 * 
 * @author ruoyi
 */
@FeignClient(contextId = "remoteUserService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteUserFallbackFactory.class)
public interface RemoteUserService
{



    @PostMapping("/system/admininfo")
    public R<LoginUser> adminLogin(@RequestParam("username") String username,@RequestParam("password") String password);

    @PostMapping("/system/userinfo")
    public R<LoginUser> info(@RequestParam("register_phone") String register_phone, @RequestParam("password") String password);

    @PostMapping("/system/smsinfo")
    public R<LoginUser> smsInfo(@RequestParam("register_phone") String registerPhone,@RequestParam("code") String code);


    @PostMapping("/system/register")
    @ApiOperation(value = "认证中心远程调用用户注册接口")
    public R<LoginUser> register(@RequestBody RegisterDto registerDto);



    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @param source 请求来源
     * @return 结果
     */
    @GetMapping("/user/info/{username}")
    public R<LoginUser> getUserInfo(@PathVariable("username") String username, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 注册用户信息
     *
     * @param sysUser 用户信息
     * @param source 请求来源
     * @return 结果
     */
    @PostMapping("/user/register")
    public R<Boolean> registerUserInfo(@RequestBody SysUser sysUser, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);



}
