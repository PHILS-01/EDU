package com.kidsworkbook.app.ui.screens.subject

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kidsworkbook.app.data.content.models.Activity
import com.kidsworkbook.app.data.content.models.Subject
import com.kidsworkbook.app.ui.screens.home.colorFor

@Composable
fun SubjectListScreen(
    subject: Subject,
    activities: List<Activity>,
    onActivityClick: (Activity) -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        TextButton(onClick = onBack) { Text("← Back") }
        Text(
            "${subject.emoji} ${subject.displayName}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))

        if (activities.isEmpty()) {
            Text("No activities yet for this subject.")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(activities) { activity ->
                    Card(
                        onClick = { onActivityClick(activity) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colorFor(subject).copy(alpha = 0.12f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(activity.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            Text(activity.description, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(4.dp))
                            Text("${activity.questions.size} questions", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
