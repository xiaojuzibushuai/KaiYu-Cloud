package com.kaiyu.content.feignclient;

import com.kaiyu.content.domain.MediaFiles;
import com.kaiyu.content.domain.MediaProcess;
import com.ruoyi.common.core.domain.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @program: kai-yu-cloud
 * @description: 媒资服务降级处理
 * @author: xiaojuzi
 * @create: 2024-04-01 10:48
 **/
@Component
public class RemoteMediaFallbackFactory implements FallbackFactory<RemoteMediaService> {

    private static final Logger log = LoggerFactory.getLogger(RemoteMediaFallbackFactory.class);

    @Override
    public RemoteMediaService create(Throwable cause) {
        log.error("媒资服务调用失败:{}", cause.getMessage());

        return new RemoteMediaService() {

            @Override
            public R<MediaFiles> getMediaFilesDetail(String mediaId) {
                log.error("媒资服务调用获取媒资详情失败:{}", cause.getMessage());
                return null;
            }

            @Override
            public R<List<MediaProcess>> getMediaFilesStatus(String mediaId) {
                log.error("媒资服务调用获取媒资处理状态失败:{}", cause.getMessage());
                return null;
            }
        };
    }
}
