package com.file.transfer.service;

import com.file.transfer.domain.CustomMultipartFile;
import com.file.transfer.domain.FileMetadata;
import java.io.IOException;

import com.file.transfer.domain.SFProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SFFileService {

    private final RestClient restClient;
    private final SFProperties sfProperties;

    private final SFAuthTokenService authTokenService;

    public SFFileService(RestClient restClient, SFProperties sfProperties, SFAuthTokenService authTokenService) {
        this.restClient = restClient;
        this.sfProperties = sfProperties;
        this.authTokenService = authTokenService;
    }

    public MultipartFile downloadFileFromSF(FileMetadata metadata) throws IOException {
        String downloadURL = sfProperties.getDownloadURL() + metadata.sfFileId() + "/VersionData";

        var resource = restClient
                .get()
                .uri(downloadURL)
                .header("Authorization", authTokenService.getBearerTokenWithBearerAppended())
                .retrieve()
                .body(Resource.class);

        var multipartFile =
                new CustomMultipartFile(resource.getContentAsByteArray(), metadata.fileName(), metadata.contentType());
        return multipartFile;
    }
}
