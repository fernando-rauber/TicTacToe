package uk.fernando.tictactoe.viewmodel

import androidx.compose.runtime.mutableStateOf
import uk.fernando.tictactoe.datastore.GamePrefsStore

class GameViewModel(private val prefsStore: GamePrefsStore) : BaseViewModel() {

    val boardSize = mutableStateOf(3)
    val winCondition = mutableStateOf(3)
    val rounds = mutableStateOf(3)


    init {
        launchDefault {
//            boardSize.value = prefsStore.getBoardSize()
//            winCondition.value = prefsStore.getWinCondition()
//            rounds.value = prefsStore.getRounds()
//            gameType.value = prefsStore.getGameType()
//            difficulty.value = prefsStore.getDifficulty()

        }
    }

}