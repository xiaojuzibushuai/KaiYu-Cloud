package com.kaiyu.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaiyu.content.domain.Course;
import com.kaiyu.content.domain.MediaFiles;
import com.kaiyu.content.domain.MediaProcess;
import com.kaiyu.content.domain.TeachplanMedia;
import com.kaiyu.content.domain.dto.BindTeachplanMediaDto;
import com.kaiyu.content.domain.dto.TeachplanDto;
import com.kaiyu.content.domain.dto.TeachplanMediaDto;
import com.kaiyu.content.feignclient.RemoteMediaService;
import com.kaiyu.content.mapper.TeachplanMediaMapper;
import com.kaiyu.content.service.ITeachplanService;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.CommonError;
import com.ruoyi.common.core.exception.KaiYuEducationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-09 09:37
 **/
@Service
public class TeachplanServiceImpl implements ITeachplanService {

    private static final Logger log = LoggerFactory.getLogger(TeachplanServiceImpl.class);

    @Autowired
    private TeachplanMediaMapper teachplanMediaMapper;

    @Autowired
    private RemoteMediaService remoteMediaService;

    @Override
    public List<TeachplanMediaDto> findCourseTeachplanDto(Long courseId) {

        if (courseId == null ) {
            log.info("findCourseTeachplanDto时课程id为空");
//            KaiYuEducationException.cast(CommonError.REQUEST_NULL);
            return null;
        }

        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachplanMedia::getCourseId, courseId);

        List<TeachplanMedia> teachplanMedia = teachplanMediaMapper.selectList(queryWrapper);

        if (teachplanMedia == null || teachplanMedia.size() == 0) {
            log.info("findCourseTeachplanDto时没有找到课程的教学计划");
//            KaiYuEducationException.cast("没有找到课程的教学计划");
            return null;

        }

        List<TeachplanMediaDto> list = new ArrayList<>();
        teachplanMedia.forEach(item -> {
            //远程调用媒资模块获取视频处理状态
            R<List<MediaProcess>> mediaFilesStatus = remoteMediaService.getMediaFilesStatus(item.getMediaId());

            if (mediaFilesStatus.getData()!= null && mediaFilesStatus.getData().size() > 0) {
                List<MediaProcess> mediaProcessList = mediaFilesStatus.getData();
                mediaProcessList.forEach(mediaProcess -> {

                    TeachplanMediaDto teachplanMediaDto = new TeachplanMediaDto();

                    //返回所需数据
                    teachplanMediaDto.setCourseId(item.getCourseId());
                    teachplanMediaDto.setMediaId(item.getMediaId());
                    teachplanMediaDto.setMediaFilename(item.getMediaFilename());

                    String remark = mediaProcess.getRemark();
                    teachplanMediaDto.setEpisode(remark.split(",")[0]);
                    teachplanMediaDto.setDpi(remark.split(",")[1]);

                    switch (mediaProcess.getStatus()){
                        case "1":
                            teachplanMediaDto.setProcess_video_state("未处理");
                            break;
                        case "2":
                            teachplanMediaDto.setProcess_video_state("处理成功");
                            break;
                        case "3":
                            teachplanMediaDto.setProcess_video_state("处理失败");
                            break;
                        case "4":
                            teachplanMediaDto.setProcess_video_state("处理中");
                            break;
                        default:
                            teachplanMediaDto.setProcess_video_state("未知错误");
                    }
                    list.add(teachplanMediaDto);
                });

            }else {
                TeachplanMediaDto teachplanMediaDto = new TeachplanMediaDto();

                //返回所需数据
                teachplanMediaDto.setCourseId(item.getCourseId());
                teachplanMediaDto.setMediaId(item.getMediaId());
                teachplanMediaDto.setMediaFilename(item.getMediaFilename());

                list.add(teachplanMediaDto);
            }
        });

        return list;
    }

    @Override
    public void unassociationMedia(Long courseId, String mediaId) {

        //可以先查下是否有这个课和文件 暂不考虑

        log.info("课程id:{},媒资文件id:{},解绑成功", courseId, mediaId);

        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachplanMedia::getCourseId, courseId)
                .eq(TeachplanMedia::getMediaId, mediaId);
        teachplanMediaMapper.delete(queryWrapper);

    }

    @Override
    public void associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {

        //可以先查下是否有这个课和文件 暂不考虑

        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<TeachplanMedia>()
                .eq(TeachplanMedia::getCourseId, bindTeachplanMediaDto.getCourseId())
                .eq(TeachplanMedia::getMediaId, bindTeachplanMediaDto.getMediaId());
        TeachplanMedia teachplanMedia = teachplanMediaMapper.selectOne(queryWrapper);
        if (teachplanMedia != null) {
            log.info("associationMedia时该课程下已存在该文件，不可重复绑定");
            KaiYuEducationException.cast("该课程下已存在该文件，不可重复绑定");
        }

        //新增
        teachplanMedia = new TeachplanMedia();
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setCourseId(bindTeachplanMediaDto.getCourseId());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMediaMapper.insert(teachplanMedia);

        log.info("课程id:{},媒资文件id:{},绑定成功", bindTeachplanMediaDto.getCourseId(), bindTeachplanMediaDto.getMediaId());

    }

    @Override
    public TeachplanDto findCourseTeachplan(Long courseId) {

        if (courseId == null ) {
            log.info("findCourseTeachplan时课程id为空");
//            KaiYuEducationException.cast(CommonError.REQUEST_NULL);
            return null;
        }

        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachplanMedia::getCourseId, courseId);

        List<TeachplanMedia> teachplanMedia = teachplanMediaMapper.selectList(queryWrapper);

        if (teachplanMedia == null || teachplanMedia.size() == 0) {
            log.info("findCourseTeachplan时没有找到课程的教学计划");
//            KaiYuEducationException.cast("没有找到课程的教学计划");
            return null;
        }

        TeachplanDto teachplanDto = new TeachplanDto();
        teachplanDto.setTeachplanMedia(teachplanMedia);

        return teachplanDto;

    }

    @Override
    public String getTeachplanMediaByCourseId(Long courseId, String episode) {

        if (courseId == null || episode == null) {
            log.info("getTeachplanMediaByCourseId时课程id或集数id为空");
//            KaiYuEducationException.cast(CommonError.REQUEST_NULL);
            return null;
        }

        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachplanMedia::getCourseId, courseId);

        List<TeachplanMedia> teachplanMedia = teachplanMediaMapper.selectList(queryWrapper);

        if (teachplanMedia == null || teachplanMedia.size() == 0) {
            log.info("findCourseTeachplanDto时没有找到课程的教学计划");
//            KaiYuEducationException.cast("没有找到课程的教学计划");
            return null;

        }


        for (TeachplanMedia item : teachplanMedia) {
            //远程调用媒资模块查询url
            R<MediaFiles> mediaFilesDetail = remoteMediaService.getMediaFilesDetail(item.getMediaId());
            if (mediaFilesDetail.getData() != null) {
                //进行判断
                MediaFiles mediaFiles = mediaFilesDetail.getData();
                if (mediaFiles.getFileType().equals("001002")){
//                    JSONArray jsonArray = JSONArray.parseArray(mediaFiles.getUrl());
                    String flag = (String) JSON.parseObject(mediaFiles.getRemark()).get("episode");
                    if (flag.equals(episode)) {
                        return mediaFiles.getUrl();
                    }
                    }
            }
        }

        return null;
    }

}
