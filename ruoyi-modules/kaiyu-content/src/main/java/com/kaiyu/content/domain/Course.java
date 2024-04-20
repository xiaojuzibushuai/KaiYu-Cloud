package com.kaiyu.content.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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

    @TableField("save_path")
    private String savePath;

    @TableField("category_id")
    private Long categoryId;

    @TableField("voice_files")
    private String voiceFiles;

    @TableField("data_files")
    private String dataFiles;

    @TableField("lrc_files")
    private String lrcFiles;

    @TableField("img_files")
    private String imgFiles;

    @TableField("is_public")
    private int isPublic;

    @TableField("love_number")
    private int loveNumber;

    @TableField("hard_number")
    private int hardNumber;

    @TableField("index_show")
    private int indexShow;

    private LocalDateTime uptime;

    private int priority;

    @TableField("play_time")
    private int playTime;

    @TableField("course_class")
    private String courseClass;

    private String volume;

    @TableField("video_files")
    private String videoFiles;

    @TableField("process_video_path")
    private String processVideoPath;

    @TableField("category_id")
    private String processVideoState;

}
