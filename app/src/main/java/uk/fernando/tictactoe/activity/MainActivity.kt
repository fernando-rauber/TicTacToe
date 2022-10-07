package uk.fernando.tictactoe.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import uk.fernando.tictactoe.navigation.Directions
import uk.fernando.tictactoe.navigation.buildGraph
import uk.fernando.tictactoe.theme.TicTacToeTheme

@OptIn(ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val controller = rememberNavController()

//            UpdateStatusBar(color = dark)

            TicTacToeTheme {

                Box() {
                    NavHost(
                        navController = controller,
                        startDestination = Directions.home.path
                    ) {
                        buildGraph(controller)
                    }
                }
            }
        }
    }
}