package com.vocabulary.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.vocabulary.data.SampleData
import com.vocabulary.data.database.VocabularyDatabase
import com.vocabulary.data.model.*
import com.vocabulary.data.repository.VocabularyRepository
import com.vocabulary.data.repository.VocabularyStatistics
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = VocabularyDatabase.getDatabase(application)
    private val repository = VocabularyRepository(
        database.wordDao(),
        database.wordProgressDao(),
        database.dailyWordDao()
    )

    private val _todayWord = MutableLiveData<Word?>()
    val todayWord: LiveData<Word?> = _todayWord

    private val _wordProgress = MutableLiveData<WordProgress?>()
    val wordProgress: LiveData<WordProgress?> = _wordProgress

    private val _statistics = MutableLiveData<VocabularyStatistics>()
    val statistics: LiveData<VocabularyStatistics> = _statistics

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val allWords = repository.getAllWords().asLiveData()
    val favoriteWords = repository.getFavoriteWords().asLiveData()
    val learnedWords = repository.getLearnedWords().asLiveData()

    init {
        initializeDatabase()
        loadTodayWord()
        loadStatistics()
    }

    private fun initializeDatabase() {
        viewModelScope.launch {
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
            val word = repository.getTodayWord()
            _todayWord.value = word
            word?.let { loadWordProgress(it.id) }
            _isLoading.value = false
        }
    }

    private fun loadWordProgress(wordId: Long) {
        viewModelScope.launch {
            val progress = repository.getWordProgress(wordId)
            _wordProgress.value = progress
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _todayWord.value?.let { word ->
                repository.toggleFavorite(word.id)
                loadWordProgress(word.id)
            }
        }
    }

    fun markAsLearned() {
        viewModelScope.launch {
            _todayWord.value?.let { word ->
                repository.markWordAsLearned(word.id)
                loadWordProgress(word.id)
                loadStatistics()
            }
        }
    }

    fun recordQuizAnswer(isCorrect: Boolean) {
        viewModelScope.launch {
            _todayWord.value?.let { word ->
                repository.recordQuizAnswer(word.id, isCorrect)
                loadWordProgress(word.id)
                loadStatistics()
            }
        }
    }

    fun loadStatistics() {
        viewModelScope.launch {
            val stats = repository.getStatistics()
            _statistics.value = stats
        }
    }

    fun getWordsByDifficulty(level: DifficultyLevel) =
        repository.getWordsByDifficulty(level).asLiveData()

    fun getWordsByCategory(category: WordCategory) =
        repository.getWordsByCategory(category).asLiveData()

    fun searchWords(query: String) =
        repository.searchWords(query).asLiveData()
}
