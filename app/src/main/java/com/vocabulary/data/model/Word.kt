package com.vocabulary.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val word: String,

    val pronunciation: String,

    val partOfSpeech: String, // noun, verb, adjective, etc.

    val definition: String,

    val example: String,

    val synonyms: String, // comma-separated

    val antonyms: String, // comma-separated

    val etymology: String, // word origin/history

    val difficultyLevel: String, // DifficultyLevel enum as string

    val category: String, // WordCategory enum as string

    val imageUrl: String? = null,

    val audioUrl: String? = null,

    val dateAdded: Long = System.currentTimeMillis()
) {
    fun getDifficulty(): DifficultyLevel = DifficultyLevel.fromString(difficultyLevel)

    fun getCategory(): WordCategory = WordCategory.fromString(category)

    fun getSynonymsList(): List<String> =
        synonyms.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    fun getAntonymsList(): List<String> =
        antonyms.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}
