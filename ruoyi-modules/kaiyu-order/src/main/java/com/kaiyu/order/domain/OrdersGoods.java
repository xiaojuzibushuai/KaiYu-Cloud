package com.kaiyu.order.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * @program: KaiYu-Cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-06-20 11:46
 **/
@Data
@ApiModel("订单详情表")
@TableName("orders_goods")
public class OrdersGoods implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 商品id
     */
    private String goodsId;

    /**
     * 商品类型
     */
    private String goodsType;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品交易价，单位分
     */
    private Float goodsPrice;

    /**
     * 商品详情json,可为空
     */
    private String goodsDetail;

}
