package com.kaiyu.learning.feignclient;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.api.model.UserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-23 10:41
 **/
@Component
public class RemoteSystemFallbackFactory implements FallbackFactory<RemoteSystemService> {

    private static final Logger log = LoggerFactory.getLogger(RemoteSystemFallbackFactory.class);

    @Override
    public RemoteSystemService create(Throwable cause) {
        log.error("系统服务调用失败:{}", cause.getMessage());
        return new RemoteSystemService() {

        };
    }
}
