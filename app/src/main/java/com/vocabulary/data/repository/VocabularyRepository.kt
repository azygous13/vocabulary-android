package com.vocabulary.data.repository

import com.vocabulary.data.dao.DailyWordDao
import com.vocabulary.data.dao.WordDao
import com.vocabulary.data.dao.WordProgressDao
import com.vocabulary.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*

class VocabularyRepository(
    private val wordDao: WordDao,
    private val wordProgressDao: WordProgressDao,
    private val dailyWordDao: DailyWordDao
) {
    // Word operations
    fun getAllWords(): Flow<List<Word>> = wordDao.getAllWords()

    suspend fun getWordById(wordId: Long): Word? = wordDao.getWordById(wordId)

    fun getWordsByDifficulty(level: DifficultyLevel): Flow<List<Word>> =
        wordDao.getWordsByDifficulty(level.name)

    fun getWordsByCategory(category: WordCategory): Flow<List<Word>> =
        wordDao.getWordsByCategory(category.name)

    fun searchWords(query: String): Flow<List<Word>> = wordDao.searchWords(query)

    suspend fun insertWord(word: Word): Long = wordDao.insertWord(word)

    suspend fun insertWords(words: List<Word>) = wordDao.insertWords(words)

    suspend fun getWordCount(): Int = wordDao.getWordCount()

    // Daily word operations
    suspend fun getTodayWord(): Word? {
        val today = getCurrentDate()
        val dailyWord = dailyWordDao.getDailyWordByDate(today)

        return if (dailyWord != null) {
            wordDao.getWordById(dailyWord.wordId)
        } else {
            // Select a new word for today
            selectNewDailyWord()
        }
    }

    private suspend fun selectNewDailyWord(): Word? {
        // Get all words
        val allWords = wordDao.getAllWords().firstOrNull() ?: return null
        if (allWords.isEmpty()) return null

        // Get learned word IDs to avoid repetition
        val learnedWords = wordProgressDao.getLearnedWords().firstOrNull() ?: emptyList()
        val learnedWordIds = learnedWords.map { it.wordId }.toSet()

        // Prefer unlearned words
        val unlearnedWords = allWords.filter { it.id !in learnedWordIds }
        val selectedWord = if (unlearnedWords.isNotEmpty()) {
            unlearnedWords.random()
        } else {
            allWords.random()
        }

        // Save as today's word
        val today = getCurrentDate()
        dailyWordDao.insertDailyWord(
            DailyWord(date = today, wordId = selectedWord.id)
        )

        return selectedWord
    }

    fun getRecentDailyWords(limit: Int = 7): Flow<List<DailyWord>> =
        dailyWordDao.getRecentDailyWords(limit)

    // Word progress operations
    suspend fun getWordProgress(wordId: Long): WordProgress {
        return wordProgressDao.getProgressByWordId(wordId)
            ?: WordProgress(wordId = wordId)
    }

    fun observeWordProgress(wordId: Long): Flow<WordProgress?> =
        wordProgressDao.observeProgressByWordId(wordId)

    suspend fun markWordAsLearned(wordId: Long) {
        val progress = getWordProgress(wordId)
        val updatedProgress = progress.copy(
            isLearned = true,
            firstLearnedDate = progress.firstLearnedDate ?: System.currentTimeMillis()
        )
        wordProgressDao.insertProgress(updatedProgress)
    }

    suspend fun toggleLearned(wordId: Long) {
        val progress = getWordProgress(wordId)
        val updatedProgress = if (!progress.isLearned) {
            // Mark as learned
            progress.copy(
                isLearned = true,
                firstLearnedDate = progress.firstLearnedDate ?: System.currentTimeMillis()
            )
        } else {
            // Unmark as learned
            progress.copy(
                isLearned = false,
                firstLearnedDate = null
            )
        }
        wordProgressDao.insertProgress(updatedProgress)
    }

    suspend fun toggleFavorite(wordId: Long) {
        val progress = getWordProgress(wordId)
        val updatedProgress = progress.copy(isFavorite = !progress.isFavorite)
        wordProgressDao.insertProgress(updatedProgress)
    }

    suspend fun recordQuizAnswer(wordId: Long, isCorrect: Boolean) {
        val progress = getWordProgress(wordId)
        val updatedProgress = if (isCorrect) {
            progress.copy(
                correctAnswers = progress.correctAnswers + 1,
                lastPracticed = System.currentTimeMillis(),
                reviewCount = progress.reviewCount + 1,
                masteryLevel = calculateMasteryLevel(
                    progress.correctAnswers + 1,
                    progress.incorrectAnswers
                )
            )
        } else {
            progress.copy(
                incorrectAnswers = progress.incorrectAnswers + 1,
                lastPracticed = System.currentTimeMillis(),
                reviewCount = progress.reviewCount + 1,
                masteryLevel = calculateMasteryLevel(
                    progress.correctAnswers,
                    progress.incorrectAnswers + 1
                )
            )
        }
        wordProgressDao.insertProgress(updatedProgress)
    }

    private fun calculateMasteryLevel(correct: Int, incorrect: Int): Int {
        val total = correct + incorrect
        if (total == 0) return 0

        val accuracy = (correct.toFloat() / total) * 100
        val reviewBonus = minOf(total * 5, 20) // Up to 20% bonus for reviews

        return minOf((accuracy + reviewBonus).toInt(), 100)
    }

    fun getFavoriteWords(): Flow<List<WordProgress>> =
        wordProgressDao.getFavoriteWords()

    fun getLearnedWords(): Flow<List<WordProgress>> =
        wordProgressDao.getLearnedWords()

    suspend fun getLearnedWordCount(): Int =
        wordProgressDao.getLearnedWordCount()

    suspend fun getMasteredWordCount(): Int =
        wordProgressDao.getMasteredWordCount()

    // Statistics
    suspend fun getStatistics(): VocabularyStatistics {
        val totalWords = wordDao.getWordCount()
        val learnedCount = wordProgressDao.getLearnedWordCount()
        val masteredCount = wordProgressDao.getMasteredWordCount(80)
        val averageMastery = wordProgressDao.getAverageMasteryLevel() ?: 0f

        return VocabularyStatistics(
            totalWords = totalWords,
            learnedWords = learnedCount,
            masteredWords = masteredCount,
            averageMasteryLevel = averageMastery
        )
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}

data class VocabularyStatistics(
    val totalWords: Int,
    val learnedWords: Int,
    val masteredWords: Int,
    val averageMasteryLevel: Float
)
