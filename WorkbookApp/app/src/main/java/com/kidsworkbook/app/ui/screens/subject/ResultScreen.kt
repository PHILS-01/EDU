package com.kidsworkbook.app.ui.screens.subject

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ResultScreen(
    correctAnswers: Int,
    totalQuestions: Int,
    durationSeconds: Long,
    completedAtMillis: Long,
    onDone: () -> Unit
) {
    val scorePercent = if (totalQuestions == 0) 0 else (correctAnswers * 100) / totalQuestions
    val emoji = when {
        scorePercent >= 80 -> "🌟"
        scorePercent >= 50 -> "👍"
        else -> "💪"
    }
    val dateFormatted = remember(completedAtMillis) {
        SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault()).format(Date(completedAtMillis))
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(emoji, fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        Text("You got $correctAnswers of $totalQuestions correct!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Score: $scorePercent%", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(4.dp))
        Text("Time taken: ${durationSeconds}s", style = MaterialTheme.typography.bodyMedium)
        Text("Completed: $dateFormatted", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(32.dp))
        Button(onClick = onDone, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Text("Continue")
        }
    }
}
