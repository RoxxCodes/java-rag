package com.example.rag.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadResponse {
    private String message;
    private String filename;
    private int segmentsIndexed;
}
