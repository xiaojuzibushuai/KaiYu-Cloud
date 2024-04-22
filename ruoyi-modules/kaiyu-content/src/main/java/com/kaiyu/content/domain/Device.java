package com.kaiyu.content.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-19 15:24
 **/
@Data
@ToString
@ApiModel("设备表")
public class Device implements Serializable
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String apikey;
    private String deviceid;
    private String devicename;
    private String wakeword;
    @TableField(value = "software_version")
    private String software_version;
    @TableField(value = "is_master")
    private String is_master;
    private String productid;
    private String clientid;
    private String mac;
    private String remark;
    @TableField(value = "d_type")
    private int d_type;
    private String status;
    @TableField(value = "create_at")
    private LocalDateTime create_at;
    @TableField(value = "status_update")
    private LocalDateTime status_update;
    private String topic;
    @TableField(value = "is_auth")
    private int is_auth;
    @TableField(value = "qrcode_suffix_data")
    private String qrcode_suffix_data;
    private String city;
    private String school;
    @TableField(value = "d_class")
    private String d_class;
    private String phone;
    @TableField(value = "course_name")
    private String course_name;
    @TableField(value = "course_id")
    private Long course_id;
    @TableField(value = "menu_id")
    private Long menu_id;
    @TableField(value = "menu_course_pointer")
    private int menu_course_pointer;
    private int volume;
    @TableField(value = "face_count")
    private int face_count;
    @TableField(value = "is_upgrade")
    private Boolean is_upgrade;
    private int direction;


}
