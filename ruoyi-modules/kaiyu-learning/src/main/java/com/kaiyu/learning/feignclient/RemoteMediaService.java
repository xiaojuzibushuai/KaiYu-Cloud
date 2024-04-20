package com.kaiyu.learning.feignclient;

import com.kaiyu.learning.domain.RestResponse;
import com.ruoyi.common.core.constant.ServiceNameConstants;
import com.ruoyi.common.security.annotation.Logical;
import com.ruoyi.common.security.annotation.RequiresRoles;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @program: kai-yu-cloud
 * @description: 媒资服务
 * @author: xiaojuzi
 * @create: 2024-04-01 10:49
 **/
@FeignClient(contextId = "RemoteMediaService", value = ServiceNameConstants.MEDIA_SERVICE, fallbackFactory = RemoteMediaFallbackFactory.class)
public interface RemoteMediaService {


    /**
     * 获取媒资url
     * @param mediaId 媒资id
     * @return
     */
    @ApiOperation("获取视频播放地址")
    @PostMapping("/media/preview/{mediaId}")
    public RestResponse<Object> getPlayUrlByMediaId(@PathVariable("mediaId") String mediaId);


    
    
}
