package uk.fernando.tictactoe.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.delay
import uk.fernando.tictactoe.datastore.GamePrefsStore
import uk.fernando.tictactoe.enum.CellResult
import uk.fernando.tictactoe.ext.getRandomAvatar
import uk.fernando.tictactoe.model.CellModel
import uk.fernando.tictactoe.model.Counter
import uk.fernando.tictactoe.model.Player
import uk.fernando.tictactoe.usecase.GameUseCase

open class TicGameViewModel(private val prefsStore: GamePrefsStore, private val useCase: GameUseCase) : BaseViewModel() {

    var winCondition = 3

    val rounds = mutableStateOf(3)
    val currentRound = mutableStateOf(1)
    val player1 = mutableStateOf(Player(getRandomAvatar(), "You"))
    val player2 = mutableStateOf(Player(getRandomAvatar(player1.value.avatar), ""))
    val playerWinner = mutableStateOf<Player?>(null)
    val isPLayer1Turn = mutableStateOf(true)

    protected val _gamePosition = mutableStateListOf<CellModel>()
    val gamePosition: List<CellModel> = _gamePosition

    init {
        launchDefault {
            winCondition = prefsStore.getWinCondition()
            rounds.value = prefsStore.getRounds()

            if (prefsStore.getGameType() == 1) { // Single player
                //difficulty.value = prefsStore.getDifficulty()
                player2.value = player2.value.copy(name = "AI")
            } else { // Multiplayer
                val player2Name = prefsStore.getPLayer2Name()
                player2.value = player2.value.copy(name = player2Name)
            }

            _gamePosition.addAll(useCase.createCards(prefsStore.getBoardSize()))
        }
    }

    open fun setCellValue(position: Int): CellResult {
        playerWinner.value?.let {
            launchDefault {
                playerWinner.value = null
                delay(100)
                playerWinner.value = it
            }
            return CellResult.DO_NOTHING
        }

        return insertValueCellTicTacToe(position)
    }

    open fun insertValueCellTicTacToe(position: Int): CellResult {
        val cell = _gamePosition[position]
        if (cell.isX == null) {
            _gamePosition[position] = cell.copy(isX = isPLayer1Turn.value)

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

    fun updateWinnerCells(counter: Counter) {
        counter.ids.forEachIndexed { index, position ->
            _gamePosition[position] = _gamePosition[position].copy(
                direction = counter.direction,
                paddingStart = index == 0,
                paddingEnd = index == (winCondition - 1)
            )
        }
    }

    fun startNextRound() {
        (0 until _gamePosition.size).forEach { index ->
            _gamePosition[index] = _gamePosition[index].copy(direction = null, isX = null, size = null)
        }

        currentRound.value++
        playerWinner.value = null
    }
}