package com.example.rag.llm;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OpenAiProvider implements LlmProvider {

    @Value("${openai.api-key:}")
    private String apiKey;

    @Value("${openai.model:gpt-4o-mini}")
    private String chatModelName;

    @Value("${openai.embedding-model:text-embedding-ada-002}")
    private String embeddingModelName;

    private ChatLanguageModel chatModel;
    private EmbeddingModel embeddingModel;

    @Override
    public ChatLanguageModel getChatModel() {
        if (chatModel == null) {
            log.info("Initializing OpenAI chat model: {}", chatModelName);
            chatModel = OpenAiChatModel.builder()
                    .apiKey(apiKey)
                    .modelName(chatModelName)
                    .temperature(0.7)
                    .build();
        }
        return chatModel;
    }

    @Override
    public EmbeddingModel getEmbeddingModel() {
        if (embeddingModel == null) {
            log.info("Initializing OpenAI embedding model: {}", embeddingModelName);
            embeddingModel = OpenAiEmbeddingModel.builder()
                    .apiKey(apiKey)
                    .modelName(embeddingModelName)
                    .build();
        }
        return embeddingModel;
    }

    @Override
    public String getProviderName() {
        return "OpenAI";
    }

    @Override
    public boolean isAvailable() {
        return apiKey != null && !apiKey.isBlank();
    }
}
