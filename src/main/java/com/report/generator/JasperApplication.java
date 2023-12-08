package com.report.generator;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;

@SpringBootApplication
public class JasperApplication {
    @Autowired
    private FTPClient ftpClient;

    public static void main(String[] args) {
        SpringApplication.run(JasperApplication.class, args);
    }

    @PostConstruct
    public void showAllDirectoriesAndFilesOverFTPServer() throws IOException {
        Arrays.stream(ftpClient.listFiles("/jasper-reports/Reports/pdf")).forEach(System.out::println);
        Arrays.stream(ftpClient.listFiles("/jasper-reports/Reports/xlsx")).forEach(System.out::println);
    }

}