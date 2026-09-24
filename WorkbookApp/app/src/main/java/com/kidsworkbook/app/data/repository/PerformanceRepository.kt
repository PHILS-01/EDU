package com.kidsworkbook.app.data.repository

import com.kidsworkbook.app.data.content.models.Subject
import com.kidsworkbook.app.data.local.dao.PerformanceDao
import com.kidsworkbook.app.data.local.dao.SubjectAverage
import com.kidsworkbook.app.data.local.entities.PerformanceRecord
import kotlinx.coroutines.flow.Flow

class PerformanceRepository(private val dao: PerformanceDao) {

    /**
     * Records the result of a completed activity. Date/time is captured
     * automatically inside PerformanceRecord's default (System.currentTimeMillis())
     * — callers never pass a timestamp in.
     */
    suspend fun recordResult(
        studentId: Long,
        subject: Subject,
        activityId: String,
        activityTitle: String,
        correctAnswers: Int,
        totalQuestions: Int,
        durationSeconds: Long
    ) {
        dao.insert(
            PerformanceRecord(
                studentId = studentId,
                subject = subject,
                activityId = activityId,
                activityTitle = activityTitle,
                correctAnswers = correctAnswers,
                totalQuestions = totalQuestions,
                durationSeconds = durationSeconds
            )
        )
    }

    fun observeHistory(studentId: Long): Flow<List<PerformanceRecord>> = dao.observeForStudent(studentId)

    fun observeSubjectAverages(studentId: Long): Flow<List<SubjectAverage>> = dao.observeSubjectAverages(studentId)

    fun observeTotalAttempts(studentId: Long): Flow<Int> = dao.observeTotalAttempts(studentId)
}
