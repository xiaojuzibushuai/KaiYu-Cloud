package com.kaiyu.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaiyu.content.domain.CourseAudio;
import com.kaiyu.content.domain.dto.CourseAudioDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-03-28 17:22
 **/
public interface CourseAudioMapper extends BaseMapper<CourseAudio> {


    List<CourseAudioDto> getCourseAudio();

    CourseAudioDto getAudioJsonByCourseId(@Param("courseId") Long courseId, @Param("episode") Long episode);
}
