package com.vocabulary.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vocabulary.data.database.VocabularyDatabase
import com.vocabulary.data.repository.VocabularyRepository

class DailyWordWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val database = VocabularyDatabase.getDatabase(applicationContext)
            val repository = VocabularyRepository(
                database.wordDao(),
                database.wordProgressDao(),
                database.dailyWordDao()
            )

            // Get today's word
            val word = repository.getTodayWord()

            word?.let {
                // Show notification
                NotificationHelper.showDailyWordNotification(
                    applicationContext,
                    it.word,
                    it.definition
                )
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
