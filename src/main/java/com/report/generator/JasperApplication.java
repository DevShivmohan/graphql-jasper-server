package com.report.generator;

import com.report.generator.service.FTPClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;

@SpringBootApplication
public class JasperApplication {
    @Autowired
    private FTPClientService ftpClientService;

    public static void main(String[] args) {
        SpringApplication.run(JasperApplication.class, args);
    }

    @PostConstruct
    public void showAllDirectoriesAndFilesOverFTPServer() throws IOException {
        Arrays.stream(ftpClientService.listFiles("/jasper-reports/Reports/pdf")).forEach(System.out::println);
        Arrays.stream(ftpClientService.listFiles("/jasper-reports/Reports/xlsx")).forEach(System.out::println);
        Arrays.stream(ftpClientService.listFiles("/jasper-reports/Reports/html")).forEach(System.out::println);
        System.out.println("File delete status - "+ftpClientService.deleteFile("/jasper-reports/Reports/pdf/BankAccountReportWithCustomStyle_1702034759209.pdf"));
        Arrays.stream(ftpClientService.listFiles("/jasper-reports/Reports/pdf")).forEach(System.out::println);
    }

}