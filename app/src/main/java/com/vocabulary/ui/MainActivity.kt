package com.vocabulary.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import com.vocabulary.notification.NotificationHelper

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Schedule daily notification (9 AM)
        NotificationHelper.scheduleDailyNotification(this, 9, 0)

        setContent {
            MaterialTheme {
                VocabularyApp(viewModel = viewModel)
            }
        }
    }
}
