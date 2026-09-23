package com.kidsworkbook.app.ui.screens.onboarding

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.kidsworkbook.app.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    viewModel: ProfileViewModel,
    onProfileSaved: () -> Unit
) {
    val activeStudent by viewModel.activeStudent.collectAsState()
    val saveComplete by viewModel.saveComplete.collectAsState()

    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var name by remember(activeStudent) { mutableStateOf(activeStudent?.name ?: "") }
    var ageText by remember(activeStudent) { mutableStateOf(activeStudent?.age?.toString() ?: "7") }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> photoUri = uri }

    LaunchedEffect(saveComplete) {
        if (saveComplete) {
            viewModel.resetSaveState()
            onProfileSaved()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Text("Let's set up your workbook!", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        // Photo picker
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
                .clickable { imagePicker.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            val displayUri = photoUri ?: activeStudent?.photoPath?.let { Uri.parse("file://$it") }
            if (displayUri != null) {
                AsyncImage(
                    model = displayUri,
                    contentDescription = "Student photo",
                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                )
            } else {
                Text("📷\nTap to add\nphoto", textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Student's name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = ageText,
            onValueChange = { input -> if (input.all { it.isDigit() } && input.length <= 2) ageText = input },
            label = { Text("Age") },
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))
        Text(
            "Date and time are recorded automatically — no need to enter them.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                val age = ageText.toIntOrNull() ?: return@Button
                viewModel.saveProfile(activeStudent?.id, name, age, photoUri)
            },
            enabled = name.isNotBlank() && (ageText.toIntOrNull() ?: 0) > 0,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Save & Start Learning", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
