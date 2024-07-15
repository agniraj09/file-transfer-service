package com.file.transfer.service;

import com.amazonaws.util.IOUtils;
import com.file.transfer.domain.FTPConfig;
import lombok.RequiredArgsConstructor;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
@RequiredArgsConstructor
public class FTPFileService {
    private FTPClient ftpClient;
    private FTPConfig ftpConfig;


    public String uploadFileToFTP(String filepath, MultipartFile file) throws IOException {
        try {
            ftpClient = connectFTP(ftpConfig);
            ftpClient.changeWorkingDirectory(filepath);
            if (ftpClient.storeFile(file.getName(), new FileInputStream(convertMultipartFileToFile(file))))
                return "File upload to FTP is successful";
            else
                return "File upload to FTP is unsuccessful";
        }
        finally{
            if(ftpClient.isConnected())
                ftpClient.disconnect();
        }
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {

        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;

    }

    public Resource downloadFile(String filePath, String fileName) throws IOException {
        byte[] byteArray = null;
        try {

            ftpClient = connectFTP(ftpConfig);
            ftpClient.changeWorkingDirectory(filePath);
            InputStream file = ftpClient.retrieveFileStream(filePath+"/"+fileName);
            return new ByteArrayResource(IOUtils.toByteArray(file));
        } finally{
                if(ftpClient.isConnected())
                    ftpClient.disconnect();
            }
    }
    FTPClient connectFTP(FTPConfig ftpConfig) throws IOException {
        ftpClient = new FTPClient();
        ftpClient.connect(ftpConfig.getUrl(), Integer.getInteger(ftpConfig.getPort()));
        int reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
        }

        ftpClient.login(ftpConfig.getUser(), ftpConfig.getPassword());

        return ftpClient;
    }
}
