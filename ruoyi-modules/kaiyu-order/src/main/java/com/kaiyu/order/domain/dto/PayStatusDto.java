package com.kaiyu.order.domain.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @program: KaiYu-Cloud
 * @description: 支付状态dto
 * @author: xiaojuzi
 * @create: 2024-06-21 14:03
 **/
@Data
@ToString
public class PayStatusDto {
    //商户订单号
    String out_trade_no;
    //第三方支付平台交易号
    String trade_no;
    //交易状态
    String trade_status;
    //交易方式
    String trade_type;
    //appid
    String app_id;
    //total_amount
    String total_amount;
    //附加信息
    String attach;
}
