package com.kidsworkbook.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kidsworkbook.app.data.content.models.Subject
import com.kidsworkbook.app.data.local.entities.PerformanceRecord
import kotlinx.coroutines.flow.Flow

data class SubjectAverage(
    val subject: Subject,
    val avgScore: Double,
    val attempts: Int
)

@Dao
interface PerformanceDao {

    @Insert
    suspend fun insert(record: PerformanceRecord): Long

    @Query("SELECT * FROM performance_record WHERE studentId = :studentId ORDER BY timestampEpochMillis DESC")
    fun observeForStudent(studentId: Long): Flow<List<PerformanceRecord>>

    @Query("SELECT * FROM performance_record WHERE studentId = :studentId AND subject = :subject ORDER BY timestampEpochMillis DESC")
    fun observeForStudentBySubject(studentId: Long, subject: Subject): Flow<List<PerformanceRecord>>

    @Query(
        """
        SELECT subject AS subject,
               AVG( (correctAnswers * 100.0) / NULLIF(totalQuestions, 0) ) AS avgScore,
               COUNT(*) AS attempts
        FROM performance_record
        WHERE studentId = :studentId
        GROUP BY subject
        """
    )
    fun observeSubjectAverages(studentId: Long): Flow<List<SubjectAverage>>

    @Query("SELECT COUNT(*) FROM performance_record WHERE studentId = :studentId")
    fun observeTotalAttempts(studentId: Long): Flow<Int>
}
