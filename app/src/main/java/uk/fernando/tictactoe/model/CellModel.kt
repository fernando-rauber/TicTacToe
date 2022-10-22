package uk.fernando.tictactoe.model

import uk.fernando.tictactoe.enum.WinnerDirection

data class CellModel(
    val image: Int? = null,
    val direction: WinnerDirection? = null,
    val showBarLeft: Boolean = true,
    val showBarBottom: Boolean = true
)
