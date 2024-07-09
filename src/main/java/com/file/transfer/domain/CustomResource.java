package com.file.transfer.domain;

import com.amazonaws.services.s3.model.S3Object;
import lombok.Getter;
import org.springframework.core.io.InputStreamResource;

public class CustomResource extends InputStreamResource {
    private final String filename;
    private final long contentLength;

    @Getter
    private final String contentType;

    public CustomResource(S3Object s3Object, String objectKey) {
        super(s3Object.getObjectContent());
        this.filename = objectKey;
        this.contentLength = s3Object.getObjectMetadata().getContentLength();
        this.contentType = s3Object.getObjectMetadata().getContentType();
    }

    @Override
    public String getDescription() {
        return filename;
    }

    @Override
    public long contentLength() {
        return this.contentLength;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }
}
