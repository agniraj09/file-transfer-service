package com.file.transfer.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "com.file.transfer.aws")
public class AWSS3Config {

    @NotBlank(message = "bucketName is missing")
    private String bucketName;

    @NotBlank(message = "accessKey is missing")
    private String accessKey;

    @NotBlank(message = "secretKey is missing")
    private String secretKey;

    @NotBlank(message = "endpointUrl is missing")
    private String endpointUrl;
}
