package com.kaiyu.media.controller;

import com.alibaba.fastjson.JSONArray;
import com.kaiyu.media.domain.MediaFiles;
import com.kaiyu.media.domain.RestResponse;
import com.kaiyu.media.service.MediaFileService;
import com.ruoyi.common.security.annotation.Logical;
import com.ruoyi.common.security.annotation.RequiresRoles;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-09 14:55
 **/
@RestController
@RequestMapping("/media")
@Api(value = "媒资预览接口",tags = "媒资预览接口")
public class MediaOpenController {

    @Autowired
    private MediaFileService mediaFileService;

    /**
     * 获取视频播放地址
     * @param mediaId
     * @return
     */
    @ApiOperation("获取视频播放地址")
    @RequiresRoles(value = {"admin", "common"}, logical = Logical.OR)
    @PostMapping("/preview/{mediaId}")
    public RestResponse<Object> getPlayUrlByMediaId(@PathVariable("mediaId") String mediaId) {
        MediaFiles mediaFile = mediaFileService.getFileById(mediaId);
        if (mediaFile.getFileType().equals("001002")){
            JSONArray jsonArray = JSONArray.parseArray(mediaFile.getUrl());
            return RestResponse.success(jsonArray);
        }else {
            return RestResponse.success(mediaFile.getUrl());
        }
    }

}
