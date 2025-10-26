package com.example.captainslog.viewmodel

import androidx.lifecycle.ViewModel
import com.example.captainslog.data.MockData
import com.example.captainslog.data.models.LogEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LogViewModel : ViewModel() {
    private val _logs = MutableStateFlow<List<LogEntry>>(MockData.logEntries)
    val logs: StateFlow<List<LogEntry>> = _logs.asStateFlow()

    fun deleteLog(logId: String) {
        _logs.value = _logs.value.filter { it.id != logId }
    }

    fun getLogById(logId: String): LogEntry? {
        return _logs.value.find { it.id == logId }
    }
}
