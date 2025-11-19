package com.vocabulary.data.dao

import androidx.room.*
import com.vocabulary.data.model.DailyWord
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyWordDao {
    @Query("SELECT * FROM daily_words WHERE date = :date")
    suspend fun getDailyWordByDate(date: String): DailyWord?

    @Query("SELECT * FROM daily_words ORDER BY date DESC LIMIT :limit")
    fun getRecentDailyWords(limit: Int = 7): Flow<List<DailyWord>>

    @Query("SELECT * FROM daily_words ORDER BY date DESC")
    fun getAllDailyWords(): Flow<List<DailyWord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyWord(dailyWord: DailyWord)

    @Delete
    suspend fun deleteDailyWord(dailyWord: DailyWord)

    @Query("DELETE FROM daily_words WHERE date < :cutoffDate")
    suspend fun deleteOldDailyWords(cutoffDate: String)

    @Query("DELETE FROM daily_words")
    suspend fun deleteAllDailyWords()
}
