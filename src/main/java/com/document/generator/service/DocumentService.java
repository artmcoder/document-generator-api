package com.document.generator.service;

import com.document.generator.dto.DocumentDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@Slf4j
@RequiredArgsConstructor
public class DocumentService {
    @Value("${application.domain}")
    private String domain;
    private static final String PDF_RESOURCES = "/static/";
    private final SpringTemplateEngine templateEngine;

    public File generatePdf(String documentInJson, MultipartFile signatureInMultipartFile) throws IOException, DocumentException {
        saveSignature(signatureInMultipartFile);
        DocumentDTO documentDTO = new ObjectMapper().readValue(documentInJson, DocumentDTO.class);
         String html = loadAndFillTemplate(getContext(documentDTO));
        log.info("Generating new document with number: {}", documentDTO.getNumber());
        File document = renderPdf(html);
        new File("src/main/resources/static/images/signature.png").delete();
        return document;
    }

    private void saveSignature(MultipartFile signatureInMultipartFile) {
        Path signaturePath = Paths.get("src/main/resources/static/images/", "signature.png");
        try (OutputStream os = Files.newOutputStream(signaturePath)) {
            log.info("Saving new signature");
            os.write(signatureInMultipartFile.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File renderPdf(String html) throws IOException, DocumentException {
        File file = File.createTempFile("document", ".pdf");
        OutputStream outputStream = new FileOutputStream(file);
        ITextRenderer renderer = new ITextRenderer(20f * 4f / 3f, 20);
        renderer.setDocumentFromString(html, new ClassPathResource(PDF_RESOURCES).getURL().toExternalForm());
        renderer.getFontResolver().addFont("src/main/resources/static/fonts/calibri.ttf", BaseFont.IDENTITY_H, true);
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();
        return file;
    }

    private String loadAndFillTemplate(Context context) {
        return templateEngine.process("pdf-doc", context);
    }

    private Context getContext(DocumentDTO documentDTO) {
        Context context = new Context();
        String[] documentText = documentDTO.getDocumentText().split("\n");
        context.setVariable("document", documentDTO);
        context.setVariable("documentText", documentText);
        context.setVariable("signaturePath", domain + "api/v1/documents/signature/signature.png");
        return context;
    }
}
