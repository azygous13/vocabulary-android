package com.vocabulary.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.vocabulary.data.database.VocabularyDatabase
import com.vocabulary.data.model.Word
import com.vocabulary.data.repository.VocabularyRepository
import com.vocabulary.databinding.ActivityQuizBinding
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class QuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private lateinit var repository: VocabularyRepository
    private var currentWord: Word? = null
    private var correctAnswerIndex: Int = 0
    private var questionCount = 0
    private var correctCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val database = VocabularyDatabase.getDatabase(this)
        repository = VocabularyRepository(
            database.wordDao(),
            database.wordProgressDao(),
            database.dailyWordDao()
        )

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.btnSubmitAnswer.setOnClickListener { checkAnswer() }

        loadNextQuestion()
    }

    private fun loadNextQuestion() {
        lifecycleScope.launch {
            val allWords = repository.getAllWords().firstOrNull()
            if (allWords.isNullOrEmpty()) {
                Snackbar.make(binding.root, "No words available", Snackbar.LENGTH_SHORT).show()
                return@launch
            }

            currentWord = allWords.random()
            currentWord?.let { word ->
                binding.tvQuizWord.text = word.word

                // Create multiple choice options
                val wrongWords = allWords.filter { it.id != word.id }.shuffled().take(3)
                val options = (wrongWords + word).shuffled()
                correctAnswerIndex = options.indexOf(word)

                binding.radioOption1.text = options[0].definition
                binding.radioOption2.text = options[1].definition
                binding.radioOption3.text = options[2].definition
                binding.radioOption4.text = options[3].definition

                binding.radioGroupOptions.clearCheck()
                binding.tvResult.visibility = View.GONE
            }
        }
    }

    private fun checkAnswer() {
        val selectedId = binding.radioGroupOptions.checkedRadioButtonId
        if (selectedId == -1) {
            Snackbar.make(binding.root, "Please select an answer", Snackbar.LENGTH_SHORT).show()
            return
        }

        val selectedIndex = when (selectedId) {
            binding.radioOption1.id -> 0
            binding.radioOption2.id -> 1
            binding.radioOption3.id -> 2
            binding.radioOption4.id -> 3
            else -> -1
        }

        questionCount++
        val isCorrect = selectedIndex == correctAnswerIndex

        if (isCorrect) {
            correctCount++
            binding.tvResult.text = "✓ Correct!"
            binding.tvResult.setTextColor(getColor(android.R.color.holo_green_dark))
        } else {
            binding.tvResult.text = "✗ Incorrect. The answer was: ${currentWord?.definition}"
            binding.tvResult.setTextColor(getColor(android.R.color.holo_red_dark))
        }
        binding.tvResult.visibility = View.VISIBLE

        // Record answer
        currentWord?.let { word ->
            lifecycleScope.launch {
                repository.recordQuizAnswer(word.id, isCorrect)
            }
        }

        // Load next question after delay
        binding.root.postDelayed({
            loadNextQuestion()
        }, 2000)

        // Update title with score
        supportActionBar?.title = "Quiz - Score: $correctCount/$questionCount"
    }
}
