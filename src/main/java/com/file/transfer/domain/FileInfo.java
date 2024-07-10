package com.file.transfer.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@Valid
public record FileInfo(
        @NotBlank(message = "fileName cannot be blank") String fileName,
        @NotBlank(message = "sfFileId cannot be blank") String sfFileId,
        @NotBlank(message = "contentType cannot be blank") String contentType) {}
