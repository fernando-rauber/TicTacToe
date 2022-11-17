package uk.fernando.tictactoe.model

import uk.fernando.tictactoe.enum.WinnerDirection

data class CellModel(
    val position: Int,
    val isX: Boolean? = null,
    val size: Int? = null, // Eat Tac Toe
    val paddingStart: Boolean = false,
    val paddingEnd: Boolean = false,
    val direction: WinnerDirection? = null,
    val showBarLeft: Boolean = true,
    val showBarBottom: Boolean = true
)
