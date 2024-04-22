package com.kaiyu.content.service.impl;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaiyu.content.domain.Category;
import com.kaiyu.content.domain.PageParams;
import com.kaiyu.content.domain.PageResult;
import com.kaiyu.content.domain.dto.QueryAdminCourseDto;
import com.kaiyu.content.domain.dto.QueryCourseDto;
import com.kaiyu.content.domain.vo.CourseCategoryVo;
import com.kaiyu.content.mapper.CategoryMapper;
import com.ruoyi.common.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.kaiyu.content.mapper.CourseMapper;
import com.kaiyu.content.domain.Course;
import com.kaiyu.content.service.ICourseService;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author xiaojuzi
 * @date 2024-03-28
 */
@Service
public class CourseServiceImpl implements ICourseService 
{

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    CategoryMapper categoryMapper;



    /**
     *查询课程列表 xiaojuzi
     * @param courseId
     * @param categoryId
     * @param courseClass
     * @return
     */
    @Override
    public List<Course> getCourse(Long courseId, Long categoryId, String courseClass) {

        // 参数校验和处理
        courseId = validateAndNormalizeId(courseId);
        categoryId = validateAndNormalizeId(categoryId);
        courseClass = StringUtils.isEmpty(courseClass) ? null : courseClass;

        //先查缓存
        String courseKey = "CourseId:"+courseId+"-"+"CategoryId:"+categoryId+"-"+"CourseClass:"+courseClass;
        String courseKey1 = courseKey.replace("null","");

        String coursesJson = (String) redisTemplate.opsForHash().get("getCourses",courseKey1);
        if (StringUtils.isNotEmpty(coursesJson)) {
            List<Course> courses = JSON.parseArray(coursesJson, Course.class);
//            System.out.println("从缓存中获取数据");
            return courses;
        } else {
            LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();

            if (courseId != null) {
                queryWrapper.eq(Course::getId, courseId);
            }

            if (categoryId != null) {
                queryWrapper.eq(Course::getCategoryId, categoryId);
            }

            if (StringUtils.isNotEmpty(courseClass)) {
                queryWrapper.eq(Course::getCourseClass, courseClass);
            }

            List<Course> courses = courseMapper.selectList(queryWrapper);

            //缓存数据
            redisTemplate.opsForHash().put("getCourses",courseKey1,JSON.toJSONString(courses));
            //设置过期时间
            redisTemplate.expire("getCourses",30 + new Random().nextInt(100), TimeUnit.SECONDS);

            return courses;
        }
    }

    @Override
    public PageResult<CourseCategoryVo> queryCourseByMultipleConditions(PageParams pageParams, QueryAdminCourseDto queryCourseDto) {

        //获取数据集
        Long offset = (pageParams.getPageNo() - 1) * pageParams.getPageSize();
        List<CourseCategoryVo> courseCategoryVos = courseMapper.queryCourseByMultipleConditions(pageParams,offset,queryCourseDto);

        //获取总条数
        Long total = courseMapper.countCourseByMultipleConditions(queryCourseDto);


        PageResult<CourseCategoryVo> courseList = new PageResult<>(courseCategoryVos, total, pageParams.getPageNo(), pageParams.getPageSize());

        return courseList;
    }

    /**
     * 私有方法 参数校验
     * xiaojuzi
     * @param id
     * @return
     */
    private Long validateAndNormalizeId(Long id) {
        return id != null && id > 0 ? id : null;
    }

}
