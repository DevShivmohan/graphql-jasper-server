package com.report.generator.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "report.ftp")
public class FTPConfigProperties {
    private String ip;
    private int port;
    private String username;
    private String password;
}
