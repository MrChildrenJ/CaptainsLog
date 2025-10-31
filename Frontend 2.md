# Captain's Log Android Application - Component Design

## Activities

**MainActivity**
- Purpose: Entry point and navigation host
- Responsibilities: Handle permissions, initialize Compose UI

## UI Components (Composables)

| Component | Purpose | Key Features |
|-----------|---------|--------------|
| MainScreen | Navigation container | Bottom navigation, screen routing |
| LogListScreen | Display user's audio logs | List view, search bar, expandable items |
| RecordScreen | Audio recording interface | Record/stop controls, duration display |
| LogDetailScreen | Individual log view | Playback controls, transcription display, share button |
| FriendsScreen | Social features | Friend list, add/remove functionality |
| SearchScreen | Log search interface | Query input, filtered results |
| SharedLogsScreen | View shared logs | Logs from friends |

## ViewModels

| ViewModel | State Management | Core Functions |
|-----------|------------------|----------------|
| LogViewModel | logs: StateFlow<List<LogEntry>><br>isLoading: StateFlow<Boolean> | loadLogs()<br>deleteLog(id)<br>refreshLogs() |
| RecordViewModel | isRecording: StateFlow<Boolean><br>duration: StateFlow<Int> | startRecording()<br>stopRecording()<br>saveLog(title) |
| LogDetailViewModel | currentLog: StateFlow<LogDetail><br>playbackState: StateFlow<PlaybackState> | loadLogDetail(id)<br>playAudio()<br>pauseAudio()<br>shareWithFriends(ids) |
| FriendsViewModel | friends: StateFlow<List<Friend>><br>requests: StateFlow<List<Request>> | loadFriends()<br>sendRequest(username)<br>acceptRequest(id) |
| SearchViewModel | results: StateFlow<List<LogEntry>><br>query: StateFlow<String> | performSearch(query)<br>clearSearch() |

## Repositories

| Repository | Purpose | Main Operations |
|------------|---------|-----------------|
| LogRepository | Log data management | • Create/read/update/delete logs<br>• Handle audio file uploads<br>• Sync with backend |
| AudioRepository | Audio operations | • Record audio<br>• Play audio files<br>• Manage local storage |
| UserRepository | User management | • Authentication<br>• Profile management<br>• Session handling |
| FriendRepository | Social features | • Friend list operations<br>• Request handling<br>• Sharing permissions |
| SearchRepository | Search functionality | • Query processing<br>• Result filtering<br>• Cache management |

## Data Models

**Core Entities**
- `LogEntry`: id, userId, title, audioUrl, transcription, timestamp, duration
- `User`: id, username, displayName, profileImageUrl
- `Friend`: userId, friendId, status, sharedLogsCount
- `FriendRequest`: id, fromUserId, toUserId, status, timestamp
- `PlaybackState`: isPlaying, currentPosition, duration

## Service Classes

| Service | Function |
|---------|----------|
| AudioRecordingService | Handle audio capture using MediaRecorder |
| AudioPlaybackService | Manage playback with ExoPlayer |
| TranscriptionService | Interface with backend transcription API |

## Navigation Structure

```
MainScreen
├── LogListScreen (default)
├── RecordScreen
├── FriendsScreen
├── SearchScreen
└── LogDetailScreen (parameterized by logId)
```

## Dependency Injection Setup

Using Hilt for dependency injection:
- `AppModule`: Provides singleton instances
- `NetworkModule`: API service configuration
- `DatabaseModule`: Local storage setup
