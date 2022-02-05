package com.document.generator.service.impl;

import com.document.generator.dto.DocumentDTO;
import com.document.generator.pdf.DocumentGenerator;
import com.document.generator.service.DocumentService;
import com.lowagie.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Document service. It generating pdf documents
 * @author Artem Yakunin
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    private final DocumentGenerator documentGenerator;

    @Override
    public Path generateDocument(DocumentDTO documentDTO, MultipartFile signature) throws  IOException, DocumentException {
        Path document = Paths.get(documentGenerator.generateDocument(documentDTO, signature).getAbsolutePath());
        Files.delete(Paths.get("src/main/resources/static/images/signature.png"));
        return document;
    }
}

