package com.document.generator.pdf;

import com.document.generator.dto.DocumentDTO;
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
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Use this class to generate pdf document
 * @author Artem Yakunin
 * @version 1.0
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DocumentGenerator {
    private static final String PDF_RESOURCES = "/static/";
    private final SpringTemplateEngine templateEngine;
    private final SignatureCheckService signatureCheckService;
    private final SignatureProvider signatureProvider;
    @Value("${application.domain}")
    private String applicationDomain;

    /**
     * Generate document function
     * @param documentDTO
     * @param signatureInMultipartFile
     * @return
     * @throws IOException
     * @throws DocumentException
     */
    public File generateDocument(DocumentDTO documentDTO, MultipartFile signatureInMultipartFile) throws IOException, DocumentException {
        signatureProvider.saveSignature(signatureInMultipartFile);
        boolean paragraphBreak = signatureCheckService.checkSignature(documentDTO.getDocumentText());
        String documentInHtml = templateEngine.process("pdf-doc", getContext(documentDTO, paragraphBreak));
        log.info("Generating new document with number: {}", documentDTO.getNumber());
        return renderPdf(documentInHtml);
    }

    /**
     * Create document function
     * @param html
     * @return
     * @throws IOException
     * @throws DocumentException
     */
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

    /**
     * Get thymeleaf context function
     * @param documentDTO
     * @param paragraphBreak
     * @return
     */
    private Context getContext(DocumentDTO documentDTO, boolean paragraphBreak) {
        Context context = new Context();
        String[] documentText = documentDTO.getDocumentText().split("\n");
        context.setVariable("document", documentDTO);
        context.setVariable("documentText", documentText);
        context.setVariable("documentTextLength", documentText.length-1);
        context.setVariable("lastLine", documentText[documentText.length-1]);
        context.setVariable("signaturePath", applicationDomain + "api/v1/documents/signature/signature.png");
        context.setVariable("paragraphBreak", paragraphBreak);
        return context;
    }
}
