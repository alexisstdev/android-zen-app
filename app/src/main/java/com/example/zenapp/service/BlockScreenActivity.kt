package com.example.zenapp.service

import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zenapp.ui.theme.ZenAppTheme

class BlockScreenActivity : ComponentActivity() {
    
    private var countDownTimer: CountDownTimer? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val appName = intent.getStringExtra("APP_NAME") ?: "App"
        val pauseTime = intent.getStringExtra("PAUSE_TIME") ?: "30 segundos"
        val customMessage = intent.getStringExtra("CUSTOM_MESSAGE") 
            ?: "Mantén el enfoque. Esta app estará disponible en un momento."
        
        val pauseMillis = parseTimeToMillis(pauseTime)
        
        setContent {
            ZenAppTheme {
                var remainingSeconds by remember { mutableIntStateOf((pauseMillis / 1000).toInt()) }
                var canProceed by remember { mutableStateOf(false) }
                
                LaunchedEffect(Unit) {
                    countDownTimer = object : CountDownTimer(pauseMillis, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            remainingSeconds = (millisUntilFinished / 1000).toInt()
                        }
                        
                        override fun onFinish() {
                            canProceed = true
                        }
                    }.start()
                }
                
                DisposableEffect(Unit) {
                    onDispose {
                        countDownTimer?.cancel()
                    }
                }
                
                BlockScreen(
                    appName = appName,
                    customMessage = customMessage,
                    remainingSeconds = remainingSeconds,
                    canProceed = canProceed,
                    onClose = { finish() },
                    onProceed = { 
                        // Permitir abrir la app por ahora
                        finish() 
                    }
                )
            }
        }
    }
    
    private fun parseTimeToMillis(timeString: String): Long {
        return when {
            timeString.contains("segundo") -> {
                val seconds = timeString.filter { it.isDigit() }.toLongOrNull() ?: 30
                seconds * 1000
            }
            timeString.contains("minuto") -> {
                val minutes = timeString.filter { it.isDigit() }.toLongOrNull() ?: 1
                minutes * 60 * 1000
            }
            timeString.contains("hora") -> {
                val hours = timeString.filter { it.isDigit() }.toLongOrNull() ?: 1
                hours * 60 * 60 * 1000
            }
            else -> 30000 // Default 30 segundos
        }
    }
    
    override fun onBackPressed() {
        // Prevenir que el usuario salga presionando back
        // Solo permitir cerrar con el botón
    }
}

@Composable
fun BlockScreen(
    appName: String,
    customMessage: String,
    remainingSeconds: Int,
    canProceed: Boolean,
    onClose: () -> Unit,
    onProceed: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)),
        contentAlignment = Alignment.Center
    ) {       
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {              
                Text(
                    text = appName,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE1E1E1),
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = customMessage,
                    fontSize = 16.sp,
                    color = Color(0xFF888888),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Timer
                if (!canProceed) {
                    Card(
                        modifier = Modifier.size(120.dp),
                        shape = RoundedCornerShape(60.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2A2A2A)
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = formatTime(remainingSeconds),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFBB86FC)
                            )
                        }
                    }
                    
                    Text(
                        text = "Espera para continuar",
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Botones
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (canProceed) {
                        Button(
                            onClick = onProceed,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFBB86FC)
                            )
                        ) {
                            Text(
                                text = "Abrir de todas formas",
                                fontSize = 16.sp,
                                color = Color(0xFF000000),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    OutlinedButton(
                        onClick = onClose,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFBB86FC)
                        )
                    ) {
                        Text(
                            text = "Mantener el enfoque",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            
        }
    }
}

private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return if (mins > 0) {
        String.format("%d:%02d", mins, secs)
    } else {
        String.format("%ds", secs)
    }
}
