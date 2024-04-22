package com.kaiyu.content.domain.dto;

import com.kaiyu.content.domain.TeachplanMedia;
import lombok.Data;
import lombok.ToString;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-16 14:59
 **/
@Data
@ToString
public class TeachplanMediaDto extends TeachplanMedia {

    private String dpi;
    private String episode;
    //未处理状态为1 已处理为2 处理失败为3 处理中为4
    private String process_video_state;

//    private String video_base_url;

}
