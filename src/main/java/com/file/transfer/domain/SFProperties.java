package com.file.transfer.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "com.file.transfer.sf")
public class SFProperties {

    // URLs
    @NotBlank(message = "login URL is missing")
    private String loginURL;

    @NotBlank(message = "file download URL is missing")
    private String downloadURL;

    // Auth Token Properties
    @NotBlank(message = "username is missing")
    private String username;

    @NotBlank(message = "password is missing")
    private String password;

    @NotBlank(message = "clientId is missing")
    private String clientId;

    @NotBlank(message = "clientSecret is missing")
    private String clientSecret;

    @NotBlank(message = "grantType is missing")
    private String grantType;
}
