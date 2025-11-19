package com.vocabulary.data.dao

import androidx.room.*
import com.vocabulary.data.model.Word
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM words")
    fun getAllWords(): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE id = :wordId")
    suspend fun getWordById(wordId: Long): Word?

    @Query("SELECT * FROM words WHERE difficultyLevel = :level")
    fun getWordsByDifficulty(level: String): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE category = :category")
    fun getWordsByCategory(category: String): Flow<List<Word>>

    @Query("SELECT * FROM words WHERE difficultyLevel = :level AND category = :category")
    fun getWordsByDifficultyAndCategory(level: String, category: String): Flow<List<Word>>

    @Query("SELECT * FROM words ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWord(): Word?

    @Query("SELECT * FROM words WHERE difficultyLevel = :level ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomWordByDifficulty(level: String): Word?

    @Query("SELECT * FROM words WHERE word LIKE '%' || :query || '%'")
    fun searchWords(query: String): Flow<List<Word>>

    @Query("SELECT COUNT(*) FROM words")
    suspend fun getWordCount(): Int

    @Query("SELECT COUNT(*) FROM words WHERE difficultyLevel = :level")
    suspend fun getWordCountByDifficulty(level: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: Word): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<Word>)

    @Update
    suspend fun updateWord(word: Word)

    @Delete
    suspend fun deleteWord(word: Word)

    @Query("DELETE FROM words")
    suspend fun deleteAllWords()
}
