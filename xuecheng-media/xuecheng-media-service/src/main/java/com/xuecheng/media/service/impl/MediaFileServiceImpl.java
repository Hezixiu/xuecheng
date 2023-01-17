package com.xuecheng.media.service.impl;

import com.alibaba.nacos.common.http.param.MediaType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.exception.XueChengException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.minio.*;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@Slf4j
@Service
public class MediaFileServiceImpl implements MediaFileService {

    @Autowired
    MediaFilesMapper mediaFilesMapper;
    @Autowired
    MinioClient minioClient;

    @Autowired
    MediaFileService mediaFileServiceProxy;

//    从配置文件中获取桶的名称
    @Value("${minio.bucket.files}")
    private String bucketName;
    @Value("${minio.bucket.videofiles}")
    private String bucketName_video;
    @Override
    public PageResult<MediaFiles> queryMediaFiles(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();
//        进行模糊查询
        queryWrapper.like(MediaFiles::getFileType,queryMediaParamsDto.getFileType());
        queryWrapper.like(MediaFiles::getFilename,queryMediaParamsDto.getFilename());
        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());

        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        PageResult<MediaFiles> mediaListResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
        return mediaListResult;

    }

    @Override
    public UploadFileResultDto uploadFile(Long companyId, byte[] fileBytes, UploadFileParamsDto uploadFileParamsDto, String folderPath,String objectName) {

        try {
            String md5ID = DigestUtils.md5Hex(fileBytes);
            String fileName = uploadFileParamsDto.getFilename();
            if (folderPath==null||StringUtils.isEmpty(folderPath.trim())){
    //            如果传入的是空或者只有空格
    //            自动生成目录
                folderPath = getFileFolder(new Date(), true, true, true);
    //            如果目录里面没有/那么就自动补一个/
            } else if (!folderPath.contains("/")) {
                folderPath +="/";
            }
//        如果objectName也传入的是空或者只有空格那么自动生成
            if (objectName==null||objectName.trim().isEmpty()){
                objectName= md5ID+fileName.substring(fileName.lastIndexOf("."));
            }
            objectName = folderPath+objectName;

            addMediaFilesToMinio(fileBytes,bucketName,objectName);

            MediaFiles mediaFiles = mediaFileServiceProxy.addMediaFilesToDB(companyId, md5ID, uploadFileParamsDto, bucketName, objectName);
            UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
            BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);

            return uploadFileResultDto;
        } catch (Exception e) {
            e.printStackTrace();

            XueChengException.cast("上传文件失败");
        }
        return new UploadFileResultDto();
    }
//   将文件上传到分布式文件系统
    private void addMediaFilesToMinio(byte[] bytes, String bucketName,String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
//        资源的媒体类型
        String contentType = MediaType.APPLICATION_OCTET_STREAM; //默认未知的二级制流类型
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes, 0, bytes.length - 1);
        if (objectName.contains(".")) {
            String extension = objectName.substring(objectName.lastIndexOf("."));
            ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
            if (extensionMatch!=null){
                contentType=extensionMatch.getMimeType();
            }
        }

        //          上传到minio
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .contentType(contentType)
                .stream(byteArrayInputStream,byteArrayInputStream.available(),-1)  //分片大小 最小5M
                .build();
        minioClient.putObject(putObjectArgs);
        byteArrayInputStream.close();
    }
    //   将文件上传到分布式文件系统
    private void addMediaFilesToMinio(String filePath, String bucketName,String objectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        try {
            //          上传到minio
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .filename(filePath)
                    .build();
            minioClient.uploadObject(uploadObjectArgs);
            log.debug("文件上传成功：{}",filePath);
        } catch (Exception e) {
            XueChengException.cast("文件上传到文件系统失败");
        }
    }



    //    将文件对象存入DB
    @Transactional
    public   MediaFiles addMediaFilesToDB(Long companyId,String fileId,UploadFileParamsDto uploadFileParamsDto,String bucketName,String objectName){
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
        if (mediaFiles == null){
            mediaFiles = new MediaFiles();
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setFileId(fileId);
            mediaFiles.setId(fileId);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setBucket(bucketName);
            mediaFiles.setFilePath(objectName);
            mediaFiles.setUrl("/"+bucketName+"/"+objectName);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setStatus("1");
            mediaFiles.setAuditStatus("002003");
            mediaFilesMapper.insert(mediaFiles);
        }
        return mediaFiles;

    }
    //根据日期拼接目录
    private String getFileFolder(Date date, boolean year, boolean month, boolean day){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //获取当前日期字符串
        String dateString = sdf.format(new Date());
        //取出年、月、日
        String[] dateStringArray = dateString.split("-");
        StringBuffer folderString = new StringBuffer();
        if(year){
            folderString.append(dateStringArray[0]);
            folderString.append("/");
        }
        if(month){
            folderString.append(dateStringArray[1]);
            folderString.append("/");
        }
        if(day){
            folderString.append(dateStringArray[2]);
            folderString.append("/");
        }
        return folderString.toString();
    }



    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
//        在数据库中存在 并且在文件系统中也存在 此文件才存在
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles==null){
            return RestResponse.success(false);
        }
        GetObjectArgs args = GetObjectArgs.builder().bucket(mediaFiles.getBucket()).object(mediaFiles.getFilePath()).build();
        try {
            InputStream inputStream = minioClient.getObject(args);
            if (inputStream==null){
                return RestResponse.success(false);
            }
        } catch (Exception e) {
            return RestResponse.success(false);

        }

        return RestResponse.success(true);
    }

    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
