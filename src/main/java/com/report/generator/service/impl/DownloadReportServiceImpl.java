package com.report.generator.service.impl;

import com.report.generator.config.FileSystemProperties;
import com.report.generator.constants.ApiConstants;
import com.report.generator.dto.JasperReportDto;
import com.report.generator.service.DownloadReportService;
import com.report.generator.service.ReportFileNameConfiguration;
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
    public File downloadReportIntoFileSystem(final InputStream inputStream, final ReportFileNameConfiguration reportFileNameConfiguration,final JasperReportDto jasperReportDto) throws Throwable {
        final String configuredReportName= reportFileNameConfiguration.getConfiguredReportFileNameWithCustomConfiguration(jasperReportDto);
        final File outputBasePath = new File(fileSystemProperties.getBasePath() + File.separator + fileSystemProperties.getRootReportDirectoryName() + File.separator
                + configuredReportName
                .substring(configuredReportName.indexOf(ApiConstants.DOT)+1));
        if (!outputBasePath.exists() && !outputBasePath.mkdirs())
            throw new Throwable("Directory creation error " + outputBasePath.getName());
       final File outputFile = new File(outputBasePath.getAbsolutePath() + File.separator + configuredReportName);
        if((Files.copy(inputStream,outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING))<=0)
            throw new Throwable("File copying error");
        inputStream.close();
        return outputFile;
    }
}
