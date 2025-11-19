package com.vocabulary.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.vocabulary.R
import com.vocabulary.databinding.ActivityMainBinding
import com.vocabulary.notification.NotificationHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Schedule daily notification (9 AM)
        NotificationHelper.scheduleDailyNotification(this, 9, 0)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        // Collect StateFlow using lifecycleScope with repeatOnLifecycle
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Collect today's word
                launch {
                    viewModel.todayWord.collectLatest { word ->
                        word?.let {
                            binding.tvWord.text = it.word
                            binding.tvPronunciation.text = it.pronunciation
                            binding.chipPartOfSpeech.text = it.partOfSpeech
                            binding.tvDefinition.text = it.definition
                            binding.tvExample.text = "\"${it.example}\""
                        }
                    }
                }

                // Collect word progress
                launch {
                    viewModel.wordProgress.collectLatest { progress ->
                        progress?.let {
                            updateFavoriteButton(it.isFavorite)
                            updateLearnedButton(it.isLearned)
                        }
                    }
                }

                // Collect statistics
                launch {
                    viewModel.statistics.collectLatest { stats ->
                        stats?.let {
                            binding.tvTotalWords.text = it.totalWords.toString()
                            binding.tvLearnedWords.text = it.learnedWords.toString()
                            binding.tvMasteredWords.text = it.masteredWords.toString()
                        }
                    }
                }

                // Collect loading state
                launch {
                    viewModel.isLoading.collectLatest { isLoading ->
                        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnFavorite.setOnClickListener {
            viewModel.toggleFavorite()
        }

        binding.btnMarkLearned.setOnClickListener {
            viewModel.toggleLearned()
        }

        binding.btnViewDetails.setOnClickListener {
            val word = viewModel.todayWord.value
            word?.let {
                val intent = Intent(this, WordDetailActivity::class.java)
                intent.putExtra("WORD_ID", it.id)
                startActivity(intent)
            }
        }

        binding.cardPractice.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
        }

        binding.cardBrowse.setOnClickListener {
            val intent = Intent(this, BrowseWordsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        val icon = if (isFavorite) {
            android.R.drawable.btn_star_big_on
        } else {
            android.R.drawable.btn_star_big_off
        }
        binding.btnFavorite.setIconResource(icon)
    }

    private fun updateLearnedButton(isLearned: Boolean) {
        if (isLearned) {
            binding.btnMarkLearned.text = "Learned ✓ (Tap to unmark)"
        } else {
            binding.btnMarkLearned.text = getString(R.string.mark_as_learned)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorites -> {
                val intent = Intent(this, FavoritesActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_settings -> {
                // TODO: Open settings
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
