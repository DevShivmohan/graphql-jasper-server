package com.report.generator.service;

import com.report.generator.dto.JasperReportDto;

public interface ReportFileNameConfiguration {
    /**
     * Configure with your own configuration for report file name
     * @return
     */
    String getConfiguredReportFileNameWithCustomConfiguration(final JasperReportDto jasperReportDto);
}
