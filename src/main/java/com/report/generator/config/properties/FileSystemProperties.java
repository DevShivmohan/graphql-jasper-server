package com.report.generator.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "report.file-system")
public class FileSystemProperties {
    private String rootReportDirectoryName;
    private String basePath;
}
