package com.report.generator.bean;

import com.report.generator.config.properties.FTPConfigProperties;
import com.report.generator.config.properties.FileSystemProperties;
import com.report.generator.config.properties.JasperServerConfigProperties;
import com.report.generator.constants.ApiConstants;
import lombok.AllArgsConstructor;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Configuration
@EnableConfigurationProperties({JasperServerConfigProperties.class, FileSystemProperties.class, FTPConfigProperties.class})
@AllArgsConstructor
public class Beans {
    private final JasperServerConfigProperties jasperServerConfigProperties;
    private final FileSystemProperties fileSystemProperties;
    private final FTPConfigProperties ftpConfigProperties;
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(jasperServerConfigProperties.getUsername(), jasperServerConfigProperties.getPassword()));
        return restTemplate;
    }

    private void createReportRootAndReportDirectory(final FTPClient ftpClient) throws IOException {
        final StringBuilder stringBuilder=new StringBuilder(ApiConstants.FORWARD_SLASH);
        for(String path:fileSystemProperties.getBasePath().split(ApiConstants.FORWARD_SLASH)){
            if(!path.isBlank()){
                stringBuilder.append(path);
                ftpClient.makeDirectory(stringBuilder.toString());
                stringBuilder.append(ApiConstants.FORWARD_SLASH);
            }
        }
        // After created base path directory
        // creation of root report directory name
        stringBuilder.append(fileSystemProperties.getRootReportDirectoryName());
        ftpClient.makeDirectory(stringBuilder.toString());
    }

    @Bean
    public FTPClient ftpClient() throws Throwable {
        final FTPClient ftpClient=new FTPClient();
        ftpClient.connect(ftpConfigProperties.getIp(),ftpConfigProperties.getPort());
        ftpClient.enterLocalPassiveMode();
        ftpClient.login(ftpConfigProperties.getUsername(),ftpConfigProperties.getPassword());
        createReportRootAndReportDirectory(ftpClient);
        return ftpClient;
    }
}
