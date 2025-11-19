package com.vocabulary.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vocabulary.data.database.VocabularyDatabase
import com.vocabulary.data.model.Word
import com.vocabulary.ui.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: MainViewModel,
    quizType: String = "MULTIPLE_CHOICE",
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val allWords by viewModel.allWords.collectAsStateWithLifecycle(emptyList())

    var currentWord by remember { mutableStateOf<Word?>(null) }
    var options by remember { mutableStateOf<List<Word>>(emptyList()) }
    var correctAnswerIndex by remember { mutableIntStateOf(0) }
    var selectedIndex by remember { mutableIntStateOf(-1) }
    var showResult by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf("") }
    var isCorrect by remember { mutableStateOf(false) }
    var questionCount by remember { mutableIntStateOf(0) }
    var correctCount by remember { mutableIntStateOf(0) }
    var isAnswerChecked by remember { mutableStateOf(false) }

    // Load initial question
    LaunchedEffect(allWords) {
        if (allWords.isNotEmpty()) {
            loadNextQuestion(
                allWords = allWords,
                onWordLoaded = { word, opts, answerIdx ->
                    currentWord = word
                    options = opts
                    correctAnswerIndex = answerIdx
                    selectedIndex = -1
                    showResult = false
                    isAnswerChecked = false
                }
            )
        }
    }

    val quizTypeEnum = remember(quizType) {
        try {
            QuizType.valueOf(quizType)
        } catch (e: IllegalArgumentException) {
            QuizType.MULTIPLE_CHOICE
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (questionCount > 0) {
                            "${quizTypeEnum.displayName} - Score: $correctCount/$questionCount"
                        } else {
                            quizTypeEnum.displayName
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
        if (allWords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No words available",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Question Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "What is the definition of:",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        currentWord?.let { word ->
                            Text(
                                text = word.word,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Options Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        options.forEachIndexed { index, option ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = selectedIndex == index,
                                        onClick = {
                                            if (!isAnswerChecked) {
                                                selectedIndex = index
                                            }
                                        },
                                        role = Role.RadioButton
                                    )
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedIndex == index,
                                    onClick = null,
                                    enabled = !isAnswerChecked
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = option.definition,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            if (index < options.size - 1) {
                                HorizontalDivider()
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (selectedIndex == -1) return@Button

                        if (!isAnswerChecked) {
                            // Check answer
                            questionCount++
                            isAnswerChecked = true
                            isCorrect = selectedIndex == correctAnswerIndex

                            if (isCorrect) {
                                correctCount++
                                resultMessage = "✓ Correct!"
                            } else {
                                resultMessage = "✗ Incorrect. The answer was: ${currentWord?.definition}"
                            }
                            showResult = true

                            // Record answer
                            currentWord?.let { word ->
                                coroutineScope.launch {
                                    withContext(Dispatchers.IO) {
                                        val database = VocabularyDatabase.getDatabase(context)
                                        val repository = com.vocabulary.data.repository.VocabularyRepository(
                                            database.wordDao(),
                                            database.wordProgressDao(),
                                            database.dailyWordDao()
                                        )
                                        repository.recordQuizAnswer(word.id, isCorrect)
                                    }

                                    // Wait 2 seconds then load next question
                                    delay(2000)
                                    loadNextQuestion(
                                        allWords = allWords,
                                        onWordLoaded = { word, opts, answerIdx ->
                                            currentWord = word
                                            options = opts
                                            correctAnswerIndex = answerIdx
                                            selectedIndex = -1
                                            showResult = false
                                            isAnswerChecked = false
                                        }
                                    )
                                }
                            }
                        }
                    },
                    enabled = selectedIndex != -1 && !isAnswerChecked,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit Answer")
                }

                // Result
                if (showResult) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCorrect) {
                                Color(0xFF4CAF50).copy(alpha = 0.1f)
                            } else {
                                Color(0xFFF44336).copy(alpha = 0.1f)
                            }
                        )
                    ) {
                        Text(
                            text = resultMessage,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isCorrect) {
                                Color(0xFF2E7D32)
                            } else {
                                Color(0xFFC62828)
                            },
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

private suspend fun loadNextQuestion(
    allWords: List<Word>,
    onWordLoaded: (Word, List<Word>, Int) -> Unit
) {
    if (allWords.isEmpty()) return

    withContext(Dispatchers.Default) {
        val selectedWord = allWords.random()
        val wrongWords = allWords.filter { it.id != selectedWord.id }.shuffled().take(3)
        val quizOptions = (wrongWords + selectedWord).shuffled()
        val correctIndex = quizOptions.indexOf(selectedWord)

        withContext(Dispatchers.Main) {
            onWordLoaded(selectedWord, quizOptions, correctIndex)
        }
    }
}
