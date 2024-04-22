package com.kaiyu.content.service;

import java.util.List;
import com.kaiyu.content.domain.Course;
import com.kaiyu.content.domain.PageParams;
import com.kaiyu.content.domain.PageResult;
import com.kaiyu.content.domain.dto.QueryAdminCourseDto;
import com.kaiyu.content.domain.dto.QueryCourseDto;
import com.kaiyu.content.domain.vo.CourseCategoryVo;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author xiaojuzi
 * @date 2024-03-28
 */
public interface ICourseService 
{

    /**
     *查询课程列表 xiaojuzi
     */

    List<Course> getCourse(Long courseId,Long categoryId,String courseClass);

    PageResult<CourseCategoryVo> queryCourseByMultipleConditions(PageParams pageParams, QueryAdminCourseDto queryCourseDto);
}
