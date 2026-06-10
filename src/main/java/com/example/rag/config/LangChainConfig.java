package com.example.rag.config;

import com.example.rag.llm.LlmProviderFactory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class LangChainConfig {

    private final LlmProviderFactory llmProviderFactory;

    @Value("${llm.embedding-dimension:384}")
    private int embeddingDimension;

    @Bean
    public EmbeddingModel embeddingModel() {
        log.info("Creating embedding model from provider: {}", llmProviderFactory.getActiveProviderName());
        return llmProviderFactory.getActiveProvider().getEmbeddingModel();
    }

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        log.info("Creating chat model from provider: {}", llmProviderFactory.getActiveProviderName());
        return llmProviderFactory.getActiveProvider().getChatModel();
    }

    @Bean
    public EmbeddingStore embeddingStore(DataSource dataSource) {
        log.info("Creating embedding store with dimension: {}", embeddingDimension);
        return PgVectorEmbeddingStore.builder()
                .dataSource(dataSource)
                .table("embeddings")
                .dimension(embeddingDimension)
                .build();
    }
}
