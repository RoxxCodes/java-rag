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

@Slf4j
@Configuration
@RequiredArgsConstructor
public class LangChainConfig {

    private final LlmProviderFactory llmProviderFactory;

    @Value("${llm.embedding-dimension:384}")
    private int embeddingDimension;

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

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
    public EmbeddingStore embeddingStore() {
        log.info("Creating embedding store with dimension: {}", embeddingDimension);
        // Parse host and port from JDBC URL (format: jdbc:postgresql://host:port/database)
        String url = jdbcUrl.replace("jdbc:postgresql://", "");
        String[] hostPortAndDb = url.split("/");
        String[] hostAndPort = hostPortAndDb[0].split(":");
        String host = hostAndPort[0];
        int port = hostAndPort.length > 1 ? Integer.parseInt(hostAndPort[1]) : 5432;
        String database = hostPortAndDb.length > 1 ? hostPortAndDb[1] : "ragdb";

        return PgVectorEmbeddingStore.builder()
                .host(host)
                .port(port)
                .database(database)
                .user(username)
                .password(password)
                .table("embeddings")
                .dimension(embeddingDimension)
                .build();
    }
}
