//package com.kaiyu.order.controller;
//
//import cn.elegent.pay.ElegentPay;
//import cn.elegent.pay.dto.*;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @program: payment-demo 测试 备选方案
// * @description:
// * @author: xiaojuzi
// * @create: 2024-03-26 10:38
// **/
//
//@Api(tags = "优雅组件支付接口测试")
//@RestController
//@RequestMapping("/order/pay")
//public class PayController {
//
//    @Autowired
//    private ElegentPay elegentPay;
//
//    /**
//     * 创建订单
//     * @return
//     */
//    @ApiOperation("创建订单")
//    @GetMapping("/requestPay/{tradeType}/{platform}")
//    public PayResponse requestPay(@PathVariable("tradeType") String tradeType, @PathVariable("platform") String platform){
//        PayRequest payRequest = new PayRequest();
//        payRequest.setTotalFee(100);//金额
//        payRequest.setOrderSn(System.currentTimeMillis()+"");//订单号
//        payRequest.setBody("elegent");// 商品名称
//        payRequest.setOpenid("oJ9WJ5MhIS-hiwuUX0GmsHDzqTyQ");
//        PayResponse payResponse = elegentPay.requestPay(payRequest,tradeType, platform);
//        return payResponse;
//    }
//
//    /**
//     * 关闭订单
//     * @param orderNo
//     * @return
//     */
//    @ApiOperation("关闭订单")
//    @GetMapping("/closeOrder/{orderNo}/{platform}")
//    public Boolean closeOrder(@PathVariable("orderNo") String orderNo,@PathVariable("platform") String platform){
//        Boolean aBoolean = elegentPay.closePay(orderNo,platform);
//        return aBoolean;
//    }
//
//    /**
//     * 退款
//     * @param orderNo
//     * @return
//     */
//    @ApiOperation("退款")
//    @GetMapping("/refund/{orderNo}/{platform}")
//    public boolean refund(@PathVariable("orderNo") String orderNo,@PathVariable("platform") String platform){
//        RefundRequest refundRequest=new RefundRequest();
//        refundRequest.setTotalFee(100);
//        refundRequest.setRefundAmount(100);
//        refundRequest.setOrderSn(orderNo);
//        refundRequest.setRequestNo(System.currentTimeMillis()+"");
//        Boolean refund = elegentPay.refund(refundRequest,platform);
//        return refund;
//    }
//
//    /**
//     * 查询订单
//     * @param orderNo
//     * @return
//     */
//    @ApiOperation("查询订单")
//    @GetMapping("/query/{orderNo}/{platform}")
//    public QueryResponse query(@PathVariable("orderNo") String orderNo, @PathVariable("platform") String platform){
//        QueryResponse queryResponse = elegentPay.queryTradingOrderNo(orderNo,platform);
//        return queryResponse;
//    }
//
//    /**
//     * 查询退款订单
//     * @param orderNo
//     * @return
//     */
//    @ApiOperation("查询退款订单")
//    @GetMapping("/queryRefund/{orderNo}/{platform}")
//    public QueryRefundResponse queryRefund(@PathVariable("orderNo") String orderNo, @PathVariable("platform") String platform){
//        QueryRefundResponse queryRefundResponse = elegentPay.queryRefundTrading(orderNo,platform);
//        return queryRefundResponse;
//    }
//
//}
