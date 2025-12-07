package com.example.zenapp.ui.settings

import android.content.Context
import android.content.Intent
import android.provider.Settings
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zenapp.ui.appblock.AppBlockActivity

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    var isAccessibilityEnabled by remember { 
        mutableStateOf(isAccessibilityServiceEnabled(context)) 
    }
    var isDisabled by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var pauseDuration by remember { mutableStateOf("15 minutos") }
    var remainingTime by remember { mutableStateOf(pauseDuration) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Ajustes",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFE1E1E1),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Mostrar card de accesibilidad solo si no está activo
        if (!isAccessibilityEnabled) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E1E1E)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Servicio requerido",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFFE1E1E1)
                            )
                            Text(
                                text = "Activar para detectar apps bloqueadas",
                                fontSize = 13.sp,
                                color = Color(0xFF888888),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        
                        Button(
                            onClick = {
                                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                                context.startActivity(intent)
                            },
                            modifier = Modifier.height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFBB86FC)
                            )
                        ) {
                            Text(
                                text = "Activar",
                                color = Color(0xFF000000),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    Text(
                        text = "1. Ve a Accesibilidad en la configuración que se abrirá\n" +
                              "2. Busca y selecciona \"Zen App\"\n" +
                              "3. Activa el servicio y acepta los permisos",
                        fontSize = 13.sp,
                        color = Color(0xFF666666),
                        lineHeight = 20.sp
                    )
                    
                    TextButton(
                        onClick = { 
                            isAccessibilityEnabled = isAccessibilityServiceEnabled(context)
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "Ya lo activé",
                            color = Color(0xFFBB86FC),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
        
        // Deshabilitar todos los bloqueos Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
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
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
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

                SpinnerDropdown(
                    options = listOf("5 minutos", "15 minutos", "30 minutos", "1 hora", "2 horas", "4 horas"),
                    selectedOption = pauseDuration,
                    onOptionSelected = {
                        pauseDuration = it
                        remainingTime = it
                    },
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (isPaused) {
                    Text(
                        text = "Tiempo restante $remainingTime",
                        fontSize = 14.sp,
                        color = Color(0xFFE1E1E1),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Button(
                    onClick = {
                        isPaused = !isPaused
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2D2D2D)
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
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
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

        // Vista Previa Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
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
        
        // Instrucciones Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Privacidad",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFE1E1E1)
                )
                
                Text(
                    text = "ZenApp solo detecta el nombre de las apps que abres. No lee contenido, no registra pulsaciones de teclado, y no recopila información personal.",
                    fontSize = 13.sp,
                    color = Color(0xFF888888),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

private fun isAccessibilityServiceEnabled(context: Context): Boolean {
    val service = "${context.packageName}/${context.packageName}.service.AppBlockerService"
    val enabledServices = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    )
    return enabledServices?.contains(service) == true
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

