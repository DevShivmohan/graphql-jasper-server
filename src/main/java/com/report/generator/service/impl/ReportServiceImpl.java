package com.report.generator.service.impl;

import com.report.generator.config.properties.FileSystemProperties;
import com.report.generator.constants.ApiConstants;
import com.report.generator.dto.JasperReportDto;
import com.report.generator.dto.ReportDownloadRequestDTO;
import com.report.generator.service.FTPClientService;
import com.report.generator.service.ReportFileNameConfiguration;
import com.report.generator.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

@Service
@EnableConfigurationProperties(FileSystemProperties.class)
@AllArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final FileSystemProperties fileSystemProperties;
    private final FTPClientService ftpClientService;

    @Override
    public File downloadReportIntoFileSystem(final InputStream inputStream, final ReportFileNameConfiguration reportFileNameConfiguration,final JasperReportDto jasperReportDto) throws Throwable {
        final String configuredReportName= reportFileNameConfiguration.getConfiguredReportFileNameWithCustomConfiguration(jasperReportDto);
        final File outputBasePath = new File(fileSystemProperties.getBasePath() + File.separator + fileSystemProperties.getRootReportDirectoryName() + File.separator
                + configuredReportName
                .substring(configuredReportName.indexOf(ApiConstants.DOT)+1));
        ftpClientService.makeDirectory(outputBasePath.getAbsolutePath()); // make sub directories
       final File outputFile = new File(outputBasePath.getAbsolutePath() + File.separator + configuredReportName);
        if(!ftpClientService.saveFile(outputFile.getAbsolutePath(),inputStream))
            throw new Throwable("File copying error to FTP server");
        inputStream.close();
        return outputFile;
    }

    @Override
    public byte[] fetchReportFromFileSystem(ReportDownloadRequestDTO reportDownloadRequestDTO) throws Throwable {
        return ftpClientService.retrieveFileBytesFromStream(reportDownloadRequestDTO.getReportPath());
    }
}
