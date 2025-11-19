package com.vocabulary.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vocabulary.data.database.VocabularyDatabase
import com.vocabulary.data.model.Word
import com.vocabulary.data.model.WordProgress
import com.vocabulary.ui.MainViewModel
import com.vocabulary.ui.components.rememberTextToSpeech
import com.vocabulary.ui.components.speakWord
import com.vocabulary.util.SpacedRepetitionHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpacedRepetitionScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val allWords by viewModel.allWords.collectAsStateWithLifecycle(emptyList())

    var dueWords by remember { mutableStateOf<List<Pair<Word, WordProgress>>>(emptyList()) }
    var currentIndex by remember { mutableIntStateOf(0) }
    var showAnswer by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var reviewedCount by remember { mutableIntStateOf(0) }

    // Load words due for review
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val database = VocabularyDatabase.getDatabase(context)
            val progressDao = database.wordProgressDao()
            val wordDao = database.wordDao()

            // Get all progress records due for review
            progressDao.getWordsDueForReview().collect { progressList ->
                val wordsWithProgress = progressList.mapNotNull { progress ->
                    wordDao.getWordById(progress.wordId)?.let { word ->
                        word to progress
                    }
                }
                dueWords = wordsWithProgress
                isLoading = false
            }
        }
    }

    val currentPair = dueWords.getOrNull(currentIndex)
    val tts = rememberTextToSpeech()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (dueWords.isNotEmpty()) {
                            "ทบทวนคำศัพท์ (${currentIndex + 1}/${dueWords.size}) - ทบทวนแล้ว: $reviewedCount"
                        } else {
                            "ทบทวนคำศัพท์"
                        }
                    )
                },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                dueWords.isEmpty() -> {
                    EmptyReviewState(modifier = Modifier.align(Alignment.Center))
                }
                currentPair != null -> {
                    val (word, progress) = currentPair
                    ReviewCard(
                        word = word,
                        progress = progress,
                        showAnswer = showAnswer,
                        onShowAnswer = { showAnswer = true },
                        onReview = { quality ->
                            coroutineScope.launch {
                                // Calculate next review schedule
                                val updatedProgress = SpacedRepetitionHelper.calculateNextReview(
                                    progress,
                                    quality
                                )

                                // Save to database
                                withContext(Dispatchers.IO) {
                                    val database = VocabularyDatabase.getDatabase(context)
                                    database.wordProgressDao().updateProgress(updatedProgress)
                                }

                                // Move to next word
                                reviewedCount++
                                showAnswer = false
                                if (currentIndex < dueWords.size - 1) {
                                    currentIndex++
                                } else {
                                    onNavigateBack()
                                }
                            }
                        },
                        tts = tts
                    )
                }
                else -> {
                    CompletedReviewState(
                        reviewedCount = reviewedCount,
                        onFinish = onNavigateBack,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewCard(
    word: Word,
    progress: WordProgress,
    showAnswer: Boolean,
    onShowAnswer: () -> Unit,
    onReview: (SpacedRepetitionHelper.ReviewQuality) -> Unit,
    tts: android.speech.tts.TextToSpeech?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress Stats Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatColumn("ครั้งที่ทบทวน", progress.reviewCount.toString())
                StatColumn("ความแม่นยำ", "${progress.getAccuracy().toInt()}%")
                StatColumn("ระดับความเชี่ยวชาญ", "${progress.masteryLevel}%")
            }
        }

        // Word Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "จำคำนี้ได้ไหม?",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = word.word,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    IconButton(
                        onClick = { tts?.speakWord(word.word) },
                        enabled = tts != null
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = "Play pronunciation",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Text(
                    text = word.pronunciation,
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                AssistChip(
                    onClick = { },
                    label = { Text(word.partOfSpeech) },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Answer section (shown after clicking "แสดงคำตอบ")
        AnimatedVisibility(
            visible = showAnswer,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "คำจำกัดความ",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = word.definition,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "ตัวอย่างประโยค",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "\"${word.example}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                // Review Quality Buttons
                Text(
                    text = "คุณจำคำนี้ได้แค่ไหน?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SpacedRepetitionHelper.ReviewQuality.entries.reversed().forEach { quality ->
                        ReviewQualityButton(
                            quality = quality,
                            onClick = { onReview(quality) }
                        )
                    }
                }
            }
        }

        // Show Answer Button
        if (!showAnswer) {
            Button(
                onClick = onShowAnswer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("แสดงคำตอบ")
            }
        }
    }
}

@Composable
fun ReviewQualityButton(
    quality: SpacedRepetitionHelper.ReviewQuality,
    onClick: () -> Unit
) {
    val color = when (quality) {
        SpacedRepetitionHelper.ReviewQuality.FORGOT -> Color(0xFFF44336)
        SpacedRepetitionHelper.ReviewQuality.HARD -> Color(0xFFFF9800)
        SpacedRepetitionHelper.ReviewQuality.GOOD -> Color(0xFF4CAF50)
        SpacedRepetitionHelper.ReviewQuality.EASY -> Color(0xFF2196F3)
    }

    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        )
    ) {
        Text(
            text = quality.displayName,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun StatColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun EmptyReviewState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🎉",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "ไม่มีคำศัพท์ที่ต้องทบทวน",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "คุณทำได้ดีมาก! กลับมาทบทวนใหม่ภายหลัง",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun CompletedReviewState(
    reviewedCount: Int,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "✅",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "ทบทวนเสร็จแล้ว!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "คุณทบทวนไปแล้ว $reviewedCount คำ",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onFinish) {
            Text("เสร็จสิ้น")
        }
    }
}
