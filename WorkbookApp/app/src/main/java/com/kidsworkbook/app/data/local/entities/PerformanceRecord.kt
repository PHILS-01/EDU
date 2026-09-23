package com.kidsworkbook.app.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.kidsworkbook.app.data.content.models.Subject

/**
 * One row per completed activity attempt. This is the heart of the
 * "performance database" - date/time are captured automatically via
 * System.currentTimeMillis(), never typed in by the user.
 */
@Entity(
    tableName = "performance_record",
    foreignKeys = [
        ForeignKey(
            entity = StudentProfile::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("studentId"), Index("subject")]
)
data class PerformanceRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentId: Long,
    val subject: Subject,
    val activityId: String,          // id of the activity/quiz within the content engine
    val activityTitle: String,
    val correctAnswers: Int,
    val totalQuestions: Int,
    val durationSeconds: Long,
    // Auto-captured, not user-entered:
    val timestampEpochMillis: Long = System.currentTimeMillis()
) {
    val scorePercent: Int
        get() = if (totalQuestions == 0) 0 else (correctAnswers * 100) / totalQuestions
}
