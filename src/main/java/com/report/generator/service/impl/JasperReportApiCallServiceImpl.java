package com.report.generator.service.impl;

import com.report.generator.service.JasperReportApiCallService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;

@Service
@AllArgsConstructor
public class JasperReportApiCallServiceImpl implements JasperReportApiCallService {
    private final RestTemplate restTemplate;
    @Override
    public InputStream callJasperReportApi(String uri) {
        final ResponseEntity<byte[]> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, null, byte[].class);
        return responseEntity.getStatusCode() == HttpStatus.OK ? new ByteArrayInputStream(Objects.requireNonNull(responseEntity.getBody())) : null;
    }
}
