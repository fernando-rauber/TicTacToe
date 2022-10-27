package uk.fernando.tictactoe.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.delay
import uk.fernando.tictactoe.R
import uk.fernando.tictactoe.datastore.GamePrefsStore
import uk.fernando.tictactoe.ext.getRandomAvatar
import uk.fernando.tictactoe.ext.isPlayerX
import uk.fernando.tictactoe.model.CellModel
import uk.fernando.tictactoe.model.Counter
import uk.fernando.tictactoe.model.Player
import uk.fernando.tictactoe.usecase.GameUseCase

class GameViewModel(private val prefsStore: GamePrefsStore, private val useCase: GameUseCase) : BaseViewModel() {

    private var winCondition = 3
    val rounds = mutableStateOf(3)
    val currentRound = mutableStateOf(1)

    val player1 = mutableStateOf(Player(getRandomAvatar(), "You"))
    val player2 = mutableStateOf(Player(getRandomAvatar(player1.value.avatar), ""))
    val playerWinner = mutableStateOf<Player?>(null)

    val isPLayer1Turn = mutableStateOf(true)

    private val _gamePosition = mutableStateListOf<CellModel>()
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

            createCards(prefsStore.getBoardSize())
        }
    }

    fun onPositionClick(position: Int): Boolean {
        playerWinner.value?.let {
            launchDefault {
                playerWinner.value = null
                delay(100)
                playerWinner.value = it
            }
            return false
        }

        if (_gamePosition[position].image == null) {
            _gamePosition[position] = _gamePosition[position].copy(image = if (isPLayer1Turn.value) R.drawable.img_x else R.drawable.img_o)

            isPLayer1Turn.value = !isPLayer1Turn.value // Next Player

            useCase.validateBoard(_gamePosition, winCondition)?.let {
                playerWinner.value = if (it.value!!.isPlayerX()) {
                    player1.value.score++
                    player1.value
                } else {
                    player2.value.score++
                    player2.value
                }

                updateWinnerCells(it)
                return true
            }
        }
        return false
    }

    private fun updateWinnerCells(counter: Counter) {
        counter.ids.forEachIndexed { index, position ->
            _gamePosition[position] = _gamePosition[position].copy(
                direction = counter.direction,
                paddingStart = index == 0,
                paddingEnd = index == (winCondition - 1)
            )
        }
//        // Display Bottom sheet 'Next Round'
//        endRoundDialog.value = true
    }

    fun startNextRound() {
        (0 until _gamePosition.size).forEach { index ->
            _gamePosition[index] = _gamePosition[index].copy(image = null, direction = null)
        }

        currentRound.value++
        playerWinner.value = null
    }

    private fun createCards(boardSize: Int) {
        val boardSizeTotal = boardSize * boardSize

        val list = mutableListOf<CellModel>()

        for (position in 0 until boardSizeTotal) {
            list.add(
                CellModel(
                    showBarLeft = position % boardSize < boardSize - 1,
                    showBarBottom = position < (boardSizeTotal - boardSize)
                )
            )
        }

        _gamePosition.addAll(list)
    }
}