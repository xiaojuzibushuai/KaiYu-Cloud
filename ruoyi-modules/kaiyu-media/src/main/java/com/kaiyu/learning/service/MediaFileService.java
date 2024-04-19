package com.kaiyu.learning.service;

import com.kaiyu.learning.domain.MediaFiles;
import com.kaiyu.learning.domain.PageParams;
import com.kaiyu.learning.domain.PageResult;
import com.kaiyu.learning.domain.RestResponse;
import com.kaiyu.learning.domain.dto.QueryMediaParamsDto;
import com.kaiyu.learning.domain.dto.UploadFileParamsDto;
import com.kaiyu.learning.domain.dto.UploadFileResultDto;

import java.io.File;
import java.util.List;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-01 15:09
 **/
public interface MediaFileService {

    PageResult<MediaFiles> queryMediaFiles(PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    UploadFileResultDto uploadFile(UploadFileParamsDto uploadFileParamsDto,  String localFilePath,String folder, String objectName );

    /**
     * 将文件信息添加到文件表
     * @param uploadFileParamsDto 上传文件的信息
     * @param objectName 对象名称
     * @param fileMD5 文件md5码
     * @param bucket 桶
     * @param url  文件OSS的url，默认为空
     * @return
     */
    MediaFiles addMediaFilesToDB(UploadFileParamsDto uploadFileParamsDto, String objectName,
                                 String fileMD5, String bucket,String url);

    public String addMediaFilesToOSS(String localFilePath,String objectName,String bucket);


    /**
     * 文件上传前检查文件
     * xiaojuzi
     * @param fileMd5 文件的md5值
     * @return
     */
    RestResponse<Boolean> checkFile(String fileMd5);

    /**
     * 检查分块是否存在
     * @param fileMd5 文件的MD5值
     * @param chunkIndex 分块序号
     * @return
     */
    RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);

    /**
     * 上传分块
     * @param fileMd5 文件MD5
     * @param chunk 分块序号
     * @param localChunkFilePath 本地文件
     * @return
     */

    RestResponse uploadChunk(String fileMd5, int chunk,  String localChunkFilePath);


    /**
     * 合并分块
     * @param fileMd5
     * @param chunkTotal
     * @param uploadFileParamsDto
     * @return
     */
    RestResponse mergeChunks(String fileMd5,int chunkTotal,UploadFileParamsDto uploadFileParamsDto);


    /**
     * 从oss下载文件
     * @param file
     * @param bucket
     * @param objectName
     * @return
     */
    File downloadFileFromOSS(File file, String bucket, String objectName);


    MediaFiles getFileById(String mediaId);

}
