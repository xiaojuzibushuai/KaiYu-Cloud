package com.kaiyu.content.service.impl;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaiyu.content.domain.*;
import com.kaiyu.content.domain.dto.QueryAdminCourseDto;
import com.kaiyu.content.domain.dto.QueryCourseDto;
import com.kaiyu.content.domain.vo.CourseCategoryVo;
import com.kaiyu.content.feignclient.RemoteMediaService;
import com.kaiyu.content.mapper.CategoryMapper;
import com.kaiyu.content.mapper.TeachplanMediaMapper;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.kaiyu.content.mapper.CourseMapper;
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

    private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Autowired
    private RemoteMediaService remoteMediaService;


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
            log.info(courseKey+"从缓存中获取数据");
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

    @Override
    public Map getCourseDeatil(Long courseId) {
        Course course = courseMapper.selectById(courseId);
        if (course != null) {
            //查询对应的课程计划
            LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TeachplanMedia::getCourseId, courseId);

            List<TeachplanMedia> teachplanMedia = teachplanMediaMapper.selectList(queryWrapper);

            if (teachplanMedia == null || teachplanMedia.size() == 0) {
                log.info("getCourseDeatil时没有找到课程的教学计划");
                return null;
            }

            //查询对应课程计划的资源url
            List<Object> videoList = new ArrayList<>();

            for (TeachplanMedia item : teachplanMedia) {
                //远程调用媒资模块查询url
                R<MediaFiles> mediaFilesDetail = remoteMediaService.getMediaFilesDetail(item.getMediaId());
                if (mediaFilesDetail.getData() != null) {
                    //进行判断
                    MediaFiles mediaFiles = mediaFilesDetail.getData();
                    if (mediaFiles.getFileType().equals("001002")){
                        JSONArray jsonArray = JSONArray.parseArray(mediaFiles.getUrl());
                        List<JSONObject> list = jsonArray.stream().map(item1 -> {
                            JSONObject jsonObject = (JSONObject) item1;
                            jsonObject.put("episode", item.getEpisode());
                            return jsonObject;
                        }).collect(Collectors.toList());

                        videoList.addAll(list);

                    }
                }
            }

            Map<String, Object> courseDto = new HashMap<>();
            courseDto.put("course", course);
            courseDto.put("video_files", videoList);

            return courseDto;

        }

        return null;
    }

    @Override
    public List getAllCourseName() {
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Course::getId, Course::getTitle).orderByAsc(Course::getId);
        List<Course> courses = courseMapper.selectList(queryWrapper);

        if (courses != null && !courses.isEmpty()){
            return courses.parallelStream().map(item -> {
                return Map.of("id", item.getId(), "title", item.getTitle());
            }).collect(Collectors.toList());
        }

        return Collections.emptyList();
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
