package uk.fernando.tictactoe.viewmodel

import androidx.compose.runtime.mutableStateOf
import uk.fernando.tictactoe.datastore.GamePrefsStore
import uk.fernando.tictactoe.datastore.PrefsStore
import uk.fernando.tictactoe.enum.CellResult
import uk.fernando.tictactoe.model.SizeModel
import uk.fernando.tictactoe.usecase.GameUseCase
import kotlin.math.ceil

class EatGameViewModel(
    private val gamePrefs: GamePrefsStore,
    private val prefsStore: PrefsStore,
    private val useCase: GameUseCase
) : TicGameViewModel(gamePrefs, useCase) {

    val imageSize = mutableStateOf<Int?>(null)

    val playerRed = mutableStateOf(SizeModel())
    val playerGreen = mutableStateOf(SizeModel())

    init {
        launchDefault {
            setSizeQuantity(gamePrefs.getBoardSize())
        }
    }

    override fun insertValueCellTicTacToe(position: Int): CellResult {
        if (imageSize.value == null)
            return CellResult.SIZE_NOT_SELECTED

        val cell = _gamePosition[position]
        if (cell.isX == null || imageSize.value!! > cell.size!!) {
            _gamePosition[position] = cell.copy(isX = isPLayer1Turn.value, size = imageSize.value)

            reduceSizeQuantity()

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
                return CellResult.END_GAME
            }
            return CellResult.DO_NOTHING
        }
        return CellResult.ERROR
    }

    fun setImageSize(size: Int) {
        imageSize.value = size
    }

    fun closeTutorial() {
        launchDefault { prefsStore.storeTutorialStatus(false) }
    }

    private fun reduceSizeQuantity() {
        if (imageSize.value != 1) {
            if (isPLayer1Turn.value) {
                if (imageSize.value == 2)
                    playerRed.value = playerRed.value.copy(size2 = playerRed.value.size2 - 1)
                else
                    playerRed.value = playerRed.value.copy(size3 = playerRed.value.size3 - 1)
            } else {
                if (imageSize.value == 2)
                    playerGreen.value = playerGreen.value.copy(size2 = playerGreen.value.size2 - 1)
                else
                    playerGreen.value = playerGreen.value.copy(size3 = playerGreen.value.size3 - 1)
            }
        }
        imageSize.value = null
    }

    private fun setSizeQuantity(boardSize: Int) {
        val size3 = ceil(boardSize / 2f).toInt()

        playerRed.value = playerRed.value.copy(size2 = boardSize, size3 = size3)
        playerGreen.value = playerGreen.value.copy(size2 = boardSize, size3 = size3)
    }
}
