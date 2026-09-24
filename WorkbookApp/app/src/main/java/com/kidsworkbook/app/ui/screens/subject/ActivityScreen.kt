package com.kidsworkbook.app.ui.screens.subject

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kidsworkbook.app.data.content.models.Activity
import com.kidsworkbook.app.data.content.models.Diagram
import com.kidsworkbook.app.ui.screens.home.colorFor
import kotlin.math.cos
import kotlin.math.sin

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

    // Show the short notes/examples intro first, if this module has any,
    // before moving on to the questions themselves.
    var showingNotes by remember { mutableStateOf(!activity.notes.isNullOrBlank()) }

    if (showingNotes) {
        ActivityNotesScreen(
            activity = activity,
            onStart = { showingNotes = false },
            onExit = onExit
        )
        return
    }

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

        question.diagram?.let { diagram ->
            Spacer(Modifier.height(12.dp))
            QuestionDiagram(diagram, accentColor = colorFor(activity.subject))
        }

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

/**
 * A short "learn before you quiz" screen: a couple of sentences explaining
 * the concept, plus one or two worked examples, shown before the student
 * sees any questions. Skipped automatically by ActivityScreen when a
 * module has no notes.
 */
@Composable
private fun ActivityNotesScreen(
    activity: Activity,
    onStart: () -> Unit,
    onExit: () -> Unit
) {
    val accent = colorFor(activity.subject)

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        TextButton(onClick = onExit) { Text("← Exit activity") }

        Spacer(Modifier.height(8.dp))
        Text(activity.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(activity.description, style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(20.dp))

        activity.notes?.let { notes ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.10f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("What to know", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(notes, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        if (activity.examples.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Examples", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    activity.examples.forEach { example ->
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            Text("•  ", style = MaterialTheme.typography.bodyLarge)
                            Text(example, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = accent)
        ) {
            Text("Start questions (${activity.questions.size})")
        }
    }
}

/**
 * Draws a small, colourful, code-generated illustration to accompany a
 * question — dot groups for counting, a number line, a shape, a bar chart,
 * color swatches, or a clock face. Everything is vector-drawn on a Canvas so
 * it needs no bundled art and always matches the subject's accent color.
 */
@Composable
private fun QuestionDiagram(diagram: Diagram, accentColor: Color) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = accentColor.copy(alpha = 0.08f)),
        modifier = Modifier.fillMaxWidth().height(120.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            when (diagram.type) {
                "dots" -> drawDots(diagram, accentColor)
                "numberline" -> drawNumberLine(diagram, accentColor)
                "shape" -> drawShape(diagram, accentColor)
                "bars" -> drawBars(diagram, accentColor)
                "colorSwatch" -> drawColorSwatch(diagram)
                "clock" -> drawClock(diagram, accentColor)
            }
        }
    }
}

private fun parseHexColor(hex: String, fallback: Color): Color = try {
    Color(android.graphics.Color.parseColor(hex))
} catch (e: Exception) {
    fallback
}

private fun DrawScope.drawDots(diagram: Diagram, accentColor: Color) {
    val count = diagram.params["count"]?.toIntOrNull() ?: 0
    val color = diagram.params["color"]?.let { parseHexColor(it, accentColor) } ?: accentColor
    if (count <= 0) return
    val perRow = 5
    val rows = (count + perRow - 1) / perRow
    val spacingX = size.width / perRow
    val spacingY = size.height / rows.coerceAtLeast(1)
    val radius = (minOf(spacingX, spacingY) / 3f).coerceAtMost(16f)
    for (i in 0 until count) {
        val row = i / perRow
        val col = i % perRow
        val cx = spacingX * col + spacingX / 2f
        val cy = spacingY * row + spacingY / 2f
        drawCircle(color = color, radius = radius, center = Offset(cx, cy))
    }
}

private fun DrawScope.drawNumberLine(diagram: Diagram, accentColor: Color) {
    val from = diagram.params["from"]?.toIntOrNull() ?: 0
    val to = diagram.params["to"]?.toIntOrNull() ?: 10
    val highlight = diagram.params["highlight"]?.toIntOrNull()
    val y = size.height / 2f
    val left = 16f
    val right = size.width - 16f
    drawLine(accentColor, Offset(left, y), Offset(right, y), strokeWidth = 4f)
    val span = (to - from).coerceAtLeast(1)
    for (n in from..to) {
        val x = left + (right - left) * (n - from) / span
        val tickHeight = if (n == highlight) 18f else 10f
        drawLine(accentColor, Offset(x, y - tickHeight), Offset(x, y + tickHeight), strokeWidth = if (n == highlight) 6f else 3f)
        if (n == highlight) {
            drawCircle(color = accentColor, radius = 10f, center = Offset(x, y))
        }
        drawContext.canvas.nativeCanvas.drawText(
            n.toString(), x - 6f, y + tickHeight + 26f,
            android.graphics.Paint().apply {
                textSize = 24f
                setColor(android.graphics.Color.DKGRAY)
            }
        )
    }
}

