package com.vocabulary.ui.navigation

sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object WordDetail : Screen("word_detail/{wordId}") {
        fun createRoute(wordId: Long) = "word_detail/$wordId"
    }
    data object BrowseWords : Screen("browse_words")
    data object Favorites : Screen("favorites")
    data object Quiz : Screen("quiz")
}
