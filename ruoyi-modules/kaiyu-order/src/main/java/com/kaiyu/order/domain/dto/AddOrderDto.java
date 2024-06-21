package com.kaiyu.order.domain.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @program: KaiYu-Cloud
 * @description: 创建商品订单
 * @author: xiaojuzi
 * @create: 2024-06-17 17:01
 **/
@Data
@ToString
public class AddOrderDto {

    /**
     * 下单人 小程序用
     */
    private String openId;

    /**
     * 总价 订单金额(分)
     */
    private Integer totalPrice;

    /**
     * 订单类型
     */
    private String orderType;

    /**
     * 订单名称
     */
    private String orderName;
    /**
     * 订单描述
     */
    private String orderDescrip;

    /**
     * 订单明细json，不可为空
     * [{"goodsId":"","goodsType":"","goodsName":"","goodsPrice":"","goodsDetail":""},{...}]
     */
    private String orderDetail;

    /**
     * 外部系统业务id
     */
    private String outBusinessId;
}
