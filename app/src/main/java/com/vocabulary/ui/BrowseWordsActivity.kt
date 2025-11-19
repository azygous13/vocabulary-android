package com.vocabulary.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.vocabulary.databinding.ActivityBrowseWordsBinding
import com.vocabulary.ui.adapter.WordsAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BrowseWordsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBrowseWordsBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: WordsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBrowseWordsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener { finish() }

        setupRecyclerView()
        observeWords()
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

    private fun observeWords() {
        // Collect Flow using lifecycleScope with repeatOnLifecycle
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allWords.collectLatest { words ->
                    adapter.submitList(words)
                }
            }
        }
    }
}
