package com.file.transfer.domain;

import java.io.*;
import org.springframework.web.multipart.MultipartFile;

public class CustomMultipartFile implements MultipartFile {

    private byte[] input;
    private String fileName;
    private String contentType;

    public CustomMultipartFile(byte[] input, String fileName, String contentType) {
        this.input = input;
        this.fileName = fileName;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return this.fileName;
    }

    @Override
    public String getOriginalFilename() {
        return this.fileName;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public boolean isEmpty() {
        return this.input == null || this.input.length == 0;
    }

    @Override
    public long getSize() {
        return this.input.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return input;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(input);
    }

    @Override
    public void transferTo(File destination) throws IOException, IllegalStateException {
        try (FileOutputStream fos = new FileOutputStream(destination)) {
            fos.write(input);
        }
    }
}
