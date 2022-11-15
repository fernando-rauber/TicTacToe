package uk.fernando.tictactoe.usecase

import uk.fernando.logger.MyLogger
import uk.fernando.tictactoe.model.CellModel
import uk.fernando.util.ext.TAG
import kotlin.math.sqrt

class AiGameUseCase(private val logger: MyLogger) {

    fun aiTurn(list: List<CellModel>, winCondition: Int): Int {
        return checkHorizontal(list, winCondition)
    }

    private fun checkHorizontal(list: List<CellModel>, winCondition: Int): Int {
        kotlin.runCatching {
            val boardSize = sqrt(list.size.toDouble()).toInt()

            var counter = AiCounter()

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

            return checkVertical(list, winCondition)
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

        return validateTopStartTopEnd(list, winCondition)
    }

    private fun validateTopStartTopEnd(list: List<CellModel>, winCondition: Int): Int {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        val max = boardSize - winCondition
        val map = mutableMapOf<Int, AiCounter>()

        var row = 0

        list.forEachIndexed { index, cell ->

            if (index >= boardSize * row)
                row += 1

            if (index <= max) {
                map[index] = validateCell(cell.isX, AiCounter(), index)
            } else if (row > 1) {
                kotlin.runCatching {
                    val indexCounter = index - (((row - 1) * boardSize) + row - 1)

                    var counter = map[indexCounter]

                    if (counter != null) {

                        if (counter.counter == (winCondition - 1) && cell.isX == null) // after empty cell
                            return index

                        counter = validateCell(cell.isX, counter, index)

                        if (counter.counter == (winCondition - 1) && counter.index != null) // before empty cell
                            return counter.index!!

                        map[indexCounter] = counter
                    }
                }
            }
        }

        return validateTopEndTopStart(list, winCondition)
    }

    private fun validateTopEndTopStart(list: List<CellModel>, winCondition: Int): Int {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        val map = mutableMapOf<Int, AiCounter>()
        var row = 1

        list.forEachIndexed { index, cell ->

            if (index >= boardSize * row)
                row += 1

            if (index in (winCondition - 1) until boardSize) {
                map[index] = validateCell(cell.isX, AiCounter(), index)
            } else if (row > 1) {
                kotlin.runCatching {

                    val indexCounter = index - (((row - 1) * boardSize) - (row - 1))

                    var counter = map[indexCounter]

                    if (counter != null) {
                        if (counter.counter == (winCondition - 1) && cell.isX == null) // after empty cell
                            return index

                        counter = validateCell(cell.isX, counter, index)

                        if (counter.counter == (winCondition - 1) && counter.index != null) // before empty cell
                            return counter.index!!

                        map[indexCounter] = counter
                    }
                }
            }
        }

        return validateTopStartBottomStart(list, winCondition)
    }

    private fun validateTopStartBottomStart(list: List<CellModel>, winCondition: Int): Int {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        val map = mutableMapOf<Int, AiCounter>()

        var nextRow = 1

        while (nextRow <= boardSize) {

            var row = nextRow
            var rowSupporter = 0

            list.forEachIndexed { index, cell ->

                if (index >= boardSize * row) {
                    row++
                    rowSupporter++
                }

                if (index >= (boardSize * nextRow) - 1) {

                    if (index % boardSize == 0) {
                        map[index] = validateCell(cell.isX, AiCounter(), index)
                    } else {
                        kotlin.runCatching {

                            val indexCounter = index - ((boardSize + 1) * rowSupporter)
                            var counter = map[indexCounter]

                            if (counter != null) {
                                if (counter.counter == (winCondition - 1) && cell.isX == null) // after empty cell
                                    return index

                                counter = validateCell(cell.isX, counter, index)

                                if (counter.counter == (winCondition - 1) && counter.index != null) // before empty cell
                                    return counter.index!!

                                map[indexCounter] = counter
                            }
                        }
                    }
                }
            }

            nextRow++
        }

        return validateTopEndBottomEnd(list, winCondition)
    }

    private fun validateTopEndBottomEnd(list: List<CellModel>, winCondition: Int): Int {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        val map = mutableMapOf<Int, AiCounter>()

        var nextRow = 2

        while (nextRow <= boardSize) {

            var row = nextRow
            var rowSupporter = 0

            list.forEachIndexed { index, cell ->

                if (index >= boardSize * row) {
                    row++
                    rowSupporter++
                }

                if (index >= (boardSize * nextRow) - 1) {

                    if (index % boardSize == (boardSize - 1)) {
                        map[index] = validateCell(cell.isX, AiCounter(), index)
                    } else {
                        kotlin.runCatching {

                            val indexCounter = index - ((boardSize - 1) * rowSupporter)
                            var counter = map[indexCounter]

                            if (counter != null) {
                                if (counter.counter == (winCondition - 1) && cell.isX == null) // after empty cell
                                    return index

                                counter = validateCell(cell.isX, counter, index)

                                if (counter.counter == (winCondition - 1) && counter.index != null) // before empty cell
                                    return counter.index!!

                                map[indexCounter] = counter
                            }
                        }
                    }

                }
            }
            nextRow++
        }

        return checkHorizontal(list, winCondition - 1)
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