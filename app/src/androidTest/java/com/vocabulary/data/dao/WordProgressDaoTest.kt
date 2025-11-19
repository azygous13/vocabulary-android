package com.vocabulary.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.vocabulary.data.database.VocabularyDatabase
import com.vocabulary.data.model.WordProgress
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WordProgressDaoTest {

    private lateinit var database: VocabularyDatabase
    private lateinit var wordProgressDao: WordProgressDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            VocabularyDatabase::class.java
        ).build()
        wordProgressDao = database.wordProgressDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertProgress_and_getByWordId() = runTest {
        // Given
        val progress = WordProgress(
            wordId = 1,
            isLearned = true,
            masteryLevel = 75
        )

        // When
        wordProgressDao.insertProgress(progress)

        // Then
        val loaded = wordProgressDao.getProgressByWordId(1)
        assertThat(loaded).isNotNull()
        assertThat(loaded?.isLearned).isTrue()
        assertThat(loaded?.masteryLevel).isEqualTo(75)
    }

    @Test
    fun getLearnedWords() = runTest {
        // Given
        wordProgressDao.insertProgress(
            WordProgress(wordId = 1, isLearned = true)
        )
        wordProgressDao.insertProgress(
            WordProgress(wordId = 2, isLearned = false)
        )
        wordProgressDao.insertProgress(
            WordProgress(wordId = 3, isLearned = true)
        )

        // When
        val learnedWords = wordProgressDao.getLearnedWords().first()

        // Then
        assertThat(learnedWords).hasSize(2)
    }

    @Test
    fun getFavoriteWords() = runTest {
        // Given
        wordProgressDao.insertProgress(
            WordProgress(wordId = 1, isFavorite = true)
        )
        wordProgressDao.insertProgress(
            WordProgress(wordId = 2, isFavorite = false)
        )

        // When
        val favoriteWords = wordProgressDao.getFavoriteWords().first()

        // Then
        assertThat(favoriteWords).hasSize(1)
        assertThat(favoriteWords[0].wordId).isEqualTo(1)
    }

    @Test
    fun getLearnedWordCount() = runTest {
        // Given
        wordProgressDao.insertProgress(
            WordProgress(wordId = 1, isLearned = true)
        )
        wordProgressDao.insertProgress(
            WordProgress(wordId = 2, isLearned = true)
        )
        wordProgressDao.insertProgress(
            WordProgress(wordId = 3, isLearned = false)
        )

        // When
        val count = wordProgressDao.getLearnedWordCount()

        // Then
        assertThat(count).isEqualTo(2)
    }

    @Test
    fun getMasteredWordCount() = runTest {
        // Given
        wordProgressDao.insertProgress(
            WordProgress(wordId = 1, masteryLevel = 90)
        )
        wordProgressDao.insertProgress(
            WordProgress(wordId = 2, masteryLevel = 85)
        )
        wordProgressDao.insertProgress(
            WordProgress(wordId = 3, masteryLevel = 70)
        )

        // When
        val count = wordProgressDao.getMasteredWordCount(minLevel = 80)

        // Then
        assertThat(count).isEqualTo(2)
    }

    @Test
    fun getAverageMasteryLevel() = runTest {
        // Given
        wordProgressDao.insertProgress(
            WordProgress(wordId = 1, isLearned = true, masteryLevel = 80)
        )
        wordProgressDao.insertProgress(
            WordProgress(wordId = 2, isLearned = true, masteryLevel = 90)
        )
        wordProgressDao.insertProgress(
            WordProgress(wordId = 3, isLearned = true, masteryLevel = 70)
        )

        // When
        val average = wordProgressDao.getAverageMasteryLevel()

        // Then
        assertThat(average).isNotNull()
        assertThat(average).isEqualTo(80f)
    }

    @Test
    fun updateProgress() = runTest {
        // Given
        val progress = WordProgress(wordId = 1, masteryLevel = 50)
        wordProgressDao.insertProgress(progress)

        // When
        val updated = progress.copy(masteryLevel = 75)
        wordProgressDao.updateProgress(updated)

        // Then
        val loaded = wordProgressDao.getProgressByWordId(1)
        assertThat(loaded?.masteryLevel).isEqualTo(75)
    }

    @Test
    fun deleteProgress() = runTest {
        // Given
        val progress = WordProgress(wordId = 1)
        wordProgressDao.insertProgress(progress)

        // When
        wordProgressDao.deleteProgress(progress)

        // Then
        val loaded = wordProgressDao.getProgressByWordId(1)
        assertThat(loaded).isNull()
    }

    @Test
    fun observeProgressByWordId_emits_updates() = runTest {
        // Given
        val progress = WordProgress(wordId = 1, masteryLevel = 50)
        wordProgressDao.insertProgress(progress)

        // When
        val flow = wordProgressDao.observeProgressByWordId(1)

        // Then
        val initialValue = flow.first()
        assertThat(initialValue?.masteryLevel).isEqualTo(50)

        // Update
        wordProgressDao.updateProgress(progress.copy(masteryLevel = 75))

        // Verify update
        val updatedValue = flow.first()
        assertThat(updatedValue?.masteryLevel).isEqualTo(75)
    }
}
