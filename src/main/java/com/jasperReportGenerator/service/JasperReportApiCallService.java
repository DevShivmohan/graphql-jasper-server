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
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@GraphQLApi
public class JasperReportApiCallService {
    @Value ("${jasper.server.url}")
    private String serverUrl;
    @Value ("${jasper.server.base-path}")
    private String reportOutputBasePath;
    @Value ("${jasper.server.username}")
    private String username;
    @Value ("${jasper.server.password}")
    private String password;
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

    /**
     * Jasper report api call by java.net package
     * @param jasperReportDto
     * @return
     * @throws Throwable
     */
    @GraphQLQuery (name = "getReportBySimple")
    public String downloadReportBySimpleApiCall(JasperReportDto jasperReportDto) throws Throwable {
        final StringBuilder baseURL=new StringBuilder(serverUrl);
        final URI uri=new URI(baseURL.append(jasperReportDto.getReportUri()).append(dot).append(jasperReportDto.getFileFormat()).append("?").append(reportLocale).append("=")
                .append((jasperReportDto.getLocaleName()==null || jasperReportDto.getLocaleName().isBlank() ? defaultLocale : jasperReportDto.getLocaleName())).toString());
        String base64 = Base64.getEncoder().encodeToString((username + ":" + password).getBytes(UTF_8));
        final HttpURLConnection httpURLConnection= (HttpURLConnection) new URL(uri.toString()).openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("Authorization", "Basic "+base64);
        httpURLConnection.connect();
        if(httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
            new File(reportOutputBasePath).mkdirs();
            final File outputFile=new File(reportOutputBasePath+File.separator+new File(jasperReportDto.getReportUri()+dot+jasperReportDto.getFileFormat()).getName());
            final OutputStream outputStream=new FileOutputStream(outputFile);
            Files.copy(httpURLConnection.getInputStream(),outputFile.toPath(),StandardCopyOption.REPLACE_EXISTING);
            outputStream.flush();
            outputStream.close();
            return outputFile.getAbsolutePath();
        }else
            throw new Throwable("Error while calling the api "+httpURLConnection.getResponseCode());
    }

}

