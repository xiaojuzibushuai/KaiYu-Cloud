package com.kaiyu.order.service;

import com.kaiyu.order.domain.OrdersGoods;
import com.kaiyu.order.domain.PayRecord;
import com.kaiyu.order.domain.dto.AddOrderDto;
import com.kaiyu.order.domain.dto.PayRecordDto;
import com.kaiyu.order.domain.dto.PayStatusDto;

import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

/**
 * @program: KaiYu-Cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-06-17 15:36
 **/
public interface OrderService {
    /**
     * 创建商品订单
     * @param addOrderDto   订单信息
     * @return  支付交易记录
     */
    Map<String, Object> createOrder(AddOrderDto addOrderDto,String tradeType);

    /**
     * 根据支付单号查询支付交易记录
     * @param payNo  支付单号
     * @return  支付交易记录
     */
    public PayRecord getPayRecordByPayNo(String payNo);

    PayStatusDto queryVxPayResult(String payNo);

    Map cancelVxPay(String payNo);

    /**
     * 用户支付成功回调处理接口
     * @param bodyMap
     */
    void processOrder(Map<String, Object> bodyMap) throws GeneralSecurityException;

    List<OrdersGoods> getUserOrderGoodsList(String orderId);
}
