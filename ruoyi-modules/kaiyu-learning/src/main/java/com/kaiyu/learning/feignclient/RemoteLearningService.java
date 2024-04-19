package com.kaiyu.learning.feignclient;

import com.ruoyi.common.core.constant.ServiceNameConstants;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @program: kai-yu-cloud
 * @description: 媒资服务
 * @author: xiaojuzi
 * @create: 2024-04-01 10:49
 **/
@FeignClient(contextId = "RemoteLearningService", value = ServiceNameConstants.LEARNING_SERVICE, fallbackFactory = RemoteLearningFallbackFactory.class)
public interface RemoteLearningService {


    
}
