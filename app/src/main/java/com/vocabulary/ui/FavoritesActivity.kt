package com.vocabulary.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.vocabulary.data.database.VocabularyDatabase
import com.vocabulary.databinding.ActivityFavoritesBinding
import com.vocabulary.ui.adapter.WordsAdapter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

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
        lifecycleScope.launch {
            val database = VocabularyDatabase.getDatabase(applicationContext)
            val favoriteProgress = database.wordProgressDao().getFavoriteWords().firstOrNull() ?: emptyList()

            val favoriteWords = favoriteProgress.mapNotNull { progress ->
                database.wordDao().getWordById(progress.wordId)
            }

            adapter.submitList(favoriteWords)
        }
    }
}
