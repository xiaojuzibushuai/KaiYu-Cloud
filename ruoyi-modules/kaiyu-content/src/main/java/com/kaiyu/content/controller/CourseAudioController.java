package com.kaiyu.content.controller;

import com.alibaba.fastjson.JSONObject;
import com.kaiyu.content.domain.dto.CourseAudioDto;
import com.kaiyu.content.domain.dto.EditCourseAudioDto;
import com.kaiyu.content.service.ICourseAudioService;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.security.annotation.Logical;
import com.ruoyi.common.security.annotation.RequiresRoles;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-03-28 17:19
 **/
@RestController
@RequestMapping("/content/courseAudio")
@Api(value = "课程脚本基本信息接口",tags = "课程脚本基本信息接口")
public class CourseAudioController{

    @Autowired
    ICourseAudioService courseAudioService;

    /**
     * 查询课程脚本列表
     * @return 课程集合
     * @author xiaojuzi
     */
    @GetMapping("/getAudioJson")
//    @InnerAuth
    @RequiresRoles(value = {"admin", "common"}, logical = Logical.OR)
    @ApiOperation("查询课程脚本列表")
    public R<List<CourseAudioDto>> getCourseAudio(){

        return R.ok(courseAudioService.getCourseAudio());
    }

    /**
     * 查询课程脚本详细
     * @param courseId 课程ID
     * @return 课程脚本详细
     * @author xiaojuzi
     */
    @GetMapping("/getAudioJsonByCourseId/{courseId}/{episode}")
//    @InnerAuth
    @RequiresRoles(value = {"admin", "common"}, logical = Logical.OR)
    @ApiOperation("根据课程id查询课程脚本详细")
    public R getAudioJsonByCourseId(@ApiParam("课程id") @PathVariable Long courseId,
                                                @ApiParam("课程集数") @PathVariable Long episode){

        return R.ok(courseAudioService.getAudioJsonByCourseId(courseId,episode));
    }

    /**
     * 新增课程脚本
     * @return
     * @author xiaojuzi
     */
    @PostMapping("/saveCourseAudioJson")
//    @InnerAuth
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @ApiOperation("新增课程脚本")
    public R<CourseAudioDto> saveCourseAudio(@ApiParam("{\n" +
            "    \"courseId\": 274,\n" +
            "    \"timePoint\": [\n" +
            "        {\n" +
            "            \"sendType\": \"answer\",\n" +
            "            \"startTime\": 5,\n" +
            "            \"formatStartTime\": \"0/0/5\",\n" +
            "            \"endTime\": 6,\n" +
            "            \"marks\": \"测试指令\",\n" +
            "            \"timeId\": \"1705653501294\",\n" +
            "            \"gameUrl\": \"\",\n" +
            "            \"data\": {\n" +
            "                \"gametype\": \"02\",\n" +
            "                \"answer\": \"2\",\n" +
            "                \"parentid\": \"2\"\n" +
            "            }\n" +
            "        },\n" +
            "        {\n" +
            "            \"sendType\": \"answer\",\n" +
            "            \"startTime\": 5,\n" +
            "            \"formatStartTime\": \"0/0/5\",\n" +
            "            \"endTime\": 6,\n" +
            "            \"marks\": \"测试指令\",\n" +
            "            \"timeId\": \"1705653501294\",\n" +
            "            \"gameUrl\": \"\",\n" +
            "            \"data\": {\n" +
            "                \"gametype\": \"03\",\n" +
            "                \"answer\": \"1\",\n" +
            "                \"parentid\": \"2\"\n" +
            "            }\n" +
            "        }\n" +
            "    ],\n" +
            "    \"episode\": 1\n" +
            "}") @RequestBody EditCourseAudioDto courseAudioDto){

        return R.ok(courseAudioService.saveCourseAudio(courseAudioDto));

    }

    /**
     * 修改课程id下某个视频脚本
     * @return
     * @author xiaojuzi
     */
    @PutMapping("/updateCourseAudioJson")
    //    @InnerAuth
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @ApiOperation("修改课程id下某个视频脚本")
    public R<CourseAudioDto> updateCourseAudio(@ApiParam("{\n" +
            "    \"courseId\": 273,\n" +
            "    \"sendType\": \"answer\",\n" +
            "    \"startTime\": 3,\n" +
            "    \"formatStartTime\": \"0/0/7\",\n" +
            "    \"endTime\": 9,\n" +
            "    \"marks\": \"测试指令12\",\n" +
            "    \"timeId\": \"1705653501295\",\n" +
            "    \"gameUrl\": \"\",\n" +
            "    \"data\": {\n" +
            "        \"gametype\": \"07\",\n" +
            "        \"answer\": \"7\",\n" +
            "        \"parentid\": \"1\"\n" +
            "    },\n" +
            "    \"episode\": 1\n" +
            "}") @RequestBody EditCourseAudioDto courseAudioDto){

        return R.ok(courseAudioService.updateCourseAudio(courseAudioDto));
    }


    /**
     * 删除课程id下的某个视频脚本
     * @return
     * @author xiaojuzi
     */
    @DeleteMapping("/deleteAudioJsonByCourseId")
//    @InnerAuth
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @ApiOperation("删除课程id下的某个视频脚本")
    public R deleteCourseAudioByCourseId(@ApiParam("{\n" +
            "    \"courseId\": 273,\n" +
            "    \"timeId\": \"1705653501294\",\n" +
            "    \"episode\": 1\n" +
            "}") @RequestBody EditCourseAudioDto courseAudioDto){

        courseAudioService.deleteCourseAudio(courseAudioDto);

        return R.ok("删除课程id下的某个视频脚本,删除成功");

    }



}
