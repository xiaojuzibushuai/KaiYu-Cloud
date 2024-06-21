package com.kaiyu.order.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @program: KaiYu-Cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-06-17 16:30
 **/
@Data
@ApiModel("订单表")
@TableName("orders")
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private Long id;

    /**
     * 总价 订单金额(分)
     */
    private Integer totalPrice;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    /**
     * 交易状态 第五个微服务服务编号为500 开始
     * [{"code":"500001","desc":"未支付"},
     * {"code":"500002","desc":"已支付"},
     * {"code":"500003","desc":"已关闭"},
     * {"code":"500004","desc":"已退款"},
     * {"code":"500005","desc":"已完成"}]
     */
    private String status;

    /**
     * 用户id
     */
    private String userId;


    /**
     * 订单类型
     * [{"code":"502001","desc":"购买课程"},
     * {"code":"502002","desc":"学习资料"}]
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
     * 订单明细json
     */
    private String orderDetail;

    /**
     * 外部系统业务id
     */
    private String outBusinessId;

}
