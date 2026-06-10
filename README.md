# RAG Application with Spring Boot and LangChain4j

A Retrieval-Augmented Generation (RAG) application built with Java Spring Boot, LangChain4j, and PostgreSQL with pgvector.

Supports multiple LLM providers: **Google Gemini** (default, free tier) and **OpenAI**.

## Architecture

```
PDF Upload → Parse & Split → Generate Embeddings → Store in PostgreSQL (pgvector)
                                                                    ↓
Question API → Embed Question → Similarity Search → Retrieve Context → LLM Answer
```

## Features

- **Multi-Provider LLM Support**: Switch between Google Gemini and OpenAI at runtime
- **Free Tier Support**: Default to Google Gemini (60 requests/minute free)
- **Local Embeddings**: Uses all-MiniLM-L6-v2 (no API calls for embeddings)
- **PostgreSQL with pgvector**: Vector similarity search

## Prerequisites

- Java 21
- Maven
- Docker (for PostgreSQL)
- API Key (one of the following):
  - Google AI API Key (free): https://aistudio.google.com/app/apikey
  - OpenAI API Key (paid): https://platform.openai.com/api-keys

## Quick Start

### 1. Configure API Key

**Option A: Google Gemini (Recommended - Free)**
```bash
export GOOGLE_API_KEY="your-google-api-key-here"
```

**Option B: OpenAI**
```bash
export OPENAI_API_KEY="your-openai-api-key-here"
```

### 2. Start PostgreSQL

Your existing PostgreSQL Docker container `tribe-pg` is already configured with pgvector.

### 3. Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

### 4. Check Status

```bash
# Check which LLM provider is active
curl http://localhost:8080/api/status
```

## Switching LLM Providers

### Method 1: Via Configuration (application.yml)

```yaml
llm:
  provider: google  # or "openai"
```

### Method 2: Via Environment Variable

```bash
export LLM_PROVIDER=google  # or "openai"
```

### Fallback Behavior

If the preferred provider is not available (API key not set), the app automatically falls back to the first available provider.

## API Endpoints

### Upload PDF Document

```bash
curl -X POST -F "file=@document.pdf" http://localhost:8080/api/documents/upload
```

### Ask a Question

```bash
curl -X POST http://localhost:8080/api/ask \
  -H "Content-Type: application/json" \
  -d '{"question": "What is the main topic of the document?"}'
```

### Check Status

```bash
curl http://localhost:8080/api/status
```

## Provider Comparison

| Provider | Chat Model | Embeddings | Cost | Setup |
|----------|-----------|------------|------|-------|
| **Google Gemini** | `gemini-2.0-flash` | Local (all-MiniLM) | Free tier (60 req/min) | API key |
| **OpenAI** | `gpt-4o-mini` | `text-embedding-ada-002` | Paid | API key |

## Project Structure

```
├── src/main/java/com/example/rag/
│   ├── RagApplication.java
│   ├── config/
│   │   └── LangChainConfig.java       # Provider configuration
│   ├── controller/
│   │   ├── DocumentController.java
│   │   └── RagController.java
│   ├── dto/
│   │   ├── AnswerResponse.java
│   │   ├── QuestionRequest.java
│   │   └── UploadResponse.java
│   ├── llm/                           # LLM Provider Interfaces
│   │   ├── LlmProvider.java           # Interface
│   │   ├── LlmProviderFactory.java    # Factory for switching
│   │   ├── GoogleGeminiProvider.java  # Google implementation
│   │   └── OpenAiProvider.java        # OpenAI implementation
│   └── service/
│       ├── DocumentIngestionService.java
│       └── RagService.java
```

## How It Works

1. **Document Ingestion**:
   - PDF parsed using Apache PDFBox
   - Text split into chunks
   - Embeddings generated locally (all-MiniLM-L6-v2)
   - Stored in PostgreSQL with pgvector

2. **Question Answering**:
   - Question converted to embedding
   - Similar documents retrieved using cosine similarity
   - Context sent to active LLM provider
   - Answer returned

## Adding New Providers

To add a new LLM provider:

1. Implement `LlmProvider` interface:
```java
@Component
public class OllamaProvider implements LlmProvider {
    // Implement methods
}
```

2. Add dependency to `pom.xml`

3. The provider is automatically registered and available for selection.

## Configuration

| Property | Description | Default |
|----------|-------------|---------|
| `llm.provider` | Preferred provider (`google`, `openai`) | `google` |
| `llm.embedding-dimension` | Vector dimension (384 or 1536) | `384` |
| `google.api-key` | Google AI API key | - |
| `google.model` | Gemini model | `gemini-2.0-flash` |
| `openai.api-key` | OpenAI API key | - |
| `openai.model` | OpenAI chat model | `gpt-4o-mini` |

## License

MIT
