package com.jasperReportGenerator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data
@ConfigurationProperties(prefix = "jasper.server")
public class JasperServerConfigProperties {
    private String url;
    private String username;
    private String password;
    private String defaultLocale;
}
