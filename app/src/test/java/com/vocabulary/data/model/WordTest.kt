package com.vocabulary.data.model

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class WordTest {

    @Test
    fun `getDifficulty returns correct DifficultyLevel`() {
        val word = Word(
            id = 1,
            word = "Test",
            pronunciation = "/test/",
            partOfSpeech = "noun",
            definition = "A test word",
            example = "This is a test.",
            synonyms = "exam",
            antonyms = "real",
            etymology = "From Latin",
            difficultyLevel = DifficultyLevel.INTERMEDIATE.name,
            category = WordCategory.GENERAL.name
        )

        assertThat(word.getDifficulty()).isEqualTo(DifficultyLevel.INTERMEDIATE)
    }

    @Test
    fun `getCategory returns correct WordCategory`() {
        val word = Word(
            id = 1,
            word = "Algorithm",
            pronunciation = "/ˈæl.ɡə.rɪ.ðəm/",
            partOfSpeech = "noun",
            definition = "A process or set of rules",
            example = "The algorithm solves the problem.",
            synonyms = "procedure, method",
            antonyms = "",
            etymology = "From Arabic",
            difficultyLevel = DifficultyLevel.ADVANCED.name,
            category = WordCategory.TECHNOLOGY.name
        )

        assertThat(word.getCategory()).isEqualTo(WordCategory.TECHNOLOGY)
    }

    @Test
    fun `getSynonymsList returns correct list`() {
        val word = Word(
            id = 1,
            word = "Happy",
            pronunciation = "/ˈhæp.i/",
            partOfSpeech = "adjective",
            definition = "Feeling joy",
            example = "She was happy.",
            synonyms = "joyful, cheerful, delighted",
            antonyms = "sad",
            etymology = "From Middle English",
            difficultyLevel = DifficultyLevel.BEGINNER.name,
            category = WordCategory.GENERAL.name
        )

        val synonyms = word.getSynonymsList()
        assertThat(synonyms).hasSize(3)
        assertThat(synonyms).containsExactly("joyful", "cheerful", "delighted")
    }

    @Test
    fun `getAntonymsList returns correct list`() {
        val word = Word(
            id = 1,
            word = "Good",
            pronunciation = "/ɡʊd/",
            partOfSpeech = "adjective",
            definition = "Positive quality",
            example = "It's good.",
            synonyms = "great, excellent",
            antonyms = "bad, poor, terrible",
            etymology = "From Old English",
            difficultyLevel = DifficultyLevel.BEGINNER.name,
            category = WordCategory.GENERAL.name
        )

        val antonyms = word.getAntonymsList()
        assertThat(antonyms).hasSize(3)
        assertThat(antonyms).containsExactly("bad", "poor", "terrible")
    }

    @Test
    fun `getSynonymsList handles empty string`() {
        val word = Word(
            id = 1,
            word = "Test",
            pronunciation = "/test/",
            partOfSpeech = "noun",
            definition = "A test",
            example = "Testing.",
            synonyms = "",
            antonyms = "",
            etymology = "Latin",
            difficultyLevel = DifficultyLevel.BEGINNER.name,
            category = WordCategory.GENERAL.name
        )

        assertThat(word.getSynonymsList()).isEmpty()
        assertThat(word.getAntonymsList()).isEmpty()
    }
}
