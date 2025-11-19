package com.vocabulary.ui.components

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import java.util.*

@Composable
fun rememberTextToSpeech(): TextToSpeech? {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var isInitialized by remember { mutableStateOf(false) }

    DisposableEffect(context) {
        val textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                isInitialized = true
            }
        }
        tts = textToSpeech

        onDispose {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
    }

    return if (isInitialized) tts else null
}

fun TextToSpeech.speakWord(word: String) {
    speak(word, TextToSpeech.QUEUE_FLUSH, null, null)
}
