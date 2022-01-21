package com.document.generator.dto;

import lombok.Data;

@Data
public class DocumentDTO {
    private String fullName;
    private String date;
    private int number;
    private String documentText;
}
