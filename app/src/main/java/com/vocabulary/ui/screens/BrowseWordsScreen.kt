package com.vocabulary.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vocabulary.data.model.Word
import com.vocabulary.data.model.WordCategory
import com.vocabulary.ui.MainViewModel
import com.vocabulary.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseWordsScreen(
    viewModel: MainViewModel,
    onNavigateToWordDetail: (Long) -> Unit,
    onNavigateBack: () -> Unit
) {
    val allWords by viewModel.allWords.collectAsStateWithLifecycle(emptyList())
    var selectedCategory by remember { mutableStateOf<WordCategory?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = selectedCategory?.displayName ?: "Browse Words",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedCategory != null) {
                            selectedCategory = null
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (selectedCategory == null) {
            // Show category collection
            ModernCategoryCollection(
                allWords = allWords,
                onCategoryClick = { category ->
                    selectedCategory = category
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            // Show filtered words
            val filteredWords = remember(allWords, selectedCategory) {
                allWords.filter { it.getCategoryEnum() == selectedCategory }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(filteredWords, key = { it.id }) { word ->
                    ModernWordListItem(
                        word = word,
                        onClick = { onNavigateToWordDetail(word.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ModernCategoryCollection(
    allWords: List<Word>,
    onCategoryClick: (WordCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(WordCategory.entries) { category ->
            val wordCount = allWords.count { it.getCategoryEnum() == category }
            ModernCategoryCard(
                category = category,
                wordCount = wordCount,
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

// Category-specific colors and icons
fun getCategoryGradient(category: WordCategory): Brush {
    return when (category) {
        WordCategory.GENERAL -> Brush.linearGradient(
            colors = listOf(Primary, PrimaryLight)
        )
        WordCategory.BUSINESS -> Brush.linearGradient(
            colors = listOf(AccentBlue, Secondary)
        )
        WordCategory.ACADEMIC -> Brush.linearGradient(
            colors = listOf(Tertiary, TertiaryLight)
        )
        WordCategory.TECHNOLOGY -> Brush.linearGradient(
            colors = listOf(Secondary, AccentGreen)
        )
        WordCategory.SCIENCE -> Brush.linearGradient(
            colors = listOf(AccentGreen, AccentBlue)
        )
        WordCategory.MEDICAL -> Brush.linearGradient(
            colors = listOf(Error, AccentOrange)
        )
        WordCategory.LEGAL -> Brush.linearGradient(
            colors = listOf(OnBackground, OnSurfaceVariant)
        )
        WordCategory.ARTS -> Brush.linearGradient(
            colors = listOf(AccentPink, AccentOrange)
        )
    }
}

fun getCategoryIcon(category: WordCategory): ImageVector {
    return when (category) {
        WordCategory.GENERAL -> Icons.Default.Home
        WordCategory.BUSINESS -> Icons.Default.Work
        WordCategory.ACADEMIC -> Icons.Default.School
        WordCategory.TECHNOLOGY -> Icons.Default.Computer
        WordCategory.SCIENCE -> Icons.Default.Science
        WordCategory.MEDICAL -> Icons.Default.MedicalServices
        WordCategory.LEGAL -> Icons.Default.Gavel
        WordCategory.ARTS -> Icons.Default.Palette
    }
}

@Composable
fun ModernCategoryCard(
    category: WordCategory,
    wordCount: Int,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Primary.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = getCategoryGradient(category))
                .padding(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.3f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryIcon(category),
                        contentDescription = category.displayName,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Column {
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$wordCount words",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
fun ModernWordListItem(
    word: Word,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Primary.copy(alpha = 0.05f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon column
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = Primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = word.word.take(1).uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }

            // Content column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = word.word,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = word.pronunciation,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = word.definition,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Surface(
                        color = getCategoryColor(word.getCategoryEnum()),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = word.getCategoryEnum().displayName,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        color = getDifficultyColor(word.difficultyLevel),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = word.difficultyLevel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

fun getCategoryColor(category: WordCategory): Color {
    return when (category) {
        WordCategory.GENERAL -> Primary
        WordCategory.BUSINESS -> AccentBlue
        WordCategory.ACADEMIC -> Tertiary
        WordCategory.TECHNOLOGY -> Secondary
        WordCategory.SCIENCE -> AccentGreen
        WordCategory.MEDICAL -> Error
        WordCategory.LEGAL -> OnSurfaceVariant
        WordCategory.ARTS -> AccentPink
    }
}

fun getDifficultyColor(difficulty: String): Color {
    return when (difficulty.lowercase()) {
        "beginner" -> Success
        "intermediate" -> AccentYellow
        "advanced" -> AccentOrange
        else -> OnSurfaceVariant
    }
}
