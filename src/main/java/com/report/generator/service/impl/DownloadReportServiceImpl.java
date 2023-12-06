package com.report.generator.service.impl;

import com.report.generator.config.FileSystemProperties;
import com.report.generator.constants.ApiConstants;
import com.report.generator.service.DownloadReportService;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Service
@EnableConfigurationProperties(FileSystemProperties.class)
@AllArgsConstructor
public class DownloadReportServiceImpl implements DownloadReportService {
    private final FileSystemProperties fileSystemProperties;
    @Override
    public File downloadReportIntoFileSystem(InputStream inputStream, String reportName, String reportFormat) throws Throwable {
        final File outputBasePath = new File(fileSystemProperties.getBasePath() + File.separator + fileSystemProperties.getRootReportDirectoryName() + File.separator + reportFormat);
        if (!outputBasePath.exists() && !outputBasePath.mkdirs())
            throw new Throwable("Directory creation error " + reportFormat);
        final String reportNameFilterIfSubPath=reportName.substring(reportName.lastIndexOf(ApiConstants.FORWARD_SLASH)!=-1 ? reportName.lastIndexOf(ApiConstants.FORWARD_SLASH)+1 : 0);
        final File outputFile = new File(outputBasePath.getAbsolutePath() + File.separator + reportNameFilterIfSubPath + ApiConstants.UNDERSCORE + System.currentTimeMillis() + ApiConstants.DOT + reportFormat);
        if((Files.copy(inputStream,outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING))<=0)
            throw new Throwable("File copying error");
        inputStream.close();
        return outputFile;
    }
}
