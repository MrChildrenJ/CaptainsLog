# Captain's Log AI Models - Implementation Plan

## Speech-to-Text Model

### Primary Model: OpenAI Whisper
- **Model Version**: `whisper-large-v3`
- **Size**: 1.5GB
- **Languages**: 99+ languages supported
- **License**: MIT

### Why Whisper?
- State-of-the-art accuracy
- Works offline (no API costs)
- Handles background noise well
- Automatic punctuation and capitalization
- Speaker diarization capabilities

### Implementation Details
```python
import whisper

model = whisper.load_model("large-v3")
result = model.transcribe(
    audio_path,
    language="en",  # Auto-detect if not specified
    task="transcribe",
    temperature=0.0,  # Deterministic output
    word_timestamps=True  # For playback sync
)
```

### Fine-Tuning Requirements
**Not Required** - Whisper performs excellently out-of-the-box for general speech

**Optional Fine-Tuning** for:
- Star Trek specific terminology
- Technical/scientific jargon
- User-specific accents

### Data Requirements for Fine-Tuning
If fine-tuning is desired:
- 10-50 hours of audio with transcriptions
- Focus on domain-specific vocabulary
- Format: WAV/MP3 files + JSON transcriptions
- Example dataset structure:
  ```json
  {
    "audio_file": "log_001.wav",
    "transcription": "Captain's log, stardate 41153.7...",
    "segments": [
      {"start": 0.0, "end": 2.5, "text": "Captain's log"},
      {"start": 2.5, "end": 4.2, "text": "stardate 41153.7"}
    ]
  }
  ```

## Search Enhancement Model

### Primary Model: Sentence-BERT
- **Model**: `all-MiniLM-L6-v2`
- **Size**: 80MB
- **Purpose**: Semantic search capabilities
- **License**: Apache 2.0

### Implementation
```python
from sentence_transformers import SentenceTransformer

model = SentenceTransformer('all-MiniLM-L6-v2')

# Index creation
log_embeddings = model.encode(log_transcriptions)

# Search
query_embedding = model.encode(search_query)
similarities = cosine_similarity(query_embedding, log_embeddings)
```

### Why This Model?
- Fast inference (5ms per query)
- Good balance of speed/accuracy
- Understands semantic meaning
- Works well for conversational text

### Fine-Tuning Strategy
**Recommended** - Improve search relevance

**Training Data**:
- 1000+ query-log pairs
- Examples:
  ```json
  {
    "query": "what did I think about the gamma quadrant?",
    "relevant_logs": ["log_042", "log_156", "log_203"],
    "negative_samples": ["log_001", "log_099"]
  }
  ```

## Audio Enhancement (Optional)

### Noise Reduction: RNNoise
- **Model**: RNNoise neural network
- **Size**: 20MB
- **Purpose**: Clean audio before transcription

### Implementation
```python
import noisereduce as nr

# Reduce noise before transcription
reduced_noise_audio = nr.reduce_noise(
    y=audio_data, 
    sr=sample_rate,
    stationary=False
)
```

## Content Moderation (Optional)

### Model: Transformers Safety Classifier
- **Model**: `unitary/toxic-bert`
- **Size**: 110MB
- **Purpose**: Filter inappropriate content

### Implementation
```python
from transformers import pipeline

classifier = pipeline(
    "text-classification", 
    model="unitary/toxic-bert"
)

# Check transcription
result = classifier(transcription_text)
if result[0]['score'] > 0.9:
    flag_for_review()
```

## Model Deployment Strategy

### Container Configuration
```dockerfile
# Transcription Service Dockerfile
FROM python:3.11-slim

# Install system dependencies
RUN apt-get update && apt-get install -y ffmpeg

# Install Python packages
RUN pip install whisper transformers sentence-transformers

# Download models at build time
RUN python -c "import whisper; whisper.load_model('large-v3')"
RUN python -c "from sentence_transformers import SentenceTransformer; SentenceTransformer('all-MiniLM-L6-v2')"

# Copy application code
COPY . /app
WORKDIR /app

CMD ["python", "transcription_service.py"]
```

### GPU Optimization
```yaml
# docker-compose.yml addition
transcription-service:
  build: ./services/transcription
  deploy:
    resources:
      reservations:
        devices:
          - driver: nvidia
            count: 1
            capabilities: [gpu]
```

### Performance Optimization

1. **Batch Processing**
   - Queue multiple audio files
   - Process in batches of 5-10
   - Reduces model loading overhead

2. **Model Caching**
   - Keep models in memory
   - Use Redis for transcription cache
   - Cache embeddings for search

3. **Preprocessing Pipeline**
   ```python
   def preprocess_audio(file_path):
       # Convert to 16kHz mono
       # Normalize audio levels
       # Trim silence
       # Split long files (>30min)
       return processed_audio
   ```

## Data Collection for Future Improvements

### Metrics to Track
- Transcription accuracy (user corrections)
- Search relevance (click-through rate)
- Processing time per minute of audio
- User satisfaction ratings

### Privacy-Preserving Collection
```python
# Anonymized feedback collection
{
    "audio_duration": 120,
    "transcription_length": 450,
    "corrections_made": 3,
    "search_queries": ["hashed_query_1", "hashed_query_2"],
    "relevant_results_clicked": [0, 2]  # positions
}
```

## Alternative Models (Backup Options)

### Speech Recognition
1. **SpeechRecognition library** with Google API
   - Pros: Easy integration, good accuracy
   - Cons: Requires internet, API costs

2. **Mozilla DeepSpeech**
   - Pros: Open source, offline
   - Cons: Lower accuracy than Whisper

### Search
1. **Universal Sentence Encoder**
   - Pros: Better accuracy
   - Cons: Larger model (1GB)

2. **BM25 (Traditional)**
   - Pros: Fast, no neural network needed
   - Cons: No semantic understanding

## Estimated Resource Requirements

### Minimum (CPU only)
- RAM: 8GB
- Storage: 5GB for models
- Processing: ~2x real-time

### Recommended (with GPU)
- RAM: 16GB
- GPU: NVIDIA GTX 1060 or better
- Storage: 10GB for models + cache
- Processing: ~10x real-time
