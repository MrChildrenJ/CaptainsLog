package com.example.captainslog.viewmodel

import androidx.lifecycle.ViewModel
import com.example.captainslog.data.MockData
import com.example.captainslog.data.models.LogEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchViewModel : ViewModel() {
    private val _searchResults = MutableStateFlow<List<LogEntry>>(emptyList())
    val searchResults: StateFlow<List<LogEntry>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun search(query: String) {
        _isSearching.value = true

        // Simulate search delay
        // In real app: delay(500)

        // Mock search implementation
        _searchResults.value = if (query.isBlank()) {
            emptyList()
        } else {
            MockData.logEntries.filter { log ->
                log.title.contains(query, ignoreCase = true) ||
                log.transcription?.contains(query, ignoreCase = true) == true
            }
        }

        _isSearching.value = false
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }
}
