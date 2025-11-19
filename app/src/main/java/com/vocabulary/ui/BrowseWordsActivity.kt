package com.vocabulary.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.vocabulary.databinding.ActivityBrowseWordsBinding
import com.vocabulary.ui.adapter.WordsAdapter

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
        viewModel.allWords.observe(this) { words ->
            adapter.submitList(words)
        }
    }
}
