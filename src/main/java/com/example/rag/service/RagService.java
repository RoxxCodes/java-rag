package com.example.rag.service;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RagService {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final ChatLanguageModel chatLanguageModel;

    private static final PromptTemplate RAG_PROMPT = PromptTemplate.from("""
        You are a helpful assistant. Answer the question based only on the provided context.
        If the context doesn't contain relevant information, say "I don't have enough information to answer this question."
        Be concise and accurate.

        Context:
        {{context}}

        Question:
        {{question}}

        Answer:
        """);

    public String answerQuestion(String question) {
        log.info("Processing question: {}", question);

        // 1. Embed the question
        Embedding questionEmbedding = embeddingModel.embed(question).content();

        // 2. Retrieve similar documents (top 5)
        List<EmbeddingMatch<TextSegment>> relevantDocs = embeddingStore
                .findRelevant(questionEmbedding, 5);

        log.info("Retrieved {} relevant documents", relevantDocs.size());

        // 3. Build context from retrieved documents
        String context = buildContext(relevantDocs);

        // 4. Generate answer using LLM
        String answer = generateAnswer(question, context);

        return answer;
    }

    private String buildContext(List<EmbeddingMatch<TextSegment>> matches) {
        StringBuilder context = new StringBuilder();
        for (EmbeddingMatch<TextSegment> match : matches) {
            context.append(match.embedded().text()).append("\n\n");
        }
        return context.toString().trim();
    }

    private String generateAnswer(String question, String context) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("question", question);
        variables.put("context", context);

        Prompt prompt = RAG_PROMPT.apply(variables);

        return chatLanguageModel.generate(prompt.text());
    }
}
