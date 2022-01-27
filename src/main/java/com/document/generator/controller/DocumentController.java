package com.document.generator.controller;

import com.document.generator.dto.DocumentDTO;
import com.document.generator.service.DocumentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping("generate")
    public void generateDocument(@RequestParam("document") String documentInJson,
                                 @RequestParam("signature") MultipartFile signatureInMultipartFile,
                                 HttpServletResponse response) throws JsonProcessingException {
        DocumentDTO documentDTO = new ObjectMapper().readValue(documentInJson, DocumentDTO.class);
        try {
            Path file = Paths.get(documentService.generatePdf(documentDTO, signatureInMultipartFile).getAbsolutePath());
            if (Files.exists(file)) {
                response.setContentType("application/pdf");
                response.addHeader("Content-Disposition", "attachment; filename=" + file.getFileName());
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
            }
        } catch (DocumentException | IOException ex) {
            ex.printStackTrace();
        }
    }
}
