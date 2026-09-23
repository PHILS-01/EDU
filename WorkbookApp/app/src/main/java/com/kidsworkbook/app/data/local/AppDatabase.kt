package com.kidsworkbook.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kidsworkbook.app.data.local.dao.PerformanceDao
import com.kidsworkbook.app.data.local.dao.StudentDao
import com.kidsworkbook.app.data.local.entities.PerformanceRecord
import com.kidsworkbook.app.data.local.entities.StudentProfile

@Database(
    entities = [StudentProfile::class, PerformanceRecord::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao
    abstract fun performanceDao(): PerformanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kids_workbook.db"
                )
                    // Fine for a first release; replace with real Migrations before you
                    // ship an update that changes the schema.
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
    }
}
