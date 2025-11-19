package com.vocabulary.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vocabulary.ui.navigation.Screen
import com.vocabulary.ui.screens.*

@Composable
fun VocabularyApp(viewModel: MainViewModel = viewModel()) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(
                viewModel = viewModel,
                onNavigateToWordDetail = { wordId ->
                    navController.navigate(Screen.WordDetail.createRoute(wordId))
                },
                onNavigateToBrowse = {
                    navController.navigate(Screen.BrowseWords.route)
                },
                onNavigateToFavorites = {
                    navController.navigate(Screen.Favorites.route)
                },
                onNavigateToQuiz = {
                    navController.navigate(Screen.Quiz.route)
                }
            )
        }

        composable(
            route = Screen.WordDetail.route,
            arguments = listOf(navArgument("wordId") { type = NavType.LongType })
        ) { backStackEntry ->
            val wordId = backStackEntry.arguments?.getLong("wordId") ?: 0L
            WordDetailScreen(
                wordId = wordId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.BrowseWords.route) {
            BrowseWordsScreen(
                viewModel = viewModel,
                onNavigateToWordDetail = { wordId ->
                    navController.navigate(Screen.WordDetail.createRoute(wordId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                viewModel = viewModel,
                onNavigateToWordDetail = { wordId ->
                    navController.navigate(Screen.WordDetail.createRoute(wordId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Quiz.route) {
            QuizScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
