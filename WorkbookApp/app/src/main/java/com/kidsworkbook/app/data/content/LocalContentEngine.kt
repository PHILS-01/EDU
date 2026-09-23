package com.kidsworkbook.app.data.content

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kidsworkbook.app.data.content.models.Activity
import com.kidsworkbook.app.data.content.models.Subject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Default content engine: reads activities bundled as JSON in
 * app/src/main/assets/content/<subject>.json
 *
 * This ships so the app works immediately with no third-party account or
 * API key needed. Replace per-subject with a real vendor engine at any time
 * (see ContentEngine.kt for how).
 */
class LocalContentEngine(private val context: Context) : ContentEngine {

    override val engineId: String = "local-json"

    private val gson = Gson()
    private val cache = mutableMapOf<Subject, List<Activity>>()

    private fun assetFileFor(subject: Subject): String = when (subject) {
        Subject.ENGLISH -> "content/english.json"
        Subject.READING -> "content/reading.json"
        Subject.MATHEMATICS -> "content/mathematics.json"
        Subject.FINE_ART -> "content/fine_art.json"
        Subject.PLHE -> "content/plhe.json"
        Subject.VERBAL_REASONING -> "content/verbal_reasoning.json"
        Subject.QUANTITATIVE_REASONING -> "content/quantitative_reasoning.json"
    }

    override suspend fun listActivities(subject: Subject, studentAge: Int?): List<Activity> =
        withContext(Dispatchers.IO) {
            val activities = cache.getOrPut(subject) { loadFromAssets(subject) }
            if (studentAge == null) activities
            else activities.filter { studentAge in it.ageRange }
        }

    override suspend fun getActivity(subject: Subject, activityId: String): Activity? =
        listActivities(subject).firstOrNull { it.id == activityId }

    private fun loadFromAssets(subject: Subject): List<Activity> {
        return try {
            context.assets.open(assetFileFor(subject)).use { stream ->
                val reader = BufferedReader(InputStreamReader(stream))
                val type = object : TypeToken<List<Activity>>() {}.type
                gson.fromJson(reader, type) ?: emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
