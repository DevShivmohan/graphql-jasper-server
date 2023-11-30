package com.jasperReportGenerator.dto;

import io.leangen.graphql.annotations.GraphQLInputField;
import io.leangen.graphql.annotations.types.GraphQLType;

import java.util.List;

@GraphQLType
public class JasperReportDto {
    private String reportOutputFolder;
    private String fileFormat;
    private String reportUri;

    public String getLocaleName() {
        return localeName;
    }

    public void setLocaleName(String localeName) {
        this.localeName = localeName;
    }

    private String localeName;
    @GraphQLInputField(description = "Provide parameter id and value here. Like if I want to fetch data on basis of module to be Payment. I will write 'module=Payment'")
    private List<String> parameters;

    private String password;

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

    public List<String> getParameters () {
        return parameters;
    }

    public void setParameters (List<String> parameters) {
        this.parameters = parameters;
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
