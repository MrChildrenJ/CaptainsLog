package com.example.captainslog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.captainslog.data.models.LogEntry
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class RecordViewModel : ViewModel() {
    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingDuration = MutableStateFlow(0L)
    val recordingDuration: StateFlow<Long> = _recordingDuration.asStateFlow()

    private var timerJob: Job? = null

    fun startRecording() {
        _isRecording.value = true
        _recordingDuration.value = 0L

        // Start timer
        timerJob = viewModelScope.launch {
            while (_isRecording.value) {
                delay(1000)
                _recordingDuration.value += 1
            }
        }
    }

    fun stopRecording() {
        _isRecording.value = false
        timerJob?.cancel()
    }

    fun saveLog(title: String): LogEntry {
        // In a real app, this would save to repository
        val entry = LogEntry(
            id = UUID.randomUUID().toString(),
            userId = "user_1",
            title = title,
            audioFileUrl = "mock://audio/new_recording.mp3",
            transcription = "[Transcription would be generated here...]",
            createdAt = Date(),
            duration = _recordingDuration.value,
            isShared = false,
            sharedWith = emptyList()
        )
        _recordingDuration.value = 0L
        return entry
    }

    fun discardRecording() {
        _recordingDuration.value = 0L
        _isRecording.value = false
        timerJob?.cancel()
    }
}
