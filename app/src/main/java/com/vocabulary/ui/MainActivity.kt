package com.vocabulary.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.vocabulary.notification.NotificationHelper
import com.vocabulary.ui.theme.VocabularyTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Schedule daily notification (9 AM)
        NotificationHelper.scheduleDailyNotification(this, 9, 0)

        setContent {
            VocabularyTheme {
                VocabularyApp(viewModel = viewModel)
            }
        }
    }
}
