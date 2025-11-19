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

    val quizTypeEnum = remember(quizType) {
        try {
            QuizType.valueOf(quizType)
        } catch (e: IllegalArgumentException) {
            QuizType.MULTIPLE_CHOICE
        }
    }

    // Load initial question
    LaunchedEffect(allWords, quizTypeEnum) {
        if (allWords.isNotEmpty()) {
            when (quizTypeEnum) {
                QuizType.TRUE_FALSE -> {
                    loadTrueFalseQuestion(
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
                else -> {
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
            when (quizTypeEnum) {
                QuizType.MULTIPLE_CHOICE -> MultipleChoiceQuiz(
                    paddingValues = paddingValues,
                    currentWord = currentWord,
                    options = options,
                    selectedIndex = selectedIndex,
                    onSelectOption = { index -> if (!isAnswerChecked) selectedIndex = index },
                    isAnswerChecked = isAnswerChecked,
                    showResult = showResult,
                    resultMessage = resultMessage,
                    isCorrect = isCorrect,
                    onSubmit = {
                        if (selectedIndex == -1) return@MultipleChoiceQuiz
                        if (!isAnswerChecked) {
                            questionCount++
                            isAnswerChecked = true
                            isCorrect = selectedIndex == correctAnswerIndex
                            if (isCorrect) {
                                correctCount++
                                resultMessage = "✓ ถูกต้อง!"
                            } else {
                                resultMessage = "✗ ไม่ถูกต้อง คำตอบที่ถูกคือ: ${currentWord?.definition}"
                            }
                            showResult = true
                            recordAnswerAndLoadNext(
                                context, coroutineScope, currentWord, isCorrect, allWords, quizTypeEnum
                            ) { word, opts, answerIdx ->
                                currentWord = word
                                options = opts
                                correctAnswerIndex = answerIdx
                                selectedIndex = -1
                                showResult = false
                                isAnswerChecked = false
                            }
                        }
                    }
                )
                QuizType.TRUE_FALSE -> TrueFalseQuiz(
                    paddingValues = paddingValues,
                    currentWord = currentWord,
                    displayedDefinition = options.firstOrNull()?.definition ?: "",
                    selectedAnswer = if (selectedIndex == 0) true else if (selectedIndex == 1) false else null,
                    onSelectAnswer = { isTrue -> if (!isAnswerChecked) selectedIndex = if (isTrue) 0 else 1 },
                    isAnswerChecked = isAnswerChecked,
                    showResult = showResult,
                    resultMessage = resultMessage,
                    isCorrect = isCorrect,
                    onSubmit = {
                        if (selectedIndex == -1) return@TrueFalseQuiz
                        if (!isAnswerChecked) {
                            questionCount++
                            isAnswerChecked = true
                            isCorrect = selectedIndex == correctAnswerIndex
                            if (isCorrect) {
                                correctCount++
                                resultMessage = "✓ ถูกต้อง!"
                            } else {
                                val correctAnswer = if (correctAnswerIndex == 0) "ถูกต้อง (True)" else "ไม่ถูกต้อง (False)"
                                resultMessage = "✗ ไม่ถูกต้อง คำตอบที่ถูกคือ: $correctAnswer"
                            }
                            showResult = true
                            recordAnswerAndLoadNext(
                                context, coroutineScope, currentWord, isCorrect, allWords, quizTypeEnum
                            ) { word, opts, answerIdx ->
                                currentWord = word
                                options = opts
                                correctAnswerIndex = answerIdx
                                selectedIndex = -1
                                showResult = false
                                isAnswerChecked = false
                            }
                        }
                    }
                )
                QuizType.FILL_BLANK -> FillInBlankQuiz(
                    paddingValues = paddingValues,
                    currentWord = currentWord,
                    options = options,
                    selectedIndex = selectedIndex,
                    onSelectOption = { index -> if (!isAnswerChecked) selectedIndex = index },
                    isAnswerChecked = isAnswerChecked,
                    showResult = showResult,
                    resultMessage = resultMessage,
                    isCorrect = isCorrect,
                    onSubmit = {
                        if (selectedIndex == -1) return@FillInBlankQuiz
                        if (!isAnswerChecked) {
                            questionCount++
                            isAnswerChecked = true
                            isCorrect = selectedIndex == correctAnswerIndex
                            if (isCorrect) {
                                correctCount++
                                resultMessage = "✓ ถูกต้อง!"
                            } else {
                                resultMessage = "✗ ไม่ถูกต้อง คำตอบที่ถูกคือ: ${currentWord?.word}"
                            }
                            showResult = true
                            recordAnswerAndLoadNext(
                                context, coroutineScope, currentWord, isCorrect, allWords, quizTypeEnum
                            ) { word, opts, answerIdx ->
                                currentWord = word
                                options = opts
                                correctAnswerIndex = answerIdx
                                selectedIndex = -1
                                showResult = false
                                isAnswerChecked = false
                            }
                        }
                    }
                )
                QuizType.MATCHING -> MatchingQuiz(
                    paddingValues = paddingValues,
                    allWords = allWords,
                    onComplete = { correct ->
                        questionCount++
                        if (correct) {
                            correctCount++
                        }
                        // Load new matching set
                    }
                )
            }
        }
    }
}

private fun recordAnswerAndLoadNext(
    context: android.content.Context,
    coroutineScope: CoroutineScope,
    currentWord: Word?,
    isCorrect: Boolean,
    allWords: List<Word>,
    quizType: QuizType,
    onWordLoaded: (Word, List<Word>, Int) -> Unit
) {
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
            delay(2000)
            when (quizType) {
                QuizType.TRUE_FALSE -> loadTrueFalseQuestion(
                    allWords = allWords,
                    onWordLoaded = onWordLoaded
                )
                else -> loadNextQuestion(
                    allWords = allWords,
                    onWordLoaded = onWordLoaded
                )
            }
        }
    }
}

@Composable
fun MultipleChoiceQuiz(
    paddingValues: PaddingValues,
    currentWord: Word?,
    options: List<Word>,
    selectedIndex: Int,
    onSelectOption: (Int) -> Unit,
    isAnswerChecked: Boolean,
    showResult: Boolean,
    resultMessage: String,
    isCorrect: Boolean,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "คำจำกัดความของคำนี้คืออะไร:",
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

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                options.forEachIndexed { index, option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedIndex == index,
                                onClick = { onSelectOption(index) },
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

        Button(
            onClick = onSubmit,
            enabled = selectedIndex != -1 && !isAnswerChecked,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ส่งคำตอบ")
        }

        if (showResult) {
            Spacer(modifier = Modifier.height(16.dp))
            ResultCard(resultMessage, isCorrect)
        }
    }
}

@Composable
fun TrueFalseQuiz(
    paddingValues: PaddingValues,
    currentWord: Word?,
    displayedDefinition: String,
    selectedAnswer: Boolean?,
    onSelectAnswer: (Boolean) -> Unit,
    isAnswerChecked: Boolean,
    showResult: Boolean,
    resultMessage: String,
    isCorrect: Boolean,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "คำจำกัดความนี้ถูกต้องหรือไม่?",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                currentWord?.let { word ->
                    Text(
                        text = word.word,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = displayedDefinition,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { onSelectAnswer(true) },
                enabled = !isAnswerChecked,
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (selectedAnswer == true)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    text = "ถูกต้อง\n(True)",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            OutlinedButton(
                onClick = { onSelectAnswer(false) },
                enabled = !isAnswerChecked,
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (selectedAnswer == false)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    text = "ไม่ถูกต้อง\n(False)",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSubmit,
            enabled = selectedAnswer != null && !isAnswerChecked,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ส่งคำตอบ")
        }

        if (showResult) {
            Spacer(modifier = Modifier.height(16.dp))
            ResultCard(resultMessage, isCorrect)
        }
    }
}

@Composable
fun FillInBlankQuiz(
    paddingValues: PaddingValues,
    currentWord: Word?,
    options: List<Word>,
    selectedIndex: Int,
    onSelectOption: (Int) -> Unit,
    isAnswerChecked: Boolean,
    showResult: Boolean,
    resultMessage: String,
    isCorrect: Boolean,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "เติมคำที่ขาดหายไป:",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                currentWord?.let { word ->
                    val example = word.example
                    val wordInExample = word.word
                    val blankExample = example.replace(
                        wordInExample,
                        "_______",
                        ignoreCase = true
                    )
                    Text(
                        text = "\"$blankExample\"",
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "เลือกคำที่เหมาะสม:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                options.forEachIndexed { index, option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedIndex == index,
                                onClick = { onSelectOption(index) },
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
                            text = option.word,
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

        Button(
            onClick = onSubmit,
            enabled = selectedIndex != -1 && !isAnswerChecked,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ส่งคำตอบ")
        }

        if (showResult) {
            Spacer(modifier = Modifier.height(16.dp))
            ResultCard(resultMessage, isCorrect)
        }
    }
}

@Composable
fun MatchingQuiz(
    paddingValues: PaddingValues,
    allWords: List<Word>,
    onComplete: (Boolean) -> Unit
) {
    val quizWords = remember { allWords.shuffled().take(4) }
    val shuffledDefinitions = remember { quizWords.map { it.definition }.shuffled() }
    var selectedWordIndex by remember { mutableIntStateOf(-1) }
    var selectedDefIndex by remember { mutableIntStateOf(-1) }
    var matchedPairs by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var incorrectPairs by remember { mutableStateOf<Set<Pair<Int, Int>>>(emptySet()) }
    var isComplete by remember { mutableStateOf(false) }

    LaunchedEffect(selectedWordIndex, selectedDefIndex) {
        if (selectedWordIndex != -1 && selectedDefIndex != -1) {
            val word = quizWords[selectedWordIndex]
            val definition = shuffledDefinitions[selectedDefIndex]

            if (word.definition == definition) {
                matchedPairs = matchedPairs + selectedWordIndex
                if (matchedPairs.size == quizWords.size) {
                    isComplete = true
                    onComplete(true)
                }
            } else {
                incorrectPairs = incorrectPairs + Pair(selectedWordIndex, selectedDefIndex)
                delay(500)
                incorrectPairs = emptySet()
            }
            selectedWordIndex = -1
            selectedDefIndex = -1
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "จับคู่คำศัพท์กับคำจำกัดความ",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "กดเลือกคำศัพท์ทางซ้าย แล้วจับคู่กับคำจำกัดความทางขวา",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Words column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quizWords.forEachIndexed { index, word ->
                    Card(
                        onClick = {
                            if (index !in matchedPairs) {
                                selectedWordIndex = index
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                index in matchedPairs -> MaterialTheme.colorScheme.primaryContainer
                                selectedWordIndex == index -> MaterialTheme.colorScheme.secondaryContainer
                                else -> MaterialTheme.colorScheme.surface
                            }
                        ),
                        enabled = index !in matchedPairs
                    ) {
                        Text(
                            text = word.word,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Definitions column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                shuffledDefinitions.forEachIndexed { index, definition ->
                    val isMatched = matchedPairs.any { wordIdx ->
                        quizWords[wordIdx].definition == definition
                    }
                    Card(
                        onClick = {
                            if (!isMatched) {
                                selectedDefIndex = index
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isMatched -> MaterialTheme.colorScheme.primaryContainer
                                selectedDefIndex == index -> MaterialTheme.colorScheme.secondaryContainer
                                else -> MaterialTheme.colorScheme.surface
                            }
                        ),
                        enabled = !isMatched
                    ) {
                        Text(
                            text = definition,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 3
                        )
                    }
                }
            }
        }

        if (isComplete) {
            Spacer(modifier = Modifier.height(16.dp))
            ResultCard("✓ เยี่ยมมาก! จับคู่ถูกต้องทั้งหมด!", true)
        }
    }
}

@Composable
fun ResultCard(message: String, isCorrect: Boolean) {
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
            text = message,
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

private suspend fun loadTrueFalseQuestion(
    allWords: List<Word>,
    onWordLoaded: (Word, List<Word>, Int) -> Unit
) {
    if (allWords.isEmpty()) return

    withContext(Dispatchers.Default) {
        val selectedWord = allWords.random()
        // Randomly decide if this will be true or false
        val isTrue = (0..1).random() == 1

        val displayedDefinition = if (isTrue) {
            // Show correct definition
            selectedWord.definition
        } else {
            // Show wrong definition from another word
            val wrongWord = allWords.filter { it.id != selectedWord.id }.random()
            wrongWord.definition
        }

        // Create options list with just one item (the displayed definition)
        // We'll use index 0 for True, index 1 for False
        val options = listOf(Word(
            id = 0,
            word = selectedWord.word,
            pronunciation = selectedWord.pronunciation,
            partOfSpeech = selectedWord.partOfSpeech,
            definition = displayedDefinition,
            example = selectedWord.example,
            difficulty = selectedWord.difficulty,
            category = selectedWord.category
        ))

        val correctIndex = if (isTrue) 0 else 1

        withContext(Dispatchers.Main) {
            onWordLoaded(selectedWord, options, correctIndex)
        }
    }
}
