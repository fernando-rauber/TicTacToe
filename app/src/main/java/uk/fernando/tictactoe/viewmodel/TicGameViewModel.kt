package uk.fernando.tictactoe.viewmodel

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import uk.fernando.tictactoe.datastore.GamePrefsStore
import uk.fernando.tictactoe.enum.CellResult
import uk.fernando.tictactoe.ext.getRandomAvatar
import uk.fernando.tictactoe.model.CellModel
import uk.fernando.tictactoe.model.Counter
import uk.fernando.tictactoe.model.Player
import uk.fernando.tictactoe.usecase.AiGameUseCase
import uk.fernando.tictactoe.usecase.GameUseCase
import uk.fernando.tictactoe.util.GameResult

open class TicGameViewModel(private val prefsStore: GamePrefsStore, private val useCase: GameUseCase, private val aiUseCase: AiGameUseCase) : BaseViewModel() {

    var winCondition = 3
    var isAiOn = false

    val rounds = mutableIntStateOf(3)
    val currentRound = mutableIntStateOf(1)
    val player1 = mutableStateOf(Player(getRandomAvatar(), ""))
    val player2 = mutableStateOf(Player(getRandomAvatar(player1.value.avatar), ""))
    val roundResult = mutableStateOf<GameResult<Player>?>(null)
    val isPLayer1Turn = mutableStateOf(true)

    protected val _cellList = mutableStateListOf<CellModel>()
    val cellList: List<CellModel> = _cellList

    fun init() {
        launchDefault {
            winCondition = prefsStore.getWinCondition()
            rounds.intValue = prefsStore.getRounds()

            if (prefsStore.getGameType() == 1) { // Single player
                isAiOn = true
                player2.value = player2.value.copy(name = "AI")
            } else { // Multiplayer
                val player2Name = prefsStore.getPLayer2Name()
                player2.value = player2.value.copy(name = player2Name)
            }

            _cellList.clear()
            _cellList.addAll(useCase.createCards(prefsStore.getBoardSize()))
        }
    }

    open suspend fun setCellValue(position: Int): CellResult {
        if (isAiOn && !isPLayer1Turn.value)
            return CellResult.DO_NOTHING

        roundResult.value?.let {
            launchDefault {
                roundResult.value = null
                delay(100)
                roundResult.value = it
            }
            return CellResult.DO_NOTHING
        }

        return when (val result = insertValueCellTicTacToe(position)) {
            CellResult.AI_TURN -> computerTurn().first()
            else -> result
        }
    }

    private fun computerTurn() = flow {
        val index = aiUseCase.aiTurn(_cellList, winCondition)
        delay(800)
        emit(insertValueCellTicTacToe(index))
    }

    open fun insertValueCellTicTacToe(position: Int): CellResult {
        val cell = _cellList[position]
        if (cell.isX == null) {
            _cellList[position] = cell.copy(isX = isPLayer1Turn.value)

            isPLayer1Turn.value = !isPLayer1Turn.value // Next Player

            when (val gameResult = useCase.validateBoard(_cellList, winCondition)) {
                is GameResult.Winner -> {
                    val playerWinner = if (gameResult.result.isX!!) {
                        player1.value.score++
                        player1.value
                    } else {
                        player2.value.score++
                        player2.value
                    }

                    roundResult.value = GameResult.Winner(playerWinner)

                    updateWinnerCells(gameResult.result)
                    return CellResult.END_GAME
                }
                is GameResult.Draw -> {
                    roundResult.value = GameResult.Draw()
                    return CellResult.DRAW
                }
                else -> {
                    return if (isAiOn && !isPLayer1Turn.value)
                        CellResult.AI_TURN
                    else
                        CellResult.DO_NOTHING
                }
            }

        }
        return CellResult.ERROR
    }

    fun updateWinnerCells(counter: Counter) {
        counter.ids.forEachIndexed { index, position ->
            _cellList[position] = _cellList[position].copy(
                direction = counter.direction,
                paddingStart = index == 0,
                paddingEnd = index == (winCondition - 1)
            )
        }
    }

    fun startNextRound() {
        (0 until _cellList.size).forEach { index ->
            _cellList[index] = _cellList[index].copy(direction = null, isX = null, size = null)
        }

        currentRound.intValue++
        roundResult.value = null

        additionalWorkForEatTacToe()

        if (isAiOn && !isPLayer1Turn.value)
            launchDefault { computerTurn().first() }
    }

    open fun additionalWorkForEatTacToe() {}
}