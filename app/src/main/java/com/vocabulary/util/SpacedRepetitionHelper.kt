package com.vocabulary.util

import com.vocabulary.data.model.WordProgress
import kotlin.math.max

/**
 * Spaced Repetition Helper using simplified SM-2 algorithm
 */
object SpacedRepetitionHelper {

    /**
     * Quality ratings for review
     */
    enum class ReviewQuality(val value: Int, val displayName: String) {
        FORGOT(0, "ลืมหมด"),        // Complete blackout
        HARD(1, "ยาก"),              // Incorrect but remembered
        GOOD(2, "ปานกลาง"),          // Correct with difficulty
        EASY(3, "ง่าย")              // Perfect recall
    }

    /**
     * Calculate next review based on SM-2 algorithm
     * @param currentProgress Current word progress
     * @param quality Review quality (0-3)
     * @return Updated WordProgress with new review schedule
     */
    fun calculateNextReview(
        currentProgress: WordProgress,
        quality: ReviewQuality
    ): WordProgress {
        val q = quality.value

        // Calculate new easiness factor (EF)
        val newEF = calculateEasinessFactor(currentProgress.easinessFactor, q)

        // Calculate new interval
        val newInterval = calculateInterval(
            currentInterval = currentProgress.intervalDays,
            consecutiveCorrect = currentProgress.consecutiveCorrect,
            quality = q,
            easinessFactor = newEF
        )

        // Calculate next review date
        val nextReview = System.currentTimeMillis() + (newInterval * 24 * 60 * 60 * 1000L)

        // Update consecutive correct count
        val newConsecutiveCorrect = if (q >= ReviewQuality.GOOD.value) {
            currentProgress.consecutiveCorrect + 1
        } else {
            0
        }

        // Update correct/incorrect answers
        val newCorrectAnswers = if (q >= ReviewQuality.GOOD.value) {
            currentProgress.correctAnswers + 1
        } else {
            currentProgress.correctAnswers
        }

        val newIncorrectAnswers = if (q < ReviewQuality.GOOD.value) {
            currentProgress.incorrectAnswers + 1
        } else {
            currentProgress.incorrectAnswers
        }

        // Calculate mastery level (0-100)
        val newMasteryLevel = calculateMasteryLevel(
            consecutiveCorrect = newConsecutiveCorrect,
            totalReviews = currentProgress.reviewCount + 1,
            accuracy = if (newCorrectAnswers + newIncorrectAnswers > 0) {
                (newCorrectAnswers.toFloat() / (newCorrectAnswers + newIncorrectAnswers)) * 100
            } else 0f
        )

        return currentProgress.copy(
            nextReviewDate = nextReview,
            easinessFactor = newEF,
            intervalDays = newInterval,
            consecutiveCorrect = newConsecutiveCorrect,
            reviewCount = currentProgress.reviewCount + 1,
            correctAnswers = newCorrectAnswers,
            incorrectAnswers = newIncorrectAnswers,
            lastPracticed = System.currentTimeMillis(),
            masteryLevel = newMasteryLevel,
            isLearned = newMasteryLevel >= 60 // Mark as learned if mastery >= 60%
        )
    }

    /**
     * Calculate new easiness factor based on SM-2 formula
     */
    private fun calculateEasinessFactor(currentEF: Float, quality: Int): Float {
        val newEF = currentEF + (0.1f - (3 - quality) * (0.08f + (3 - quality) * 0.02f))
        return max(1.3f, newEF) // EF should never be less than 1.3
    }

    /**
     * Calculate interval days until next review
     */
    private fun calculateInterval(
        currentInterval: Int,
        consecutiveCorrect: Int,
        quality: Int,
        easinessFactor: Float
    ): Int {
        return when {
            // If failed or hard, reset to 1 day
            quality < ReviewQuality.GOOD.value -> 1

            // First successful review
            consecutiveCorrect == 0 -> 1

            // Second successful review
            consecutiveCorrect == 1 -> 6

            // Subsequent reviews: multiply by easiness factor
            else -> (currentInterval * easinessFactor).toInt()
        }
    }

    /**
     * Calculate mastery level (0-100)
     */
    private fun calculateMasteryLevel(
        consecutiveCorrect: Int,
        totalReviews: Int,
        accuracy: Float
    ): Int {
        // Base mastery on consecutive correct answers
        val consecutiveBonus = (consecutiveCorrect * 15).coerceAtMost(50)

        // Add accuracy component
        val accuracyComponent = (accuracy * 0.3f).toInt()

        // Add review count bonus (experience)
        val experienceBonus = (totalReviews * 2).coerceAtMost(20)

        return (consecutiveBonus + accuracyComponent + experienceBonus).coerceIn(0, 100)
    }

    /**
     * Get interval description in Thai
     */
    fun getIntervalDescription(days: Int): String {
        return when {
            days == 0 -> "ทบทวนทันที"
            days == 1 -> "พรุ่งนี้"
            days < 7 -> "อีก $days วัน"
            days < 30 -> "อีก ${days / 7} สัปดาห์"
            days < 365 -> "อีก ${days / 30} เดือน"
            else -> "อีก ${days / 365} ปี"
        }
    }
}