private fun DrawScope.drawShape(diagram: Diagram, accentColor: Color) {
    val kind = diagram.params["kind"] ?: "circle"
    val color = diagram.params["color"]?.let { parseHexColor(it, accentColor) } ?: accentColor
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val cy = h / 2f
    val r = minOf(w, h) / 2.6f
    when (kind) {
        "circle" -> drawCircle(color, radius = r, center = Offset(cx, cy))
        "square" -> drawRect(color, topLeft = Offset(cx - r, cy - r), size = androidx.compose.ui.geometry.Size(r * 2, r * 2))
        "rectangle" -> drawRect(color, topLeft = Offset(cx - r * 1.3f, cy - r * 0.7f), size = androidx.compose.ui.geometry.Size(r * 2.6f, r * 1.4f))
        "triangle" -> {
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(cx, cy - r)
                lineTo(cx - r, cy + r)
                lineTo(cx + r, cy + r)
                close()
            }
            drawPath(path, color)
        }
        "star" -> {
            val path = androidx.compose.ui.graphics.Path()
            val points = 5
            for (i in 0 until points * 2) {
                val angle = Math.PI / points * i - Math.PI / 2
                val rad = if (i % 2 == 0) r else r / 2.4f
                val x = cx + (rad * cos(angle)).toFloat()
                val y = cy + (rad * sin(angle)).toFloat()
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            drawPath(path, color)
        }
        "hexagon" -> {
            val path = androidx.compose.ui.graphics.Path()
            for (i in 0 until 6) {
                val angle = Math.PI / 3 * i - Math.PI / 2
                val x = cx + (r * cos(angle)).toFloat()
                val y = cy + (r * sin(angle)).toFloat()
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.close()
            drawPath(path, color)
        }
        "oval" -> drawOval(color, topLeft = Offset(cx - r * 1.3f, cy - r * 0.8f), size = androidx.compose.ui.geometry.Size(r * 2.6f, r * 1.6f))
    }
}

private fun DrawScope.drawBars(diagram: Diagram, accentColor: Color) {
    val values = diagram.params["values"]?.split(",")?.mapNotNull { it.trim().toIntOrNull() } ?: emptyList()
    val labels = diagram.params["labels"]?.split(",")?.map { it.trim() } ?: emptyList()
    if (values.isEmpty()) return
    val maxVal = (values.maxOrNull() ?: 1).coerceAtLeast(1)
    val barWidth = size.width / (values.size * 2f)
    val maxBarHeight = size.height - 30f
    val palette = listOf(accentColor, accentColor.copy(alpha = 0.7f), accentColor.copy(alpha = 0.5f), accentColor.copy(alpha = 0.35f))
    values.forEachIndexed { i, v ->
        val barHeight = maxBarHeight * v / maxVal
        val x = barWidth * (2 * i + 0.5f)
        drawRect(
            color = palette[i % palette.size],
            topLeft = Offset(x, size.height - 30f - barHeight),
            size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
        )
        val label = labels.getOrNull(i) ?: ""
        if (label.isNotEmpty()) {
            drawContext.canvas.nativeCanvas.drawText(
                label, x, size.height - 6f,
                android.graphics.Paint().apply { textSize = 22f; setColor(android.graphics.Color.DKGRAY) }
            )
        }
    }
}

private fun DrawScope.drawColorSwatch(diagram: Diagram) {
    val colors = diagram.params["colors"]?.split(",")?.map { parseHexColor(it.trim(), Color.Gray) } ?: emptyList()
    if (colors.isEmpty()) return
    val swatchSize = minOf(size.width / colors.size, size.height) * 0.7f
    val spacing = size.width / colors.size
    colors.forEachIndexed { i, c ->
        val cx = spacing * i + spacing / 2f
        val cy = size.height / 2f
        drawCircle(color = c, radius = swatchSize / 2f, center = Offset(cx, cy))
        drawCircle(color = Color.Black.copy(alpha = 0.15f), radius = swatchSize / 2f, center = Offset(cx, cy), style = Stroke(width = 2f))
    }
}

private fun DrawScope.drawClock(diagram: Diagram, accentColor: Color) {
    val hour = diagram.params["hour"]?.toIntOrNull() ?: 3
    val minute = diagram.params["minute"]?.toIntOrNull() ?: 0
    val cx = size.width / 2f
    val cy = size.height / 2f
    val r = minOf(size.width, size.height) / 2.4f
    drawCircle(color = Color.White, radius = r, center = Offset(cx, cy))
    drawCircle(color = accentColor, radius = r, center = Offset(cx, cy), style = Stroke(width = 5f))
    for (i in 0 until 12) {
        val angle = Math.PI / 6 * i - Math.PI / 2
        val x1 = cx + (r * 0.85f * cos(angle)).toFloat()
        val y1 = cy + (r * 0.85f * sin(angle)).toFloat()
        val x2 = cx + (r * cos(angle)).toFloat()
        val y2 = cy + (r * sin(angle)).toFloat()
        drawLine(accentColor, Offset(x1, y1), Offset(x2, y2), strokeWidth = 3f)
    }
    val hourAngle = Math.PI / 6 * ((hour % 12) + minute / 60f) - Math.PI / 2
    val minuteAngle = Math.PI / 30 * minute - Math.PI / 2
    drawLine(
        Color.Black,
        Offset(cx, cy),
        Offset(cx + (r * 0.5f * cos(hourAngle)).toFloat(), cy + (r * 0.5f * sin(hourAngle)).toFloat()),
        strokeWidth = 6f
    )
    drawLine(
        accentColor,
        Offset(cx, cy),
        Offset(cx + (r * 0.75f * cos(minuteAngle)).toFloat(), cy + (r * 0.75f * sin(minuteAngle)).toFloat()),
        strokeWidth = 4f
    )
    drawCircle(color = Color.Black, radius = 5f, center = Offset(cx, cy))
}
