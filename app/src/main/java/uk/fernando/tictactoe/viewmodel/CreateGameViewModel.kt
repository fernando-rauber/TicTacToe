package uk.fernando.tictactoe.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import uk.fernando.tictactoe.datastore.GamePrefsStore

class CreateGameViewModel(private val prefsStore: GamePrefsStore) : BaseViewModel() {

    val boardSize = mutableStateOf(3)
    val winCondition = mutableStateOf(3)
    val rounds = mutableStateOf(3)
    val iconType = mutableStateOf(1)
    val gameType = mutableStateOf(1)
    val playerName = mutableStateOf("Player 2")

    private val _winConditionList = mutableStateListOf<Int>()
    val winConditionList: List<Int> = _winConditionList

    init {
        launchDefault {
            boardSize.value = prefsStore.getBoardSize()
            winCondition.value = prefsStore.getWinCondition()
            rounds.value = prefsStore.getRounds()
            iconType.value = prefsStore.getIconType()
            gameType.value = prefsStore.getGameType()
            playerName.value = prefsStore.getPLayer2Name()

            updateWinConditionList(boardSize.value)
        }
    }

    fun setBoardSize(size: Int) {
        launchDefault {
            boardSize.value = size

            prefsStore.storeBoardSize(size)

            if (size < winCondition.value)
                setWinCondition(size)

            updateWinConditionList(size)
        }
    }

    fun setWinCondition(value: Int) {
        launchDefault {
            winCondition.value = value
            prefsStore.storeWinCondition(value)
        }
    }

    fun setRounds(value: Int) {
        launchDefault {
            rounds.value = value
            prefsStore.storeRounds(value)
        }
    }

    fun setIconType(value: Int) {
        launchDefault {
            iconType.value = value
            prefsStore.storeIconType(value)
        }
    }

    fun setGameType(value: Int) {
        launchDefault {
            gameType.value = value
            prefsStore.storeGameType(value)
        }
    }

    fun setPlayerName(value: String) {
        launchDefault {
            playerName.value = value.ifEmpty { "Player 2" }
            prefsStore.storePLayer2Name(playerName.value)
        }
    }

    private fun updateWinConditionList(boardSize: Int) {
        _winConditionList.clear()

        for (i in 3..if (boardSize <= 6) boardSize else 6) {
            _winConditionList.add(i)
        }
    }
}