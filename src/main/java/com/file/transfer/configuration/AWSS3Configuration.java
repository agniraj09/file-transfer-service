package com.file.transfer.configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.file.transfer.domain.AWSS3Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSS3Configuration {

    @Bean
    AmazonS3 s3client(AWSS3Config awsS3Config) {
        AWSCredentials credentials = new BasicAWSCredentials(awsS3Config.getAccessKey(), awsS3Config.getSecretKey());
        return new AmazonS3Client(credentials);
    }
}
