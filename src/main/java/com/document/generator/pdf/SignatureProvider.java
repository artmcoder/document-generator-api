package com.document.generator.pdf;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * User this class to save signature
 * @author Artem Yakunin
 * @version 1.0
 */
@Component
public class SignatureProvider {
    public void saveSignature(MultipartFile signature) {
        Path signaturePath = Paths.get("src/main/resources/static/images/", "signature.png");
        try (OutputStream os = Files.newOutputStream(signaturePath)) {
            os.write(signature.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
