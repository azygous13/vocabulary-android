package com.vocabulary.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vocabulary.data.dao.DailyWordDao
import com.vocabulary.data.dao.WordDao
import com.vocabulary.data.dao.WordProgressDao
import com.vocabulary.data.model.DailyWord
import com.vocabulary.data.model.Word
import com.vocabulary.data.model.WordProgress

@Database(
    entities = [Word::class, WordProgress::class, DailyWord::class],
    version = 2,
    exportSchema = false
)
abstract class VocabularyDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
    abstract fun wordProgressDao(): WordProgressDao
    abstract fun dailyWordDao(): DailyWordDao

    companion object {
        @Volatile
        private var INSTANCE: VocabularyDatabase? = null

        fun getDatabase(context: Context): VocabularyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VocabularyDatabase::class.java,
                    "vocabulary_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
