package com.example.zenapp.ui.blocklist

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zenapp.ui.appselection.AppSelectionActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlocklistScreen() {
    val context = LocalContext.current
    var selectedAppsCount by remember { mutableStateOf(0) }
    var customMessage by remember { mutableStateOf("Mantén el enfoque. Esta app estará disponible en un momento.") }
    var pauseTime by remember { mutableStateOf("30 segundos") }
    var dailyOpens by remember { mutableStateOf("3") }
    var sessionDuration by remember { mutableStateOf("10 minutos") }
    var isSaved by remember { mutableStateOf(false) }

    val appSelectionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            @Suppress("DEPRECATION")
            val apps = result.data?.getParcelableArrayListExtra<AppItem>(AppSelectionActivity.EXTRA_RESULT_APPS)
            selectedAppsCount = apps?.size ?: 0
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .padding(bottom = 16.dp)
    ) {
        // Header
        Text(
            text = "Bloqueos",
            fontSize = 32.sp,
            color = Color(0xFFE1E1E1),
            lineHeight = 40.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 32.dp)
        )

        // Antes de abrir section
        Text(
            text = "Antes de abrir",
            fontSize = 20.sp,
            color = Color(0xFFE1E1E1),
            modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
        )

        // Selected Apps Card
        Card(
            onClick = {
                val intent = Intent(context, AppSelectionActivity::class.java)
                appSelectionLauncher.launch(intent)
            },
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
                        text = "$selectedAppsCount app${if (selectedAppsCount != 1) "s" else ""}",
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

        // Pausar por section
        Text(
            text = "Pausar por",
            fontSize = 20.sp,
            color = Color(0xFFE1E1E1),
            modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
        )

        SpinnerDropdown(
            options = listOf("5 segundos", "10 segundos", "30 segundos", "1 minuto", "5 minutos", "10 minutos"),
            selectedOption = pauseTime,
            onOptionSelected = { pauseTime = it },
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Con el mensaje section
        Text(
            text = "Con el mensaje",
            fontSize = 20.sp,
            color = Color(0xFFE1E1E1),
            modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
        )

        OutlinedTextField(
            value = customMessage,
            onValueChange = { customMessage = it },
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

        // Objetivo diario section
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
                selectedOption = dailyOpens,
                onOptionSelected = { dailyOpens = it },
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
                selectedOption = sessionDuration,
                onOptionSelected = { sessionDuration = it },
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "cada vez",
                fontSize = 16.sp,
                color = Color(0xFF888888)
            )
        }

        // Save Button
        Button(
            onClick = {
                isSaved = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(top = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFBB86FC)
            )
        ) {
            Text(
                text = if (isSaved) "✓ Guardado" else "Guardar Configuración",
                fontSize = 16.sp,
                color = Color(0xFF000000)
            )
        }

        // Reset saved state after 2 seconds
        LaunchedEffect(isSaved) {
            if (isSaved) {
                kotlinx.coroutines.delay(2000)
                isSaved = false
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

