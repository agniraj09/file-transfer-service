package com.file.transfer.service;

import com.file.transfer.domain.FileMetadata;
import com.file.transfer.domain.FileUploadRequest;
import com.file.transfer.domain.FileUploadResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileTransferService {

    private final AWSFileService awsService;
    private final SFFileService sfFileService;

    public FileTransferService(AWSFileService awsService, SFFileService sfFileService) {
        this.awsService = awsService;
        this.sfFileService = sfFileService;
    }

    public List<FileUploadResponse> uploadFiles(FileUploadRequest request) {
        List<FileUploadResponse> responseList = new ArrayList<>();
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();

        request.fileMetadata().forEach(fileMetadata -> {
            futures.add(CompletableFuture.supplyAsync(() -> uploadSingleFile(request.folderName(), fileMetadata))
                    .thenApply(responseList::add));
        });

        futures.forEach(CompletableFuture::join);

        return responseList;
    }

    public FileUploadResponse uploadSingleFile(String folderName, FileMetadata metadata) {
        FileUploadResponse response = null;

        try {
            // downloadFileFromSF
            MultipartFile file = sfFileService.downloadFileFromSF(metadata);

            // uploadFileToS3
            if (null != file) {
                var awsResponse = awsService.uploadFile(metadata.fileName(), folderName, file);

                // TODO Insert metadata to Postgres

                response = new FileUploadResponse(metadata.sfFileId(), UUID.randomUUID().toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    public String downloadFile(String fileId) throws IOException {
        // TODO Fetch folder name & file name using fileId
        String folderName = "sample", fileName = "sample.txt", contentType = "text/plain";

        String objectKey = folderName + "/" + fileName;
        // Download file from S3
        var resource = awsService.downloadFile(objectKey);
        // Upload to SF
        return sfFileService.uploadFileToSF(fileName, resource, contentType);
    }
}
