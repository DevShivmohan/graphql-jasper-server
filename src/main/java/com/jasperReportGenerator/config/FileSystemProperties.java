package com.jasperReportGenerator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "report.file-system")
public class FileSystemProperties {
    private String rootDirectoryName;
}
