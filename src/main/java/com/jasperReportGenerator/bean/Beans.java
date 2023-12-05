package com.jasperReportGenerator.bean;

import com.jasperReportGenerator.config.FileSystemProperties;
import com.jasperReportGenerator.config.JasperServerConfigProperties;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.File;

@Configuration
@EnableConfigurationProperties({JasperServerConfigProperties.class, FileSystemProperties.class})
@AllArgsConstructor
public class Beans {
    private final JasperServerConfigProperties jasperServerConfigProperties;
    private final FileSystemProperties fileSystemProperties;
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(jasperServerConfigProperties.getUsername(), jasperServerConfigProperties.getPassword()));
        return restTemplate;
    }

    @PostConstruct
    public void createReportRootDirectory() throws Throwable {
        final File file=new File(fileSystemProperties.getBasePath()+File.separator+fileSystemProperties.getRootReportDirectoryName());
        if(!file.exists() && !file.mkdirs())
            throw new Throwable("Root directory creation error");
    }
}
