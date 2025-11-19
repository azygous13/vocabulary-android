package com.vocabulary.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vocabulary.ui.MainViewModel
import com.vocabulary.ui.components.rememberTextToSpeech
import com.vocabulary.ui.components.speakWord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToWordDetail: (Long) -> Unit,
    onNavigateToBrowse: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToSpacedRepetition: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val todayWord by viewModel.todayWord.collectAsStateWithLifecycle()
    val wordProgress by viewModel.wordProgress.collectAsStateWithLifecycle()
    val statistics by viewModel.statistics.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vocabulary") },
                actions = {
                    IconButton(onClick = onNavigateToFavorites) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorites"
                        )
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Statistics Card
                StatisticsCard(
                    totalWords = statistics?.totalWords ?: 0,
                    learnedWords = statistics?.learnedWords ?: 0,
                    masteredWords = statistics?.masteredWords ?: 0
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Word of the Day Card
                todayWord?.let { word ->
                    WordOfTheDayCard(
                        word = word,
                        isFavorite = wordProgress?.isFavorite ?: false,
                        isLearned = wordProgress?.isLearned ?: false,
                        onToggleFavorite = { viewModel.toggleFavorite() },
                        onToggleLearned = { viewModel.toggleLearned() },
                        onViewDetails = { onNavigateToWordDetail(word.id) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Actions Section
                Text(
                    text = "Quick Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    QuickActionCard(
                        title = "ทบทวนคำศัพท์",
                        emoji = "🔄",
                        onClick = onNavigateToSpacedRepetition,
                        modifier = Modifier.weight(1f)
                    )

                    QuickActionCard(
                        title = "Practice Quiz",
                        emoji = "📝",
                        onClick = onNavigateToQuiz,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                QuickActionCard(
                    title = "Browse Words",
                    emoji = "📚",
                    onClick = onNavigateToBrowse,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Loading indicator
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun StatisticsCard(
    totalWords: Int,
    learnedWords: Int,
    masteredWords: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = totalWords,
                    label = "Total"
                )

                StatItem(
                    value = learnedWords,
                    label = "Learned"
                )

                StatItem(
                    value = masteredWords,
                    label = "Mastered"
                )
            }
        }
    }
}

@Composable
fun StatItem(
    value: Int,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun WordOfTheDayCard(
    word: com.vocabulary.data.model.Word,
    isFavorite: Boolean,
    isLearned: Boolean,
    onToggleFavorite: () -> Unit,
    onToggleLearned: () -> Unit,
    onViewDetails: () -> Unit
) {
    val tts = rememberTextToSpeech()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Word",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = word.word,
                    style = MaterialTheme.typography.headlineLarge,
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
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Row(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = { },
                    label = { Text(word.partOfSpeech) }
                )

                AssistChip(
                    onClick = { },
                    label = { Text(word.getCategoryEnum().displayName) }
                )

                AssistChip(
                    onClick = { },
                    label = { Text(word.getDifficultyEnum().displayName) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Definition",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = word.definition,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Example",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "\"${word.example}\"",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onToggleLearned,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = if (isLearned) "Learned ✓ (Tap to unmark)" else "Mark as Learned"
                    )
                }

                OutlinedButton(
                    onClick = onViewDetails,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("View Details")
                }
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    emoji: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
