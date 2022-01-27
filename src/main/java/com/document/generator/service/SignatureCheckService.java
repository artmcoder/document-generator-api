package com.document.generator.service;

import com.document.generator.dto.DocumentDTO;
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

@Service
@RequiredArgsConstructor
public class SignatureCheckService {
    private static final String PDF_RESOURCES = "/static/";
    private final SpringTemplateEngine templateEngine;
    @Value("${application.domain}")
    private String domain;

    public boolean checkSignature(String[] documentText) throws IOException, DocumentException {
        int pagesCountWithoutSignature = getPagesCount(getHtml(documentText, CheckType.WITHOUT_SIGNATURE));
        int pagesCountWithSignature = getPagesCount(getHtml(documentText, CheckType.SIGNATURE));;
        int pagesCountWithDoubleSignature = getPagesCount(getHtml(documentText, CheckType.DOUBLE_SIGNATURE));
        System.out.println(pagesCountWithDoubleSignature);
        System.out.println(pagesCountWithoutSignature);
        System.out.println(pagesCountWithSignature);
        if ((pagesCountWithoutSignature == pagesCountWithSignature) && (pagesCountWithSignature == pagesCountWithDoubleSignature)) return false;
        else return true;
    }

    private String getHtml(String[] documentText, CheckType checkType) {
        Context context = new Context();
        context.setVariable("checkType", checkType.toString());
        context.setVariable("documentText", documentText);
        context.setVariable("signaturePath", domain + "api/v1/documents/signature/signature.png");
        return loadAndFillTemplate(context);
    }

    private String loadAndFillTemplate(Context context) {
        return templateEngine.process("check-signature", context);
    }

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
}
