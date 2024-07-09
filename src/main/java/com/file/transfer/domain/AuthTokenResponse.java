package com.file.transfer.domain;

import com.fasterxml.jackson.annotation.JsonAlias;

public record AuthTokenResponse(@JsonAlias("access_token") String accessToken, @JsonAlias("issued_at") Long issuedAt) {}
