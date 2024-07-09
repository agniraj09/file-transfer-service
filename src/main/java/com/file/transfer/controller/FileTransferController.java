package com.file.transfer.controller;

import com.file.transfer.domain.FileUploadRequest;
import com.file.transfer.domain.FileUploadResponse;
import com.file.transfer.service.FileTransferService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileTransferController {

    @Autowired
    FileTransferService fileTransferService;

    @PostMapping("/upload")
    ResponseEntity<List<FileUploadResponse>> uploadFile(@RequestBody FileUploadRequest request) {
        return ResponseEntity.ok(fileTransferService.uploadFiles(request));
    }
}
