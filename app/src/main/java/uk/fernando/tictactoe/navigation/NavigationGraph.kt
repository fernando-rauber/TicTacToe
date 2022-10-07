package uk.fernando.tictactoe.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uk.fernando.tictactoe.screen.HomePage
import uk.fernando.tictactoe.screen.SettingsPage


@ExperimentalAnimationApi
fun NavGraphBuilder.buildGraph(navController: NavController) {

    composable(Directions.home.path) {
        HomePage(navController)
    }

//    composable(Directions.game.withArgsFormat(LEVEL_ID, CATEGORY_ID)) {
//        val levelId = it.arguments?.getString(LEVEL_ID)
//        val categoryId = it.arguments?.getString(CATEGORY_ID)
//
//        if (levelId == null || categoryId == null)
//            navController.popBackStack()
//        else
//            GamePage(navController, levelId.toInt(), categoryId.toInt())
//    }

    composable(Directions.settings.path) {
        SettingsPage(navController)
    }
}

