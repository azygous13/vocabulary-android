package com.vocabulary.data.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class WordProgressTest {

    @Test
    fun `getAccuracy calculates correctly with answers`() {
        val progress = WordProgress(
            wordId = 1,
            correctAnswers = 7,
            incorrectAnswers = 3,
            reviewCount = 10
        )

        assertThat(progress.getAccuracy()).isEqualTo(70f)
    }

    @Test
    fun `getAccuracy returns zero when no answers`() {
        val progress = WordProgress(
            wordId = 1,
            correctAnswers = 0,
            incorrectAnswers = 0
        )

        assertThat(progress.getAccuracy()).isEqualTo(0f)
    }

    @Test
    fun `getAccuracy returns 100 when all correct`() {
        val progress = WordProgress(
            wordId = 1,
            correctAnswers = 10,
            incorrectAnswers = 0,
            reviewCount = 10
        )

        assertThat(progress.getAccuracy()).isEqualTo(100f)
    }

    @Test
    fun `isNew returns true when reviewCount is zero`() {
        val progress = WordProgress(
            wordId = 1,
            reviewCount = 0
        )

        assertThat(progress.isNew()).isTrue()
    }

    @Test
    fun `isNew returns false when reviewCount is positive`() {
        val progress = WordProgress(
            wordId = 1,
            reviewCount = 5
        )

        assertThat(progress.isNew()).isFalse()
    }

    @Test
    fun `default values are set correctly`() {
        val progress = WordProgress(wordId = 1)

        assertThat(progress.isLearned).isFalse()
        assertThat(progress.isFavorite).isFalse()
        assertThat(progress.correctAnswers).isEqualTo(0)
        assertThat(progress.incorrectAnswers).isEqualTo(0)
        assertThat(progress.lastPracticed).isNull()
        assertThat(progress.firstLearnedDate).isNull()
        assertThat(progress.reviewCount).isEqualTo(0)
        assertThat(progress.masteryLevel).isEqualTo(0)
    }
}
