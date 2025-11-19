package com.vocabulary.data.repository

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.vocabulary.data.dao.DailyWordDao
import com.vocabulary.data.dao.WordDao
import com.vocabulary.data.dao.WordProgressDao
import com.vocabulary.data.model.*
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VocabularyRepositoryFlowTest {

    private lateinit var repository: VocabularyRepository
    private lateinit var wordDao: WordDao
    private lateinit var wordProgressDao: WordProgressDao
    private lateinit var dailyWordDao: DailyWordDao

    private val testWords = listOf(
        Word(
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
        ),
        Word(
            id = 2,
            word = "Algorithm",
            pronunciation = "/ˈæl.ɡə.rɪ.ðəm/",
            partOfSpeech = "noun",
            definition = "A process or set of rules",
            example = "The algorithm works.",
            synonyms = "procedure",
            antonyms = "",
            etymology = "From Arabic",
            difficultyLevel = DifficultyLevel.ADVANCED.name,
            category = WordCategory.TECHNOLOGY.name
        )
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
    fun `getAllWords Flow emits word list`() = runTest {
        // Given
        coEvery { wordDao.getAllWords() } returns flowOf(testWords)

        // When & Then
        repository.getAllWords().test {
            val emission = awaitItem()
            assertThat(emission).hasSize(2)
            assertThat(emission[0].word).isEqualTo("Test")
            assertThat(emission[1].word).isEqualTo("Algorithm")
            awaitComplete()
        }
    }

    @Test
    fun `getWordsByDifficulty Flow filters correctly`() = runTest {
        // Given
        val intermediateWords = testWords.filter {
            it.difficultyLevel == DifficultyLevel.INTERMEDIATE.name
        }
        coEvery {
            wordDao.getWordsByDifficulty(DifficultyLevel.INTERMEDIATE.name)
        } returns flowOf(intermediateWords)

        // When & Then
        repository.getWordsByDifficulty(DifficultyLevel.INTERMEDIATE).test {
            val emission = awaitItem()
            assertThat(emission).hasSize(1)
            assertThat(emission[0].word).isEqualTo("Test")
            awaitComplete()
        }
    }

    @Test
    fun `getWordsByCategory Flow filters correctly`() = runTest {
        // Given
        val techWords = testWords.filter {
            it.category == WordCategory.TECHNOLOGY.name
        }
        coEvery {
            wordDao.getWordsByCategory(WordCategory.TECHNOLOGY.name)
        } returns flowOf(techWords)

        // When & Then
        repository.getWordsByCategory(WordCategory.TECHNOLOGY).test {
            val emission = awaitItem()
            assertThat(emission).hasSize(1)
            assertThat(emission[0].word).isEqualTo("Algorithm")
            awaitComplete()
        }
    }

    @Test
    fun `searchWords Flow emits search results`() = runTest {
        // Given
        val searchResults = testWords.filter { it.word.contains("Test", ignoreCase = true) }
        coEvery { wordDao.searchWords("Test") } returns flowOf(searchResults)

        // When & Then
        repository.searchWords("Test").test {
            val emission = awaitItem()
            assertThat(emission).hasSize(1)
            assertThat(emission[0].word).isEqualTo("Test")
            awaitComplete()
        }
    }

    @Test
    fun `getFavoriteWords Flow emits favorite progress`() = runTest {
        // Given
        val favoriteProgress = listOf(
            WordProgress(wordId = 1, isFavorite = true),
            WordProgress(wordId = 2, isFavorite = true)
        )
        coEvery { wordProgressDao.getFavoriteWords() } returns flowOf(favoriteProgress)

        // When & Then
        repository.getFavoriteWords().test {
            val emission = awaitItem()
            assertThat(emission).hasSize(2)
            assertThat(emission.all { it.isFavorite }).isTrue()
            awaitComplete()
        }
    }

    @Test
    fun `getLearnedWords Flow emits learned progress`() = runTest {
        // Given
        val learnedProgress = listOf(
            WordProgress(wordId = 1, isLearned = true),
            WordProgress(wordId = 3, isLearned = true)
        )
        coEvery { wordProgressDao.getLearnedWords() } returns flowOf(learnedProgress)

        // When & Then
        repository.getLearnedWords().test {
            val emission = awaitItem()
            assertThat(emission).hasSize(2)
            assertThat(emission.all { it.isLearned }).isTrue()
            awaitComplete()
        }
    }

    @Test
    fun `observeWordProgress Flow emits progress updates`() = runTest {
        // Given
        val progress = WordProgress(wordId = 1, masteryLevel = 75)
        coEvery { wordProgressDao.observeProgressByWordId(1) } returns flowOf(progress)

        // When & Then
        repository.observeWordProgress(1).test {
            val emission = awaitItem()
            assertThat(emission?.wordId).isEqualTo(1)
            assertThat(emission?.masteryLevel).isEqualTo(75)
            awaitComplete()
        }
    }

    @Test
    fun `getAllWords Flow handles empty list`() = runTest {
        // Given
        coEvery { wordDao.getAllWords() } returns flowOf(emptyList())

        // When & Then
        repository.getAllWords().test {
            val emission = awaitItem()
            assertThat(emission).isEmpty()
            awaitComplete()
        }
    }

    @Test
    fun `searchWords Flow handles no results`() = runTest {
        // Given
        coEvery { wordDao.searchWords("NonExistent") } returns flowOf(emptyList())

        // When & Then
        repository.searchWords("NonExistent").test {
            val emission = awaitItem()
            assertThat(emission).isEmpty()
            awaitComplete()
        }
    }
}
