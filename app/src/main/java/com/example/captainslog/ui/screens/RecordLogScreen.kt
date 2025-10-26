package com.example.captainslog.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.captainslog.viewmodel.RecordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordLogScreen(
    onSave: () -> Unit,
    onCancel: () -> Unit,
    viewModel: RecordViewModel = viewModel()
) {
    val isRecording by viewModel.isRecording.collectAsState()
    val recordingDuration by viewModel.recordingDuration.collectAsState()
    var title by remember { mutableStateOf("") }
    var showSaveDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Record Captain's Log") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Cancel")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Recording indicator
            Text(
                text = if (isRecording) "Recording..." else "Ready to Record",
                style = MaterialTheme.typography.headlineSmall,
                color = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Timer display
            Text(
                text = formatTime(recordingDuration),
                style = MaterialTheme.typography.displayLarge
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Record/Stop button
            FloatingActionButton(
                onClick = {
                    if (isRecording) {
                        viewModel.stopRecording()
                        showSaveDialog = true
                    } else {
                        viewModel.startRecording()
                    }
                },
                modifier = Modifier.size(80.dp),
                containerColor = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = if (isRecording) "Stop Recording" else "Start Recording",
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isRecording) "Tap to stop" else "Tap to start",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    // Save dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Log Entry") },
            text = {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    placeholder = { Text("Enter a title for this log...") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.saveLog(title)
                            onSave()
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.discardRecording()
                        showSaveDialog = false
                        onCancel()
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
