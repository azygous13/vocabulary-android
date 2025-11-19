package com.vocabulary.data.model

enum class WordCategory(val displayName: String) {
    GENERAL("General"),
    BUSINESS("Business"),
    ACADEMIC("Academic"),
    TECHNOLOGY("Technology"),
    SCIENCE("Science"),
    MEDICAL("Medical"),
    LEGAL("Legal"),
    ARTS("Arts & Literature");

    companion object {
        fun fromString(value: String): WordCategory {
            return entries.find { it.name == value } ?: GENERAL
        }
    }
}
