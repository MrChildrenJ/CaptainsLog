# Captain's Log Backend System - Architecture Design

## API Gateway Routes

### Authentication Service
| Method | Endpoint | Purpose | Request Body | Response |
|--------|----------|---------|--------------|----------|
| POST | /auth/register | Create new account | {username, email, password} | {userId, token} |
| POST | /auth/login | User authentication | {email, password} | {token, refreshToken} |
| POST | /auth/refresh | Token renewal | {refreshToken} | {token} |
| GET | /auth/verify | Session validation | Authorization header | {valid, userId} |

### Log Management Service
| Method | Endpoint | Purpose | Request/Params | Response |
|--------|----------|---------|----------------|----------|
| GET | /logs | Retrieve user logs | ?page=1&limit=20 | {logs[], total} |
| POST | /logs | Create new log | Multipart: audio file + metadata | {logId, status} |
| GET | /logs/{id} | Get specific log | Path parameter | {log details} |
| PUT | /logs/{id} | Update log info | {title, notes} | {success} |
| DELETE | /logs/{id} | Remove log | Path parameter | {success} |
| POST | /logs/{id}/share | Share with friends | {friendIds[]} | {shareIds[]} |

### Social Features Service
| Method | Endpoint | Purpose | Request/Params | Response |
|--------|----------|---------|----------------|----------|
| GET | /friends | List all friends | - | {friends[]} |
| POST | /friends/request | Send friend request | {targetUsername} | {requestId} |
| GET | /friends/requests | Pending requests | - | {requests[]} |
| PUT | /friends/request/{id} | Accept/reject request | {action: accept/reject} | {success} |
| DELETE | /friends/{id} | Remove friend | Path parameter | {success} |
| GET | /shared | Logs shared with me | ?page=1&limit=20 | {sharedLogs[]} |

### Search Service
| Method | Endpoint | Purpose | Request/Params | Response |
|--------|----------|---------|----------------|----------|
| GET | /search | Search logs | ?q=query&filter=date | {results[]} |
| GET | /search/suggest | Auto-complete | ?q=partial | {suggestions[]} |

## Microservice Architecture

### Service Components

| Service | Technology | Responsibilities | Communication |
|---------|------------|------------------|---------------|
| API Gateway | Node.js/Express | • Request routing<br>• Authentication middleware<br>• Rate limiting<br>• CORS handling | REST endpoints |
| Auth Service | Node.js | • JWT token management<br>• User registration<br>• Password handling<br>• Session control | Internal HTTP |
| Log Service | Node.js | • CRUD operations<br>• File management<br>• Metadata storage<br>• Share logic | Internal HTTP |
| Audio Service | Python/FastAPI | • File validation<br>• Format conversion<br>• Storage handling<br>• Compression | Internal HTTP |
| Transcription Service | Python | • Speech-to-text<br>• Job queuing<br>• Result caching | Message Queue |
| Search Service | Node.js | • Full-text search<br>• Index management<br>• Query optimization | Internal HTTP |

### Data Storage

| Database | Purpose | Schema Overview |
|----------|---------|-----------------|
| PostgreSQL | Primary data | • users table<br>• logs table<br>• friendships table<br>• log_shares table |
| Redis | Caching & Sessions | • Session tokens<br>• Search cache<br>• Transcription queue |
| MinIO/S3 | File storage | • Audio files<br>• User avatars |

### Inter-Service Communication

**Synchronous (REST)**
- API Gateway → All services
- Services use internal DNS names
- JWT tokens passed in headers

**Asynchronous (Message Queue)**
- Log creation → Transcription queue
- Transcription complete → Search indexing
- Share events → Notification queue

## Container Configuration

**Docker Services**
```yaml
services:
  - api-gateway (port: 3000)
  - auth-service (port: 3001)
  - log-service (port: 3002)
  - audio-service (port: 3003)
  - transcription-service (port: 3004)
  - search-service (port: 3005)
  - postgres (port: 5432)
  - redis (port: 6379)
  - minio (port: 9000)
```

## Security Measures

| Aspect | Implementation |
|--------|----------------|
| Authentication | JWT with 24h expiration |
| File Upload | 50MB limit, audio formats only |
| Rate Limiting | 100 requests/minute per user |
| Data Validation | Input sanitization on all endpoints |
| HTTPS | TLS for all external communication |
