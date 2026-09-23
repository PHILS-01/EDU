package com.kidsworkbook.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidsworkbook.app.data.content.models.Subject
import com.kidsworkbook.app.data.local.dao.SubjectAverage
import com.kidsworkbook.app.data.local.entities.PerformanceRecord
import com.kidsworkbook.app.data.repository.PerformanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PerformanceViewModel(private val repository: PerformanceRepository) : ViewModel() {

    private val _studentId = MutableStateFlow<Long?>(null)

    val history: StateFlow<List<PerformanceRecord>> = _studentId
        .flatMapLatest { id -> if (id == null) kotlinx.coroutines.flow.flowOf(emptyList()) else repository.observeHistory(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val subjectAverages: StateFlow<List<SubjectAverage>> = _studentId
        .flatMapLatest { id -> if (id == null) kotlinx.coroutines.flow.flowOf(emptyList()) else repository.observeSubjectAverages(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalAttempts: StateFlow<Int> = _studentId
        .flatMapLatest { id -> if (id == null) kotlinx.coroutines.flow.flowOf(0) else repository.observeTotalAttempts(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setActiveStudent(studentId: Long) {
        _studentId.value = studentId
    }

    fun recordResult(
        studentId: Long,
        subject: Subject,
        activityId: String,
        activityTitle: String,
        correctAnswers: Int,
        totalQuestions: Int,
        durationSeconds: Long
    ) {
        viewModelScope.launch {
            repository.recordResult(
                studentId, subject, activityId, activityTitle,
                correctAnswers, totalQuestions, durationSeconds
            )
        }
    }
}
