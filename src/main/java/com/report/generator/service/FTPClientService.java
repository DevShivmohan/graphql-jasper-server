package com.report.generator.service;

import java.io.IOException;
import java.io.InputStream;

public interface FTPClientService {
    boolean saveFile(String remote, InputStream local) throws IOException;
    boolean makeDirectory(String pathname) throws IOException;
}
