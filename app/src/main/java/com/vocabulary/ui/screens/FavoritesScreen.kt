package com.vocabulary.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vocabulary.data.database.VocabularyDatabase
import com.vocabulary.data.model.Word
import com.vocabulary.ui.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: MainViewModel,
    onNavigateToWordDetail: (Long) -> Unit,
    onNavigateBack: () -> Unit
) {
    val favoriteProgress by viewModel.favoriteWords.collectAsStateWithLifecycle(emptyList())

    // Convert WordProgress to Word
    var favoriteWords by remember { mutableStateOf<List<Word>>(emptyList()) }
    val database = VocabularyDatabase.getDatabase(androidx.compose.ui.platform.LocalContext.current)

    LaunchedEffect(favoriteProgress) {
        favoriteWords = withContext(Dispatchers.IO) {
            favoriteProgress.mapNotNull { progress ->
                database.wordDao().getWordById(progress.wordId)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorite Words") },
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
        if (favoriteWords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No favorite words yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(favoriteWords, key = { it.id }) { word ->
                    WordListItem(
                        word = word,
                        onClick = { onNavigateToWordDetail(word.id) }
                    )
                }
            }
        }
    }
}
