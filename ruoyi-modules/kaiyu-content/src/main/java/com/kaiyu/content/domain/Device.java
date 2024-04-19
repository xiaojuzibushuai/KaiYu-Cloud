package com.kaiyu.content.domain;

import com.baomidou.mybatisplus.annotation.IdType;
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
    private String software_version;
    private String is_master;
    private String productid;
    private String clientid;
    private String mac;
    private String remark;
    private int d_type;
    private String status;
    private LocalDateTime create_at;
    private LocalDateTime status_update;
    private String topic;
    private int is_auth;
    private String qrcode_suffix_data;
    private String city;
    private String school;
    private String d_class;
    private String phone;
    private String course_name;
    private Long course_id;
    private Long menu_id;
    private int menu_course_pointer;
    private int volume;
    private int face_count;
    private Boolean is_upgrade;
    private int direction;


}
