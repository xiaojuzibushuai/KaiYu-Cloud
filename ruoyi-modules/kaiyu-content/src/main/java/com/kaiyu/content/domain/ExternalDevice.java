package com.kaiyu.content.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-23 16:48
 **/
@Data
@ToString
@TableName("external_device")
public class ExternalDevice implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String deviceid;

    private String devicename;

    private String mac;
    private String topic;
    @TableField("status_update")
    private LocalDateTime status_update;

    @TableField("d_type")
    private int dType;

    @TableField("qrcode_suffix_data")
    private String qrcode_suffix_data;

}
