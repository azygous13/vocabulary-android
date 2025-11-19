package com.vocabulary.data.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.vocabulary.data.database.VocabularyDatabase
import com.vocabulary.data.model.DifficultyLevel
import com.vocabulary.data.model.Word
import com.vocabulary.data.model.WordCategory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WordDaoTest {

    private lateinit var database: VocabularyDatabase
    private lateinit var wordDao: WordDao

    private val testWord1 = Word(
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

    private val testWord2 = Word(
        id = 2,
        word = "Algorithm",
        pronunciation = "/ˈæl.ɡə.rɪ.ðəm/",
        partOfSpeech = "noun",
        definition = "A process or set of rules",
        example = "The algorithm works.",
        synonyms = "procedure, method",
        antonyms = "",
        etymology = "From Arabic",
        difficultyLevel = DifficultyLevel.ADVANCED.name,
        category = WordCategory.TECHNOLOGY.name
    )

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            VocabularyDatabase::class.java
        ).build()
        wordDao = database.wordDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertWord_and_getById() = runTest {
        // When
        wordDao.insertWord(testWord1)

        // Then
        val loaded = wordDao.getWordById(1)
        assertThat(loaded).isNotNull()
        assertThat(loaded?.word).isEqualTo("Test")
    }

    @Test
    fun insertWords_and_getAllWords() = runTest {
        // When
        wordDao.insertWords(listOf(testWord1, testWord2))

        // Then
        val allWords = wordDao.getAllWords().first()
        assertThat(allWords).hasSize(2)
    }

    @Test
    fun getWordsByDifficulty() = runTest {
        // Given
        wordDao.insertWords(listOf(testWord1, testWord2))

        // When
        val intermediateWords = wordDao.getWordsByDifficulty(
            DifficultyLevel.INTERMEDIATE.name
        ).first()

        // Then
        assertThat(intermediateWords).hasSize(1)
        assertThat(intermediateWords[0].word).isEqualTo("Test")
    }

    @Test
    fun getWordsByCategory() = runTest {
        // Given
        wordDao.insertWords(listOf(testWord1, testWord2))

        // When
        val techWords = wordDao.getWordsByCategory(
            WordCategory.TECHNOLOGY.name
        ).first()

        // Then
        assertThat(techWords).hasSize(1)
        assertThat(techWords[0].word).isEqualTo("Algorithm")
    }

    @Test
    fun searchWords() = runTest {
        // Given
        wordDao.insertWords(listOf(testWord1, testWord2))

        // When
        val results = wordDao.searchWords("algo").first()

        // Then
        assertThat(results).hasSize(1)
        assertThat(results[0].word).isEqualTo("Algorithm")
    }

    @Test
    fun getRandomWord() = runTest {
        // Given
        wordDao.insertWords(listOf(testWord1, testWord2))

        // When
        val randomWord = wordDao.getRandomWord()

        // Then
        assertThat(randomWord).isNotNull()
        assertThat(randomWord?.word).isAnyOf("Test", "Algorithm")
    }

    @Test
    fun getWordCount() = runTest {
        // Given
        wordDao.insertWords(listOf(testWord1, testWord2))

        // When
        val count = wordDao.getWordCount()

        // Then
        assertThat(count).isEqualTo(2)
    }

    @Test
    fun deleteWord() = runTest {
        // Given
        wordDao.insertWord(testWord1)

        // When
        wordDao.deleteWord(testWord1)

        // Then
        val count = wordDao.getWordCount()
        assertThat(count).isEqualTo(0)
    }

    @Test
    fun updateWord() = runTest {
        // Given
        wordDao.insertWord(testWord1)

        // When
        val updatedWord = testWord1.copy(definition = "Updated definition")
        wordDao.updateWord(updatedWord)

        // Then
        val loaded = wordDao.getWordById(1)
        assertThat(loaded?.definition).isEqualTo("Updated definition")
    }
}
