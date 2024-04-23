package com.kaiyu.media.controller;

import com.kaiyu.media.domain.MediaFiles;
import com.kaiyu.media.domain.MediaProcess;
import com.kaiyu.media.domain.PageParams;
import com.kaiyu.media.domain.PageResult;
import com.kaiyu.media.domain.dto.QueryMediaParamsDto;
import com.kaiyu.media.domain.dto.UploadFileParamsDto;
import com.kaiyu.media.domain.dto.UploadFileResultDto;
import com.kaiyu.media.service.MediaFileProcessService;
import com.kaiyu.media.service.MediaFileService;
import com.kaiyu.media.util.FileTypeUtil;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.KaiYuEducationException;
import com.ruoyi.common.core.utils.uuid.UUID;
import com.ruoyi.common.security.annotation.Logical;
import com.ruoyi.common.security.annotation.RequiresRoles;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @program: kai-yu-cloud
 * @description: 媒资文件管理
 * @author: xiaojuzi
 * @create: 2024-04-01 15:04
 **/
@RestController
@RequestMapping("/media/mediaFiles")
@Api(value = "媒资文件管理接口",tags = "媒资文件管理接口")
public class MediaFilesController {

    @Autowired
    MediaFileService mediaFileService;

    @Autowired
    MediaFileProcessService mediaFileProcessService;


    @ApiOperation("媒资列表查询接口")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @PostMapping("/getMediaFiles")
    public PageResult<MediaFiles> getMediaFiles(@RequestParam(value = "pageNo", defaultValue = "1") Long pageNo,
                                                @RequestParam(value = "pageSize", defaultValue = "10") Long pageSize,
                                                @RequestBody QueryMediaParamsDto queryMediaParamsDto) {
        PageParams pageParams = new PageParams();
        if (pageNo >= 1) {
            pageParams.setPageNo(pageNo);
        }
        if (pageSize >= 1) {
            pageParams.setPageSize(pageSize);
        }

        return mediaFileService.queryMediaFiles(pageParams, queryMediaParamsDto);
    }

    @ApiOperation("媒资列表查询视频资源接口")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @PostMapping("/getMediaVideoFiles")
    public R<List<MediaFiles>> getMediaVideoFiles(@RequestBody QueryMediaParamsDto queryMediaParamsDto) {
        List<MediaFiles> mediaFiles = mediaFileService.queryMediaVideoFiles(queryMediaParamsDto);
        return R.ok(mediaFiles);
    }


    @ApiOperation("上传小文件")
    @RequiresRoles(value = {"admin"}, logical = Logical.OR)
    @PostMapping(value = "/upload/coursefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<UploadFileResultDto> upload(@RequestPart("filedata") MultipartFile upload,
                                         @ApiParam("上传的文件夹目录")@RequestParam(value = "folder", required = false) String folder,
                                         @ApiParam("对象名称") @RequestParam(value = "objectName", required = false) String objectName) {

        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFileSize(upload.getSize());
        String contentType = upload.getContentType();

        uploadFileParamsDto.setFileType(FileTypeUtil.getFileTypeCode(contentType));

        uploadFileParamsDto.setRemark("");
        uploadFileParamsDto.setFilename(upload.getOriginalFilename());
        uploadFileParamsDto.setContentType(contentType);

        try {
            // 方案一 加载到内存 更快 但是内存占用大 xiaojuzi
//            UploadFileResultDto uploadFileResultDto = mediaFileService.uploadFile(uploadFileParamsDto, upload.getBytes(), folder, objectName);

            //  方案二 直接写入磁盘 更慢 但是内存占用小 xiaojuzi
            //创建临时文件 并将上传的文件拷贝到临时文件
            File tempFile = File.createTempFile("oss-"+ UUID.randomUUID(), "temp");
            //JVM退出时删除临时文件
            tempFile.deleteOnExit();
            upload.transferTo(tempFile);
            String absolutePath = tempFile.getAbsolutePath();
            //上传文件
            UploadFileResultDto uploadFileResultDto = mediaFileService.uploadFile(uploadFileParamsDto, absolutePath, folder , objectName);

            if (uploadFileResultDto == null) {
                return R.fail("上传文件重复或网络错误");
            }

            return R.ok(uploadFileResultDto);

        } catch (IOException e) {
            KaiYuEducationException.cast("上传文件过程出错:" + e.getMessage());
        }
        return R.fail("上传文件过程出错");
    }


    @ApiOperation("查询媒资详情接口")
    @RequiresRoles(value = {"admin", "common"}, logical = Logical.OR)
    @PostMapping("/getMediaFilesDetail")
    public R<MediaFiles> getMediaFilesDetail(@RequestParam("mediaId") String mediaId) {
        MediaFiles mediaFiles = mediaFileService.getFileById(mediaId);
        return R.ok(mediaFiles);
    }

    @ApiOperation("查询媒资视频处理状态接口")
    @RequiresRoles(value = {"admin", "common"}, logical = Logical.OR)
    @PostMapping("/getMediaFilesStatus")
    public R<List<MediaProcess>> getMediaFilesStatus(@RequestParam("mediaId") String mediaId) {
        List<MediaProcess> mediaProcess = mediaFileProcessService.getMediaFilesStatus(mediaId);
        return R.ok(mediaProcess);
    }

}
