package com.mole.health.util;

import cn.hutool.core.util.StrUtil;
import com.mole.health.properties.MinioProperties;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class MinioUtil {
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Autowired
    public MinioUtil(MinioClient minioClient, MinioProperties minioProperties) {
        this.minioClient = minioClient;
        this.minioProperties = minioProperties;
    }

    /**
     * 检查存储桶是否存在
     *
     * @param bucketName 存储桶名称
     * @return 存储桶是否存在
     * @throws Exception 异常
     */
    public boolean bucketExists(String bucketName) throws Exception {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    /**
     * 创建存储桶
     *
     * @param bucketName 存储桶名称
     * @throws Exception 异常
     */
    public void createBucket(String bucketName) throws Exception {
        if (!bucketExists(bucketName)) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("Bucket {} created successfully", bucketName);
        }
    }

    /**
     * 设置存储桶策略
     *
     * @param bucketName 存储桶名称
     * @throws Exception 异常
     */
    public void setBucketPolicy(String bucketName) throws Exception {
        String policy = minioProperties.getBucketPolicy().replace("your-bucket-name", bucketName);
        minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(policy).build());
        log.info("Bucket policy set for {}", bucketName);
    }

    /**
     * 上传文件到指定存储桶
     *
     * @param bucketName 存储桶名称
     * @param file       文件
     * @return 文件名
     * @throws Exception 异常
     */
    public String uploadFile(String bucketName, MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build());
        log.info("File {} uploaded successfully to bucket {}", fileName, bucketName);
        return fileName;
    }

    /**
     * 上传文件到指定存储桶并指定文件名
     *
     * @param bucketName 存储桶名称
     * @param fileName   文件名
     * @param file       文件
     * @return 文件名
     * @throws Exception 异常
     */
    public String uploadFileWithFileName(String bucketName, String fileName, MultipartFile file) throws Exception {
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType())
                .build());
        log.info("File {} uploaded successfully to bucket {}", fileName, bucketName);
        return fileName;
    }

    /**
     * 从指定存储桶下载文件
     *
     * @param bucketName 存储桶名称
     * @param fileName   文件名
     * @return 文件字节数组
     * @throws Exception 异常
     */
    public byte[] downloadFile(String bucketName, String fileName) throws Exception {
        InputStream stream = minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(fileName).build());
        return IOUtils.toByteArray(stream);
    }

    /**
     * 从指定存储桶删除文件
     *
     * @param bucketName 存储桶名称
     * @param fileName   文件名
     * @throws Exception 异常
     */
    public void deleteFile(String bucketName, String fileName) throws Exception {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
        log.info("File {} deleted successfully from bucket {}", fileName, bucketName);
    }

    /**
     * 获取文件的预签名 URL
     *
     * @param bucketName 存储桶名称
     * @param fileName   文件名
     * @return 预签名 URL
     * @throws Exception 异常
     */
    public String getFileUrl(String bucketName, String fileName) throws Exception {
        String internalUrl = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(fileName)
                .expiry(7, TimeUnit.DAYS)
                .build());

        return StrUtil.isNotBlank(minioProperties.getExternalEndpoint())
                ? StrUtil.replace(internalUrl, minioProperties.getEndpoint(), minioProperties.getExternalEndpoint())
                : internalUrl;
    }
}
