package uk.fernando.tictactoe.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import uk.fernando.tictactoe.R
import uk.fernando.tictactoe.datastore.GamePrefsStore
import uk.fernando.tictactoe.model.CellModel
import uk.fernando.tictactoe.usecase.Counter
import uk.fernando.tictactoe.usecase.GameUseCase

class GameViewModel(private val prefsStore: GamePrefsStore, private val useCase: GameUseCase) : BaseViewModel() {

    val boardSize = mutableStateOf<Int?>(null)
    val winCondition = mutableStateOf(3)
    val rounds = mutableStateOf(3)
    val currentRound = mutableStateOf(1)

    private var lastPlayer = R.drawable.img_o

    private val _gamePosition = mutableStateListOf<CellModel>()
    val gamePosition: List<CellModel> = _gamePosition

    init {
        launchDefault {
            boardSize.value = prefsStore.getBoardSize()
            winCondition.value = prefsStore.getWinCondition()
//            rounds.value = prefsStore.getRounds()
//            gameType.value = prefsStore.getGameType()
//            difficulty.value = prefsStore.getDifficulty()
            createCards(boardSize.value!!)
        }
    }

    fun onPositionClick(position: Int) {
        if (_gamePosition[position].image == null) {
            _gamePosition[position] = _gamePosition[position].copy(image = getPlayerImage())

            useCase.validateBoard(_gamePosition, 3)?.let {
                updateWinnerCells(it)
            }
        }
    }

    private fun updateWinnerCells(counter: Counter){
        counter.ids.forEach { position ->
            _gamePosition[position] = _gamePosition[position].copy(direction = counter.direction)
        }
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

    private fun getPlayerImage(): Int {
        lastPlayer = if (lastPlayer == R.drawable.img_o)
            R.drawable.img_x
        else
            R.drawable.img_o

        return lastPlayer
    }
}