//        得到分块文件在文件系统的所在的目录
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        String chunkFilePath = chunkFileFolderPath+chunkIndex;
        //        在数据库中存在 并且在文件系统中也存在 此文件才存在
        GetObjectArgs args = GetObjectArgs.builder().bucket(bucketName_video).object(chunkFilePath).build();
        try {
            InputStream inputStream = minioClient.getObject(args);
            if (inputStream==null){
                return RestResponse.success(false);
            }
        } catch (Exception e) {
            return RestResponse.success(false);

        }
        return RestResponse.success(true);
    }
    //得到分块文件的目录
    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + "chunk" + "/";
    }
//    得到合并文件的目录
    private String getMergeFilePath(String fileMd5,String fileExt) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5+fileExt;
    }
    @Override
    public RestResponse uploadChunk(String fileMd5, int chunk, byte[] bytes) {


        //得到分块文件的目录路径
        String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
        //得到分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunk;

        try {
            //将文件存储至minIO
            addMediaFilesToMinio(bytes, bucketName_video,chunkFilePath);

        } catch (Exception ex) {
            ex.printStackTrace();
            XueChengException.cast("上传过程出错请重试");
        }
        return RestResponse.success();
    }
    /**
     * @Author Linzkr
     * @Description
     * @Date 2023/1/17 13:55
     * @param companyId
	 * @param fileMd5
	 * @param chunkTotal
	 * @param uploadFileParamsDto
     * @return
     */
    @Override
    public RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto) throws IOException {
//        下载分块
        File[] files = checkChunkStatus(fileMd5, chunkTotal);
//         得到合并后文件的扩展名
        String filename= uploadFileParamsDto.getFilename();
        String extension = filename.substring(filename.lastIndexOf("."));
//        创建一个临时文件作为合并文件
        File tempMergeFile = File.createTempFile("chunk", extension);

//        合并分块
//        创建流对象
        RandomAccessFile writeFileStream = new RandomAccessFile(tempMergeFile, "rw");

        try {
            byte[] bytes = new byte[1024];
            for (File file : files) {
    //            创建读取流
                RandomAccessFile readFileStream = new RandomAccessFile(file,"r");
                try {
                    int len =-1;
                    while ((len= readFileStream.read(bytes))!=-1){
                        writeFileStream.write(bytes,0,len);
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }finally {
                    readFileStream.close();
                }
            }
            writeFileStream.close();
//            try {
//                FileInputStream mergeFileStream = new FileInputStream(tempMergeFile);
//    //        校验合并后的文件是否正确
//
//                String mergeMd5Hex = DigestUtils.md5Hex(mergeFileStream);
//                if (fileMd5.equals(mergeMd5Hex)){
//                    System.out.println("合并成功");
//                }else {
//                    log.debug("合并文件校验不通过：{}"+tempMergeFile.getAbsolutePath());
//                    XueChengException.cast("合并文件校验不通过");
//                }
//                mergeFileStream.close();
//            } catch (IOException e) {
//                XueChengException.cast("合并文件出错");
//            }
//        将合并 的文件存入分布式文件系统
//        拿到合并文件在minio的存储路径
            String mergeFilePath = getMergeFilePath(fileMd5, extension);
            try {
                addMediaFilesToMinio(tempMergeFile.getAbsolutePath(), bucketName_video, mergeFilePath);
            } catch (Exception e) {
                XueChengException.cast("合并文件添加到文件系统失败");
            }
            uploadFileParamsDto.setFileSize(tempMergeFile.length());
            addMediaFilesToDB(companyId, fileMd5, uploadFileParamsDto, bucketName_video, mergeFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if (files!=null){
                for (File file : files) {
                    if (file.exists()){
                        file.delete();
                    }
                }
            }
//            删除合并的临时文件
            if (tempMergeFile!=null) {
                tempMergeFile.delete();
            }
        }

        return RestResponse.success(true);
    }

    /**
     * @Author Linzkr
     * @Descpription 下载分块
     * @Date 2023/1/17 13:57
     * @param fileMd5  文件MD5
	 * @param chunkTotal 分块数量
     * @return 文件数组
     */
    private File[] checkChunkStatus(String fileMd5,int chunkTotal) {
        try {
//        得到分块文件所在的目录
            String chunkFileFolderPath = getChunkFileFolderPath(fileMd5);
            File[] chunkFiles = new File[chunkTotal];
//            分块文件的数组
            for (int i = 0; i < chunkTotal; i++) {
                File chunkFile = File.createTempFile("chunk", null);
                String objectName = chunkFileFolderPath+i;
                chunkFile = downloadFileFromMinIO(chunkFile, bucketName_video, objectName);
                chunkFiles[i]=chunkFile;
            }
            return chunkFiles;
        } catch (IOException e) {
            XueChengException.cast("检查分块失败");
        }
        return null;
    }

    public File downloadFileFromMinIO(File file, String bucketName, String objectName) {

            GetObjectArgs args = GetObjectArgs.builder().bucket(bucketName).object(objectName).build();
//                从文件系统中获取到了分块的数据
        try(InputStream inputStream = minioClient.getObject(args);
            FileOutputStream fileOutputStream = new FileOutputStream(file) ;) {
            IOUtils.copy(inputStream,fileOutputStream);
            return file;
        } catch (Exception e) {
            XueChengException.cast("下载分块文件出错");
        }
        return null;

    }
}
