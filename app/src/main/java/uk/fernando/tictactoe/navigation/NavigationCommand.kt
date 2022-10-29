package uk.fernando.tictactoe.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

interface NavigationCommand {
    val path: String
    val arguments: List<NamedNavArgument>

    // build navigation path (for screen navigation)
    fun withArgs(vararg args: String): String {
        return buildString {
            append(path)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }

    fun withArgsFormat(vararg args: String): String {
        return buildString {
            append(path)
            args.forEach { arg ->
                append("/{$arg}")
            }
        }
    }
}

object Directions {

    val home = object : NavigationCommand {
        override val path: String
            get() = "home"
        override val arguments: List<NamedNavArgument>
            get() = emptyList()
    }

    val createGame = object : NavigationCommand {
        override val path: String
            get() = "create_game"
        override val arguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(GAME_TYPE) { type = NavType.StringType }
            )
    }

    val game = object : NavigationCommand {
        override val path: String
            get() = "game"
        override val arguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(BOARD_SIZE) { type = NavType.StringType },
                navArgument(WIN_CONDITION) { type = NavType.StringType },
                navArgument(GAME_TYPE) { type = NavType.StringType }
            )
    }

    val settings = object : NavigationCommand {
        override val path: String
            get() = "settings"
        override val arguments: List<NamedNavArgument>
            get() = emptyList()
    }

    const val GAME_TYPE = "game_type"
    const val BOARD_SIZE = "board_size"
    const val WIN_CONDITION = "win_condition"
}


