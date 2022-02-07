package com.document.generator;

import com.document.generator.dto.DocumentDTO;
import com.document.generator.pdf.SignatureCheckService;
import com.document.generator.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.DocumentException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationTest {
    private final String resourcesPath = "src/test/resources/";
    @Autowired
    private DocumentService documentService;
    @Autowired
    private SignatureCheckService signatureCheckService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testRequestToGenerateDocument() throws Exception {
        MockHttpServletRequestBuilder requestParams = multipart("/api/v1/documents/generate")
                .file("signature", Files.readAllBytes(Paths.get("src/test/resources/signature.png")))
                .param("document", new ObjectMapper().writeValueAsString(getDocumentDTO()));
        this.mockMvc.perform(requestParams)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testCheckSignature() throws IOException, DocumentException {
        StringBuilder documentHtml = new StringBuilder();
        Files.readAllLines(Paths.get(resourcesPath + "document.html")).forEach(documentHtml::append);
        assertFalse(signatureCheckService.checkSignature(documentHtml.toString()));
    }

    @Test
    public void testDocumentGeneration() throws IOException, DocumentException {
        assertNotNull(generateDocument());
    }

    @Test
    public void testDocumentSize() throws IOException, DocumentException {
        assertEquals(25111, generateDocument().toFile().length());
    }

    private Path generateDocument() throws IOException, DocumentException {
        return documentService.generateDocument(getDocumentDTO(), getSignature());
    }

    private MultipartFile getSignature() throws IOException {
        Path signaturePath = Paths.get(resourcesPath + "signature.png");
        byte[] content = Files.readAllBytes(signaturePath);
        return new MockMultipartFile("signature",
                signaturePath.toFile().getName(), "image/png", content);
    }

    private DocumentDTO getDocumentDTO() throws IOException {
        DocumentDTO documentDTO = new DocumentDTO();
        documentDTO.setNumber(20);
        documentDTO.setDate("20 августа 2021 года");
        documentDTO.setFullName("Иванов Иван Иванович");
        documentDTO.setDocumentText(getDocumentText());
        return documentDTO;
    }

    private String getDocumentText() throws IOException {
        StringBuilder documentTextBuilder = new StringBuilder();
        Files.readAllLines(Paths.get(resourcesPath + "documentText.txt"))
                .forEach(documentTextBuilder::append);
        return documentTextBuilder.toString();
    }
}