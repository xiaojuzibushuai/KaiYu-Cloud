package com.kaiyu.media.feignclient;

import com.ruoyi.common.core.constant.ServiceNameConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.security.annotation.Logical;
import com.ruoyi.common.security.annotation.RequiresRoles;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @program: kai-yu-cloud
 * @description: 内容服务
 * @author: xiaojuzi
 * @create: 2024-03-28 10:49
 **/
@FeignClient(contextId = "RemoteContentService", value = ServiceNameConstants.CONTENT_SERVICE, fallbackFactory = RemoteContentFallbackFactory.class)
public interface RemoteContentService {


    @ApiOperation("查询是否有课程计划绑定指定媒资文件")
    @PostMapping("/content/teachplan/checkCourseBindMedia")
    public R checkCourseBindMedia(@RequestParam("mediaId") String mediaId);


    
    
}
