package com.kidsworkbook.app.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidsworkbook.app.data.local.entities.StudentProfile
import com.kidsworkbook.app.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: StudentRepository) : ViewModel() {

    val activeStudent: StateFlow<StudentProfile?> = repository.observeActiveStudent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _saveComplete = MutableStateFlow(false)
    val saveComplete: StateFlow<Boolean> = _saveComplete

    fun saveProfile(existingId: Long?, name: String, age: Int, photoUri: Uri?) {
        if (name.isBlank() || age <= 0) return
        viewModelScope.launch {
            repository.saveProfile(existingId, name.trim(), age, photoUri)
            _saveComplete.value = true
        }
    }

    fun resetSaveState() {
        _saveComplete.value = false
    }
}
