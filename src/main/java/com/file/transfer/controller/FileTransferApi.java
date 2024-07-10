package com.file.transfer.controller;

import com.file.transfer.domain.FileUploadRequest;
import com.file.transfer.domain.FileUploadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface FileTransferApi {

    @Operation(
            summary = "Uploads files to file storage system",
            description =
                    """
                            <pre>
                            {
                                 "folderName": "payroll-files",
                                 "fileInfo": [
                                     {
                                         "fileName": "March-2020-payrolls.txt",
                                         "sfFileId": "068dL000002gfLlQAI",
                                         "contentType": "text/plain"
                                     }
                                 ],
                                 "uploadedByUserID": "jsteve"
                             }</pre>
                            """,
            tags = {"Upload"},
            responses = {@ApiResponse(responseCode = "200", description = "Returns the file ids from storage system")})
    ResponseEntity<List<FileUploadResponse>> uploadFiles(FileUploadRequest request);

    @Operation(
            summary = "Downloads file/files from file storage system",
            tags = {"Download"},
            responses = {@ApiResponse(responseCode = "200", description = "Fetches file file from storage system")})
    ResponseEntity<String> downloadFile(
            @Parameter(description = "Unique File ID(UUID)", example = "6c2c6280-bfd7-4b9a-a91e-383acff2f1fe")
                    UUID fileId);
}
