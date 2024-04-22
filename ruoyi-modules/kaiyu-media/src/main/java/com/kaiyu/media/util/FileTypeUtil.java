package com.kaiyu.media.util;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-01 16:47
 **/
public class FileTypeUtil {
    /**
     * Video MIME类型：
     * video/mp4：MP4视频文件
     * video/quicktime：QuickTime视频文件
     * video/x-msvideo：AVI视频文件
     * video/x-flv：Flash视频文件
     * video/x-matroska：Matroska视频文件（MKV格式）
     * Audio MIME类型：
     * audio/mpeg：MP3音频文件
     * audio/wav：WAV音频文件
     * audio/x-aiff：AIFF音频文件
     * audio/x-midi：MIDI音频文件
     * audio/ogg：Ogg音频文件
     * @param contentType
     * @return
     */
    public static String getFileTypeCode(String contentType) {
        if (contentType.startsWith("image/")) {
            return "001001";
        } else if (contentType.startsWith("video/")) {
            return "001002";
        } else if (contentType.startsWith("audio/")) {
            return "001003";
        } else if (contentType.equals("application/pdf") || contentType.equals("application/msword") || contentType.equals("application/vnd.ms-excel")) {
            return "001004";
        } else {
            return "001005";
        }
    }

    /**
     * 根据文件FileTypeCode获取tag
     * xiaojuzi
     */
    public static String getTagByFileTypeCode(String fileTypeCode) {
            if (fileTypeCode.equals("001001")) {
                return "课程图片";
            } else if (fileTypeCode.equals("001002")) {
                return "课程视频";
            } else if (fileTypeCode.equals("001003")) {
                return "课程音频";
            } else if (fileTypeCode.equals("001004")) {
                return "课程文档";
            } else {
                return "其他";
            }
        }

}
