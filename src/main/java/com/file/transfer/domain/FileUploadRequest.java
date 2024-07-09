package com.file.transfer.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record FileUploadRequest(
        @NotBlank(message = "folderName cannot be blank") String folderName,
        @NotEmpty(message = "fileMetadata cannot be empty") List<FileMetadata> fileMetadata,
        @NotBlank(message = "uploadedByUserID cannot be blank") String uploadedByUserID) {}
