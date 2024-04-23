package com.ruoyi.auth.controller;

import javax.servlet.http.HttpServletRequest;

import com.ruoyi.auth.domain.dto.AuthParamsDto;
import com.ruoyi.auth.domain.dto.SendSmsDto;
import com.ruoyi.auth.service.UserLoginService;
import com.ruoyi.auth.util.SMSValidation;
import com.ruoyi.system.api.model.RegisterDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.auth.form.RegisterBody;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.JwtUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.security.auth.AuthUtil;
import com.ruoyi.common.security.service.TokenService;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.system.api.model.LoginUser;

/**
 * token 控制
 * 
 * @author ruoyi
 * 重写 by xiaojuzi
 *
 */
@RestController
@Api(value = "认证授权相关接口",tags = "认证授权相关接口")
@RequestMapping("/auth")
public class TokenController
{
    @Autowired
    private TokenService tokenService;

//    @Autowired
//    private SysLoginService sysLoginService;

    @Autowired
    private UserLoginService userLoginService;

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public R<?> login(@RequestBody AuthParamsDto form)
    {
        if (form.getRegister_phone() == null) {
            return R.fail("手机号不能为空");
        }
        if (form.getAuthType() == null) {
            return R.fail("请选择登录方式");
        }
        if (form.getAuthType().equalsIgnoreCase("password")){
            //密码登录
            LoginUser userInfo =userLoginService.login(form.getRegister_phone(), form.getPassword());
            if (userInfo == null) {
                return R.fail("手机号或密码错误");
            }
            // 获取登录token
            return R.ok(tokenService.createToken(userInfo));

        }else if (form.getAuthType().equalsIgnoreCase("sms")){
            //短信登录
            LoginUser userInfo =userLoginService.smsInfo(form.getRegister_phone(), form.getCheckcode());
            if (userInfo == null) {
                return R.fail("手机号验证码错误或失效");
            }
            // 获取登录token
            return R.ok(tokenService.createToken(userInfo));
        }

        return R.fail("登录失败,参数不合法");
    }

    @PostMapping("/admin/login")
    @ApiOperation("管理员登录")
    public R<?> adminLogin(@RequestBody AuthParamsDto form)
    {
        if (form.getUsername() == null || form.getPassword() == null) {
            return R.fail("用户名或密码不能为空");
        }
        // 用户登录
//        LoginUser userInfo = sysLoginService.login(form.getUsername(), form.getPassword());
        LoginUser userInfo =userLoginService.adminLogin(form.getUsername(), form.getPassword());
        if (userInfo == null) {
            return R.fail("用户名或密码错误");
        }
        // 获取登录token
        return R.ok(tokenService.createToken(userInfo));
    }



    @DeleteMapping("/logout")
    @ApiOperation("统一退出")
    public R<?> logout(HttpServletRequest request)
    {
        String token = SecurityUtils.getToken(request);
        if (StringUtils.isNotEmpty(token))
        {
//            String username = JwtUtils.getUserName(token);
            // 删除用户缓存记录
            AuthUtil.logoutByToken(token);
            return R.ok("success");

            // 记录用户退出日志
//            sysLoginService.logout(username);
        }
        return R.ok("fail");
    }

    @PostMapping("/refresh")
    @ApiOperation("令牌刷新")
    public R<?> refresh(HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser))
        {
            // 刷新令牌有效期
            tokenService.refreshToken(loginUser);
            return R.ok("success");
        }
        return R.ok("fail");
    }

    @PostMapping("/sendSms")
    @ApiOperation("短信验证码发送")
    public R<?> sendSms(@RequestBody SendSmsDto sendSmsDto) {

        if (StringUtils.isEmpty(sendSmsDto.getRegister_phone()) || StringUtils.isEmpty(sendSmsDto.getSendType())) {
            return R.fail("手机号或验证码业务类型不能为空");
        }
        String phone = sendSmsDto.getRegister_phone();
        String sendType = sendSmsDto.getSendType();

        boolean validPhoneNumber = SMSValidation.isValidPhoneNumber(phone);
        if (!validPhoneNumber) {
            return R.fail("手机号格式错误");
        }

        String result = userLoginService.sendSms(phone,sendType);
        if (result != "success") {
            return R.fail(result);
        }

        return R.ok("success");

    }


    @PostMapping("/register")
    @ApiOperation("用户注册")
    public R<?> register(@RequestBody RegisterDto registerDto)
    {
        // 用户注册
        LoginUser register = userLoginService.register(registerDto);
        if (register == null) {
            return R.fail("用户手机号已存在，注册失败");
        }

        return R.ok(register,"注册成功");
    }
}
