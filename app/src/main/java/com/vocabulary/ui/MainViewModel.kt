package com.vocabulary.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vocabulary.data.SampleData
import com.vocabulary.data.database.VocabularyDatabase
import com.vocabulary.data.model.*
import com.vocabulary.data.repository.VocabularyRepository
import com.vocabulary.data.repository.VocabularyStatistics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = VocabularyDatabase.getDatabase(application)
    private val repository = VocabularyRepository(
        database.wordDao(),
        database.wordProgressDao(),
        database.dailyWordDao()
    )

    // Use StateFlow instead of LiveData for better coroutines integration
    private val _todayWord = MutableStateFlow<Word?>(null)
    val todayWord: StateFlow<Word?> = _todayWord.asStateFlow()

    private val _wordProgress = MutableStateFlow<WordProgress?>(null)
    val wordProgress: StateFlow<WordProgress?> = _wordProgress.asStateFlow()

    private val _statistics = MutableStateFlow<VocabularyStatistics?>(null)
    val statistics: StateFlow<VocabularyStatistics?> = _statistics.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Use Flow for reactive data
    val allWords: Flow<List<Word>> = repository.getAllWords()
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoriteWords: Flow<List<WordProgress>> = repository.getFavoriteWords()
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val learnedWords: Flow<List<WordProgress>> = repository.getLearnedWords()
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        initializeDatabase()
        loadTodayWord()
        loadStatistics()
    }

    private fun initializeDatabase() {
        viewModelScope.launch(Dispatchers.IO) {
            val wordCount = repository.getWordCount()
            if (wordCount == 0) {
                // Populate with sample data
                repository.insertWords(SampleData.getSampleWords())
            }
        }
    }

    fun loadTodayWord() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val word = withContext(Dispatchers.IO) {
                    repository.getTodayWord()
                }
                _todayWord.value = word
                word?.let { loadWordProgress(it.id) }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadWordProgress(wordId: Long) {
        viewModelScope.launch {
            try {
                val progress = withContext(Dispatchers.IO) {
                    repository.getWordProgress(wordId)
                }
                _wordProgress.value = progress
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _todayWord.value?.let { word ->
                withContext(Dispatchers.IO) {
                    repository.toggleFavorite(word.id)
                }
                loadWordProgress(word.id)
            }
        }
    }

    fun toggleLearned() {
        viewModelScope.launch {
            _todayWord.value?.let { word ->
                withContext(Dispatchers.IO) {
                    repository.toggleLearned(word.id)
                }
                loadWordProgress(word.id)
                loadStatistics()
            }
        }
    }

    fun markAsLearned() {
        viewModelScope.launch {
            _todayWord.value?.let { word ->
                withContext(Dispatchers.IO) {
                    repository.markWordAsLearned(word.id)
                }
                loadWordProgress(word.id)
                loadStatistics()
            }
        }
    }

    fun recordQuizAnswer(isCorrect: Boolean) {
        viewModelScope.launch {
            _todayWord.value?.let { word ->
                withContext(Dispatchers.IO) {
                    repository.recordQuizAnswer(word.id, isCorrect)
                }
                loadWordProgress(word.id)
                loadStatistics()
            }
        }
    }

    fun loadStatistics() {
        viewModelScope.launch {
            try {
                val stats = withContext(Dispatchers.IO) {
                    repository.getStatistics()
                }
                _statistics.value = stats
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getWordsByDifficulty(level: DifficultyLevel): Flow<List<Word>> =
        repository.getWordsByDifficulty(level)
            .flowOn(Dispatchers.IO)

    fun getWordsByCategory(category: WordCategory): Flow<List<Word>> =
        repository.getWordsByCategory(category)
            .flowOn(Dispatchers.IO)

    fun searchWords(query: String): Flow<List<Word>> =
        repository.searchWords(query)
            .flowOn(Dispatchers.IO)
}
