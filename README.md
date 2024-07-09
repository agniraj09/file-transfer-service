# file-transfer-service

Swagger URL
---
### http://localhost:8080/file-transfer-service/swagger-ui/index.html

File Upload Request
---
>URL : POST http://localhost:8080/file-transfer-service/file/upload

```
{
    "folderName": "sample",
    "fileMetadata": [
        {
            "fileName": "sample.txt",
            "sfFileId": "068dL000002gfLlQAI",
            "contentType": "text/plain"
        }
    ],
    "uploadedByUserID": "agni"
}
```
File Download Request
---
>URL : GET http://localhost:8080/file-transfer-service/file/download?fileId=132

