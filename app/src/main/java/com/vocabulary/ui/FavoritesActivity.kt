package com.vocabulary.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.vocabulary.data.database.VocabularyDatabase
import com.vocabulary.databinding.ActivityFavoritesBinding
import com.vocabulary.ui.adapter.WordsAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoritesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoritesBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: WordsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Favorite Words"

        binding.toolbar.setNavigationOnClickListener { finish() }

        setupRecyclerView()
        loadFavoriteWords()
    }

    private fun setupRecyclerView() {
        adapter = WordsAdapter { word ->
            val intent = Intent(this, WordDetailActivity::class.java)
            intent.putExtra("WORD_ID", word.id)
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun loadFavoriteWords() {
        // Collect favorite words Flow using lifecycleScope with repeatOnLifecycle
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                val database = VocabularyDatabase.getDatabase(applicationContext)

                // Collect favorite progress changes
                database.wordProgressDao().getFavoriteWords().collectLatest { favoriteProgress ->
                    // Fetch word details on IO dispatcher
                    val favoriteWords = withContext(Dispatchers.IO) {
                        favoriteProgress.mapNotNull { progress ->
                            database.wordDao().getWordById(progress.wordId)
                        }
                    }

                    // Update UI on Main dispatcher
                    adapter.submitList(favoriteWords)
                }
            }
        }
    }
}
