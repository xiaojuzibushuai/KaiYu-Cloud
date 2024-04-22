package com.kaiyu.content.service;

import com.kaiyu.content.domain.dto.CourseAudioDto;
import com.kaiyu.content.domain.dto.EditCourseAudioDto;
import com.ruoyi.common.core.domain.R;

import java.util.List;

/**
 * @program: kai-yu-cloud
 * @description:课程脚本相关接口
 * @author: xiaojuzi
 * @create: 2024-03-28 17:03
 **/
public interface ICourseAudioService {

    public List<CourseAudioDto> getCourseAudio();
    
    public CourseAudioDto saveCourseAudio(EditCourseAudioDto courseAudioDto);

    public CourseAudioDto updateCourseAudio(EditCourseAudioDto courseAudioDto);
    
    public void deleteCourseAudio(EditCourseAudioDto courseAudioDto);

    Object getAudioJsonByCourseId(Long courseId,Long episode);
}
