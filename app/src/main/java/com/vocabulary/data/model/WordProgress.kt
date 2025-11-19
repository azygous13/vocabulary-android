package com.vocabulary.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word_progress")
data class WordProgress(
    @PrimaryKey
    val wordId: Long,

    val isLearned: Boolean = false,

    val isFavorite: Boolean = false,

    val correctAnswers: Int = 0,

    val incorrectAnswers: Int = 0,

    val lastPracticed: Long? = null,

    val firstLearnedDate: Long? = null,

    val reviewCount: Int = 0,

    val masteryLevel: Int = 0, // 0-100 percentage

    // Spaced Repetition fields
    val nextReviewDate: Long? = null, // Timestamp for next review

    val easinessFactor: Float = 2.5f, // SM-2 algorithm easiness (1.3 - 2.5+)

    val intervalDays: Int = 0, // Days until next review

    val consecutiveCorrect: Int = 0 // Streak of correct answers
) {
    fun getAccuracy(): Float {
        val total = correctAnswers + incorrectAnswers
        return if (total > 0) (correctAnswers.toFloat() / total) * 100 else 0f
    }

    fun isNew(): Boolean = reviewCount == 0

    fun isDueForReview(): Boolean {
        val now = System.currentTimeMillis()
        return nextReviewDate == null || nextReviewDate <= now
    }

    fun getDaysUntilReview(): Int {
        if (nextReviewDate == null) return 0
        val now = System.currentTimeMillis()
        val diff = nextReviewDate - now
        return (diff / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
    }
}
