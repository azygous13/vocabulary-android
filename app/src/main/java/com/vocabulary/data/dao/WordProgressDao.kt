package com.vocabulary.data.dao

import androidx.room.*
import com.vocabulary.data.model.WordProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface WordProgressDao {
    @Query("SELECT * FROM word_progress WHERE wordId = :wordId")
    suspend fun getProgressByWordId(wordId: Long): WordProgress?

    @Query("SELECT * FROM word_progress WHERE wordId = :wordId")
    fun observeProgressByWordId(wordId: Long): Flow<WordProgress?>

    @Query("SELECT * FROM word_progress WHERE isLearned = 1")
    fun getLearnedWords(): Flow<List<WordProgress>>

    @Query("SELECT * FROM word_progress WHERE isFavorite = 1")
    fun getFavoriteWords(): Flow<List<WordProgress>>

    @Query("SELECT COUNT(*) FROM word_progress WHERE isLearned = 1")
    suspend fun getLearnedWordCount(): Int

    @Query("SELECT COUNT(*) FROM word_progress WHERE masteryLevel >= :minLevel")
    suspend fun getMasteredWordCount(minLevel: Int = 80): Int

    @Query("SELECT AVG(masteryLevel) FROM word_progress WHERE isLearned = 1")
    suspend fun getAverageMasteryLevel(): Float?

    @Query("SELECT * FROM word_progress ORDER BY lastPracticed DESC LIMIT :limit")
    fun getRecentlyPracticedWords(limit: Int = 10): Flow<List<WordProgress>>

    @Query("SELECT * FROM word_progress WHERE nextReviewDate IS NULL OR nextReviewDate <= :currentTime ORDER BY nextReviewDate ASC")
    fun getWordsDueForReview(currentTime: Long = System.currentTimeMillis()): Flow<List<WordProgress>>

    @Query("SELECT COUNT(*) FROM word_progress WHERE nextReviewDate IS NULL OR nextReviewDate <= :currentTime")
    suspend fun getDueForReviewCount(currentTime: Long = System.currentTimeMillis()): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: WordProgress)

    @Update
    suspend fun updateProgress(progress: WordProgress)

    @Delete
    suspend fun deleteProgress(progress: WordProgress)

    @Query("DELETE FROM word_progress")
    suspend fun deleteAllProgress()
}
