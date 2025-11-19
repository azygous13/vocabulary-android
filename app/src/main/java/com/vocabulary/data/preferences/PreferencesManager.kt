package com.vocabulary.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.vocabulary.data.model.DifficultyLevel
import com.vocabulary.data.model.UserPreferences
import com.vocabulary.data.model.WordCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class PreferencesManager(private val context: Context) {

    companion object {
        private val PREFERRED_CATEGORIES = stringSetPreferencesKey("preferred_categories")
        private val PREFERRED_DIFFICULTY_LEVELS = stringSetPreferencesKey("preferred_difficulty_levels")
        private val NOTIFICATION_HOUR = intPreferencesKey("notification_hour")
        private val NOTIFICATION_MINUTE = intPreferencesKey("notification_minute")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val DAILY_WORD_GOAL = intPreferencesKey("daily_word_goal")
        private val SHOW_ONLY_NEW_WORDS = booleanPreferencesKey("show_only_new_words")
        private val AUTO_PLAY_AUDIO = booleanPreferencesKey("auto_play_audio")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { preferences ->
        val categoryStrings = preferences[PREFERRED_CATEGORIES] ?: WordCategory.entries.map { it.name }.toSet()
        val categories = categoryStrings.mapNotNull { categoryName ->
            try {
                WordCategory.valueOf(categoryName)
            } catch (e: IllegalArgumentException) {
                null
            }
        }.toSet().ifEmpty { WordCategory.entries.toSet() }

        val difficultyStrings = preferences[PREFERRED_DIFFICULTY_LEVELS] ?: DifficultyLevel.entries.map { it.name }.toSet()
        val difficulties = difficultyStrings.mapNotNull { difficultyName ->
            try {
                DifficultyLevel.valueOf(difficultyName)
            } catch (e: IllegalArgumentException) {
                null
            }
        }.toSet().ifEmpty { DifficultyLevel.entries.toSet() }

        UserPreferences(
            preferredCategories = categories,
            preferredDifficultyLevels = difficulties,
            notificationHour = preferences[NOTIFICATION_HOUR] ?: 9,
            notificationMinute = preferences[NOTIFICATION_MINUTE] ?: 0,
            notificationsEnabled = preferences[NOTIFICATIONS_ENABLED] ?: true,
            dailyWordGoal = preferences[DAILY_WORD_GOAL] ?: 5,
            showOnlyNewWords = preferences[SHOW_ONLY_NEW_WORDS] ?: false,
            autoPlayAudio = preferences[AUTO_PLAY_AUDIO] ?: false
        )
    }

    suspend fun updatePreferredCategories(categories: Set<WordCategory>) {
        context.dataStore.edit { preferences ->
            preferences[PREFERRED_CATEGORIES] = categories.map { it.name }.toSet()
        }
    }

    suspend fun updatePreferredDifficultyLevels(levels: Set<DifficultyLevel>) {
        context.dataStore.edit { preferences ->
            preferences[PREFERRED_DIFFICULTY_LEVELS] = levels.map { it.name }.toSet()
        }
    }

    suspend fun updateNotificationTime(hour: Int, minute: Int) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_HOUR] = hour
            preferences[NOTIFICATION_MINUTE] = minute
        }
    }

    suspend fun updateNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun updateDailyWordGoal(goal: Int) {
        context.dataStore.edit { preferences ->
            preferences[DAILY_WORD_GOAL] = goal
        }
    }

    suspend fun updateShowOnlyNewWords(showOnlyNew: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_ONLY_NEW_WORDS] = showOnlyNew
        }
    }

    suspend fun updateAutoPlayAudio(autoPlay: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_PLAY_AUDIO] = autoPlay
        }
    }
}
