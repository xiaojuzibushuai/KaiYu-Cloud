//package com.kaiyu.order.service.impl;
//
//import cn.elegent.pay.CallBackService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
///**
// * 优雅支付组件回调类
// */
//@Service
//@Slf4j
//public class CallBackServiceImpl implements CallBackService {
//
//
//    @Override
//    public void successPay(String orderSn) {
//        log.info("支付成功回调！!!"+orderSn);
//    }
//
//    @Override
//    public void failPay(String orderSn) {
//        log.info("支付失败回调！!!!"+orderSn);
//    }
//
//
//    @Override
//    public void successRefund(String orderSn) {
//        log.info("退款成功回调！!!!"+orderSn);
//    }
//
//    @Override
//    public void failRefund(String orderSn) {
//        log.info("退款失败回调！!!!"+orderSn);
//    }
//
//
//}
