package com.kaiyu.order.domain.dto;

import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

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
     * 先废除 约定
     * 订单类型
     */
//    private String orderType;

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
     * [{"goodsId":"","goodsType":"","goodsName":"","goodsPrice":"","goodsDetail":"","validDays":""},{...}]
     */
    private String orderDetail;

    /**
     * 先废除 约定
     * 外部系统业务id
     */
//    private String outBusinessId;

    private String orderBusinessIds;

    /**
     * 自定义数据说明
     * {"toRechargePhone":[18707281085,18707281085],"":"",}
     */
    private Map<String, Object> attach = new HashMap<>();

}
