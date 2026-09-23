package com.kidsworkbook.app.data.content.models

/**
 * A single question in an activity. Kept deliberately simple (multiple choice)
 * since this is for 7-year-olds; extend with imageUrl / audioUrl fields if a
 * third-party SDK provides richer media per question.
 */
data class Question(
    val id: String,
    val prompt: String,
    val choices: List<String>,
    val correctIndex: Int,
    val imageUrl: String? = null
)

/**
 * One workbook activity (a mini quiz/exercise) belonging to a Subject.
 */
data class Activity(
    val id: String,
    val subject: Subject,
    val title: String,
    val description: String,
    val ageMin: Int = 6,
    val ageMax: Int = 8,
    val questions: List<Question>
) {
    val ageRange: IntRange get() = ageMin..ageMax
}
