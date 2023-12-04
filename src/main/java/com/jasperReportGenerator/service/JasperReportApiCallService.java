package com.jasperReportGenerator.service;

import com.jasperReportGenerator.dto.JasperReportDto;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
@GraphQLApi
public class JasperReportApiCallService {
    @Value ("${jasper.server.url}")
    private String serverUrl;
    @Value ("${jasper.server.locale}")
    private String reportLocale;
    @Value ("${jasper.server.dot}")
    private String dot;
    @Value ("${jasper.server.default-locale}")
    private String defaultLocale;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * A/B
     * A/B/C/fit.txt
     * A/D/C/fit.txt
     * To fetch the report from jasper server
     * @param jasperReportDto
     * @return
     * @throws Throwable
     */
    @GraphQLQuery (name = "getReport")
    public String downloadReport(JasperReportDto jasperReportDto) throws Throwable {
        final StringBuilder baseURL=new StringBuilder(serverUrl);
        final UriComponentsBuilder uriComponentsBuilder=UriComponentsBuilder.fromUriString(baseURL.append(jasperReportDto.getReportUri()).append(dot).append(jasperReportDto.getFileFormat()).toString())
                .queryParam(reportLocale,(jasperReportDto.getLocaleName()==null || jasperReportDto.getLocaleName().isBlank() ? defaultLocale : jasperReportDto.getLocaleName()));
        try{
            final ResponseEntity<byte[]> responseEntity= restTemplate.exchange(uriComponentsBuilder.toUriString(), HttpMethod.GET,null, byte[].class);
            if(responseEntity.getStatusCode()== HttpStatus.OK){
                new File(jasperReportDto.getReportOutputFolder()).mkdirs();
                final File outputFile=new File(jasperReportDto.getReportOutputFolder()+File.separator+new File(jasperReportDto.getReportUri()+dot+jasperReportDto.getFileFormat()).getName());
                if(Files.copy(new ByteArrayInputStream(Objects.requireNonNull(responseEntity.getBody())),outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING)<=0)
                    throw new Throwable("File copying error");
                return "File downloaded successfully to path - "+outputFile.getAbsolutePath();
            }
            throw new Throwable("File downloading error");
        }catch (Throwable throwable){
            throwable.printStackTrace();
            throw throwable;
        }
    }

}

