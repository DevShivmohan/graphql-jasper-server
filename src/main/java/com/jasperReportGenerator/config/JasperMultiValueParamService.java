package com.jasperReportGenerator.config;

import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.jasperReportGenerator.dto.JasperReportMultiValueParamDto;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Anand Jambhale
 * @see application.properties
 */
@Service
@GraphQLApi
public class JasperMultiValueParamService {
    @Value ("${jasper.server.url}")
    private String serverUrl;
    @Value ("${jasper.server.username}")
    private String username;
    @Value ("${jasper.server.password}")
    private String password;
    @Autowired
    private FileNameProviderService fileNameProviderService;
    @GraphQLQuery (name = "getEncryptedMultiValueParamReport")
    public String getEncryptedMultiValueParamReport (JasperReportMultiValueParamDto jasperReportDto)
            throws IOException {
        String url = serverUrl + "/rest_v2/reports/" + jasperReportDto.getReportUri() + "."
                + jasperReportDto.getFileFormat() + "?j_username=" + username + "&j_password="
                + password;
        for (String param:jasperReportDto.getParamsAndValues().keySet()){
            for (String value:jasperReportDto.getParamsAndValues().get(param)){
                url+="&"+param+"="+value;
            }
        }
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
