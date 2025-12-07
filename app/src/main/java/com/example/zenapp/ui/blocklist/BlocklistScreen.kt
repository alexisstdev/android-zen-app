package com.example.zenapp.ui.blocklist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun BlocklistScreen(
    onNavigateToAppSelection: () -> Unit,
    viewModel: BlocklistViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 16.dp)
        ) {
            // Error message
            if (uiState.errorMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF5C1A1A)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.errorMessage ?: "",
                            fontSize = 14.sp,
                            color = Color(0xFFFF6B6B),
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("OK", color = Color(0xFFBB86FC))
                        }
                    }
                }
            }

            // Dynamic content section
            if (uiState.focusQuote != null || uiState.dailyTask != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💡 Inspiración del día",
                                fontSize = 16.sp,
                                color = Color(0xFFBB86FC)
                            )
                            IconButton(
                                onClick = { viewModel.refreshDynamicContent() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Actualizar",
                                    tint = Color(0xFFBB86FC),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (uiState.focusQuote != null) {
                            Text(
                                text = uiState.focusQuote ?: "",
                                fontSize = 14.sp,
                                color = Color(0xFFE1E1E1),
                                lineHeight = 20.sp
                            )
                        }
                        
                        if (uiState.dailyTask != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "📝 ${uiState.dailyTask}",
                                fontSize = 13.sp,
                                color = Color(0xFF888888),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            Text(
                text = "Antes de abrir",
                fontSize = 20.sp,
                color = Color(0xFFE1E1E1),
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
            )

            Card(
                onClick = onNavigateToAppSelection,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${uiState.selectedApps.size} app${if (uiState.selectedApps.size != 1) "s" else ""}",
                            fontSize = 28.sp,
                            color = Color(0xFFE1E1E1)
                        )
                        Text(
                            text = "Toca para seleccionar apps",
                            fontSize = 14.sp,
                            color = Color(0xFF888888),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Seleccionar apps",
                        tint = Color(0xFF888888),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Text(
                text = "Pausar por",
                fontSize = 20.sp,
                color = Color(0xFFE1E1E1),
                modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
            )

            SpinnerDropdown(
                options = listOf("5 segundos", "10 segundos", "30 segundos", "1 minuto", "5 minutos", "10 minutos"),
                selectedOption = uiState.pauseTime,
                onOptionSelected = viewModel::updatePauseTime,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Con el mensaje",
                fontSize = 20.sp,
                color = Color(0xFFE1E1E1),
                modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
            )

            OutlinedTextField(
                value = uiState.customMessage,
                onValueChange = viewModel::updateCustomMessage,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                minLines = 3,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1E1E1E),
                    unfocusedContainerColor = Color(0xFF1E1E1E),
                    focusedTextColor = Color(0xFFE1E1E1),
                    unfocusedTextColor = Color(0xFFE1E1E1),
                    focusedBorderColor = Color(0xFF333333),
                    unfocusedBorderColor = Color(0xFF333333)
                )
            )

            Text(
                text = "Objetivo diario",
                fontSize = 20.sp,
                color = Color(0xFFE1E1E1),
                modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Abrir cada app",
                    fontSize = 16.sp,
                    color = Color(0xFFE1E1E1)
                )

                SpinnerDropdown(
                    options = listOf("1", "2", "3", "5", "10", "Ilimitado"),
                    selectedOption = uiState.dailyOpens,
                    onOptionSelected = viewModel::updateDailyOpens,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "veces/día",
                    fontSize = 16.sp,
                    color = Color(0xFF888888)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Por",
                    fontSize = 16.sp,
                    color = Color(0xFFE1E1E1)
                )

                SpinnerDropdown(
                    options = listOf("5 minutos", "10 minutos", "15 minutos", "30 minutos", "1 hora"),
                    selectedOption = uiState.sessionDuration,
                    onOptionSelected = viewModel::updateSessionDuration,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "cada vez",
                    fontSize = 16.sp,
                    color = Color(0xFF888888)
                )
            }

            Button(
                onClick = viewModel::saveSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFBB86FC)
                ),
                enabled = !uiState.isSyncing
            ) {
                if (uiState.isSyncing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFF000000),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = when {
                        uiState.isSyncing -> "Sincronizando..."
                        uiState.isSaved -> "✓ Guardado"
                        else -> "Guardar Configuración"
                    },
                    fontSize = 16.sp,
                    color = Color(0xFF000000)
                )
            }

            LaunchedEffect(uiState.isSaved) {
                if (uiState.isSaved) {
                    kotlinx.coroutines.delay(2000)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpinnerDropdown(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1E1E1E),
                unfocusedContainerColor = Color(0xFF1E1E1E),
                focusedTextColor = Color(0xFFE1E1E1),
                unfocusedTextColor = Color(0xFFE1E1E1),
                focusedBorderColor = Color(0xFF333333),
                unfocusedBorderColor = Color(0xFF333333)
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF1E1E1E))
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = Color(0xFFE1E1E1)) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = Color(0xFFE1E1E1)
                    )
                )
            }
        }
    }
}

