package com.vocabulary.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_words")
data class DailyWord(
    @PrimaryKey
    val date: String, // Format: YYYY-MM-DD

    val wordId: Long,

    val timestamp: Long = System.currentTimeMillis()
)
