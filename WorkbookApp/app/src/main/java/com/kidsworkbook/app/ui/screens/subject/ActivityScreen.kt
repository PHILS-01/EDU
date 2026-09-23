package com.kidsworkbook.app.ui.screens.subject

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kidsworkbook.app.data.content.models.Activity
import com.kidsworkbook.app.ui.screens.home.colorFor

/**
 * Presents each question one at a time. Start time is captured the moment
 * the composable enters composition; duration is computed automatically
 * when the last question is answered — the student/teacher never enters
 * a date, time, or duration themselves.
 */
@Composable
fun ActivityScreen(
    activity: Activity,
    onComplete: (correctAnswers: Int, totalQuestions: Int, durationSeconds: Long) -> Unit,
    onExit: () -> Unit
) {
    val startTimeMillis = remember { System.currentTimeMillis() }
    var currentIndex by remember { mutableStateOf(0) }
    var correctCount by remember { mutableStateOf(0) }
    var selectedChoice by remember { mutableStateOf<Int?>(null) }
    var showFeedback by remember { mutableStateOf(false) }

    val question = activity.questions.getOrNull(currentIndex)

    if (question == null) {
        // Finished — auto-stamp duration and report result upward.
        LaunchedEffect(Unit) {
            val durationSeconds = (System.currentTimeMillis() - startTimeMillis) / 1000
            onComplete(correctCount, activity.questions.size, durationSeconds)
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        TextButton(onClick = onExit) { Text("← Exit activity") }

        LinearProgressIndicator(
            progress = (currentIndex).toFloat() / activity.questions.size,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            color = colorFor(activity.subject)
        )

        Text(
            "Question ${currentIndex + 1} of ${activity.questions.size}",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(16.dp))
        Text(question.prompt, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))

        question.choices.forEachIndexed { index, choice ->
            val isSelected = selectedChoice == index
            val isCorrectChoice = index == question.correctIndex

            val containerColor = when {
                !showFeedback -> MaterialTheme.colorScheme.surfaceVariant
                isSelected && isCorrectChoice -> androidx.compose.ui.graphics.Color(0xFFB9F6CA)
                isSelected && !isCorrectChoice -> androidx.compose.ui.graphics.Color(0xFFFFCDD2)
                isCorrectChoice -> androidx.compose.ui.graphics.Color(0xFFB9F6CA)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }

            Card(
                onClick = {
                    if (!showFeedback) {
                        selectedChoice = index
                        showFeedback = true
                        if (isCorrectChoice) correctCount++
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = containerColor),
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
            ) {
                Text(choice, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(Modifier.weight(1f))

        if (showFeedback) {
            Button(
                onClick = {
                    selectedChoice = null
                    showFeedback = false
                    currentIndex++
                },
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text(if (currentIndex == activity.questions.size - 1) "Finish" else "Next question")
            }
        }
    }
}
