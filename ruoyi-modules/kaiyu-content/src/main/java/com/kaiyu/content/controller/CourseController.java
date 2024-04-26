package com.kaiyu.content.controller;

import java.util.List;
import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.kaiyu.content.domain.PageParams;
import com.kaiyu.content.domain.PageResult;
import com.kaiyu.content.domain.dto.QueryAdminCourseDto;
import com.kaiyu.content.domain.dto.QueryCourseDto;
import com.kaiyu.content.domain.vo.CourseCategoryVo;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.KaiYuEducationException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.security.annotation.InnerAuth;
import com.ruoyi.common.security.annotation.Logical;
import com.ruoyi.common.security.annotation.RequiresRoles;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.kaiyu.content.domain.Course;
import com.kaiyu.content.service.ICourseService;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.core.web.page.TableDataInfo;

/**
 * 【课程基本信息接口】Controller
 * 
 * @author xiaojuzi
 * @date 2024-03-28
 */
@RestController
@RequestMapping("/content/course")
@Api(value = "课程基本信息接口",tags = "课程基本信息接口")
public class CourseController {

    @Autowired
    ICourseService courseService;

    /**
     * 查询课程列表
     * @return 课程集合
     * @author xiaojuzi
     */
    @PostMapping("/getCourse")
//    @InnerAuth
    @RequiresRoles(value = {"admin", "common"}, logical = Logical.OR)
    @ApiOperation("查询课程列表")
    public R<List<Course>> getCourse(@RequestBody QueryCourseDto queryCourseDto){

        if ((queryCourseDto.getCourseId() == null) & (queryCourseDto.getCategoryId() == null)
        & (StringUtils.isEmpty(queryCourseDto.getCourseClass()))) {
//            KaiYuEducationException.cast("参数不能全为空！");
            return R.fail("参数不能全为空！");
        }

        return R.ok(courseService.getCourse(queryCourseDto.getCourseId(),queryCourseDto.getCategoryId(),
                queryCourseDto.getCourseClass()));

    }

    @ApiOperation("前端播放课程时查询课程特定结构详情")
    @PostMapping("/getCourseDeatil")
    @RequiresRoles(value = {"admin", "common"}, logical = Logical.OR)
    public R getCourseDeatil(@RequestParam("courseId") Long courseId ){
        if (courseId <= 0) {
            return R.fail("courseId参数不能为空！");
        }
        Map courseDeatil = courseService.getCourseDeatil(courseId);
        if (courseDeatil == null || courseDeatil.size() == 0) {
            return R.fail("课程计划不存在！");
        }

        return R.ok(courseDeatil);

    }



    @PostMapping("/getCourseByMultipleConditions")
//    @InnerAuth
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @ApiOperation("查询课程内容列表多条件筛选")
    public PageResult<CourseCategoryVo> getCourseByMultipleConditions(@RequestParam(value = "pageNo", defaultValue = "1") Long pageNo,
                                                                      @RequestParam(value = "pageSize", defaultValue = "10") Long pageSize,
                                                                      @RequestBody QueryAdminCourseDto queryCourseDto){
        PageParams pageParams = new PageParams();
        if (pageNo >= 1) {
            pageParams.setPageNo(pageNo);
        }
        if (pageSize >= 1) {
            pageParams.setPageSize(pageSize);
        }

        return courseService.queryCourseByMultipleConditions(pageParams, queryCourseDto);

    }


}
