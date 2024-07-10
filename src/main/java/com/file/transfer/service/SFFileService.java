package com.file.transfer.service;

import com.file.transfer.constants.AppConstants;
import com.file.transfer.domain.CustomMultipartFile;
import com.file.transfer.domain.FileInfo;
import com.file.transfer.domain.SFProperties;
import com.file.transfer.exception.FileNotFoundException;
import com.file.transfer.utility.StringUtility;
import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

    public MultipartFile downloadFileFromSF(FileInfo metadata) throws IOException {
        String downloadURL = sfProperties.getDownloadURL() + metadata.sfFileId() + "/VersionData";

        var resource = restClient
                .get()
                .uri(downloadURL)
                .header("Authorization", authTokenService.getBearerTokenWithBearerAppended())
                .retrieve()
                .onStatus(status -> HttpStatus.NOT_FOUND == status, (req, res) -> {
                    throw new FileNotFoundException(AppConstants.FILE_NOT_FOUND);
                })
                .onStatus(status -> HttpStatus.INTERNAL_SERVER_ERROR == status, (req, res) -> {
                    throw new RuntimeException(StringUtility.streamToString(res.getBody()));
                })
                .toEntity(Resource.class)
                .getBody();

        return new CustomMultipartFile(resource.getContentAsByteArray(), metadata.fileName(), metadata.contentType());
    }

    public String uploadFileToSF(String fileName, Resource resource) {
        var request = buildFileUploadToSFRequest(fileName, resource);

        return restClient
                .post()
                .uri(sfProperties.getUploadURL())
                .header("Authorization", authTokenService.getBearerTokenWithBearerAppended())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(status -> HttpStatus.NOT_FOUND == status, (req, res) -> {
                    throw new FileNotFoundException(AppConstants.FILE_NOT_FOUND);
                })
                .onStatus(status -> HttpStatus.INTERNAL_SERVER_ERROR == status, (req, res) -> {
                    throw new RuntimeException(StringUtility.streamToString(res.getBody()));
                })
                .toEntity(String.class)
                .getBody();
    }

    private MultiValueMap<String, Object> buildFileUploadToSFRequest(String fileName, Resource file) {
        MultiValueMap<String, Object> request = new LinkedMultiValueMap<>();

        request.add("VersionData", new HttpEntity<>(file, multipartHeader()));

        String json = "{\"PathOnClient\" : \"" + fileName + "\"}";
        request.add("entity_content", new HttpEntity<>(json, jsonHeader()));

        return request;
    }

    private MultiValueMap<String, String> multipartHeader() {
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return jsonHeaders;
    }

    private HttpHeaders jsonHeader() {
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        return jsonHeaders;
    }
}
