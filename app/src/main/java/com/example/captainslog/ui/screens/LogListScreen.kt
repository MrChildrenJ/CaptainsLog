package com.example.captainslog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.captainslog.data.models.LogEntry
import com.example.captainslog.viewmodel.LogViewModel
import com.example.captainslog.viewmodel.RecordViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogListScreen(
    onLogClick: (String) -> Unit,
    onCreateNew: () -> Unit,
    viewModel: LogViewModel = viewModel(),
    recordViewModel: RecordViewModel = viewModel()
) {
    val logs by viewModel.logs.collectAsState()
    val isRecording by recordViewModel.isRecording.collectAsState()
    val recordingDuration by recordViewModel.recordingDuration.collectAsState()
    var showSaveDialog by remember { mutableStateOf(false) }
    var recordingTitle by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Captain's Logs") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Log list content
            if (logs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No logs yet. Tap the red button to record!")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(logs) { log ->
                        LogEntryItem(
                            logEntry = log,
                            onClick = { onLogClick(log.id) }
                        )
                    }
                }
            }

            // Recording status overlay (when recording)
            if (isRecording) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 180.dp)
                        .background(
                            MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Recording...",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = formatTime(recordingDuration),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }

            // Red circle record button at bottom center
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(if (isRecording) Color(0xFFEF5350) else Color(0xFFD32F2F))
                    .clickable {
                        if (isRecording) {
                            recordViewModel.stopRecording()
                            showSaveDialog = true
                        } else {
                            recordViewModel.startRecording()
                        }
                    }
            ) {
                if (isRecording) {
                    // Show square stop icon when recording
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(24.dp)
                            .background(Color.White, shape = MaterialTheme.shapes.small)
                    )
                }
            }
        }
    }

    // Save dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Log Entry") },
            text = {
                OutlinedTextField(
                    value = recordingTitle,
                    onValueChange = { recordingTitle = it },
                    label = { Text("Title") },
                    placeholder = { Text("Enter a title for this log...") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (recordingTitle.isNotBlank()) {
                            recordViewModel.saveLog(recordingTitle)
                            recordingTitle = ""
                            showSaveDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        recordViewModel.discardRecording()
                        recordingTitle = ""
                        showSaveDialog = false
                    }
                ) {
                    Text("Discard")
                }
            }
        )
    }
}

private fun formatTime(seconds: Long): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

@Composable
fun LogEntryItem(
    logEntry: LogEntry,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = logEntry.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = formatDuration(logEntry.duration),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (logEntry.transcription != null) {
                Text(
                    text = logEntry.transcription,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatDate(logEntry.createdAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (logEntry.isShared) {
                    Text(
                        text = "Shared",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

private fun formatDate(date: java.util.Date): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(date)
}

private fun formatDuration(seconds: Long): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%d:%02d", minutes, remainingSeconds)
}
