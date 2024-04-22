package com.kaiyu.media.controller;

import com.kaiyu.media.domain.RestResponse;
import com.kaiyu.media.domain.dto.UploadFileParamsDto;
import com.kaiyu.media.service.MediaFileService;
import com.ruoyi.common.core.exception.KaiYuEducationException;
import com.ruoyi.common.core.utils.uuid.UUID;
import com.ruoyi.common.security.annotation.Logical;
import com.ruoyi.common.security.annotation.RequiresRoles;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @program: kai-yu-cloud
 * @description: 大文件上传
 * @author: xiaojuzi
 * @create: 2024-04-01 16:08
 **/
@RestController
@RequestMapping("/media/mediaFiles")
@Api(value = "大文件上传接口",tags = "大文件上传接口")
public class BigFilesController {

    @Autowired
    private MediaFileService mediaFileService;

    @ApiOperation(value = "文件上传前检查文件")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkFile(@RequestParam("fileMd5") String fileMd5) {
        return mediaFileService.checkFile(fileMd5);
    }

    @ApiOperation(value = "分块文件上传前检查分块")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkChunk(@RequestParam("fileMd5") String fileMd5, @RequestParam("chunk") int chunk) {
        return mediaFileService.checkChunk(fileMd5, chunk);
    }

    @ApiOperation(value = "上传分块文件")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @PostMapping("/upload/uploadchunk")
    public RestResponse uploadChunk(@RequestParam("file") MultipartFile file, @RequestParam("fileMd5") String fileMd5, @RequestParam("chunk") int chunk) {

        try {
            //创建临时文件
            File tempFile = File.createTempFile("oss-"+ UUID.randomUUID(), "temp");
            //JVM退出时删除临时文件
            tempFile.deleteOnExit();
            //上传的文件拷贝到临时文件
            file.transferTo(tempFile);
            //文件路径
            String absolutePath = tempFile.getAbsolutePath();
            RestResponse restResponse = mediaFileService.uploadChunk(fileMd5, chunk, absolutePath);

            return restResponse;

        } catch (IOException e) {
            KaiYuEducationException.cast("上传文件过程出错:" + e.getMessage());
        }
        return RestResponse.validfail( "上传分块失败",false);
    }

    @ApiOperation(value = "合并分块文件")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @PostMapping("/upload/mergechunks")
    public RestResponse mergeChunks(@RequestParam("fileMd5") String fileMd5, @RequestParam("fileName") String fileName,
                                    @ApiParam("文件备注(清晰度)") @RequestParam("remark") String remark,
                                    @RequestParam("chunkTotal") int chunkTotal){

        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFileType("001002");
        uploadFileParamsDto.setTags("课程视频");
        uploadFileParamsDto.setRemark(remark);
        uploadFileParamsDto.setFilename(fileName);

        return mediaFileService.mergeChunks(fileMd5, chunkTotal, uploadFileParamsDto);
    }

    
}
