package com.file.transfer.service;

import com.file.transfer.domain.CustomMultipartFile;
import com.file.transfer.domain.FileMetadata;
import com.file.transfer.domain.SFProperties;
import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

        return new CustomMultipartFile(resource.getContentAsByteArray(), metadata.fileName(), metadata.contentType());
    }

    public String uploadFileToSF(String fileName, Resource resource, String contentType) throws IOException {
        var request = buildFileUploadToSFRequest(fileName, resource);

        var response = restClient
                .post()
                .uri(sfProperties.getUploadURL())
                .header("Authorization", authTokenService.getBearerTokenWithBearerAppended())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(String.class)
                .getBody();
        return response;
    }

    private MultiValueMap<String, Object> buildFileUploadToSFRequest(String fileName, Resource file) {
        MultiValueMap<String, Object> request = new LinkedMultiValueMap<>();

        HttpHeaders fileHeaders = new HttpHeaders();
        fileHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        request.add("VersionData", new HttpEntity<>(file, fileHeaders));

        String json = "{\"PathOnClient\" : \"" + fileName + "\"}";
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        request.add("entity_content", new HttpEntity<>(json, jsonHeaders));

        return request;
    }

    private MultiValueMap<String, String> multipartHeader() {
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        return jsonHeaders;
    }

    private HttpHeaders jsonHeader() {
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        return jsonHeaders;
    }
}
