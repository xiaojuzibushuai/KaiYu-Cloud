package com.kaiyu.content.domain;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * 【请填写功能名称】对象 course
 * 
 * @author xiaojuzi
 * @date 2024-03-28
 */
@Data
@ToString
@ApiModel("课程表")
public class Course implements Serializable
{
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("课程id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String title;

    private String detail;

    private String savePath;

    private Long categoryId;

    private String voiceFiles;

    private String dataFiles;

    private String lrcFiles;

    private String imgFiles;

    private int isPublic;

    private int loveNumber;

    private int hardNumber;

    private int indexShow;

    private Date uptime;

    private int priority;

    private int playTime;

    private String courseClass;

    private String volume;

    private String videoFiles;

    private String processVideoPath;

    private String processVideoState;

}
