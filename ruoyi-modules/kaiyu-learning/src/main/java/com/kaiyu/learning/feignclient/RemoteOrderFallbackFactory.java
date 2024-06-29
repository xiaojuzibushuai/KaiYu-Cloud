package com.kaiyu.learning.feignclient;

import com.kaiyu.learning.domain.RestResponse;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.api.model.UserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @program: KaiYu-Cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-06-29 08:49
 **/
@Component
public class RemoteOrderFallbackFactory implements FallbackFactory<RemoteOrderService> {

    private static final Logger log = LoggerFactory.getLogger(RemoteOrderFallbackFactory.class);
    @Override
    public RemoteOrderService create(Throwable cause) {
        log.error("系统服务调用失败:{}", cause.getMessage());
        return new RemoteOrderService() {

            @Override
            public RestResponse getUserOrderGoodsList(String orderId) {
                return null;
            }
        };
    }
}
