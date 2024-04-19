package com.kaiyu.content.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaiyu.content.domain.Course;
import com.kaiyu.content.domain.PageParams;
import com.kaiyu.content.domain.dto.QueryAdminCourseDto;
import com.kaiyu.content.domain.vo.CourseCategoryVo;
import org.apache.ibatis.annotations.Param;

/**
 * 【课程信息】Mapper接口
 * 
 * @author xiaojuzi
 * @date 2024-03-28
 */
public interface CourseMapper extends BaseMapper<Course>
{

    /**
     * 获取课程信息 多条件查询 xiaojuzi
     *
     * @param courseId
     * @param categoryId
     * @param courseClass
     * @return
     */
    public List<Course> getCourse(@Param("courseId") Long courseId, @Param("categoryId") Long categoryId,
                                  @Param("courseClass") String courseClass);



    public List<CourseCategoryVo> queryCourseByMultipleConditions(@Param("pageParams") PageParams pageParams,
                                                            @Param("queryCourseDto") QueryAdminCourseDto queryCourseDto);

    Long countCourseByMultipleConditions(@Param("queryCourseDto")QueryAdminCourseDto queryCourseDto);

}
