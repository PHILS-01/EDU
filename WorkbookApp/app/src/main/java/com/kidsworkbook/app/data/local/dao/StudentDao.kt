package com.kidsworkbook.app.data.local.dao

import androidx.room.*
import com.kidsworkbook.app.data.local.entities.StudentProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(student: StudentProfile): Long

    @Update
    suspend fun update(student: StudentProfile)

    @Query("SELECT * FROM student_profile ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<StudentProfile>>

    @Query("SELECT * FROM student_profile WHERE id = :id")
    suspend fun getById(id: Long): StudentProfile?

    @Query("SELECT * FROM student_profile ORDER BY createdAt DESC LIMIT 1")
    fun observeActiveStudent(): Flow<StudentProfile?>

    @Delete
    suspend fun delete(student: StudentProfile)
}
