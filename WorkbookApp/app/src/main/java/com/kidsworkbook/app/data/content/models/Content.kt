package com.kidsworkbook.app.data.content.models

/**
 * A lightweight, code-drawn diagram shown above a question's prompt. Rendered
 * on-device with Compose Canvas (see QuestionDiagram in ActivityScreen.kt) so
 * modules work fully offline with no bundled images or network calls.
 *
 * type: one of "dots", "numberline", "shape", "bars", "colorSwatch", "clock"
 * params: small string-keyed payload the renderer interprets per type, e.g.
 *   dots        -> {"count": "5", "color": "#3E8EF7"}
 *   numberline  -> {"from": "0", "to": "10", "highlight": "7"}
 *   shape       -> {"kind": "triangle", "color": "#FF7A45"}
 *   bars        -> {"values": "3,5,2", "labels": "A,B,C"}
 *   colorSwatch -> {"colors": "#3E8EF7,#FFC64B"}
 *   clock       -> {"hour": "3", "minute": "0"}
 */
data class Diagram(
    val type: String,
    val params: Map<String, String> = emptyMap()
)

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
    val imageUrl: String? = null,
    val diagram: Diagram? = null
)

/**
 * One workbook activity (a mini quiz/exercise) belonging to a Subject.
 *
 * notes: a short, kid-friendly explanation of the concept, shown on an
 * intro screen before the questions start.
 * examples: one or two short worked examples shown alongside the notes,
 * so the student sees the idea in action before being quizzed on it.
 */
data class Activity(
    val id: String,
    val subject: Subject,
    val title: String,
    val description: String,
    val ageMin: Int = 6,
    val ageMax: Int = 8,
    val notes: String? = null,
    val examples: List<String> = emptyList(),
    val questions: List<Question>
) {
    val ageRange: IntRange get() = ageMin..ageMax
}
