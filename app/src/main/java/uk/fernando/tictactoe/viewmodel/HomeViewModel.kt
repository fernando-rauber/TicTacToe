package uk.fernando.tictactoe.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import uk.fernando.tictactoe.datastore.GamePrefsStore

class HomeViewModel(private val prefsStore: GamePrefsStore) : BaseViewModel() {

    val boardSize = mutableStateOf(3)
    val winCondition = mutableStateOf(3)
    val rounds = mutableStateOf(3)
    val gameType = mutableStateOf(1)
    val difficulty = mutableStateOf(1)
    val playerName = mutableStateOf("Player 2")

    private val _winConditionList = mutableStateListOf<Int>()
    val winConditionList: List<Int> = _winConditionList

    init {
        launchDefault {
            boardSize.value = prefsStore.getBoardSize()
            winCondition.value = prefsStore.getWinCondition()
            rounds.value = prefsStore.getRounds()
            gameType.value = prefsStore.getGameType()
            difficulty.value = prefsStore.getDifficulty()
            playerName.value = prefsStore.getPLayer2Name()

            updateWinConditionList(boardSize.value)
        }
    }

    fun setBoardSize(size: Int) {
        boardSize.value = size
        launchDefault {
            prefsStore.storeBoardSize(size)

            if (size < winCondition.value)
                setWinCondition(size)

            updateWinConditionList(size)
        }
    }

    fun setWinCondition(value: Int) {
        winCondition.value = value
        launchDefault { prefsStore.storeWinCondition(value) }
    }

    fun setRounds(value: Int) {
        rounds.value = value
        launchDefault { prefsStore.storeRounds(value) }
    }

    fun setGameType(value: Int) {
        gameType.value = value
        launchDefault { prefsStore.storeGameType(value) }
    }

    fun setDifficulty(value: Int) {
        difficulty.value = value
        launchDefault { prefsStore.storeDifficulty(value) }
    }

    fun setPlayerName(value: String) {
        launchDefault { prefsStore.storePLayer2Name(value) }
    }

    private fun updateWinConditionList(boardSize: Int) {
        _winConditionList.clear()

        for (i in 3..if (boardSize <= 8) boardSize else 8) {
            _winConditionList.add(i)
        }
    }
}