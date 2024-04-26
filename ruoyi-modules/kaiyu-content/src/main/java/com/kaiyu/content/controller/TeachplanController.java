package com.kaiyu.content.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kaiyu.content.domain.MediaFiles;
import com.kaiyu.content.domain.TeachplanMedia;
import com.kaiyu.content.domain.dto.BindTeachplanMediaDto;
import com.kaiyu.content.domain.dto.TeachplanDto;
import com.kaiyu.content.domain.dto.TeachplanMediaDto;
import com.kaiyu.content.service.ITeachplanService;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.security.annotation.Logical;
import com.ruoyi.common.security.annotation.RequiresRoles;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-09 09:36
 **/
@RestController
@RequestMapping("/content/teachplan")
@Api(value = "课程计划编辑接口", tags = "课程计划编辑接口")
public class TeachplanController {

    @Autowired
    private ITeachplanService teachplanService;

    @ApiOperation("管理后台接口-查询课程计划MediaDto返回")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @PostMapping("/getTeachplanMediaDtoByCourseId/{courseId}")
    public R<List<TeachplanMediaDto>> getTeachplanMediaDtoByCourseId(@PathVariable("courseId") Long courseId) {

        return R.ok(teachplanService.findCourseTeachplanDto(courseId));

    }

    @ApiOperation("查询课程计划列表返回")
    @RequiresRoles(value = {"admin", "common"}, logical = Logical.OR)
    @PostMapping("/getTeachplanByCourseId/{courseId}")
    public R<TeachplanDto> getTeachplanByCourseId(@PathVariable("courseId") Long courseId) {

        return R.ok(teachplanService.findCourseTeachplan(courseId));

    }

    @ApiOperation("查询对应课程视频媒资对象的url")
    @RequiresRoles(value = {"admin", "common"}, logical = Logical.OR)
    @PostMapping("/getTeachplanMediaByCourseId/{courseId}/{episode}")
    public R getTeachplanMediaByCourseId(@PathVariable("courseId") Long courseId, @PathVariable("episode") String episode) {
        String url = teachplanService.getTeachplanMediaByCourseId(courseId, episode);
        JSONArray jsonArray = JSONArray.parseArray(url);

        return R.ok(jsonArray);

    }


    @ApiOperation("课程计划与媒资信息绑定")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @PostMapping("/association/media")
    public R associationMedia(@RequestBody BindTeachplanMediaDto bindTeachplanMediaDto) {

        if (StringUtils.isEmpty(bindTeachplanMediaDto.getMediaId()) || StringUtils.isEmpty(bindTeachplanMediaDto.getEpisode()) ||
                bindTeachplanMediaDto.getCourseId() < 0 || StringUtils.isEmpty(bindTeachplanMediaDto.getFileName())) {
            return R.fail("课程计划与媒资信息绑定,参数不合法");
        }

        String s = teachplanService.associationMedia(bindTeachplanMediaDto);
        if (s.equals("success")) {
            return R.ok("课程计划与媒资信息绑定成功");
        }else {
            return R.fail(s);
        }
    }

    @ApiOperation("课程计划解除媒资信息绑定")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @DeleteMapping("/unassociation/media/{courseId}/{mediaId}/{episode}")
    public R unassociationMedia(@PathVariable("courseId") @NotNull Long courseId, @PathVariable("mediaId") @NotBlank String mediaId,
                                @PathVariable("episode") @NotBlank String episode) {
        teachplanService.unassociationMedia(courseId, mediaId,episode);
        return R.ok("课程计划解除媒资信息绑定成功");
    }

    @ApiOperation("查询是否有课程计划绑定指定媒资文件")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @PostMapping("/checkCourseBindMedia")
    public R checkCourseBindMedia(@RequestParam("mediaId") String mediaId) {
        if (StringUtils.isEmpty(mediaId)) {
            return R.fail("查询是否有课程计划绑定指定媒资文件,参数不合法");
        }
        List<TeachplanMedia> teachplanMedias = teachplanService.getTeachplanByMediaId(mediaId);
        if (!teachplanMedias.isEmpty()) {
            return R.ok("true");
        }
        return R.ok("false");
    }



}
