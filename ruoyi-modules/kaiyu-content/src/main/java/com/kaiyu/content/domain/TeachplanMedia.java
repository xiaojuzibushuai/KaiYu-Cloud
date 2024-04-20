package com.kaiyu.content.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: kai-yu-cloud
 * @description: 课程计划与媒资关系表
 * @author: xiaojuzi
 * @create: 2024-04-09 09:31
 **/
@Data
@TableName("teachplan_media")
public class TeachplanMedia  implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 媒资文件id
     */
    private String mediaId;

    /**
     * 课程计划标识
     * 设计的有暂时不用 xiaojuzi
     */
    private Long teachplanId;

    /**
     * 课程标识
     */
    private Long courseId;

    /**
     *  课程集数
     */
    private String episode;

    /**
     * 媒资文件原始名称
     */
    @TableField("media_fileName")
    private String mediaFilename;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    /**
     * 创建人
     */
    private String createPeople;

    /**
     * 修改人
     */
    private String changePeople;


}
