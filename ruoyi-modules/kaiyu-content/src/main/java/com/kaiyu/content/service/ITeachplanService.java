package com.kaiyu.content.service;

import com.kaiyu.content.domain.MediaFiles;
import com.kaiyu.content.domain.TeachplanMedia;
import com.kaiyu.content.domain.dto.BindTeachplanMediaDto;
import com.kaiyu.content.domain.dto.TeachplanDto;
import com.kaiyu.content.domain.dto.TeachplanMediaDto;

import java.util.List;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-09 09:37
 **/
public interface ITeachplanService {

    List<TeachplanMediaDto> findCourseTeachplanDto(Long courseId);

    void unassociationMedia(Long courseId, String mediaId);

    void associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

    TeachplanDto findCourseTeachplan(Long courseId);


    String getTeachplanMediaByCourseId(Long courseId, String episode);
}
