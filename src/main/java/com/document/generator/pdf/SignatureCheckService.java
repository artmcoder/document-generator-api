package com.document.generator.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * With this class you can check how to sign a document
 * @author Artem Yakunin
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class SignatureCheckService {
    private static final String PDF_RESOURCES = "/static/";
    private final SpringTemplateEngine templateEngine;
    @Value("${application.domain}")
    private String applicationDomain;

    /**
     * Main check signature function
     * @param documentText
     * @return
     * @throws IOException
     * @throws DocumentException
     */
    public boolean checkSignature(String documentText) throws IOException, DocumentException {
        String[] documentTextLines = documentText.split("\n");
        int pagesCountWithoutSignature = getPagesCount(getHtml(documentTextLines, CheckType.WITHOUT_SIGNATURE));
        int pagesCountWithSignature = getPagesCount(getHtml(documentTextLines, CheckType.SIGNATURE));
        int pagesCountWithDoubleSignature = getPagesCount(getHtml(documentTextLines, CheckType.DOUBLE_SIGNATURE));
        return (pagesCountWithoutSignature != pagesCountWithSignature) || (pagesCountWithSignature != pagesCountWithDoubleSignature);
    }

    /**
     * Getting pages count function
     * @param html
     * @return
     * @throws IOException
     * @throws DocumentException
     */
    private int getPagesCount(String html) throws IOException, DocumentException {
        File file = File.createTempFile("document", ".pdf");
        OutputStream outputStream = new FileOutputStream(file);
        ITextRenderer renderer = new ITextRenderer(20f * 4f / 3f, 20);
        renderer.setDocumentFromString(html, new ClassPathResource(PDF_RESOURCES).getURL().toExternalForm());
        renderer.getFontResolver().addFont("src/main/resources/static/fonts/calibri.ttf", BaseFont.IDENTITY_H, true);
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();
        return renderer.getWriter().getPageNumber();
    }

    /**
     * Get document html code function
     * @param documentText
     * @param checkType
     * @return
     */
    private String getHtml(String[] documentText, CheckType checkType) {
        Context context = new Context();
        context.setVariable("checkType", checkType.toString());
        context.setVariable("documentText", documentText);
        context.setVariable("signaturePath", applicationDomain + "api/v1/documents/signature/signature.png");
        return templateEngine.process("check-signature", context);
    }
}
