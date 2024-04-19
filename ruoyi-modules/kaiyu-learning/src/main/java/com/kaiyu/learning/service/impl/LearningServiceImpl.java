package com.kaiyu.learning.service.impl;

import com.kaiyu.learning.domain.RestResponse;
import com.kaiyu.learning.domain.TeachplanMedia;
import com.kaiyu.learning.domain.dto.TeachplanDto;
import com.kaiyu.learning.feignclient.RemoteContentService;
import com.kaiyu.learning.feignclient.RemoteMediaService;
import com.kaiyu.learning.service.LearningService;
import com.ruoyi.common.core.domain.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-09 14:31
 **/
@Service
public class LearningServiceImpl implements LearningService {

    @Autowired
//    @Qualifier("")
    RemoteContentService remoteContentService;

    @Autowired
//    @Qualifier("")
    RemoteMediaService remoteMediaService;

    @Override
    public RestResponse<Object> getVideo(Long courseId, String mediaId) {

        try {
            // 1. 远程调用内容管理服务，查询课程计划 业务设计待修改
            R<TeachplanDto> teachplanDtoR = remoteContentService.getTeachplanByCourseId(courseId);

            TeachplanDto teachplanDto = teachplanDtoR.getData();
            if (teachplanDto == null) {
                return RestResponse.validfail("课程计划不存在");
            }

            List<TeachplanMedia> teachplanMediaList = teachplanDto.getTeachplanMedia();
            if (teachplanMediaList == null || teachplanMediaList.isEmpty()) {
                return RestResponse.validfail("课程媒体不存在");
            }

            Optional<RestResponse<Object>> res = teachplanMediaList.stream()
                    .filter(teachplanMedia -> mediaId.equals(teachplanMedia.getMediaId()))
                    .findFirst()
                    .map(teachplanMedia -> {
                        // 2. 判断课程是否收费 业务设计待修改

                        // 3. 远程调用媒资管理服务，获取视频播放地址 业务设计待修改
                        return remoteMediaService.getPlayUrlByMediaId(mediaId);
                    });
            if (res.isPresent()) {
                if(res.get().getResult()!=null){
                    return res.get();
                }else {
                    return RestResponse.validfail("课程媒体不存在");
                }
            }else {
                return RestResponse.validfail("课程媒体不存在");
            }

        } catch (Exception e) {
            // 异常处理，例如网络问题或服务调用失败
            return RestResponse.validfail("获取视频播放地址失败: " + e.getMessage());
        }
    }

    @Override
    public RestResponse<Object> videoAutoPushActionToDevice(String operate, String sceneid) {
        if (operate == null ||   sceneid == null) {
            return RestResponse.validfail("参数不合法");
        }
        return RestResponse.success();

    }



}
