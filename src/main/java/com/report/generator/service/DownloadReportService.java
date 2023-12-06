package com.report.generator.service;

import java.io.File;
import java.io.InputStream;

public interface DownloadReportService {
    File downloadReportIntoFileSystem(final InputStream inputStream, String reportName, String reportFormat) throws Throwable;
}
