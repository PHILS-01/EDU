package com.kidsworkbook.app.data.content.models

/**
 * The four workbook subjects requested for a 7-year-old curriculum.
 * "PLHE" = Physical, Life and Health Education (kept as one combined subject,
 * matching the request; split it into two enum values later if you want
 * separate tracking for Physical Education vs Life & Health Education).
 */
enum class Subject(val displayName: String, val emoji: String) {
    ENGLISH("English", "📚"),
    READING("Reading", "📖"),
    MATHEMATICS("Mathematics", "🔢"),
    FINE_ART("Fine Art", "🎨"),
    PLHE("Physical, Life & Health Education", "🏃"),
    VERBAL_REASONING("Verbal Reasoning", "🗣️"),
    QUANTITATIVE_REASONING("Quantitative Reasoning", "🧮")
}
