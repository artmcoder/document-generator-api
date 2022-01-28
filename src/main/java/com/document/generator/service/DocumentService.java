package com.document.generator.service;

import com.document.generator.dto.DocumentDTO;
import com.lowagie.text.DocumentException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface DocumentService {
    Path generateDocument(DocumentDTO documentDTO, MultipartFile signature) throws IOException, DocumentException;
}
