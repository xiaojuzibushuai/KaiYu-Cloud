package com.kaiyu.order;

import com.kaiyu.order.config.WxPayConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.security.PrivateKey;

@SpringBootTest
class KaiyuOrderApplicationTests {

    @Resource
    private WxPayConfig wxPayConfig;

    @Resource
    private CloseableHttpClient wxPayClient;


    @Test
    void getWxPayConfig() {
        String mchId = wxPayConfig.getMchId();
        System.out.println(mchId);
    }

    /**
     * 获取商户的私钥
     */
    @Test
    void testGetPrivateKey() {

        //获取私钥路径
        String privateKeyPath = wxPayConfig.getPrivateKeyPath();

        //获取私钥
        PrivateKey privateKey = wxPayConfig.getPrivateKey(privateKeyPath);

        System.out.println(privateKey);

    }



    @Test
    void contextLoads() {
    }

}
