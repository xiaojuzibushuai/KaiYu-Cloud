package com.kaiyu.media.service;

import com.kaiyu.media.domain.MediaProcess;

import java.util.List;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-03 14:20
 **/
public interface MediaFileProcessService {

    /**
     * 获取待处理任务
     * @param shardIndex    分片序号
     * @param shardTotal    分片总数
     * @param count         获取记录数
     * @return  待处理任务集合
     */
    List<MediaProcess> getMediaProcessList(int shardIndex, int shardTotal, int count);

    /**
     * 开启一个任务
     * 保证分布式锁 乐观锁保证
     * @param id 任务id
     * @return  true 开启任务成功，false 开启任务失败
     */
    public boolean startTask(long id);

    /**
     *  保存任务结果
     * @param taskId  任务id
     * @param status  任务状态
     * @param fileId 文件id
     * @param url
     * @param errorMsg 错误信息
     */
    void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg);


    List<MediaProcess> getMediaFilesStatus(String mediaId);
}
