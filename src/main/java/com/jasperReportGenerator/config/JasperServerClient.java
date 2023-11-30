package com.jasperReportGenerator.config;

import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.jasperReportGenerator.dto.JasperReportDto;
import com.jasperReportGenerator.dto.JasperReportMultiValueParamDto;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

/**
 * @author Anand Jambhale
 * @see application.properties
 */
@Service
@GraphQLApi
public class JasperServerClient {
    @Value ("${jasper.server.url}")
    private String serverUrl;
    @Value ("${jasper.server.username}")
    private String username;
    @Value ("${jasper.server.password}")
    private String password;
    @Value ("${jasper.server.locale}")
    private String reportLocale;
    @Value ("${jasper.server.dot}")
    private String dot;
    @Value ("${jasper.server.default-locale}")
    private String defaultLocale;
    @Autowired
    private FileNameProviderService fileNameProviderService;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * To fetch the report from jasper server
     * @param jasperReportDto
     * @return
     * @throws Throwable
     */
    @GraphQLQuery (name = "getReport")
    public String downloadReport (JasperReportDto jasperReportDto) throws Throwable {
        final StringBuilder baseURL=new StringBuilder(serverUrl);
        UriComponentsBuilder uriComponentsBuilder=UriComponentsBuilder.fromUriString(baseURL.append(jasperReportDto.getReportUri()).append(dot).append(jasperReportDto.getFileFormat()).toString())
                .queryParam(reportLocale,(jasperReportDto.getLocaleName()==null || jasperReportDto.getLocaleName().isBlank() ? defaultLocale : jasperReportDto.getLocaleName()));
        try{
            ResponseEntity<byte[]> responseEntity= restTemplate.exchange(uriComponentsBuilder.toUriString(), HttpMethod.GET,null, byte[].class);
            if(responseEntity.getStatusCode()== HttpStatus.OK){
                new File(jasperReportDto.getReportOutputFolder()).mkdirs();
                final File outputFile=new File(jasperReportDto.getReportOutputFolder()+File.separator+new File(jasperReportDto.getReportUri()+dot+jasperReportDto.getFileFormat()).getName());
                Files.copy(new ByteArrayInputStream(Objects.requireNonNull(responseEntity.getBody())),outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return "File downloaded successfully to path "+outputFile.getAbsolutePath();
            }
            throw new Throwable("File downloading error");
        }catch (Throwable throwable){
            throwable.printStackTrace();
            throw throwable;
        }
    }




    @GraphQLQuery (name = "getParametrizedReport")
    public String getParametrizedReport (JasperReportDto jasperReportDto) throws IOException {
        String url = serverUrl + "/rest_v2/reports/" + jasperReportDto.getReportUri() + "."
                + jasperReportDto.getFileFormat() + "?j_username=" + username + "&j_password="
                + password + "&";
        if (Objects.nonNull(jasperReportDto.getParameters()) && !jasperReportDto.getParameters()
                .isEmpty()) {
            for (String parameter : jasperReportDto.getParameters()) {
            }
        }
        URL downloadUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
        connection.setRequestMethod("GET");
        String outputPath = null;
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = fileNameProviderService.getFileName(jasperReportDto.getReportUri());
            outputPath = jasperReportDto.getReportOutputFolder() + fileName + "."
                    + jasperReportDto.getFileFormat();
            try (InputStream inputStream = connection.getInputStream();
                    FileOutputStream outputStream = new FileOutputStream(outputPath)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            return ("Report downloaded successfully, file path: " + outputPath);
        }
        else {
            throw new IOException("Report download failed. HTTP response code: " + responseCode);
        }
    }

    @GraphQLQuery (name = "getEncryptedReport")
    public String getEncryptedReport (JasperReportDto jasperReportDto)
            throws IOException {
        String url = serverUrl + "/rest_v2/reports/" + jasperReportDto.getReportUri() + "."
                + jasperReportDto.getFileFormat() + "?j_username=" + username + "&j_password="
                + password;
        URL downloadUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String temporaryPdf = jasperReportDto.getReportOutputFolder() + "temporary" + "."
                    + jasperReportDto.getFileFormat();
            try (InputStream inputStream = connection.getInputStream();
                    FileOutputStream outputStream = new FileOutputStream(temporaryPdf)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            PdfWriter pdfWriter = null;
            try {
                // Input and output file paths
                String inputFile = temporaryPdf;
                String fileName = fileNameProviderService.getFileName(jasperReportDto.getReportUri());
                String outputFile = jasperReportDto.getReportOutputFolder() + fileName + "."
                        + jasperReportDto.getFileFormat();
                // Set the user and owner passwords
                String userPassword = jasperReportDto.getPassword(); // User password (to open the PDF)
                String ownerPassword = "ownerpassword"; // Owner password (to change permissions)
                // Load the input PDF
                PdfReader pdfReader = new PdfReader(inputFile);
                // Create a PdfWriter to write the output PDF with encryption
                pdfWriter = new PdfWriter(outputFile,
                        new WriterProperties().setStandardEncryption(userPassword.getBytes(),
                                ownerPassword.getBytes(), EncryptionConstants.ALLOW_PRINTING,
                                EncryptionConstants.ENCRYPTION_AES_128));
                // Create a PdfDocument instance
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter);
                // Close the PdfDocument to save changes
                pdfDocument.close();
                System.out.println("PDF password protection applied successfully.");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                pdfWriter.close();
                Path pdfPath = Paths.get(temporaryPdf);
                try {
                    Files.delete(pdfPath);
                    System.out.println("PDF file deleted successfully.");
                }
                catch (IOException e) {
                    System.err.println("Unable to delete the PDF file: " + e.getMessage());
                }
            }
            return ("Report downloaded successfully, file path: " + temporaryPdf);
        }
        else {
            throw new IOException("Report download failed. HTTP response code: " + responseCode);
        }
    }

}

