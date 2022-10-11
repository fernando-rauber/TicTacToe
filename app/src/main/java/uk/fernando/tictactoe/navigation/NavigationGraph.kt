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

    composable(Directions.game.path) {

    }

    composable(Directions.settings.path) {
        SettingsPage(navController)
    }
}

