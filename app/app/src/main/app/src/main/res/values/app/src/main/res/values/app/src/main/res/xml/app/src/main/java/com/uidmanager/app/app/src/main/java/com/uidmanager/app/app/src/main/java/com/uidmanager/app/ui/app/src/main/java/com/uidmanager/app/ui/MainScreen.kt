package com.uidmanager.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.uidmanager.app.Entry
import com.uidmanager.app.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onStartOverlay: () -> Unit,
    onStopOverlay: () -> Unit,
    onExport: (List<Entry>) -> Unit
) {
    val entries by viewModel.entries.collectAsState()
    val defaultPwd by viewModel.defaultPassword.collectAsState()
    val activeRowId by viewModel.activeRowId.collectAsState()

    var showPwdDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { showPwdDialog = true }) {
                Text("Settings")
            }
            Button(onClick = { onExport(entries) }) {
                Text("Export XLSX")
            }
            Button(onClick = onStartOverlay) {
                Text("Start Overlay")
            }
            Button(onClick = onStopOverlay) {
                Text("Stop Overlay")
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            "Default Password: ${defaultPwd.ifBlank { "(not set)" }}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(8.dp))

        val horizontal = rememberScrollState()
        val vertical = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color.Gray)
                .verticalScroll(vertical)
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(horizontal)
                    .background(Color(0xFFE8EAED))
                    .padding(vertical = 8.dp, horizontal = 4.dp)
            ) {
                Cell("#", weight = 0.5f, bold = true)
                Cell("UID", weight = 2f, bold = true)
                Cell("Password", weight = 2f, bold = true)
                Cell("Cookies", weight = 4f, bold = true)
                Cell("", weight = 0.8f, bold = true)
            }

            entries.forEachIndexed { index, entry ->
                val isActive = entry.id == activeRowId
                val bgColor = when {
                    isActive -> Color(0xFFFFF8E1)
                    entry.uid.isNotBlank() && entry.cookies.isNotBlank() -> Color(0xFFE8F5E9)
                    else -> Color.White
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(horizontal)
                        .background(bgColor)
                        .border(width = 0.5.dp, color = Color.LightGray)
                        .padding(vertical = 6.dp, horizontal = 4.dp)
                ) {
                    Cell("${index + 1}", weight = 0.5f)
                    Cell(entry.uid, weight = 2f)
                    Cell(entry.password, weight = 2f)
                    Cell(entry.cookies, weight = 4f)
                    IconButton(
                        onClick = { viewModel.deleteRow(entry.id) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        FloatingActionButton(
            onClick = { viewModel.createNewRow() },
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add row")
        }
    }

    if (showPwdDialog) {
        var text by remember { mutableStateOf(defaultPwd) }
        AlertDialog(
            onDismissRequest = { showPwdDialog = false },
            title = { Text("Default Password") },
            text = {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Applied to new rows") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setDefaultPassword(text)
                    showPwdDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showPwdDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun Cell(text: String, weight: Float, bold: Boolean = false) {
    Box(
        modifier = Modifier
            .widthIn(min = 80.dp)
            .fillMaxWidth(weight)
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
