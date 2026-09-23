package com.kidsworkbook.app.data.local

import androidx.room.TypeConverter
import com.kidsworkbook.app.data.content.models.Subject

class Converters {
    @TypeConverter
    fun fromSubject(subject: Subject): String = subject.name

    @TypeConverter
    fun toSubject(value: String): Subject = Subject.valueOf(value)
}
