package uk.fernando.tictactoe.viewmodel

import androidx.compose.runtime.mutableStateOf
import uk.fernando.tictactoe.datastore.GamePrefsStore
import uk.fernando.tictactoe.model.DollCounter
import uk.fernando.tictactoe.usecase.GameUseCase
import kotlin.math.ceil

class EatGameViewModel(private val prefsStore: GamePrefsStore, private val useCase: GameUseCase) : TicGameViewModel(prefsStore, useCase) {

    private var imageSize: Int? = null // Used for eat tac toe

    val playerRed = mutableStateOf(DollCounter())
    val playerGreen = mutableStateOf(DollCounter())

    init {
        launchDefault {
            setDollQuantity(prefsStore.getBoardSize())
        }
    }

    override fun insertValueCellTicTacToe(position: Int): Boolean? {
        if (imageSize == null)
            return false

        val cell = _gamePosition[position]
        if (cell.isX == null || imageSize!! > cell.size!!) {
            _gamePosition[position] = cell.copy(isX = isPLayer1Turn.value, size = imageSize)

            reduceDollQuantity()

            isPLayer1Turn.value = !isPLayer1Turn.value // Next Player

            useCase.validateBoard(_gamePosition, winCondition)?.let {
                playerWinner.value = if (it.isX!!) {
                    player1.value.score++
                    player1.value
                } else {
                    player2.value.score++
                    player2.value
                }

                updateWinnerCells(it)
                return true
            }
            return null
        }
        return false
    }

    fun setImageSize(size: Int) {
        imageSize = size
    }

    private fun reduceDollQuantity() {
        if (imageSize != 1) {
            if (isPLayer1Turn.value) {
                if (imageSize == 2)
                    playerRed.value = playerRed.value.copy(size2 = playerRed.value.size2 - 1)
                else
                    playerRed.value = playerRed.value.copy(size3 = playerRed.value.size3 - 1)
            } else {
                if (imageSize == 2)
                    playerGreen.value = playerGreen.value.copy(size2 = playerGreen.value.size2 - 1)
                else
                    playerGreen.value = playerGreen.value.copy(size3 = playerGreen.value.size3 - 1)
            }

            imageSize = null
        }
    }

    private fun setDollQuantity(boardSize: Int) {
        val size3 = ceil(boardSize / 2f).toInt()

        playerRed.value = playerRed.value.copy(size2 = boardSize, size3 = size3)
        playerGreen.value = playerGreen.value.copy(size2 = boardSize, size3 = size3)
    }
}
