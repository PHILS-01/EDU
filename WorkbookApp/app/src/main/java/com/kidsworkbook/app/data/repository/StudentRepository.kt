package com.kidsworkbook.app.data.repository

import android.content.Context
import android.net.Uri
import com.kidsworkbook.app.data.local.dao.StudentDao
import com.kidsworkbook.app.data.local.entities.StudentProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File

class StudentRepository(
    private val dao: StudentDao,
    private val appContext: Context
) {
    fun observeActiveStudent(): Flow<StudentProfile?> = dao.observeActiveStudent()
    fun observeAll(): Flow<List<StudentProfile>> = dao.observeAll()

    /**
     * Creates or updates a profile. `updatedAt`/`createdAt` are stamped
     * automatically here — the caller never supplies them.
     */
    suspend fun saveProfile(
        existingId: Long?,
        name: String,
        age: Int,
        photoUri: Uri?
    ): Long = withContext(Dispatchers.IO) {
        val photoPath = photoUri?.let { copyPhotoToInternalStorage(it) }
        val now = System.currentTimeMillis()
        val profile = if (existingId == null) {
            StudentProfile(name = name, age = age, photoPath = photoPath, createdAt = now, updatedAt = now)
        } else {
            val existing = dao.getById(existingId)
            (existing ?: StudentProfile(name = name, age = age, createdAt = now, updatedAt = now)).copy(
                name = name,
                age = age,
                photoPath = photoPath ?: existing?.photoPath,
                updatedAt = now
            )
        }
        dao.upsert(profile)
    }

    /** Copies the picked image into the app's private storage so it survives across sessions. */
    private fun copyPhotoToInternalStorage(uri: Uri): String? {
        return try {
            val photosDir = File(appContext.filesDir, "photos").apply { mkdirs() }
            val destFile = File(photosDir, "student_${System.currentTimeMillis()}.jpg")
            appContext.contentResolver.openInputStream(uri)?.use { input ->
                destFile.outputStream().use { output -> input.copyTo(output) }
            }
            destFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}
