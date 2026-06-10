package com.example.rag.llm;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;

/**
 * Interface for LLM providers to allow switching between different models
 * (OpenAI, Google Gemini, Ollama, etc.) at runtime.
 */
public interface LlmProvider {

    /**
     * Get the chat language model for generating responses
     */
    ChatLanguageModel getChatModel();

    /**
     * Get the embedding model for vectorizing text
     */
    EmbeddingModel getEmbeddingModel();

    /**
     * Get the provider name
     */
    String getProviderName();

    /**
     * Check if this provider is available (API key configured, etc.)
     */
    boolean isAvailable();
}
