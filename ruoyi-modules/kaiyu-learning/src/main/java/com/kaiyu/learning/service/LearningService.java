package com.kaiyu.learning.service;

import com.kaiyu.learning.domain.RestResponse;
import com.kaiyu.learning.domain.dto.PushAnswerDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-09 14:30
 **/
public interface LearningService {

    RestResponse<Object> getVideo(Long courseId, String mediaId);


    RestResponse<Object> videoAutoPushActionToDevice(String operate,String sceneid);

    List getSceneList(HttpServletRequest request);

    RestResponse<Object> pushAnswerToKeyBoard(PushAnswerDto pushAnswerDto);

    RestResponse<Object> videoAutoPushDatToDevice(String url, String sceneid);

    RestResponse<Object> videoAutoPushFileDatToDevice(byte[] bytes, String sceneid);
}
