//package com.kaiyu.order.config;
//
//import com.alipay.api.AlipayApiException;
//import com.alipay.api.AlipayClient;
//import com.alipay.api.AlipayConstants;
//import com.alipay.api.DefaultAlipayClient;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.core.env.Environment;
//
//import javax.annotation.Resource;
//
///**
// * @program: KaiYu-Cloud
// * @description: 支付宝配置参数
// * @author: xiaojuzi
// * @create: 2024-06-17 15:01
// **/
//
//@Configuration
////加载配置文件
//@PropertySource("classpath:alipay-sandbox.properties")
//public class AlipayConfig {
//
//    @Resource
//    private Environment config;
//
//    @Bean
//    public AlipayClient alipayClient() throws AlipayApiException {
//
//        com.alipay.api.AlipayConfig alipayConfig = new com.alipay.api.AlipayConfig();
//
//        //设置网关地址
//        alipayConfig.setServerUrl(config.getProperty("alipay.gateway-url"));
//        //设置应用Id
//        alipayConfig.setAppId(config.getProperty("alipay.app-id"));
//        //设置应用私钥
//        alipayConfig.setPrivateKey(config.getProperty("alipay.merchant-private-key"));
//        //设置请求格式，固定值json
//        alipayConfig.setFormat(AlipayConstants.FORMAT_JSON);
//        //设置字符集
//        alipayConfig.setCharset(AlipayConstants.CHARSET_UTF8);
//        //设置支付宝公钥
//        alipayConfig.setAlipayPublicKey(config.getProperty("alipay.alipay-public-key"));
//        //设置签名类型
//        alipayConfig.setSignType(AlipayConstants.SIGN_TYPE_RSA2);
//        //构造client
//        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);
//
//        return alipayClient;
//    }
//
//}
