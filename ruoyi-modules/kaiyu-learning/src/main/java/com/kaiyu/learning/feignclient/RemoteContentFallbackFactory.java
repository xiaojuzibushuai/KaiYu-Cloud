package com.kaiyu.learning.feignclient;

import com.kaiyu.learning.domain.dto.TeachplanDto;
import com.ruoyi.common.core.domain.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @program: kai-yu-cloud
 * @description: 内容服务降级处理
 * @author: xiaojuzi
 * @create: 2024-03-28 10:48
 **/

@Component
public class RemoteContentFallbackFactory implements FallbackFactory<RemoteContentService> {

    private static final Logger log = LoggerFactory.getLogger(RemoteContentFallbackFactory.class);

    @Override
    public RemoteContentService create(Throwable cause) {
        log.error("内容服务调用失败:{}", cause.getMessage());

        return new RemoteContentService() {
            @Override
            public R<TeachplanDto> getTeachplanByCourseId(Long courseId) {
                log.error("调用内容管理服务查询教学计划发生熔断:{}", cause.toString(),cause);
                return null;
            }
        };
    }

}
