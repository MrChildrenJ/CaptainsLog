package com.example.captainslog.data.models

data class PlaybackState(
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L, // in milliseconds
    val duration: Long = 0L // in milliseconds
)
