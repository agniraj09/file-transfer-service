package com.file.transfer.service;

import com.file.transfer.constants.AppConstants;
import com.file.transfer.domain.AuthTokenResponse;
import com.file.transfer.domain.SFProperties;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class SFAuthTokenService {

    private static LocalDateTime expiry_time = LocalDateTime.now();
    private static String bearerToken;

    private final SFProperties sfProperties;

    private final RestClient restClient;

    public SFAuthTokenService(SFProperties sfProperties, RestClient restClient) {
        this.sfProperties = sfProperties;
        this.restClient = restClient;
    }

    public String getBearerTokenWithBearerAppended() {
        return "Bearer " + getBearerToken();
    }

    public String getBearerToken() {
        return LocalDateTime.now().isBefore(expiry_time) ? bearerToken : generateNewToken();
    }

    private synchronized String generateNewToken() {
        MultiValueMap<String, Object> request = buildAuthTokenRequest();

        var response = restClient
                .post()
                .uri(sfProperties.getLoginURL())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(request)
                .retrieve()
                .toEntity(AuthTokenResponse.class)
                .getBody();

        // Token is valid for 60 minutes. To be on safer side, adding 50 minutes validity.
        var expiry = response.issuedAt() + AppConstants.VALIDITY;

        expiry_time = LocalDateTime.ofInstant(Instant.ofEpochMilli(expiry), ZoneId.systemDefault());
        bearerToken = response.accessToken();
        return bearerToken;
    }

    private MultiValueMap<String, Object> buildAuthTokenRequest() {
        MultiValueMap<String, Object> request = new LinkedMultiValueMap<>();
        request.add("username", sfProperties.getUsername());
        request.add("password", sfProperties.getPassword());
        request.add("client_id", sfProperties.getClientId());
        request.add("client_secret", sfProperties.getClientSecret());
        request.add("grant_type", sfProperties.getGrantType());
        return request;
    }
}
