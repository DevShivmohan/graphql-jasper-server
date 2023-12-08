package com.report.generator.endpoint;

import com.report.generator.config.properties.JasperServerConfigProperties;
import com.report.generator.constants.ApiConstants;
import com.report.generator.dto.JasperReportDto;
import com.report.generator.service.DownloadReportService;
import com.report.generator.service.JasperReportApiCallService;
import com.report.generator.service.ReportFileNameConfiguration;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@GraphQLApi
@Service
@AllArgsConstructor
@Slf4j
@EnableConfigurationProperties(JasperServerConfigProperties.class)
public class JasperReportApiQueryResolver {
    private final JasperServerConfigProperties jasperServerConfigProperties;
    private final DownloadReportService downloadReportService;
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
        return downloadReportService.downloadReportIntoFileSystem(inputStream, configureCustomReportName(),jasperReportDto).getAbsolutePath();
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
