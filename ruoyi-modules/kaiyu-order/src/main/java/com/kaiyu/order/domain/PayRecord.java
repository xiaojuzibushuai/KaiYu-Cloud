package com.kaiyu.order.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: KaiYu-Cloud
 * @description: 支付记录表
 * @author: xiaojuzi
 * @create: 2024-06-17 17:04
 **/
@Data
@ApiModel("支付记录表")
@TableName("pay_record")
public class PayRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 支付记录号
     */
    private Long id;

    /**
     * 本系统支付交易号
     */
    private Long payNo;

    /**
     * 第三方支付交易流水号
     */
    private String outPayNo;

    /**
     * 第三方支付渠道编号
     * [{"code":"503001","desc":"微信支付"},
     * {"code":"503002","desc":"支付宝"}]
     */
    private String outPayChannel;

    /**
     * 商品订单号
     */
    private Long orderId;

    /**
     * 订单名称
     */
    private String orderName;
    /**
     * 订单总价单位分
     */
    private Integer totalPrice;

    /**
     * 币种CNY
     */
    private String currency;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    /**
     * 支付状态
     * [{"code":"501001","desc":"未支付"},
     * {"code":"501002","desc":"已支付"},
     * {"code":"501003","desc":"已取消"},
     *  {"code":"501004","desc":"已退款"},
     * ]
     */
    private String status;

    /**
     * 支付成功时间
     */
    private LocalDateTime paySuccessTime;

    /**
     * 用户id
     */
    private String userId;


}
