package com.file.transfer.service;

import com.file.transfer.constants.AppConstants;
import com.file.transfer.domain.FileInfo;
import com.file.transfer.domain.FileUploadRequest;
import com.file.transfer.domain.FileUploadResponse;
import com.file.transfer.domain.entity.FileMetadata;
import com.file.transfer.exception.FileNotFoundException;
import com.file.transfer.repository.FileMetadataRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
public class FileTransferService {

    private final AWSFileService awsService;
    private final SFFileService sfFileService;
    private final TransactionTemplate transactionTemplate;
    private final FileMetadataRepository fileMetadataRepository;

    public FileTransferService(
            AWSFileService awsService,
            SFFileService sfFileService,
            TransactionTemplate transactionTemplate,
            FileMetadataRepository fileMetadataRepository) {
        this.awsService = awsService;
        this.sfFileService = sfFileService;
        this.transactionTemplate = transactionTemplate;
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
                awsService.uploadFile(fileInfo.fileName(), folderName, file);

                var entity = transactionTemplate.execute(action -> saveFileMetadata(folderName, fileInfo));

                response = new FileUploadResponse(
                        fileInfo.sfFileId(), entity.getFileId().toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return response;
    }

    private FileMetadata saveFileMetadata(String folderName, FileInfo fileInfo) {
        return fileMetadataRepository.save(FileMetadata.builder()
                .sfFileId(fileInfo.sfFileId())
                .fileName(fileInfo.fileName())
                .folderName(folderName)
                .contentType(fileInfo.contentType())
                .createdTimestamp(LocalDateTime.now())
                .modifiedTimestamp(LocalDateTime.now())
                .build());
    }

    public String downloadFile(UUID fileId) {
        var file = fileMetadataRepository
                .findById(fileId)
                .orElseThrow(() -> new FileNotFoundException(AppConstants.FILE_NOT_FOUND));

        String objectKey = file.getFolderName() + "/" + file.getFileName();
        // Download file from S3
        var resource = awsService.downloadFile(objectKey);
        // Upload to SF
        return sfFileService.uploadFileToSF(file.getFileName(), resource);
    }
}
