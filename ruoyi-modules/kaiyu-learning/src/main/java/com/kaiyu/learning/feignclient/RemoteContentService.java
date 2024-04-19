package com.kaiyu.learning.feignclient;

import com.kaiyu.learning.domain.dto.TeachplanDto;
import com.ruoyi.common.core.constant.ServiceNameConstants;
import com.ruoyi.common.core.domain.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @program: kai-yu-cloud
 * @description: 内容服务
 * @author: xiaojuzi
 * @create: 2024-03-28 10:49
 **/
@FeignClient(contextId = "RemoteContentService", value = ServiceNameConstants.CONTENT_SERVICE, fallbackFactory = RemoteContentFallbackFactory.class)
public interface RemoteContentService {

    @ApiOperation("查询课程计划")
    @PostMapping("/content/teachplan/getTeachplanByCourseId/{courseId}")
    public R<TeachplanDto> getTeachplanByCourseId(@PathVariable("courseId") Long courseId);


    
    
}
