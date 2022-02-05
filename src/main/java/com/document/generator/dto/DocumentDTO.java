package com.document.generator.dto;

import lombok.Data;

/**
 * Document DTO object
 * @author Artem Yakunin
 * @version 1.0
 */
@Data
public class DocumentDTO {
    private String fullName;
    private String date;
    private int number;
    private String documentText;
}
