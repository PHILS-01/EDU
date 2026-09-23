package com.kidsworkbook.app

import android.app.Application
import com.kidsworkbook.app.data.local.AppDatabase
import com.kidsworkbook.app.data.repository.PerformanceRepository
import com.kidsworkbook.app.data.repository.StudentRepository

/**
 * Simple manual DI container (no Hilt/Dagger, to keep the project easy to read
 * and build). Swap this for Hilt later if the app grows.
 */
class WorkbookApplication : Application() {

    lateinit var database: AppDatabase
        private set
    lateinit var studentRepository: StudentRepository
        private set
    lateinit var performanceRepository: PerformanceRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
        studentRepository = StudentRepository(database.studentDao(), this)
        performanceRepository = PerformanceRepository(database.performanceDao())
    }
}
