package com.example.rag.controller;

import com.example.rag.dto.UploadResponse;
import com.example.rag.service.DocumentIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DocumentController {

    private final DocumentIngestionService ingestionService;

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadPdf(@RequestParam("file") MultipartFile file) {
        log.info("Received file upload: {}", file.getOriginalFilename());
        
        try {
            ingestionService.ingestPdf(file);
            return ResponseEntity.ok(new UploadResponse(
                "Document uploaded and indexed successfully",
                file.getOriginalFilename(),
                -1
            ));
        } catch (Exception e) {
            log.error("Error processing file: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new UploadResponse(
                "Error: " + e.getMessage(),
                file.getOriginalFilename(),
                0
            ));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Document service is running");
    }
}
