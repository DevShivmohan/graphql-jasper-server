package com.report.generator.service;

import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.io.InputStream;

public interface FTPClientService {
    boolean saveFile(String remote, InputStream local) throws IOException;
    boolean makeDirectory(String pathname) throws IOException;
    FTPFile[] listFiles(String pathname) throws IOException;
    byte[] retrieveFileBytesFromStream(String reportFile) throws Throwable;
}
