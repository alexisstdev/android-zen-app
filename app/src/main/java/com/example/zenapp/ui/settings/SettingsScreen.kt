package com.example.zenapp.ui.settings

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zenapp.ui.appblock.AppBlockActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    var isDisabled by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var pauseDuration by remember { mutableStateOf("15 minutos") }
    var remainingTime by remember { mutableStateOf(pauseDuration) }

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
            text = "Ajustes Rápidos",
            fontSize = 32.sp,
            color = Color(0xFFE1E1E1),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 32.dp)
        )

        // Deshabilitar todos los bloqueos Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
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
                Text(
                    text = "Deshabilitar todos los bloqueos",
                    fontSize = 18.sp,
                    color = Color(0xFFE1E1E1),
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = isDisabled,
                    onCheckedChange = { isDisabled = it }
                )
            }
        }

        // Pausar bloqueos Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Pausar bloqueos temporalmente",
                    fontSize = 18.sp,
                    color = Color(0xFFE1E1E1),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Selecciona cuánto tiempo quieres pausar los bloqueos",
                    fontSize = 13.sp,
                    color = Color(0xFF888888),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Duración de la pausa",
                    fontSize = 16.sp,
                    color = Color(0xFFE1E1E1),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Pause duration dropdown
                SpinnerDropdown(
                    options = listOf("5 minutos", "15 minutos", "30 minutos", "1 hora", "2 horas", "4 horas"),
                    selectedOption = pauseDuration,
                    onOptionSelected = {
                        pauseDuration = it
                        remainingTime = it
                    },
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Remaining time text (only visible when paused)
                if (isPaused) {
                    Text(
                        text = "Tiempo restante $remainingTime",
                        fontSize = 14.sp,
                        color = Color(0xFFE1E1E1),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Pause/Cancel button
                Button(
                    onClick = {
                        isPaused = !isPaused
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPaused) Color(0xFF2D2D2D) else Color(0xFF2D2D2D)
                    )
                ) {
                    Text(
                        text = if (isPaused) "Cancelar Pausa" else "Pausar Ahora",
                        fontSize = 16.sp,
                        color = Color(0xFFE1E1E1)
                    )
                }
            }
        }

        // Estadísticas Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Estadísticas de hoy",
                    fontSize = 18.sp,
                    color = Color(0xFFE1E1E1),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "12",
                            fontSize = 36.sp,
                            color = Color(0xFFE1E1E1)
                        )
                        Text(
                            text = "Bloqueos",
                            fontSize = 13.sp,
                            color = Color(0xFF888888)
                        )
                    }

                    HorizontalDivider(
                        color = Color(0xFF333333),
                        modifier = Modifier
                            .width(1.dp)
                            .height(60.dp)
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "45m",
                            fontSize = 36.sp,
                            color = Color(0xFFE1E1E1)
                        )
                        Text(
                            text = "Tiempo ahorrado",
                            fontSize = 13.sp,
                            color = Color(0xFF888888)
                        )
                    }
                }
            }
        }

        // Testing Preview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Vista Previa de Bloqueo",
                    fontSize = 18.sp,
                    color = Color(0xFFE1E1E1),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Prueba cómo se verá la pantalla de bloqueo",
                    fontSize = 13.sp,
                    color = Color(0xFF888888),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Button(
                    onClick = {
                        val intent = Intent(context, AppBlockActivity::class.java)
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2D2D2D)
                    )
                ) {
                    Text(
                        text = "Ver Preview",
                        fontSize = 16.sp,
                        color = Color(0xFFE1E1E1)
                    )
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

