package com.kidsworkbook.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kidsworkbook.app.WorkbookApplication

class ViewModelFactory(private val app: WorkbookApplication, private val context: Context) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(app.studentRepository) as T
            modelClass.isAssignableFrom(ContentViewModel::class.java) ->
                ContentViewModel(context.applicationContext) as T
            modelClass.isAssignableFrom(PerformanceViewModel::class.java) ->
                PerformanceViewModel(app.performanceRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
