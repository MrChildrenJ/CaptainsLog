# Captain's Log Android App - Component Plan

## Activities

### MainActivity
```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?)
}
```

## Composables

### Main Navigation
```kotlin
@Composable
fun MainScreen(navController: NavController)

@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
)
```

### Log Entry Screens
```kotlin
@Composable
fun LogListScreen(
    viewModel: LogViewModel,
    onLogClick: (LogEntry) -> Unit,
    onCreateNew: () -> Unit
)

@Composable
fun LogEntryItem(
    logEntry: LogEntry,
    onClick: () -> Unit
)

@Composable
fun RecordLogScreen(
    viewModel: RecordViewModel,
    onSave: () -> Unit,
    onCancel: () -> Unit
)

@Composable
fun LogDetailScreen(
    logEntry: LogEntry,
    viewModel: LogDetailViewModel,
    onShare: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
)

@Composable
fun PlaybackControls(
    isPlaying: Boolean,
    progress: Float,
    onPlayPause: () -> Unit,
    onSeek: (Float) -> Unit
)
```

### Social Features
```kotlin
@Composable
fun FriendsListScreen(
    viewModel: FriendsViewModel,
    onFriendClick: (User) -> Unit
)

@Composable
fun ShareLogDialog(
    friends: List<User>,
    onShare: (List<User>) -> Unit,
    onDismiss: () -> Unit
)

@Composable
fun SharedLogsScreen(
    viewModel: SharedLogsViewModel,
    onLogClick: (LogEntry) -> Unit
)
```

### Search
```kotlin
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onResultClick: (LogEntry) -> Unit
)

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
)
```

## ViewModels

### LogViewModel
```kotlin
class LogViewModel(
    private val logRepository: LogRepository
) : ViewModel() {
    val logs: StateFlow<List<LogEntry>>
    fun loadLogs()
    fun deleteLog(logId: String)
}
```

### RecordViewModel
```kotlin
class RecordViewModel(
    private val audioRepository: AudioRepository,
    private val transcriptionRepository: TranscriptionRepository
) : ViewModel() {
    val isRecording: StateFlow<Boolean>
    val recordingDuration: StateFlow<Long>
    fun startRecording()
    fun stopRecording()
    fun saveLog(title: String)
    fun discardRecording()
}
```

### LogDetailViewModel
```kotlin
class LogDetailViewModel(
    private val logRepository: LogRepository,
    private val audioRepository: AudioRepository
) : ViewModel() {
    val playbackState: StateFlow<PlaybackState>
    val transcription: StateFlow<String>
    fun playAudio()
    fun pauseAudio()
    fun seekTo(position: Long)
    fun shareLog(friendIds: List<String>)
}
```

### FriendsViewModel
```kotlin
class FriendsViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    val friends: StateFlow<List<User>>
    val friendRequests: StateFlow<List<FriendRequest>>
    fun loadFriends()
    fun sendFriendRequest(userId: String)
    fun acceptRequest(requestId: String)
}
```

### SearchViewModel
```kotlin
class SearchViewModel(
    private val searchRepository: SearchRepository
) : ViewModel() {
    val searchResults: StateFlow<List<LogEntry>>
    val isSearching: StateFlow<Boolean>
    fun search(query: String)
}
```

### SharedLogsViewModel
```kotlin
class SharedLogsViewModel(
    private val logRepository: LogRepository
) : ViewModel() {
    val sharedLogs: StateFlow<List<LogEntry>>
    fun loadSharedLogs()
}
```

## Repositories

### LogRepository
```kotlin
interface LogRepository {
    suspend fun getAllLogs(): Flow<List<LogEntry>>
    suspend fun getLog(id: String): LogEntry?
    suspend fun saveLog(logEntry: LogEntry)
    suspend fun deleteLog(id: String)
    suspend fun getSharedLogs(): Flow<List<LogEntry>>
    suspend fun shareLog(logId: String, userIds: List<String>)
}
```

### AudioRepository
```kotlin
interface AudioRepository {
    suspend fun startRecording(): String // Returns recording ID
    suspend fun stopRecording(recordingId: String): File
    suspend fun getAudioFile(logId: String): File
    suspend fun deleteAudioFile(logId: String)
}
```

### TranscriptionRepository
```kotlin
interface TranscriptionRepository {
    suspend fun transcribeAudio(audioFile: File): String
    suspend fun getTranscription(logId: String): String?
    suspend fun saveTranscription(logId: String, text: String)
}
```

### UserRepository
```kotlin
interface UserRepository {
    suspend fun getCurrentUser(): User
    suspend fun getFriends(): Flow<List<User>>
    suspend fun searchUsers(query: String): List<User>
    suspend fun sendFriendRequest(userId: String)
    suspend fun acceptFriendRequest(requestId: String)
    suspend fun getFriendRequests(): Flow<List<FriendRequest>>
}
```

### SearchRepository
```kotlin
interface SearchRepository {
    suspend fun searchLogs(query: String): List<LogEntry>
    suspend fun searchByDateRange(start: Date, end: Date): List<LogEntry>
}
```

## Data Models

```kotlin
data class LogEntry(
    val id: String,
    val userId: String,
    val title: String,
    val audioFileUrl: String,
    val transcription: String?,
    val createdAt: Date,
    val duration: Long,
    val isShared: Boolean,
    val sharedWith: List<String> = emptyList()
)

data class User(
    val id: String,
    val username: String,
    val displayName: String,
    val profilePictureUrl: String?
)

data class FriendRequest(
    val id: String,
    val fromUserId: String,
    val toUserId: String,
    val status: RequestStatus,
    val createdAt: Date
)

enum class RequestStatus {
    PENDING, ACCEPTED, REJECTED
}

data class PlaybackState(
    val isPlaying: Boolean,
    val currentPosition: Long,
    val duration: Long
)
```

## Services

### AudioRecordingService
```kotlin
class AudioRecordingService {
    fun startRecording(): String
    fun stopRecording(): File
    fun pauseRecording()
    fun resumeRecording()
}
```

### AudioPlaybackService
```kotlin
class AudioPlaybackService {
    fun playAudio(file: File)
    fun pause()
    fun seekTo(position: Long)
    fun getCurrentPosition(): Long
    fun getDuration(): Long
    fun release()
}
```

### TranscriptionService
```kotlin
interface TranscriptionService {
    suspend fun transcribe(audioFile: File): String
}
```

## Dependency Injection

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideLogRepository(): LogRepository
    
    @Provides
    fun provideAudioRepository(): AudioRepository
    
    @Provides
    fun provideTranscriptionRepository(): TranscriptionRepository
    
    @Provides
    fun provideUserRepository(): UserRepository
    
    @Provides
    fun provideSearchRepository(): SearchRepository
}
```

## Navigation

```kotlin
sealed class Screen(val route: String) {
    object LogList : Screen("log_list")
    object Record : Screen("record")
    object LogDetail : Screen("log_detail/{logId}")
    object Friends : Screen("friends")
    object Search : Screen("search")
    object SharedLogs : Screen("shared_logs")
}
```
