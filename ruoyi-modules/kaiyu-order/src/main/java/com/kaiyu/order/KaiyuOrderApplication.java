package com.kaiyu.order;

import com.ruoyi.common.security.annotation.EnableCustomConfig;
import com.ruoyi.common.security.annotation.EnableKyFeignClients;
import com.ruoyi.common.swagger.annotation.EnableCustomSwagger2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableCustomConfig
@EnableCustomSwagger2
@EnableKyFeignClients(basePackages = {"com.ruoyi","com.kaiyu"})
@SpringBootApplication
public class KaiyuOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(KaiyuOrderApplication.class, args);
    }

}
