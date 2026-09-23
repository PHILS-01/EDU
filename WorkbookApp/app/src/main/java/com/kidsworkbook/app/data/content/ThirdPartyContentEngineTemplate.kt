package com.kidsworkbook.app.data.content

import com.kidsworkbook.app.data.content.models.Activity
import com.kidsworkbook.app.data.content.models.Subject

/**
 * TEMPLATE — copy this file, rename the class, and fill in the TODOs to wire
 * a real third-party educational SDK or REST API into one subject.
 *
 * Example real-world candidates you might plug in here:
 *   - An English phonics/reading SDK (e.g. a vendor's reading-level API)
 *   - A math drill-and-practice API
 *   - An art/drawing-lesson content API
 *   - A PE/health curriculum content API
 *
 * Nothing outside this class needs to know the vendor's data shapes — you
 * translate their response into this app's Activity/Question models here,
 * once, and the rest of the app (UI, Room performance tracking) is unaffected.
 */
class ThirdPartyContentEngineTemplate(
    private val apiKey: String,
    // TODO: inject an OkHttp/Retrofit client or the vendor's own SDK client here
) : ContentEngine {

    override val engineId: String = "vendor-template" // give it a real id, e.g. "readingeggs-sdk"

    override suspend fun listActivities(subject: Subject, studentAge: Int?): List<Activity> {
        // TODO: call the vendor SDK/API, e.g.:
        //   val response = vendorClient.getLessons(ageGroup = studentAge ?: 7)
        //   return response.lessons.map { it.toActivity() }
        return emptyList()
    }

    override suspend fun getActivity(subject: Subject, activityId: String): Activity? {
        // TODO: fetch a single lesson/quiz by id and map it to Activity
        return null
    }

    // TODO: private fun VendorLesson.toActivity(): Activity = Activity(...)
}
