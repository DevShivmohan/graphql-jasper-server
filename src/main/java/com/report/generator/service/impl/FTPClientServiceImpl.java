package com.report.generator.service.impl;

import com.report.generator.service.FTPClientService;
import lombok.AllArgsConstructor;
import org.apache.commons.net.ftp.FTPClient;
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
}
