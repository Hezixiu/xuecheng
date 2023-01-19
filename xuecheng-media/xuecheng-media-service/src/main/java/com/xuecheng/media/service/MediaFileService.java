package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @description 媒资文件管理业务类
 * @author Mr.M
 * @date 2022/9/10 8:55
 * @version 1.0
 */
public interface MediaFileService {


 public PageResult<MediaFiles> queryMediaFiles(Long companyId,PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);


     /**
      * //        上传文件的通用的接口
      * @param companyId  机构ID
      * @param fileBytes  上传的文件的字节数据
      * @param uploadFileParamsDto  文件信息
      * @param folderPath  桶中的目录
      * @param objectName  对象名称
      * @return  返回一个结果DTO
      */
     UploadFileResultDto uploadFile(Long companyId,byte[] fileBytes , UploadFileParamsDto uploadFileParamsDto,String folderPath,String objectName);
     MediaFiles addMediaFilesToDB(Long companyId,String fileId,UploadFileParamsDto uploadFileParamsDto,String bucketName,String objectName);
    void addMediaFilesToMinIO(String filePath, String bucket, String objectName);
//    检查文件是否存在
    public RestResponse<Boolean> checkFile(String fileMd5);
//    检查分块是否存在
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);
    public RestResponse uploadChunk(String fileMd5,int chunk,byte[] bytes);


    RestResponse mergechunks(Long companyId,String fileMd5,int chunkTotal,UploadFileParamsDto uploadFileParamsDto) throws IOException;

    public MediaFiles getFileById(String id );
    File downloadFileFromMinIO(File file, String bucket, String objectName);
}
