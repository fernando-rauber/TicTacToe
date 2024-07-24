package uk.fernando.tictactoe.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import uk.fernando.tictactoe.navigation.Directions
import uk.fernando.tictactoe.navigation.buildGraph
import uk.fernando.tictactoe.theme.TicTacToeTheme
import uk.fernando.tictactoe.theme.green
import uk.fernando.tictactoe.theme.greenDark
import uk.fernando.uikit.component.UpdateStatusBar

@OptIn(ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val controller = rememberNavController()

            UpdateStatusBar(greenDark)

            TicTacToeTheme {

                Box {
                    // Background
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(1.5f)
                            .background(Brush.radialGradient(colors = listOf(green.copy(.8f), green, greenDark))),
                        content = {}
                    )

                    // Content
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