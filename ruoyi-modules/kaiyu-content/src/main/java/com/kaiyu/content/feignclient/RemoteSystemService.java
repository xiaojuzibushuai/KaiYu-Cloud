package com.kaiyu.content.feignclient;

import com.ruoyi.common.core.constant.ServiceNameConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.api.model.UserVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-23 10:40
 **/
@FeignClient(contextId = "RemoteSystemService" , value = ServiceNameConstants.SYSTEM_SERVICE,fallbackFactory = RemoteSystemFallbackFactory.class)
public interface RemoteSystemService {


    @PostMapping("/system/getUserVo")
    @ApiOperation(value = "远程调用获取用户端用户信息")
    public R<UserVo> getUserVo(@RequestParam("register_phone") String register_phone);


}
