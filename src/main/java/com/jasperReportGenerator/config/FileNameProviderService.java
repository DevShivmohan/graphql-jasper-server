package com.jasperReportGenerator.config;

import org.springframework.stereotype.Service;

@Service
public class FileNameProviderService {
    public static String getFileName (String report_uri) {
        // Split the input path using the "/" separator
        String[] parts = report_uri.split("/");
        // The last element in the parts array will be fileName
        return parts[parts.length - 1];
    }
}
