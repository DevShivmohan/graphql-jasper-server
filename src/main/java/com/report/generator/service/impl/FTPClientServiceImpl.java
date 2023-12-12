package com.report.generator.service.impl;

import com.report.generator.service.FTPClientService;
import lombok.AllArgsConstructor;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@AllArgsConstructor
public class FTPClientServiceImpl implements FTPClientService {
    private final FTPClient ftpClient;
    @Override
    public boolean saveFile(String remote, InputStream local) throws IOException {
        return ftpClient.storeFile(remote,local);
    }

    @Override
    public boolean makeDirectory(String pathname) throws IOException {
        return ftpClient.makeDirectory(pathname);
    }

    @Override
    public FTPFile[] listFiles(String pathname) throws IOException {
        return ftpClient.listFiles(pathname);
    }

    @Override
    public byte[] retrieveFileBytesFromStream(String reportFile) throws Throwable {
        try (final InputStream inputStream=ftpClient.retrieveFileStream(reportFile)){
            if(inputStream==null)
                throw new Throwable("Report not found");
            final var bytes=inputStream.readAllBytes();
            inputStream.close();
            return bytes;
        } finally {
            ftpClient.completePendingCommand();
        }
    }
}
