package com.xuecheng.media;

import io.minio.*;
import io.minio.errors.*;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Linzkr
 * @description: TODO  测试Minio的上传功能
 * @date 2023/1/16 9:58
 */
public class FileUploader {
    // 创建minio的客户端对象
    static MinioClient minioClient = MinioClient.builder()
                    .endpoint("http://127.0.0.1:9005")
                    .credentials("minioadmin", "minioadmin")
                    .build();
    @Test
    public  void testUpload()  {

        try{

            // 判断bucket1存在不存在
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket("bucket1").build());
            if (!found) {
                // 如果不存在 那就创建这么一个桶
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("bucket1").build());
            } else {
                System.out.println("Bucket 'bucket1' already exists.");
            }
            // 上传文件
            // 'asiatrip'.
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket("bucket1")
                            .object("dir/testUpload01.png") //同一个桶内对象名不能重复  如果有相同的会覆盖
                            .filename("C:\\Users\\Linzkr\\OneDrive\\图片\\财务管理.png")
                            .build());
            System.out.println("上传成功");
        } catch(MinioException e){
            System.out.println("上传失败");
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
//    删除文件
    @Test
    public void delete() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        RemoveObjectArgs args = RemoveObjectArgs.builder().bucket("bucket1").object("dir/testUpload01.png").build();
        minioClient.removeObject(args);
    }
//    查询文件
    @Test
    public void getFile() throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        GetObjectArgs args = GetObjectArgs.builder().bucket("bucket1").object("报名照片.jpg").build();
        GetObjectResponse fileInputStream = minioClient.getObject(args);
        FileOutputStream fileOutputStream = new FileOutputStream("D:\\minioOutPut.jpg");
        IOUtils.copy(fileInputStream, fileOutputStream);

    }


}
