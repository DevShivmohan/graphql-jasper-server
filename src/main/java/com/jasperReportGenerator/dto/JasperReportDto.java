package com.jasperReportGenerator.dto;

import io.leangen.graphql.annotations.types.GraphQLType;
import lombok.Data;

@GraphQLType
@Data
public class JasperReportDto {
    private String reportOutputFolder;
    private String fileFormat;
    private String reportUri;
    private String localeName;
}
