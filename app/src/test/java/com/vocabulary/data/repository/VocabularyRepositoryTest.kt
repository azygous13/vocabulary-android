package com.vocabulary.data.repository

import com.google.common.truth.Truth.assertThat
import com.vocabulary.data.dao.DailyWordDao
import com.vocabulary.data.dao.WordDao
import com.vocabulary.data.dao.WordProgressDao
import com.vocabulary.data.model.*
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class VocabularyRepositoryTest {

    private lateinit var repository: VocabularyRepository
    private lateinit var wordDao: WordDao
    private lateinit var wordProgressDao: WordProgressDao
    private lateinit var dailyWordDao: DailyWordDao

    private val testWord = Word(
        id = 1,
        word = "Test",
        pronunciation = "/test/",
        partOfSpeech = "noun",
        definition = "A test word",
        example = "This is a test.",
        synonyms = "exam",
        antonyms = "real",
        etymology = "From Latin",
        difficultyLevel = DifficultyLevel.INTERMEDIATE.name,
        category = WordCategory.GENERAL.name
    )

    @Before
    fun setup() {
        wordDao = mockk()
        wordProgressDao = mockk()
        dailyWordDao = mockk()
        repository = VocabularyRepository(wordDao, wordProgressDao, dailyWordDao)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getAllWords returns flow from dao`() = runTest {
        // Given
        val words = listOf(testWord)
        coEvery { wordDao.getAllWords() } returns flowOf(words)

        // When
        val result = repository.getAllWords().first()

        // Then
        assertThat(result).isEqualTo(words)
        coVerify { wordDao.getAllWords() }
    }

    @Test
    fun `getWordById returns word from dao`() = runTest {
        // Given
        coEvery { wordDao.getWordById(1) } returns testWord

        // When
        val result = repository.getWordById(1)

        // Then
        assertThat(result).isEqualTo(testWord)
        coVerify { wordDao.getWordById(1) }
    }

    @Test
    fun `insertWord calls dao insertWord`() = runTest {
        // Given
        coEvery { wordDao.insertWord(testWord) } returns 1L

        // When
        val result = repository.insertWord(testWord)

        // Then
        assertThat(result).isEqualTo(1L)
        coVerify { wordDao.insertWord(testWord) }
    }

    @Test
    fun `markWordAsLearned updates progress correctly`() = runTest {
        // Given
        val progress = WordProgress(wordId = 1, isLearned = false)
        coEvery { wordProgressDao.getProgressByWordId(1) } returns progress
        coEvery { wordProgressDao.insertProgress(any()) } just Runs

        // When
        repository.markWordAsLearned(1)

        // Then
        coVerify {
            wordProgressDao.insertProgress(
                match { it.isLearned && it.firstLearnedDate != null }
            )
        }
    }

    @Test
    fun `toggleFavorite toggles isFavorite flag`() = runTest {
        // Given
        val progress = WordProgress(wordId = 1, isFavorite = false)
        coEvery { wordProgressDao.getProgressByWordId(1) } returns progress
        coEvery { wordProgressDao.insertProgress(any()) } just Runs

        // When
        repository.toggleFavorite(1)

        // Then
        coVerify {
            wordProgressDao.insertProgress(
                match { it.isFavorite == true }
            )
        }
    }

    @Test
    fun `recordQuizAnswer with correct answer updates progress`() = runTest {
        // Given
        val progress = WordProgress(
            wordId = 1,
            correctAnswers = 5,
            incorrectAnswers = 2,
            reviewCount = 7
        )
        coEvery { wordProgressDao.getProgressByWordId(1) } returns progress
        coEvery { wordProgressDao.insertProgress(any()) } just Runs

        // When
        repository.recordQuizAnswer(1, isCorrect = true)

        // Then
        coVerify {
            wordProgressDao.insertProgress(
                match {
                    it.correctAnswers == 6 &&
                    it.reviewCount == 8 &&
                    it.lastPracticed != null
                }
            )
        }
    }

    @Test
    fun `recordQuizAnswer with incorrect answer updates progress`() = runTest {
        // Given
        val progress = WordProgress(
            wordId = 1,
            correctAnswers = 5,
            incorrectAnswers = 2,
            reviewCount = 7
        )
        coEvery { wordProgressDao.getProgressByWordId(1) } returns progress
        coEvery { wordProgressDao.insertProgress(any()) } just Runs

        // When
        repository.recordQuizAnswer(1, isCorrect = false)

        // Then
        coVerify {
            wordProgressDao.insertProgress(
                match {
                    it.incorrectAnswers == 3 &&
                    it.reviewCount == 8 &&
                    it.lastPracticed != null
                }
            )
        }
    }

    @Test
    fun `getStatistics returns correct statistics`() = runTest {
        // Given
        coEvery { wordDao.getWordCount() } returns 100
        coEvery { wordProgressDao.getLearnedWordCount() } returns 30
        coEvery { wordProgressDao.getMasteredWordCount(80) } returns 15
        coEvery { wordProgressDao.getAverageMasteryLevel() } returns 65.5f

        // When
        val stats = repository.getStatistics()

        // Then
        assertThat(stats.totalWords).isEqualTo(100)
        assertThat(stats.learnedWords).isEqualTo(30)
        assertThat(stats.masteredWords).isEqualTo(15)
        assertThat(stats.averageMasteryLevel).isEqualTo(65.5f)
    }

    @Test
    fun `getWordProgress returns existing progress`() = runTest {
        // Given
        val existingProgress = WordProgress(wordId = 1, masteryLevel = 75)
        coEvery { wordProgressDao.getProgressByWordId(1) } returns existingProgress

        // When
        val result = repository.getWordProgress(1)

        // Then
        assertThat(result).isEqualTo(existingProgress)
    }

    @Test
    fun `getWordProgress returns new progress when none exists`() = runTest {
        // Given
        coEvery { wordProgressDao.getProgressByWordId(1) } returns null

        // When
        val result = repository.getWordProgress(1)

        // Then
        assertThat(result.wordId).isEqualTo(1)
        assertThat(result.masteryLevel).isEqualTo(0)
        assertThat(result.isLearned).isFalse()
    }
}
