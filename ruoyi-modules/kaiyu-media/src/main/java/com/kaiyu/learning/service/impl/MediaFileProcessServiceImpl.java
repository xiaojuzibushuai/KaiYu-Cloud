package com.kaiyu.learning.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kaiyu.learning.domain.MediaFiles;
import com.kaiyu.learning.domain.MediaProcess;
import com.kaiyu.learning.domain.MediaProcessHistory;
import com.kaiyu.learning.mapper.MediaFilesMapper;
import com.kaiyu.learning.mapper.MediaProcessHistoryMapper;
import com.kaiyu.learning.mapper.MediaProcessMapper;
import com.kaiyu.learning.service.MediaFileProcessService;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-03 14:20
 **/
@Service
public class MediaFileProcessServiceImpl implements MediaFileProcessService {

    private static final Logger log = LoggerFactory.getLogger(MediaFileProcessServiceImpl.class);


    @Autowired
    private MediaProcessHistoryMapper mediaProcessHistoryMapper;

    @Autowired
    private MediaProcessMapper mediaProcessMapper;

    @Autowired
    MediaFilesMapper mediaFilesMapper;


    @Override
    public List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count) {
        return mediaProcessMapper.selectListByShardIndex(shardTotal, shardIndex, count);
    }

    @Override
    public boolean startTask(long id) {
        int result = mediaProcessMapper.startTask(id);
        return result<=0?false:true;
    }

    @Override
    @Transactional
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {

        //查出任务，如果不存在则直接返回
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if(mediaProcess == null){
            return ;
        }

        //处理中 更新状态
        if (status.equals("4")){
            LambdaQueryWrapper<MediaProcess> queryWrapperById = new LambdaQueryWrapper<MediaProcess>().eq(MediaProcess::getId, taskId);
            MediaProcess mediaProcess_u = new MediaProcess();
            mediaProcess_u.setStatus("4");
            mediaProcessMapper.update(mediaProcess_u,queryWrapperById);
            log.debug("更新任务处理状态，任务信息:{}",mediaProcess_u);
            return ;
        }

        //处理失败
        if(status.equals("3")){
            LambdaQueryWrapper<MediaProcess> queryWrapperById = new LambdaQueryWrapper<MediaProcess>().eq(MediaProcess::getId, taskId);
            MediaProcess mediaProcess_u = new MediaProcess();
            mediaProcess_u.setStatus("3");
            mediaProcess_u.setErrormsg(errorMsg);
            mediaProcess_u.setFailCount(mediaProcess.getFailCount()+1);
            mediaProcessMapper.update(mediaProcess_u,queryWrapperById);
            log.debug("更新任务处理状态为失败，任务信息:{}",mediaProcess_u);
            return ;
        }

        //任务处理成功
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
        if(mediaFiles!=null){
            //更新媒资文件中的访问 url
            if(StringUtils.isEmpty(mediaFiles.getUrl())){

                JSONArray jsonArray = new JSONArray();
                jsonArray.add(JSON.parseObject(url));

                mediaFiles.setUrl(jsonArray.toJSONString());
            }else {
                //解析url
                JSONArray jsonArray = JSONArray.parseArray(mediaFiles.getUrl());
                jsonArray.add(JSON.parseObject(url));
                mediaFiles.setUrl(jsonArray.toJSONString());
            }

            mediaFiles.setStatus("2");

            mediaFilesMapper.updateById(mediaFiles);
        }

        //处理成功，更新 url 和状态
        mediaProcess.setUrl(url);
        mediaProcess.setStatus("2");
        mediaProcess.setFinishDate(LocalDateTime.now());
        mediaProcessMapper.updateById(mediaProcess);

        //添加到历史任务记录表
        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtils.copyProperties(mediaProcess, mediaProcessHistory);
        mediaProcessHistoryMapper.insert(mediaProcessHistory);

        //删除任务记录表
        mediaProcessMapper.deleteById(mediaProcess.getId());

    }

    @Override
    public List<MediaProcess> getMediaFilesStatus(String mediaId) {

        if(mediaId!=null){
            List<MediaProcess> data = new ArrayList<>();
            //查正在处理表
            LambdaQueryWrapper<MediaProcess> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(MediaProcess::getFileId,mediaId);
            List<MediaProcess> mediaProcesses = mediaProcessMapper.selectList(queryWrapper);

            if (mediaProcesses.size()>0){
                data.addAll(mediaProcesses);
            }

            //查处理历史表
            LambdaQueryWrapper<MediaProcessHistory> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(MediaProcessHistory::getFileId,mediaId);
            List<MediaProcessHistory> mediaProcessHistories = mediaProcessHistoryMapper.selectList(queryWrapper1);

            if (mediaProcessHistories.size()>0){
                mediaProcessHistories.forEach(mediaProcessHistory -> {
                    MediaProcess mediaProcess = new MediaProcess();
                    BeanUtils.copyProperties(mediaProcessHistory, mediaProcess);
                    data.add(mediaProcess);
                });
            }

            return data;
        }
        return null;
    }

}
