package com.jasperReportGenerator.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Beans {

    @Value("${jasper.server.username}")
    private String username;
    @Value ("${jasper.server.password}")
    private String password;
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(username,password));
        return restTemplate;
    }
}
