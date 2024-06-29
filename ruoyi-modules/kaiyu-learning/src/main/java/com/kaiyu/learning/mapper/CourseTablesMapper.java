package com.kaiyu.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaiyu.learning.domain.CourseTables;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @program: KaiYu-Cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-06-28 14:14
 **/
public interface CourseTablesMapper extends BaseMapper<CourseTables> {

    int insertCourseTablesBatch(@Param("courseTablesList") List<CourseTables> courseTablesList);
}
