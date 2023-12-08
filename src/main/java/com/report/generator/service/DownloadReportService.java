package com.report.generator.service;

import com.report.generator.dto.JasperReportDto;

import java.io.File;
import java.io.InputStream;

public interface DownloadReportService {
    File downloadReportIntoFileSystem(final InputStream inputStream, final ReportFileNameConfiguration reportFileNameConfiguration, final JasperReportDto jasperReportDto) throws Throwable;
}
