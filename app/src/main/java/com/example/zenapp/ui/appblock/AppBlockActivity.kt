package com.example.zenapp.ui.appblock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zenapp.ui.theme.ZenAppTheme
import kotlinx.coroutines.delay

class AppBlockActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZenAppTheme {
                AppBlockScreen(
                    onClose = { finish() }
                )
            }
        }
    }
}

@Composable
fun AppBlockScreen(
    appName: String = "Instagram",
    message: String = "Mantén el enfoque. Esta app estará disponible en un momento.",
    totalOpens: Int = 5,
    remainingOpens: Int = 3,
    waitTimeSeconds: Int = 30,
    onClose: () -> Unit = {}
) {
    var currentRemaining by remember { mutableStateOf(remainingOpens) }
    var isCountingDown by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(waitTimeSeconds) }

    // Countdown effect
    LaunchedEffect(isCountingDown) {
        if (isCountingDown && timeLeft > 0) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            // Countdown finished - app would open here
            isCountingDown = false
            timeLeft = waitTimeSeconds
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Message
            Text(
                text = message,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE1E1E1),
                textAlign = TextAlign.Center,
                lineHeight = 36.sp,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // App name and remaining count in same row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$appName $currentRemaining/$totalOpens restantes",
                    fontSize = 16.sp,
                    color = Color(0xFFAAAAAA)
                )
            }

            // Dots indicator (only if total opens < 10)
            if (totalOpens < 10) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 40.dp)
                ) {
                    repeat(totalOpens) { index ->
                        val isActive = index < currentRemaining
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .then(
                                    if (isActive) {
                                        Modifier.background(Color(0xFFE1E1E1))
                                    } else {
                                        Modifier
                                            .background(Color(0xFF121212))
                                            .border(1.dp, Color(0xFF666666), CircleShape)
                                    }
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Buttons container - always at bottom
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Open button
                Button(
                    onClick = {
                        if (!isCountingDown && currentRemaining > 0) {
                            // Subtract attempt immediately when button is pressed
                            currentRemaining--
                            isCountingDown = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFBB86FC),
                        disabledContainerColor = Color(0xFF666666)
                    ),
                    enabled = !isCountingDown && currentRemaining > 0
                ) {
                    Text(
                        text = when {
                            currentRemaining == 0 && !isCountingDown -> "Límite alcanzado"
                            isCountingDown -> "Abriendo en ${timeLeft}s"
                            else -> "Abrir (esperar ${waitTimeSeconds}s)"
                        },
                        fontSize = 16.sp,
                        color = if (currentRemaining == 0 && !isCountingDown) Color(0xFFCCCCCC) else Color(0xFF000000)
                    )
                }

                // Cancel/Close button - always visible
                TextButton(
                    onClick = {
                        if (isCountingDown) {
                            // Restore attempt if cancelled before countdown finishes
                            currentRemaining++
                            isCountingDown = false
                            timeLeft = waitTimeSeconds
                        } else {
                            onClose()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(
                        text = if (isCountingDown) "Cancelar" else "Cerrar",
                        fontSize = 16.sp,
                        color = Color(0xFFAAAAAA)
                    )
                }
            }
        }
    }
}

