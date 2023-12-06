package com.report.generator.service;

import java.io.InputStream;

public interface JasperReportApiCallService {
    InputStream callJasperReportApi(String uri);
}
