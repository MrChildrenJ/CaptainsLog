# Captain's Log Backend - System Plan

## API Routes

### Authentication
```
POST   /api/auth/register
POST   /api/auth/login
POST   /api/auth/refresh
POST   /api/auth/logout
```

### User Management
```
GET    /api/users/me
PUT    /api/users/me
GET    /api/users/search?q={query}
GET    /api/users/{userId}
```

### Friends
```
GET    /api/friends
POST   /api/friends/requests
GET    /api/friends/requests
PUT    /api/friends/requests/{requestId}/accept
PUT    /api/friends/requests/{requestId}/reject
DELETE /api/friends/{friendId}
```

### Log Entries
```
GET    /api/logs                    # Get user's logs
POST   /api/logs                    # Create new log
GET    /api/logs/{logId}           # Get specific log
PUT    /api/logs/{logId}           # Update log
DELETE /api/logs/{logId}           # Delete log
GET    /api/logs/shared            # Get logs shared with user
POST   /api/logs/{logId}/share     # Share log with friends
```

### Audio
```
POST   /api/audio/upload           # Upload audio file
GET    /api/audio/{audioId}        # Stream audio file
DELETE /api/audio/{audioId}        # Delete audio file
```

### Transcription
```
POST   /api/transcribe             # Request transcription
GET    /api/transcribe/{jobId}     # Get transcription status/result
```

### Search
```
GET    /api/search?q={query}       # Search logs by content
GET    /api/search/date?from={date}&to={date}  # Search by date range
```

## Services Architecture

### API Gateway Service
- **Technology**: Node.js with Express
- **Port**: 3000
- **Responsibilities**:
  - Route requests to appropriate services
  - Handle authentication/authorization
  - Rate limiting
  - Request validation

### Auth Service
- **Technology**: Node.js
- **Port**: 3001
- **Database**: PostgreSQL (users table)
- **Responsibilities**:
  - User registration/login
  - JWT token generation/validation
  - Session management

### Log Service
- **Technology**: Node.js
- **Port**: 3002
- **Database**: PostgreSQL (logs, shares tables)
- **Responsibilities**:
  - CRUD operations for logs
  - Sharing logic
  - Access control

### Audio Service
- **Technology**: Python with FastAPI
- **Port**: 3003
- **Storage**: S3-compatible object storage
- **Responsibilities**:
  - Audio file upload/download
  - Audio format validation
  - File compression

### Transcription Service
- **Technology**: Python
- **Port**: 3004
- **ML Model**: Whisper (OpenAI)
- **Responsibilities**:
  - Audio to text conversion
  - Async job processing
  - Result caching

### Search Service
- **Technology**: Node.js
- **Port**: 3005
- **Database**: Elasticsearch
- **Responsibilities**:
  - Full-text search
  - Index management
  - Search relevance scoring

### Notification Service
- **Technology**: Node.js
- **Port**: 3006
- **Queue**: Redis
- **Responsibilities**:
  - Push notifications
  - Email notifications
  - Real-time updates via WebSocket

## Container Architecture

```yaml
version: '3.8'

services:
  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
    
  api-gateway:
    build: ./services/api-gateway
    ports:
      - "3000:3000"
    environment:
      - JWT_SECRET=${JWT_SECRET}
    
  auth-service:
    build: ./services/auth
    environment:
      - DATABASE_URL=postgresql://user:pass@postgres:5432/auth
    
  log-service:
    build: ./services/logs
    environment:
      - DATABASE_URL=postgresql://user:pass@postgres:5432/logs
    
  audio-service:
    build: ./services/audio
    environment:
      - S3_BUCKET=${S3_BUCKET}
      - AWS_ACCESS_KEY=${AWS_ACCESS_KEY}
    
  transcription-service:
    build: ./services/transcription
    environment:
      - MODEL_PATH=/models/whisper
    volumes:
      - ./models:/models
    
  search-service:
    build: ./services/search
    environment:
      - ELASTICSEARCH_URL=http://elasticsearch:9200
    
  notification-service:
    build: ./services/notification
    environment:
      - REDIS_URL=redis://redis:6379
    
  postgres:
    image: postgres:15
    environment:
      - POSTGRES_PASSWORD=${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    
  elasticsearch:
    image: elasticsearch:8.11.0
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
    
  redis:
    image: redis:7-alpine
    
  minio:
    image: minio/minio
    command: server /data
    environment:
      - MINIO_ACCESS_KEY=${MINIO_ACCESS_KEY}
      - MINIO_SECRET_KEY=${MINIO_SECRET_KEY}
```

## Service Communication

### Synchronous Communication
- API Gateway → All Services: REST/HTTP
- Services use internal DNS names (service discovery)
- Authentication via JWT tokens passed in headers

### Asynchronous Communication
- Message Queue: Redis Pub/Sub
- Events:
  - `log.created` - Triggers transcription and search indexing
  - `transcription.completed` - Updates log with transcription
  - `log.shared` - Sends notifications

### Data Flow Examples

1. **Create Log Entry**:
   ```
   Client → API Gateway → Log Service → Database
                      ↓
                  Audio Service → Object Storage
                      ↓
                  Message Queue → Transcription Service
                      ↓
                  Search Service ← Transcription Complete
   ```

2. **Search Logs**:
   ```
   Client → API Gateway → Search Service → Elasticsearch
                                        ↓
                                   Log Service → Get full details
   ```

## Database Schema

### PostgreSQL Tables

**users**

```sql
- id: UUID PRIMARY KEY
- username: VARCHAR(50) UNIQUE
- email: VARCHAR(255) UNIQUE
- password_hash: VARCHAR(255)
- display_name: VARCHAR(100)
- created_at: TIMESTAMP
```

**logs**

```sql
- id: UUID PRIMARY KEY
- user_id: UUID REFERENCES users(id)
- title: VARCHAR(255)
- audio_url: TEXT
- transcription: TEXT
- duration: INTEGER
- created_at: TIMESTAMP
```

**friendships**

```sql
- user_id: UUID REFERENCES users(id)
- friend_id: UUID REFERENCES users(id)
- status: ENUM('pending', 'accepted')
- created_at: TIMESTAMP
- PRIMARY KEY (user_id, friend_id)
```

**log_shares**

```sql
- log_id: UUID REFERENCES logs(id)
- shared_with_user_id: UUID REFERENCES users(id)
- shared_at: TIMESTAMP
- PRIMARY KEY (log_id, shared_with_user_id)
```

### Elasticsearch Index

**logs_index**
```json
{
  "mappings": {
    "properties": {
      "log_id": { "type": "keyword" },
      "user_id": { "type": "keyword" },
      "title": { "type": "text" },
      "transcription": { "type": "text" },
      "created_at": { "type": "date" }
    }
  }
}
```

## Security Considerations

- JWT tokens with 24h expiration
- HTTPS for all external communication
- Input validation on all endpoints
- Rate limiting: 100 requests/minute per user
- Audio files: max 50MB, MP3/WAV only
- Transcription: max 30 minutes per file
