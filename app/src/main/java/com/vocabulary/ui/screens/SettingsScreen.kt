package com.vocabulary.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.vocabulary.data.model.DifficultyLevel
import com.vocabulary.data.model.WordCategory
import com.vocabulary.data.preferences.PreferencesManager
import com.vocabulary.notification.NotificationHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val preferences by preferencesManager.userPreferencesFlow.collectAsState(
        initial = com.vocabulary.data.model.UserPreferences()
    )
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("การตั้งค่า") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Category Selection
            SettingsSection(title = "หมวดหมู่ที่สนใจ") {
                CategorySelectionGrid(
                    selectedCategories = preferences.preferredCategories,
                    onCategoriesChange = { categories ->
                        coroutineScope.launch {
                            preferencesManager.updatePreferredCategories(categories)
                        }
                    }
                )
            }

            HorizontalDivider()

            // Difficulty Level Selection
            SettingsSection(title = "ระดับความยาก") {
                DifficultySelectionGrid(
                    selectedLevels = preferences.preferredDifficultyLevels,
                    onLevelsChange = { levels ->
                        coroutineScope.launch {
                            preferencesManager.updatePreferredDifficultyLevels(levels)
                        }
                    }
                )
            }

            HorizontalDivider()

            // Notification Settings
            SettingsSection(title = "การแจ้งเตือน") {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Enable/Disable Notifications
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "เปิดการแจ้งเตือน",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "รับการแจ้งเตือนคำศัพท์ประจำวัน",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = preferences.notificationsEnabled,
                            onCheckedChange = { enabled ->
                                coroutineScope.launch {
                                    preferencesManager.updateNotificationsEnabled(enabled)
                                    if (enabled) {
                                        NotificationHelper.scheduleDailyNotification(
                                            context,
                                            preferences.notificationHour,
                                            preferences.notificationMinute
                                        )
                                    } else {
                                        NotificationHelper.cancelDailyNotification(context)
                                    }
                                }
                            }
                        )
                    }

                    // Notification Time
                    if (preferences.notificationsEnabled) {
                        var showTimePicker by remember { mutableStateOf(false) }

                        OutlinedButton(
                            onClick = { showTimePicker = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("เวลาแจ้งเตือน: ${String.format("%02d:%02d", preferences.notificationHour, preferences.notificationMinute)}")
                        }

                        if (showTimePicker) {
                            TimePickerDialog(
                                initialHour = preferences.notificationHour,
                                initialMinute = preferences.notificationMinute,
                                onTimeSelected = { hour, minute ->
                                    coroutineScope.launch {
                                        preferencesManager.updateNotificationTime(hour, minute)
                                        NotificationHelper.scheduleDailyNotification(context, hour, minute)
                                    }
                                    showTimePicker = false
                                },
                                onDismiss = { showTimePicker = false }
                            )
                        }
                    }
                }
            }

            HorizontalDivider()

            // Learning Preferences
            SettingsSection(title = "การเรียนรู้") {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Daily Word Goal
                    Column {
                        Text(
                            text = "เป้าหมายคำศัพท์ต่อวัน: ${preferences.dailyWordGoal} คำ",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Slider(
                            value = preferences.dailyWordGoal.toFloat(),
                            onValueChange = { value ->
                                coroutineScope.launch {
                                    preferencesManager.updateDailyWordGoal(value.toInt())
                                }
                            },
                            valueRange = 1f..20f,
                            steps = 18,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Show Only New Words
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "แสดงเฉพาะคำใหม่",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "แสดงเฉพาะคำที่ยังไม่เคยเรียน",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = preferences.showOnlyNewWords,
                            onCheckedChange = { showOnlyNew ->
                                coroutineScope.launch {
                                    preferencesManager.updateShowOnlyNewWords(showOnlyNew)
                                }
                            }
                        )
                    }

                    // Auto Play Audio
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "เล่นเสียงอัตโนมัติ",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "เล่นเสียงออกคำศัพท์โดยอัตโนมัติ",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = preferences.autoPlayAudio,
                            onCheckedChange = { autoPlay ->
                                coroutineScope.launch {
                                    preferencesManager.updateAutoPlayAudio(autoPlay)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        content()
    }
}

@Composable
fun CategorySelectionGrid(
    selectedCategories: Set<WordCategory>,
    onCategoriesChange: (Set<WordCategory>) -> Unit
) {
    Column(
        modifier = Modifier.selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        WordCategory.entries.forEach { category ->
            val isSelected = category in selectedCategories

            FilterChip(
                selected = isSelected,
                onClick = {
                    val newCategories = if (isSelected) {
                        if (selectedCategories.size > 1) {
                            selectedCategories - category
                        } else {
                            selectedCategories // Don't allow deselecting all
                        }
                    } else {
                        selectedCategories + category
                    }
                    onCategoriesChange(newCategories)
                },
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(category.emoji)
                        Text(category.displayName)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun DifficultySelectionGrid(
    selectedLevels: Set<DifficultyLevel>,
    onLevelsChange: (Set<DifficultyLevel>) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DifficultyLevel.entries.forEach { level ->
            val isSelected = level in selectedLevels

            FilterChip(
                selected = isSelected,
                onClick = {
                    val newLevels = if (isSelected) {
                        if (selectedLevels.size > 1) {
                            selectedLevels - level
                        } else {
                            selectedLevels // Don't allow deselecting all
                        }
                    } else {
                        selectedLevels + level
                    }
                    onLevelsChange(newLevels)
                },
                label = { Text(level.displayName) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onTimeSelected: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("เลือกเวลาแจ้งเตือน") },
        text = {
            TimePicker(state = timePickerState)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(timePickerState.hour, timePickerState.minute)
                }
            ) {
                Text("ตกลง")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("ยกเลิก")
            }
        }
    )
}
