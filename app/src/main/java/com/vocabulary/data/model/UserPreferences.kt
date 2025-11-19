package com.vocabulary.data.model

data class UserPreferences(
    val preferredCategories: Set<WordCategory> = WordCategory.entries.toSet(),
    val preferredDifficultyLevels: Set<DifficultyLevel> = DifficultyLevel.entries.toSet(),
    val notificationHour: Int = 9, // 9 AM default
    val notificationMinute: Int = 0,
    val notificationsEnabled: Boolean = true,
    val dailyWordGoal: Int = 5,
    val showOnlyNewWords: Boolean = false,
    val autoPlayAudio: Boolean = false
) {
    fun isWordRelevant(word: Word): Boolean {
        val categoryMatch = word.getCategoryEnum() in preferredCategories
        val difficultyMatch = word.getDifficultyEnum() in preferredDifficultyLevels
        return categoryMatch && difficultyMatch
    }

    fun getNotificationTime(): Pair<Int, Int> = Pair(notificationHour, notificationMinute)
}
