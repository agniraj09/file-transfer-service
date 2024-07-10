package com.file.transfer.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.file.transfer.constants.AppConstants;
import com.file.transfer.domain.AWSS3Config;
import com.file.transfer.domain.CustomResource;
import com.file.transfer.exception.FileNotFoundException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class AWSFileService {

    private final AmazonS3 s3client;
    private final ObjectMapper mapper;
    private final AWSS3Config awsS3Config;

    public AWSFileService(AmazonS3 s3client, ObjectMapper mapper, AWSS3Config awsS3Config) {
        this.s3client = s3client;
        this.mapper = mapper;
        this.awsS3Config = awsS3Config;
    }

    public void uploadFile(String fileName, String folderName, MultipartFile file) throws IOException {
        var putObjectRequest = buildPutObjectRequest(fileName, folderName, file);
        var putObjectResult = s3client.putObject(putObjectRequest);
        log.info("File upload successful. VersionID -> {}", putObjectResult.getVersionId());
    }

    private PutObjectRequest buildPutObjectRequest(String fileName, String folderName, MultipartFile file)
            throws IOException {
        // Metadata builder
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        // File name builder
        fileName = folderName + "/" + fileName;

        return new PutObjectRequest(awsS3Config.getBucketName(), fileName, file.getInputStream(), metadata);
    }

    public Resource downloadFile(String objectKey) {
        if (s3client.doesObjectExist(awsS3Config.getBucketName(), objectKey)) {
            GetObjectRequest getObjectRequest = new GetObjectRequest(awsS3Config.getBucketName(), objectKey);
            S3Object s3Object = s3client.getObject(getObjectRequest);
            return new CustomResource(s3Object, objectKey);
        } else {
            throw new FileNotFoundException(AppConstants.FILE_NOT_FOUND);
        }
    }
}
