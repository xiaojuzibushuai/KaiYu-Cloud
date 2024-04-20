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
 * @create: 2024-04-20 11:49
 **/
@Data
@ToString
@TableName("user_device")
public class UserDevice implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String userid;
    private String deviceid;

    @TableField("is_choose")
    private int is_choose;

    @TableField("status_update")
    private LocalDateTime status_update;

    private Long sceneid;

    private int status;

    @TableField("is_del")
    private int is_del;

    @TableField("shareby_userid")
    private String shareby_userid;

    @TableField("share_code")
    private String share_code;

}
