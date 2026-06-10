package com.example.rag.controller;

import com.example.rag.dto.AnswerResponse;
import com.example.rag.dto.QuestionRequest;
import com.example.rag.llm.LlmProviderFactory;
import com.example.rag.service.RagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RagController {

    private final RagService ragService;
    private final LlmProviderFactory llmProviderFactory;

    @PostMapping("/ask")
    public ResponseEntity<AnswerResponse> askQuestion(@RequestBody QuestionRequest request) {
        log.info("Received question: {}", request.getQuestion());
        
        if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new AnswerResponse("Question cannot be empty"));
        }

        try {
            String answer = ragService.answerQuestion(request.getQuestion());
            return ResponseEntity.ok(new AnswerResponse(answer));
        } catch (Exception e) {
            log.error("Error processing question: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(new AnswerResponse("Error processing your question: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("RAG service is running");
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> status = new HashMap<>();
        status.put("activeProvider", llmProviderFactory.getActiveProviderName());
        status.put("status", "running");
        return ResponseEntity.ok(status);
    }
}
