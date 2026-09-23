package com.kidsworkbook.app.ui.screens.performance

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kidsworkbook.app.data.local.dao.SubjectAverage
import com.kidsworkbook.app.data.local.entities.PerformanceRecord
import com.kidsworkbook.app.ui.screens.home.colorFor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PerformanceScreen(
    subjectAverages: List<SubjectAverage>,
    history: List<PerformanceRecord>,
    onBack: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()) }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        TextButton(onClick = onBack) { Text("← Back") }
        Text("My Progress", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        if (subjectAverages.isEmpty()) {
            Text("Complete an activity to see your progress here!")
        } else {
            Text("Average score by subject", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            SubjectBarChart(subjectAverages)
        }

        Spacer(Modifier.height(24.dp))
        Text("Recent activity", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(history) { record ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = colorFor(record.subject).copy(alpha = 0.10f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text("${record.subject.emoji} ${record.activityTitle}", fontWeight = FontWeight.Bold)
                        Text("${record.correctAnswers}/${record.totalQuestions} correct (${record.scorePercent}%) • ${record.durationSeconds}s")
                        Text(
                            dateFormat.format(Date(record.timestampEpochMillis)),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubjectBarChart(averages: List<SubjectAverage>) {
    Canvas(modifier = Modifier.fillMaxWidth().height(180.dp)) {
        val barCount = averages.size
        if (barCount == 0) return@Canvas
        val gap = 24f
        val barWidth = (size.width - gap * (barCount + 1)) / barCount
        val maxBarHeight = size.height - 24f

        averages.forEachIndexed { index, avg ->
            val barHeight = (avg.avgScore.coerceIn(0.0, 100.0) / 100.0 * maxBarHeight).toFloat()
            val x = gap + index * (barWidth + gap)
            val y = size.height - barHeight
            drawRect(
                color = colorFor(avg.subject),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight)
            )
        }
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        averages.forEach { avg ->
            Text("${avg.subject.emoji} ${avg.avgScore.toInt()}%", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
