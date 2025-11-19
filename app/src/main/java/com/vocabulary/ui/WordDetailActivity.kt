package com.vocabulary.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.vocabulary.R
import com.vocabulary.databinding.ActivityWordDetailBinding
import kotlinx.coroutines.launch

class WordDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWordDetailBinding
    private val viewModel: MainViewModel by viewModels()
    private var wordId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWordDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        wordId = intent.getLongExtra("WORD_ID", -1)
        if (wordId != -1L) {
            loadWordDetails()
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadWordDetails() {
        lifecycleScope.launch {
            val database = com.vocabulary.data.database.VocabularyDatabase.getDatabase(applicationContext)
            val repository = com.vocabulary.data.repository.VocabularyRepository(
                database.wordDao(),
                database.wordProgressDao(),
                database.dailyWordDao()
            )

            val word = repository.getWordById(wordId)
            val progress = repository.getWordProgress(wordId)

            word?.let { w ->
                binding.tvWord.text = w.word
                binding.tvPronunciation.text = w.pronunciation
                binding.chipPartOfSpeech.text = w.partOfSpeech
                binding.chipDifficulty.text = w.getDifficulty().displayName
                binding.chipCategory.text = w.getCategory().displayName
                binding.tvDefinition.text = w.definition
                binding.tvExample.text = "\"${w.example}\""
                binding.tvSynonyms.text = w.synonyms
                binding.tvAntonyms.text = w.antonyms
                binding.tvEtymology.text = w.etymology

                binding.tvMasteryLevel.text = getString(R.string.mastery_level, progress.masteryLevel)
                binding.tvAccuracy.text = getString(R.string.accuracy, progress.getAccuracy())
                binding.tvReviewCount.text = getString(R.string.review_count, progress.reviewCount)
            }
        }
    }
}
