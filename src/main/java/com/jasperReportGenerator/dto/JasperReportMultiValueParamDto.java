package com.jasperReportGenerator.dto;

import io.leangen.graphql.annotations.GraphQLInputField;

import java.util.List;
import java.util.Map;

public class JasperReportMultiValueParamDto {
    private String reportOutputFolder;
    private String fileFormat;
    private String reportUri;
    private String password;
    private Map<String, List<String>> paramsAndValues;

    public Map<String, List<String>> getParamsAndValues () {
        return paramsAndValues;
    }

    public void setParamsAndValues (Map<String, List<String>> paramsAndValues) {
        this.paramsAndValues = paramsAndValues;
    }

    public String getPassword () {
        return password;
    }

    public void setPassword (String password) {
        this.password = password;
    }

    public String getReportOutputFolder() {
        return reportOutputFolder;
    }

    public void setReportOutputFolder(String reportOutputFolder) {
        this.reportOutputFolder = reportOutputFolder;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getReportUri() {
        return reportUri;
    }

    public void setReportUri(String reportUri) {
        this.reportUri = reportUri;
    }
}
