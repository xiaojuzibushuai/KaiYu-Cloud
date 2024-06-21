package com.kaiyu.order.config;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: KaiYu-Cloud
 * @description: redisson 配置类
 * @author: xiaojuzi
 * @create: 2024-06-21 17:39
 **/
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.password}")
    private String redisPassword;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort);
        if(StringUtils.isNotBlank(redisPassword)) {
            serverConfig.setPassword(redisPassword);
        }
        return Redisson.create(config);
    }

}
