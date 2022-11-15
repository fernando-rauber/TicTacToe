package uk.fernando.tictactoe.usecase

import uk.fernando.logger.MyLogger
import uk.fernando.tictactoe.enum.WinnerDirection
import uk.fernando.tictactoe.model.CellModel
import uk.fernando.tictactoe.model.Counter
import uk.fernando.tictactoe.util.GameResult
import uk.fernando.util.ext.TAG
import kotlin.math.sqrt

class AiGameUseCase(private val logger: MyLogger) {

    fun aiTurn(list: List<CellModel>, winCondition: Int): Int {
        return checkVertical(list, winCondition)
    }

    private fun checkHorizontal(list: List<CellModel>, winCondition: Int): Int {
        kotlin.runCatching {
            val boardSize = sqrt(list.size.toDouble()).toInt()

            var counter = AiCounter()

            // - | - | - | - | -
            // O | O | x | - | -
            // - | - | x | - | -
            // - | - | - | - | -
            // - | - | - | - | -

            list.forEachIndexed { index, cell ->

                if (counter.counter == (winCondition - 1) && cell.isX == null) // after empty cell
                    return index

                counter = validateCell(cell.isX, counter, index)

                if (counter.counter == (winCondition - 1) && counter.index != null) // before empty cell
                    return counter.index!!

                // reset counter when ends row
                if ((index + 1) % boardSize == 0)
                    counter.reset()
            }

            return checkHorizontal(list, winCondition - 1)
        }.onFailure {
            logger.e(TAG, it.message.toString())
            logger.addMessageToCrashlytics(TAG, "Error on validating board: msg: ${it.message}")
            logger.addExceptionToCrashlytics(it)
        }
        return 1
    }

    private fun checkVertical(list: List<CellModel>, winCondition: Int): Int {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        val map = mutableMapOf<Int, AiCounter>()

        list.forEachIndexed { index, cell ->

            var counter = map[(index + 1) % boardSize]

            if (counter == null)
                counter = AiCounter()

            if (counter.counter == (winCondition - 1) && cell.isX == null) // after empty cell
                return index

            counter = validateCell(cell.isX, counter, index)

            if (counter.counter == (winCondition - 1) && counter.index != null) // before empty cell
                return counter.index!!


            map[(index + 1) % boardSize] = counter
        }

        return checkVertical(list, winCondition - 1)
    }

    private fun validateCell(isX: Boolean?, counter: AiCounter, index: Int): AiCounter {
        if (isX == false)
            counter.reset()
        else if (isX == null) {
            counter.index = index
            counter.counter = 0
            counter.isX = null
        } else if (counter.isX != isX)
            counter.firstValue(isX)
        else
            counter.increaseCounter()

        return counter
    }

}

data class AiCounter(
    var isX: Boolean? = null,
    var counter: Int = 0,
    var index: Int? = null,
) {

    fun firstValue(newValue: Boolean?) {
        isX = newValue
        counter = if (newValue != null) 1 else 0
    }

    fun reset() {
        isX = null
        counter = 0
        index = null
    }

    fun increaseCounter() {
        counter++
    }
}