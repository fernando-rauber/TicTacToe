package uk.fernando.tictactoe.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uk.fernando.tictactoe.navigation.Directions.BOARD_SIZE
import uk.fernando.tictactoe.navigation.Directions.WIN_CONDITION
import uk.fernando.tictactoe.screen.EatGamePage
import uk.fernando.tictactoe.screen.HomePage
import uk.fernando.tictactoe.screen.SettingsPage
import uk.fernando.tictactoe.screen.TicGamePage


@ExperimentalAnimationApi
fun NavGraphBuilder.buildGraph(navController: NavController) {

    composable(Directions.home.path) {
        HomePage(navController)
    }

    composable(Directions.game.withArgsFormat(BOARD_SIZE, WIN_CONDITION)) {
        val boardSize = it.arguments?.getString(BOARD_SIZE)
        val winCondition = it.arguments?.getString(WIN_CONDITION)

        if (boardSize == null || winCondition == null)
            navController.popBackStack()
        else
            TicGamePage(navController, boardSize.toInt(), winCondition.toInt())
    }

    composable(Directions.settings.path) {
        SettingsPage(navController)
    }
}

