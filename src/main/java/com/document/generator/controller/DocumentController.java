package com.document.generator.controller;

import com.document.generator.dto.DocumentDTO;
import com.document.generator.service.DocumentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;

    @PostMapping("generate")
    public void generateDocument(@RequestParam("document") String documentInJson,
                                 @RequestParam("signature") MultipartFile signatureInMultipartFile,
                                 HttpServletResponse response) throws IOException, DocumentException {
        DocumentDTO documentDTO = new ObjectMapper().readValue(documentInJson, DocumentDTO.class);
        streamReport(response, documentService.generateDocument(documentDTO, signatureInMultipartFile));
    }

    private void streamReport(HttpServletResponse response, Path document) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-disposition", "attachment; filename=" + document.getFileName());
        Files.copy(document, response.getOutputStream());
        response.getOutputStream().flush();
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleException(JsonProcessingException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error message", "Cannot parse json file");
        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }
}
