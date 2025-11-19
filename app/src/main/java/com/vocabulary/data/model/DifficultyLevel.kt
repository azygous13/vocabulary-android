package com.vocabulary.data.model

enum class DifficultyLevel(val displayName: String) {
    BEGINNER("Beginner"),
    INTERMEDIATE("Intermediate"),
    ADVANCED("Advanced"),
    EXPERT("Expert");

    companion object {
        fun fromString(value: String): DifficultyLevel {
            return entries.find { it.name == value } ?: BEGINNER
        }
    }
}
