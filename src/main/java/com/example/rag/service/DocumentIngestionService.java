package com.example.rag.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentIngestionService {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;

    public void ingestPdf(MultipartFile pdfFile) throws IOException {
        log.info("Processing PDF: {}", pdfFile.getOriginalFilename());

        // 1. Parse PDF
        Document document = loadPdf(pdfFile);

        // 2. Split into chunks
        List<TextSegment> segments = splitDocument(document);

        // 3. Generate embeddings
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

        // 4. Store in vector database
        embeddingStore.addAll(embeddings, segments);

        log.info("Successfully ingested {} segments from {}", segments.size(), pdfFile.getOriginalFilename());
    }

    private Document loadPdf(MultipartFile file) throws IOException {
        ApachePdfBoxDocumentParser parser = new ApachePdfBoxDocumentParser();
        return parser.parse(file.getInputStream());
    }

    private List<TextSegment> splitDocument(Document document) {
        DocumentSplitter splitter = DocumentSplitters.recursive(
                500,
                50
        );
        return splitter.split(document);
    }
}
