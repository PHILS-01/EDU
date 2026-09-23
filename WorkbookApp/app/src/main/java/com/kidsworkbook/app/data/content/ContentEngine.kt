package com.kidsworkbook.app.data.content

import com.kidsworkbook.app.data.content.models.Activity
import com.kidsworkbook.app.data.content.models.Subject

/**
 * ============================================================================
 *  PLUG-IN POINT FOR THIRD-PARTY EDUCATIONAL CONTENT SDKs / APIs
 * ============================================================================
 * Every subject's workbook content is served through this interface. The app
 * never talks to Room, JSON files, or a vendor SDK directly from the UI layer
 * — it only talks to a ContentEngine. This means you can swap in a real
 * third-party engine (e.g. a licensed English-phonics SDK, a math-drill API,
 * an art-lesson content API, a PE/health curriculum API) for any one subject
 * without touching any UI or database code.
 *
 * To integrate a real third-party SDK:
 *   1. Create a class implementing ContentEngine (e.g. VendorMathEngine).
 *   2. Inside it, call the vendor's SDK/API and map its response into
 *      Activity/Question (data/content/models/Content.kt).
 *   3. Register it in ContentEngineProvider below instead of LocalContentEngine
 *      for that subject.
 * No other file in the app needs to change.
 */
interface ContentEngine {
    /** A short id identifying which engine is answering (e.g. "local-json", "vendor-xyz-sdk"). */
    val engineId: String

    /** List all activities available for a subject, optionally filtered by age. */
    suspend fun listActivities(subject: Subject, studentAge: Int? = null): List<Activity>

    /** Fetch one activity (with its questions) by id. */
    suspend fun getActivity(subject: Subject, activityId: String): Activity?
}
