package com.vocabulary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.vocabulary.data.model.Word
import com.vocabulary.databinding.ItemWordBinding

class WordsAdapter(
    private val onWordClick: (Word) -> Unit
) : ListAdapter<Word, WordsAdapter.WordViewHolder>(WordDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = ItemWordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WordViewHolder(binding, onWordClick)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class WordViewHolder(
        private val binding: ItemWordBinding,
        private val onWordClick: (Word) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(word: Word) {
            binding.tvWord.text = word.word
            binding.tvPronunciation.text = word.pronunciation
            binding.tvDefinition.text = word.definition
            binding.chipDifficulty.text = word.getDifficultyEnum().displayName

            binding.root.setOnClickListener {
                onWordClick(word)
            }
        }
    }

    class WordDiffCallback : DiffUtil.ItemCallback<Word>() {
        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem == newItem
        }
    }
}
