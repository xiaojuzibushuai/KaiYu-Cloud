package com.kaiyu.learning.controller;

import com.kaiyu.learning.service.LearningService;
import com.ruoyi.common.security.annotation.Logical;
import com.ruoyi.common.security.annotation.RequiresRoles;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.kaiyu.learning.domain.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-09 14:30
 **/


@RestController
@RequestMapping("/learning")
@Api(value = "学习课程管理接口", tags = "学习课程管理接口")
public class MyLearningController {

    @Autowired
    LearningService learningService;

    @ApiOperation("获取媒资视频url")
    @RequiresRoles(value = {"admin", "common"}, logical = Logical.OR)
    @GetMapping("/getVideo/{courseId}/{mediaId}")
    public RestResponse<Object> getVideo(@PathVariable("courseId") Long courseId, @PathVariable("mediaId") String mediaId) {

        return learningService.getVideo(courseId,mediaId);

    }

    




}
