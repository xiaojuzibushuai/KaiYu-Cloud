package com.kaiyu.learning.controller;

import com.kaiyu.learning.domain.RestResponse;
import com.kaiyu.learning.service.LearningService;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.JwtUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.security.annotation.Logical;
import com.ruoyi.common.security.annotation.RequiresRoles;
import com.ruoyi.common.security.auth.AuthUtil;
import com.ruoyi.common.security.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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


    @ApiOperation("获取用户场景列表")
    @RequiresRoles(value = {"common"}, logical = Logical.OR)
    @PostMapping("/getSceneList")
    public RestResponse<Object> getSceneList(HttpServletRequest request) {

        List sceneList = learningService.getSceneList(request);

        if (sceneList == null || sceneList.size() == 0) {
            return RestResponse.success("用户未绑定场景或网络错误");
        }

        return RestResponse.success(sceneList);

        }


    }


