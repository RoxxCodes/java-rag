package com.example.rag.llm;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Factory to select and manage LLM providers at runtime.
 * Provider priority: google > openai (configurable via application.yml)
 */
@Slf4j
@Component
public class LlmProviderFactory {

    @Value("${llm.provider:google}")
    private String preferredProvider;

    @Autowired
    private List<LlmProvider> providers;

    @Getter
    private LlmProvider activeProvider;

    @PostConstruct
    public void initialize() {
        log.info("Available LLM providers: {}", providers.stream().map(LlmProvider::getProviderName).toList());

        // First, try to use the preferred provider from config
        for (LlmProvider provider : providers) {
            if (provider.getProviderName().toLowerCase().contains(preferredProvider.toLowerCase())
                    && provider.isAvailable()) {
                activeProvider = provider;
                log.info("Using preferred LLM provider: {}", provider.getProviderName());
                return;
            }
        }

        // If preferred is not available, find any available provider
        for (LlmProvider provider : providers) {
            if (provider.isAvailable()) {
                activeProvider = provider;
                log.info("Preferred provider '{}' not available. Using: {}",
                        preferredProvider, provider.getProviderName());
                return;
            }
        }

        throw new IllegalStateException("No LLM provider is available. Please configure at least one API key.");
    }

    public String getActiveProviderName() {
        return activeProvider != null ? activeProvider.getProviderName() : "None";
    }
}
