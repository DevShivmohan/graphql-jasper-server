package com.report.generator.service;

import com.report.generator.dto.JasperReportDto;
import com.report.generator.dto.ReportDownloadRequestDTO;

import java.io.File;
import java.io.InputStream;

public interface ReportService {
    File downloadReportIntoFileSystem(final InputStream inputStream, final ReportFileNameConfiguration reportFileNameConfiguration, final JasperReportDto jasperReportDto) throws Throwable;
    byte[] fetchReportFromFileSystem(final ReportDownloadRequestDTO reportDownloadRequestDTO) throws Throwable;
}
