package com.vocabulary.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vocabulary.data.model.Word
import com.vocabulary.data.model.WordCategory
import com.vocabulary.ui.MainViewModel

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
                title = { Text(selectedCategory?.displayName ?: "Browse Words") },
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
                }
            )
        }
    ) { paddingValues ->
        if (selectedCategory == null) {
            // Show category collection
            CategoryCollection(
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
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(filteredWords, key = { it.id }) { word ->
                    WordListItem(
                        word = word,
                        onClick = { onNavigateToWordDetail(word.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCollection(
    allWords: List<Word>,
    onCategoryClick: (WordCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(WordCategory.entries) { category ->
            val wordCount = allWords.count { it.getCategoryEnum() == category }
            CategoryCard(
                category = category,
                wordCount = wordCount,
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

@Composable
fun CategoryCard(
    category: WordCategory,
    wordCount: Int,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = category.displayName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$wordCount words",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun WordListItem(
    word: Word,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = word.word,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                word.getCategoryEnum().displayName,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    )

                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                word.difficultyLevel,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    )
                }
            }

            Text(
                text = word.pronunciation,
                style = MaterialTheme.typography.bodySmall,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )

            Text(
                text = word.definition,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
