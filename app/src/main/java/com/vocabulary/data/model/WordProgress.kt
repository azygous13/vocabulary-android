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

    val masteryLevel: Int = 0 // 0-100 percentage
) {
    fun getAccuracy(): Float {
        val total = correctAnswers + incorrectAnswers
        return if (total > 0) (correctAnswers.toFloat() / total) * 100 else 0f
    }

    fun isNew(): Boolean = reviewCount == 0
}
