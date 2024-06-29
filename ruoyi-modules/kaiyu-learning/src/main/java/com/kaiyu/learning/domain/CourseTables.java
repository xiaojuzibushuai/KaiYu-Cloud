package com.kaiyu.learning.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @program: KaiYu-Cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-06-28 11:35
 **/
@Data
@TableName("course_tables")
public class CourseTables implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id 或用手机号代替
     */
    private String userId;

    /**
     * 课程id
     */
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;
    /**
     * 课程名称
     */
    private String courseType;


    /**
     * 添加时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createDate;

    /**
     * 开始服务时间
     */
    private LocalDateTime validtimeStart;

    /**
     * 到期时间
     */
    private LocalDateTime validtimeEnd;

    /**
     * 更新时间
     */
    private LocalDateTime updateDate;

    private Integer validDays;

    /**
     * 备注
     */
    private String remarks;

}
