package uk.fernando.tictactoe.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import uk.fernando.tictactoe.navigation.Directions.BOARD_SIZE
import uk.fernando.tictactoe.navigation.Directions.GAME_TYPE
import uk.fernando.tictactoe.navigation.Directions.WIN_CONDITION
import uk.fernando.tictactoe.screen.*


@ExperimentalAnimationApi
fun NavGraphBuilder.buildGraph(navController: NavController) {

    composable(Directions.home.path) {
        HomePage(navController)
    }

    composable(Directions.createGame.withArgsFormat(GAME_TYPE)) {
        val type = it.arguments?.getString(GAME_TYPE)
        if (type == null)
            navController.popBackStack()
        else
            CreateGamePage(navController, type.toInt())
    }

    composable(Directions.game.withArgsFormat(BOARD_SIZE, WIN_CONDITION, GAME_TYPE)) {
        val boardSize = it.arguments?.getString(BOARD_SIZE)
        val winCondition = it.arguments?.getString(WIN_CONDITION)
        val type = it.arguments?.getString(GAME_TYPE)

        if (boardSize == null || winCondition == null)
            navController.popBackStack()
        else {
            if (type == "1")
                TicGamePage(navController, boardSize.toInt(), winCondition.toInt())
            else
                EatGamePage(navController, boardSize.toInt(), winCondition.toInt())
        }
    }

    composable(Directions.settings.path) {
        SettingsPage(navController)
    }
}