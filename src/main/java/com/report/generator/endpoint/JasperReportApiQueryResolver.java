package com.report.generator.endpoint;

import com.report.generator.config.properties.JasperServerConfigProperties;
import com.report.generator.constants.ApiConstants;
import com.report.generator.dto.FileDownloadResponseDTO;
import com.report.generator.dto.JasperReportDto;
import com.report.generator.dto.ReportDownloadRequestDTO;
import com.report.generator.service.JasperReportApiCallService;
import com.report.generator.service.ReportFileNameConfiguration;
import com.report.generator.service.ReportService;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

@GraphQLApi
@Service
@AllArgsConstructor
@Slf4j
@EnableConfigurationProperties(JasperServerConfigProperties.class)
public class JasperReportApiQueryResolver {
    private final JasperServerConfigProperties jasperServerConfigProperties;
    private final ReportService reportService;
    private final JasperReportApiCallService jasperReportApiCallService;

    /**
     * To fetch the report from jasper server
     *
     * @param jasperReportDto
     * @return
     * @throws Throwable
     */
    @GraphQLQuery(name = "fetchReportFromReportServer")
    public String fetchReportFromReportServer(JasperReportDto jasperReportDto) throws Throwable {
        final StringBuilder baseURL = new StringBuilder(jasperServerConfigProperties.getUrl());
        final UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(baseURL.append(jasperReportDto.getReportUri()).append(ApiConstants.DOT).append(jasperReportDto.getFileFormat()).toString())
                .queryParam(ApiConstants.REPORT_LOCALE, (jasperReportDto.getLocaleName() == null || jasperReportDto.getLocaleName().isBlank() ? jasperServerConfigProperties.getDefaultLocale() : jasperReportDto.getLocaleName()));
        final var inputStream = jasperReportApiCallService.callJasperReportApi(uriComponentsBuilder.toUriString());
        return reportService.downloadReportIntoFileSystem(inputStream, configureCustomReportName(),jasperReportDto).getAbsolutePath();
    }

    /**
     * Download the report from given path and convert its bytes into Base64 string
     * @return
     * @throws IOException
     */
    @GraphQLQuery(name = "downloadReport")
    public FileDownloadResponseDTO downloadReport(final ReportDownloadRequestDTO reportDownloadRequestDTO) throws Throwable {
        final var response=reportService.fetchReportFromFileSystem(reportDownloadRequestDTO);
        final File file=new File(reportDownloadRequestDTO.getReportPath());
        return FileDownloadResponseDTO.builder().contentType(file.getName()).content(Base64.getEncoder().encodeToString(response)).build();
    }

    /**
     * Override the method to configure the report file name
     *
     * @return
     */
    private ReportFileNameConfiguration configureCustomReportName() {
        return (jasperReportDto) -> {
            final String reportName = jasperReportDto.getReportUri().substring(jasperReportDto.getReportUri().lastIndexOf(ApiConstants.FORWARD_SLASH) != -1
                    ? jasperReportDto.getReportUri().lastIndexOf(ApiConstants.FORWARD_SLASH) + 1 : 0);
            return reportName + ApiConstants.UNDERSCORE + System.currentTimeMillis() + ApiConstants.DOT + jasperReportDto.getFileFormat();
        };
    }

}
