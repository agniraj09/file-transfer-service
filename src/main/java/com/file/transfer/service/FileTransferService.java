package com.file.transfer.service;

import com.file.transfer.domain.FileInfo;
import com.file.transfer.domain.FileUploadRequest;
import com.file.transfer.domain.FileUploadResponse;
import com.file.transfer.domain.entity.FileMetadata;
import com.file.transfer.repository.FileMetadataRepository;
import java.time.LocalDateTime;
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
    private final FileMetadataRepository fileMetadataRepository;

    public FileTransferService(
            AWSFileService awsService, SFFileService sfFileService, FileMetadataRepository fileMetadataRepository) {
        this.awsService = awsService;
        this.sfFileService = sfFileService;
        this.fileMetadataRepository = fileMetadataRepository;
    }

    public List<FileUploadResponse> uploadFiles(FileUploadRequest request) {
        List<FileUploadResponse> responseList = new ArrayList<>();
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();

        request.fileInfo().forEach(fileInfo -> {
            futures.add(CompletableFuture.supplyAsync(() -> uploadSingleFile(request.folderName(), fileInfo))
                    .thenApply(responseList::add));
        });

        futures.forEach(CompletableFuture::join);

        return responseList;
    }

    public FileUploadResponse uploadSingleFile(String folderName, FileInfo fileInfo) {
        FileUploadResponse response = null;

        try {
            // downloadFileFromSF
            MultipartFile file = sfFileService.downloadFileFromSF(fileInfo);

            // uploadFileToS3
            if (null != file) {
                var awsResponse = awsService.uploadFile(fileInfo.fileName(), folderName, file);

                var entity = fileMetadataRepository.save(FileMetadata.builder()
                        .sfFileId(fileInfo.sfFileId())
                        .fileName(fileInfo.fileName())
                        .folderName(folderName)
                        .contentType(fileInfo.contentType())
                        .createdTimestamp(LocalDateTime.now())
                        .modifiedTimestamp(LocalDateTime.now())
                        .build());

                response = new FileUploadResponse(
                        fileInfo.sfFileId(), entity.getFileId().toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    public String downloadFile(String fileId) {
        var file = fileMetadataRepository.findById(UUID.fromString(fileId)).get();

        String objectKey = file.getFolderName() + "/" + file.getFileName();
        // Download file from S3
        var resource = awsService.downloadFile(objectKey);
        // Upload to SF
        return sfFileService.uploadFileToSF(file.getFileName(), resource);
    }
}
