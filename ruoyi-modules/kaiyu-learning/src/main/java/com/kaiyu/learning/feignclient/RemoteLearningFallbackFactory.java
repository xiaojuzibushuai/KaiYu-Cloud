package com.kaiyu.learning.feignclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-09 13:45
 **/
@Component
public class RemoteLearningFallbackFactory implements FallbackFactory<RemoteLearningService> {

    private static final Logger log = LoggerFactory.getLogger(RemoteLearningFallbackFactory.class);

    @Override
    public RemoteLearningService create(Throwable cause) {

        log.error("学习中心服务调用失败:{}", cause.getMessage());

        return null;
    }
}
