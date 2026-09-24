package com.kidsworkbook.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A student profile: name, age, and an optional path to a locally stored photo.
 * createdAt / updatedAt are captured automatically (never entered by the user).
 */
@Entity(tableName = "student_profile")
data class StudentProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val age: Int,
    val photoPath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
