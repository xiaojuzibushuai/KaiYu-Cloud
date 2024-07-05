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
 * @create: 2024-07-05 14:09
 **/
@Data
@ToString
@ApiModel("键盘答题信息表")
@TableName("key_board_data")
public class KeyBoardData implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String devicename;
    private String deviceid;
    private String gametype;
    private String parentid;

    private String answer;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime statusUpdate;


}
