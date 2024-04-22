package com.kaiyu.learning.feignclient;

import com.kaiyu.learning.domain.RestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

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
            public RestResponse<Object> getPlayUrlByMediaId(String mediaId) {
                log.error("获取播放地址失败:{}", cause.getMessage());

                return null;
            }
        };
    }
}
