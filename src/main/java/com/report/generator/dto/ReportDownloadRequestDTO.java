package com.report.generator.dto;

import io.leangen.graphql.annotations.types.GraphQLType;
import lombok.Data;

@Data
@GraphQLType
public class ReportDownloadRequestDTO {
    private String reportPath;
}
