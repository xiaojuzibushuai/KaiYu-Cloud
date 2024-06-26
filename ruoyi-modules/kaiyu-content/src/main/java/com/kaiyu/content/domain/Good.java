package com.kaiyu.content.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: KaiYu-Cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-06-26 18:19
 **/

@Data
@ToString
@ApiModel("商品表")
@TableName("goods")
public class Good implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("商品id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String goodImage;

    private String goodName;
    private String goodInfo;

    private String keyword;

    private String cateId;

    /**
     * 总价 订单金额(分)
     */
    private Integer price;

    private Integer isShow;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     *商品类型 # category-整套分类 course-单件课程
     */
    private String goodsType;

    /**
     * 分类id 或 课程id 或 。。。
     */
    private String outBusinessId;




}
