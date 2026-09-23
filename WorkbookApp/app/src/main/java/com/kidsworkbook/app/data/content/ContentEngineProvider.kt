package com.kidsworkbook.app.data.content

import android.content.Context
import com.kidsworkbook.app.data.content.models.Subject

/**
 * Single place that decides which ContentEngine serves each subject.
 * All four subjects default to the bundled LocalContentEngine so the app
 * runs immediately. To go live with a real vendor SDK for a subject,
 * change ONLY the line for that subject below.
 */
object ContentEngineProvider {

    fun engineFor(subject: Subject, context: Context): ContentEngine {
        val local = LocalContentEngine(context)
        return when (subject) {
            Subject.ENGLISH -> local
            Subject.READING -> local
            Subject.MATHEMATICS -> local
            Subject.FINE_ART -> local
            Subject.PLHE -> local
            Subject.VERBAL_REASONING -> local
            Subject.QUANTITATIVE_REASONING -> local
            // Example of swapping one subject to a real vendor engine:
            // Subject.ENGLISH -> ThirdPartyContentEngineTemplate(apiKey = BuildConfig.READING_SDK_KEY)
        }
    }
}
