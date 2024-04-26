package com.kaiyu.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaiyu.content.domain.Course;
import com.kaiyu.content.domain.CourseAudio;
import com.kaiyu.content.domain.dto.CourseAudioDto;
import com.kaiyu.content.domain.dto.EditCourseAudioDto;
import com.kaiyu.content.mapper.CourseAudioMapper;
import com.kaiyu.content.service.ICourseAudioService;
import com.kaiyu.content.util.JsonUtil;
import com.ruoyi.common.core.exception.CommonError;
import com.ruoyi.common.core.exception.KaiYuEducationException;
import com.ruoyi.common.core.utils.StringUtils;
import io.jsonwebtoken.lang.Collections;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-03-28 17:11
 **/
@Service
public class CourseAudioServiceImpl implements ICourseAudioService {

    private static final Logger log = LoggerFactory.getLogger(CourseAudioServiceImpl.class);


    @Autowired
    CourseAudioMapper courseAudioMapper;

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 获取课程视频脚本 xiaojuzi
     */
    @Override
    public List<CourseAudioDto> getCourseAudio() {

        return courseAudioMapper.getCourseAudio();

    }

    @Override
    public Object getAudioJsonByCourseId(Long courseId,Long episode) {
        if (courseId == null || episode == null) {
            log.info("getAudioJsonByCourseId时课程id或集数不能为空");
            return null;
//            KaiYuEducationException.cast(CommonError.REQUEST_NULL);
        }
        //先查缓存
        String courseAudioKey = "CourseId:" + courseId + "-" + "Episode:" + episode;
        String courseAudioJson = (String) redisTemplate.opsForHash().get("getAudioJsonByCourseId", courseAudioKey);
        if (StringUtils.isNotEmpty(courseAudioJson)) {
            // System.out.println("从缓存中获取数据");
            log.info(courseAudioKey+"从缓存中获取数据");
            Object parse = JSON.parse(courseAudioJson);

            JSONObject jsonObject = JSON.parseObject(parse.toString());
            JSONArray timePointArray = jsonObject.getJSONArray("timePoint");
            jsonObject.put("timePoint", timePointArray);

            return jsonObject;

        } else {

            LambdaQueryWrapper<CourseAudio> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CourseAudio::getCourseid, courseId);
            queryWrapper.eq(CourseAudio::getEpisode, episode);
            CourseAudio courseAudio = courseAudioMapper.selectOne(queryWrapper);

            if (courseAudio == null) {
                log.info("getAudioJsonByCourseId时courseAudio为空");
                return null;
            }

//            CourseAudioDto courseAudioDto = new CourseAudioDto();
//            BeanUtils.copyProperties(courseAudio, courseAudioDto);

            //缓存数据
            redisTemplate.opsForHash().put("getAudioJsonByCourseId",courseAudioKey,JSON.toJSONString(courseAudio.getAudiojson()));
            //设置过期时间
            redisTemplate.expire("getAudioJsonByCourseId",60 + new Random().nextInt(100), TimeUnit.SECONDS);

            //解析成前端需要格式
            JSONObject jsonObject = JSON.parseObject(courseAudio.getAudiojson());
            JSONArray timePointArray = jsonObject.getJSONArray("timePoint");
            jsonObject.put("timePoint", timePointArray);

            log.info(courseAudioKey+"从数据库中获取数据");

            return jsonObject;
        }
    }

    /**
     * 从db查课程脚本数据
     * xiaojuzi
     * @param courseId
     * @param episode
     * @return
     */
    private CourseAudioDto getAudioJsonByCourseIdFromDB(Long courseId,Long episode) {
        return courseAudioMapper.getAudioJsonByCourseId(courseId,episode);
    }


    /**
     * 新增课程视频脚本 xiaojuzi
     */
    @Override
    public CourseAudioDto saveCourseAudio(EditCourseAudioDto courseAudioDto) {

        if ((courseAudioDto.getCourseId() == null) || (courseAudioDto.getEpisode() == null)) {
            log.info("saveCourseAudio时课程id或集数不能为空");
            return null;
//            KaiYuEducationException.cast("课程id和课程集不能为空");
        }

        //做优化后这里需要查DB
       CourseAudio courseAudio = getAudioJsonByCourseIdFromDB(courseAudioDto.getCourseId(),courseAudioDto.getEpisode());
       
       if (courseAudio !=null){
           //追加
           JSONObject data = JSON.parseObject(courseAudio.getAudiojson());
           JSONArray timePoints = data.getJSONArray("timePoint");
           // 将新的数据添加到现有的数据中
           courseAudioDto.getTimePoint().stream().forEach(ad ->{
               timePoints.add(ad);
           });

           // 更新数据库中的 audiojson 字段

           data.put("timePoint", timePoints);
           courseAudio.setAudiojson(data.toJSONString());

           int i = courseAudioMapper.updateById(courseAudio);
           if (i <= 0) {
               log.info("saveCourseAudio时更新课程脚本信息失败");
               KaiYuEducationException.cast("更新课程脚本信息失败");
           }else {
               //清空原缓存
               String courseAudioKey = "CourseId:" + courseAudioDto.getCourseId() + "-" + "Episode:" + courseAudioDto.getEpisode();
               String courseAudioJson = (String) redisTemplate.opsForHash().get("getAudioJsonByCourseId", courseAudioKey);
               if (StringUtils.isNotEmpty(courseAudioJson)) {
                   redisTemplate.opsForHash().delete("getAudioJsonByCourseId",courseAudioKey);
               }
           }

           CourseAudioDto courseAudioDto1 = new CourseAudioDto();
           BeanUtils.copyProperties(courseAudio,courseAudioDto1);

           return courseAudioDto1;

           }

       else {
           //新增
           CourseAudio courseAudio1 = new CourseAudio();
           courseAudio1.setCourseid(courseAudioDto.getCourseId());
           courseAudio1.setEpisode(courseAudioDto.getEpisode());

           JSONObject data = new JSONObject();
           data.put("courseId", courseAudioDto.getCourseId());
           data.put("episode", courseAudioDto.getEpisode());
           data.put("timePoint",JSON.toJSONString(courseAudioDto.getTimePoint()));


           courseAudio1.setAudiojson(JSON.toJSONString(data));

           int courseAudioInsert = courseAudioMapper.insert(courseAudio1);
           if (courseAudioInsert <= 0) {
               log.info("saveCourseAudio时新增课程脚本信息失败");
               KaiYuEducationException.cast("新增课程脚本信息失败");
           }else {
               //清空原缓存
               String courseAudioKey = "CourseId:" + courseAudioDto.getCourseId() + "-" + "Episode:" + courseAudioDto.getEpisode();
               String courseAudioJson = (String) redisTemplate.opsForHash().get("getAudioJsonByCourseId", courseAudioKey);
               if (StringUtils.isNotEmpty(courseAudioJson)) {
                   redisTemplate.opsForHash().delete("getAudioJsonByCourseId",courseAudioKey);
               }
           }

           CourseAudioDto courseAudioDto1 = new CourseAudioDto();
           BeanUtils.copyProperties(courseAudio1,courseAudioDto1);

           return courseAudioDto1;

       }
    }

    /**
     * 修改对应课程视频脚本 xiaojuzi
     */
    @Override
    public CourseAudioDto updateCourseAudio(EditCourseAudioDto courseAudioDto) {

        if ((courseAudioDto.getCourseId() == null) || (courseAudioDto.getEpisode() == null)) {
            log.info("updateCourseAudio时课程id或集数不能为空");
            return null;
//            KaiYuEducationException.cast("课程id和课程集不能为空");
        }

        //做优化后这里需要查DB
        CourseAudio courseAudio = getAudioJsonByCourseIdFromDB(courseAudioDto.getCourseId(),courseAudioDto.getEpisode());

        if (courseAudio == null){
            log.info("updateCourseAudio时课程id或集数不存在");
            return null;
//            KaiYuEducationException.cast("课程id和课程集不存在");
        }
        //解析json数据
        JSONObject data = JSON.parseObject(courseAudio.getAudiojson());
        JSONArray timePoints = data.getJSONArray("timePoint");
        //记录是否更新数据
        AtomicInteger updatedCount = new AtomicInteger(0);
        //更新数据
        timePoints.stream()
                .filter(dataPoint -> {
                    JSONObject point = (JSONObject) dataPoint;
                  return  point.getString("timeId").equals(courseAudioDto.getTimeId());
                })
                .forEach(dataPoint -> {
                    JSONObject point = (JSONObject) dataPoint;
                    if (courseAudioDto.getSendType() != null) {
                        point.put("sendType", courseAudioDto.getSendType());
                        updatedCount.incrementAndGet();
                    }
                    if (courseAudioDto.getStartTime() != null) {
                        point.put("startTime", courseAudioDto.getStartTime());
                        updatedCount.incrementAndGet();
                    }
                    if (courseAudioDto.getEndTime() != null) {
                        point.put("endTime", courseAudioDto.getEndTime());
                        updatedCount.incrementAndGet();
                    }
                    if (courseAudioDto.getFormatStartTime() != null) {
                        point.put("formatStartTime", courseAudioDto.getFormatStartTime());
                        updatedCount.incrementAndGet();
                    }
                    if (courseAudioDto.getMarks() != null) {
                        point.put("marks", courseAudioDto.getMarks());
                        updatedCount.incrementAndGet();
                    }
                    if (courseAudioDto.getData() != null) {
                        JSONObject dataObject = new JSONObject(courseAudioDto.getData());
                        point.put("data", JSON.parseObject(dataObject .toJSONString()));
                        updatedCount.incrementAndGet();
                    }
                });

        if (updatedCount.get() > 0) {
            // 更新audiojson字段
            data.put("timePoint", timePoints);
            courseAudio.setAudiojson(data.toJSONString());
            int i = courseAudioMapper.updateById(courseAudio);
            if (i <= 0) {
                log.info("updateCourseAudio时更新数据失败");
                KaiYuEducationException.cast("updateCourseAudio时更新数据失败");
            }else {
                //清空原缓存
                String courseAudioKey = "CourseId:" + courseAudioDto.getCourseId() + "-" + "Episode:" + courseAudioDto.getEpisode();
                String courseAudioJson = (String) redisTemplate.opsForHash().get("getAudioJsonByCourseId", courseAudioKey);
                if (StringUtils.isNotEmpty(courseAudioJson)) {
                    redisTemplate.opsForHash().delete("getAudioJsonByCourseId",courseAudioKey);
                }
            }
        }

        CourseAudioDto courseAudioDto1 = new CourseAudioDto();
        BeanUtils.copyProperties(courseAudio,courseAudioDto1);

        return courseAudioDto1;

    }


    /**
     * 删除课程对应视频脚本 xiaojuzi
     */
    @Override
    public void deleteCourseAudio(EditCourseAudioDto courseAudioDto) {

        if ((courseAudioDto.getCourseId() == null) || (courseAudioDto.getEpisode() == null)) {
            log.info("deleteCourseAudio时课程id或集数不能为空");
            return;
//            KaiYuEducationException.cast("课程id和课程集不能为空");
        }

        //做优化后这里需要查DB
        CourseAudio courseAudio = getAudioJsonByCourseIdFromDB(courseAudioDto.getCourseId(),courseAudioDto.getEpisode());

        if (courseAudio == null){
            log.info("deleteCourseAudio时课程id或集数不存在");
            return;
//            KaiYuEducationException.cast("课程id和课程集不存在");
        }

        //解析json数据
        JSONObject data = JSON.parseObject(courseAudio.getAudiojson());
        JSONArray timePoints = data.getJSONArray("timePoint");

        //删除数据
        List<Object> filter_data = timePoints.stream()
                .filter(dataPoint -> {
                            JSONObject point = (JSONObject) dataPoint;
                            return !(point.getString("timeId").equals(courseAudioDto.getTimeId()));
                        }
                )
                .collect(Collectors.toList());


        if (filter_data.isEmpty()){
            //删除此课程脚本
            int i = courseAudioMapper.deleteById(courseAudio.getId());
            if (i <= 0) {
                log.info("deleteCourseAudio时删除数据失败");
                KaiYuEducationException.cast("deleteCourseAudio时删除数据失败");
            }else {
                //清空原缓存
                String courseAudioKey = "CourseId:" + courseAudioDto.getCourseId() + "-" + "Episode:" + courseAudioDto.getEpisode();
                String courseAudioJson = (String) redisTemplate.opsForHash().get("getAudioJsonByCourseId", courseAudioKey);
                if (StringUtils.isNotEmpty(courseAudioJson)) {
                    redisTemplate.opsForHash().delete("getAudioJsonByCourseId",courseAudioKey);
                }
            }
        }else {
            // 更新audiojson字段
            data.put("timePoint", filter_data);
            courseAudio.setAudiojson(data.toJSONString());
            int i = courseAudioMapper.updateById(courseAudio);
            if (i <= 0) {
                log.info("deleteCourseAudio时删除数据失败");
                KaiYuEducationException.cast("deleteCourseAudio时删除数据失败");
            }else {
                //清空原缓存
                String courseAudioKey = "CourseId:" + courseAudioDto.getCourseId() + "-" + "Episode:" + courseAudioDto.getEpisode();
                String courseAudioJson = (String) redisTemplate.opsForHash().get("getAudioJsonByCourseId", courseAudioKey);
                if (StringUtils.isNotEmpty(courseAudioJson)) {
                    redisTemplate.opsForHash().delete("getAudioJsonByCourseId",courseAudioKey);
                }
            }
        }

    }



}
