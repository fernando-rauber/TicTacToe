package uk.fernando.tictactoe.viewmodel

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.first
import uk.fernando.tictactoe.datastore.GamePrefsStore

class HomeViewModel(private val prefsStore: GamePrefsStore) : BaseViewModel() {

    private val _winCondition = mutableStateListOf<Int>()
    val winCondition: List<Int> = _winCondition
    private var winConditionValue: Int = 3

    init {
        launchDefault {
            updateWinConditionList(prefsStore.getBoardSize().first())
        }

    }

    fun setBoardSize(size: Int) {
        launchDefault {
            prefsStore.storeBoardSize(size)

            if (size < winConditionValue)
                setWinCondition(size)

            updateWinConditionList(size)
        }
    }

    fun setWinCondition(value: Int) {
        launchDefault {
            winConditionValue = value
            prefsStore.storeWinCondition(value)
        }
    }

    private fun updateWinConditionList(boardSize: Int) {
        _winCondition.clear()

        for (i in 3..boardSize) {
            _winCondition.add(i)
        }
    }
}