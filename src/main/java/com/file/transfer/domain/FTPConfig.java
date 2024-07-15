package com.file.transfer.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "com.file.transfer.ftp")
public class FTPConfig {

    @NotBlank(message = "FTP user can not be blank")
    private String user;

    @NotBlank(message = "FTP password can not be blank")
    private String password;

    @NotBlank(message = "FTP url can not be blank")
    private String url;

    private String port;
}
