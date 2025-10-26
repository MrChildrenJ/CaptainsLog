package com.example.captainslog.data.models

import java.util.Date

data class LogEntry(
    val id: String,
    val userId: String,
    val title: String,
    val audioFileUrl: String,
    val transcription: String?,
    val createdAt: Date,
    val duration: Long, // in seconds
    val isShared: Boolean,
    val sharedWith: List<String> = emptyList()
)
