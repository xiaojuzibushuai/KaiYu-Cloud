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

        System.out.println("(♥◠‿◠)ﾉﾞ  订单中心模块启动成功   ლ(´ڡ`ლ)ﾞ  \n" +
                " .-------.       ____     __        \n" +
                " |  _ _   \\      \\   \\   /  /    \n" +
                " | ( ' )  |       \\  _. /  '       \n" +
                " |(_ o _) /        _( )_ .'         \n" +
                " | (_,_).' __  ___(_ o _)'          \n" +
                " |  |\\ \\  |  ||   |(_,_)'         \n" +
                " |  | \\ `'   /|   `-'  /           \n" +
                " |  |  \\    /  \\      /           \n" +
                " ''-'   `'-'    `-..-'              ");
    }
    }

