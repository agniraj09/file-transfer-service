package com.file.transfer.controller;

import com.file.transfer.domain.FileUploadRequest;
import com.file.transfer.domain.FileUploadResponse;
import com.file.transfer.service.FileTransferService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/file")
public class FileTransferController {

    @Autowired
    FileTransferService fileTransferService;

    @PostMapping("/upload")
    ResponseEntity<List<FileUploadResponse>> uploadFiles(@RequestBody @Valid FileUploadRequest request) {
        return ResponseEntity.ok(fileTransferService.uploadFiles(request));
    }

    @GetMapping("/download")
    ResponseEntity<String> downloadFile(@RequestParam("fileId") String fileId) throws IOException {
        return ResponseEntity.ok(fileTransferService.downloadFile(fileId));
    }
}
