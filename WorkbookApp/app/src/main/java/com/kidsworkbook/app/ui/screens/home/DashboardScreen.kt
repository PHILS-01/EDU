package com.kidsworkbook.app.ui.screens.home

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kidsworkbook.app.data.content.models.Subject
import com.kidsworkbook.app.data.local.entities.StudentProfile
import com.kidsworkbook.app.ui.theme.SubjectArt
import com.kidsworkbook.app.ui.theme.SubjectEnglish
import com.kidsworkbook.app.ui.theme.SubjectMath
import com.kidsworkbook.app.ui.theme.SubjectPLHE
import com.kidsworkbook.app.ui.theme.SubjectQuantitativeReasoning
import com.kidsworkbook.app.ui.theme.SubjectReading
import com.kidsworkbook.app.ui.theme.SubjectVerbalReasoning

fun colorFor(subject: Subject) = when (subject) {
    Subject.ENGLISH -> SubjectEnglish
    Subject.READING -> SubjectReading
    Subject.MATHEMATICS -> SubjectMath
    Subject.FINE_ART -> SubjectArt
    Subject.PLHE -> SubjectPLHE
    Subject.VERBAL_REASONING -> SubjectVerbalReasoning
    Subject.QUANTITATIVE_REASONING -> SubjectQuantitativeReasoning
}

@Composable
fun DashboardScreen(
    student: StudentProfile,
    totalAttempts: Int,
    onSubjectClick: (Subject) -> Unit,
    onViewProgress: () -> Unit,
    onEditProfile: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().clickable { onEditProfile() }
        ) {
            Box(
                modifier = Modifier.size(64.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                if (student.photoPath != null) {
                    AsyncImage(
                        model = Uri.parse("file://${student.photoPath}"),
                        contentDescription = "Student photo",
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                    )
                } else {
                    Text("🙂")
                }
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Hi, ${student.name}!", style = MaterialTheme.typography.headlineMedium)
                Text("Age ${student.age} • $totalAttempts activities completed",
                    style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(Modifier.height(28.dp))
        Text("Choose a subject", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(Subject.values().toList()) { subject ->
                SubjectCard(subject = subject, onClick = { onSubjectClick(subject) })
            }
        }

        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onViewProgress, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Text("View My Progress 📈")
        }
    }
}

@Composable
private fun SubjectCard(subject: Subject, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colorFor(subject).copy(alpha = 0.15f)),
        modifier = Modifier.fillMaxWidth().height(140.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(subject.emoji, style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(8.dp))
            Text(
                subject.displayName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
