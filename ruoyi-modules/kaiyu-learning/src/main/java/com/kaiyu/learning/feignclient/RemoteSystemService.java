package com.kaiyu.learning.feignclient;

import com.kaiyu.learning.domain.RestResponse;
import com.ruoyi.common.core.constant.ServiceNameConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.security.annotation.Logical;
import com.ruoyi.common.security.annotation.RequiresRoles;
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

    @PostMapping("/getUserVoByOpenId")
    @ApiOperation(value = "通过openId远程调用获取用户端用户信息")
    public R<UserVo> getUserInfoByOpenId(@RequestParam("openId") String openId);


}
