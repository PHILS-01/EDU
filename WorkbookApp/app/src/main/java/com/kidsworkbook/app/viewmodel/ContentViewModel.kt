package com.kidsworkbook.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidsworkbook.app.data.content.ContentEngineProvider
import com.kidsworkbook.app.data.content.models.Activity
import com.kidsworkbook.app.data.content.models.Subject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContentViewModel(private val appContext: Context) : ViewModel() {

    private val _activities = MutableStateFlow<List<Activity>>(emptyList())
    val activities: StateFlow<List<Activity>> = _activities.asStateFlow()

    private val _currentActivity = MutableStateFlow<Activity?>(null)
    val currentActivity: StateFlow<Activity?> = _currentActivity.asStateFlow()

    fun loadActivities(subject: Subject, studentAge: Int?) {
        viewModelScope.launch {
            val engine = ContentEngineProvider.engineFor(subject, appContext)
            _activities.value = engine.listActivities(subject, studentAge)
        }
    }

    fun loadActivity(subject: Subject, activityId: String) {
        viewModelScope.launch {
            val engine = ContentEngineProvider.engineFor(subject, appContext)
            _currentActivity.value = engine.getActivity(subject, activityId)
        }
    }

    fun clearCurrentActivity() {
        _currentActivity.value = null
    }
}
