package com.example.rag.llm;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GoogleGeminiProvider implements LlmProvider {

    @Value("${google.api-key:}")
    private String apiKey;

    @Value("${google.model:gemini-2.0-flash}")
    private String modelName;

    private ChatLanguageModel chatModel;
    private EmbeddingModel embeddingModel;

    @Override
    public ChatLanguageModel getChatModel() {
        if (chatModel == null) {
            log.info("Initializing Google Gemini chat model: {}", modelName);
            chatModel = GoogleAiGeminiChatModel.builder()
                    .apiKey(apiKey)
                    .modelName(modelName)
                    .temperature(0.7)
                    .build();
        }
        return chatModel;
    }

    @Override
    public EmbeddingModel getEmbeddingModel() {
        if (embeddingModel == null) {
            log.info("Initializing local embedding model (all-MiniLM-L6-v2)");
            embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        }
        return embeddingModel;
    }

    @Override
    public String getProviderName() {
        return "Google Gemini";
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isBlank();
    }
}
