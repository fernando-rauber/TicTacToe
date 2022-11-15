package uk.fernando.tictactoe.usecase

import uk.fernando.logger.MyLogger
import uk.fernando.tictactoe.enum.WinnerDirection
import uk.fernando.tictactoe.model.CellModel
import uk.fernando.tictactoe.model.Counter
import uk.fernando.tictactoe.util.GameResult
import uk.fernando.util.ext.TAG
import kotlin.math.sqrt

class GameUseCase(private val logger: MyLogger) {

    fun createCards(boardSize: Int): List<CellModel> {
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

        return list
    }

    fun validateBoard(list: List<CellModel>, winCondition: Int): GameResult<Counter> {
        kotlin.runCatching {
            val boardSize = sqrt(list.size.toDouble()).toInt()

            var counter = Counter(WinnerDirection.HORIZONTAL)
            list.forEachIndexed { index, cell ->

                cell.isX?.let {

                    counter = validateCell(cell.isX, counter, index)

                    if (counter.counter == winCondition) // Winner
                        return GameResult.Winner(counter)
                }

                // reset counter - Horizontal
                if (cell.isX == null || (index + 1) % boardSize == 0) {
                    counter.isX = null
                    counter.counter = 0
                }
            }

            return validateBoardVertical(list, winCondition)
        }.onFailure {
            logger.e(TAG, it.message.toString())
            logger.addMessageToCrashlytics(TAG, "Error on validating board: msg: ${it.message}")
            logger.addExceptionToCrashlytics(it)
        }
        return GameResult.Nothing()
    }

    private fun validateBoardVertical(list: List<CellModel>, winCondition: Int): GameResult<Counter> {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        val map = mutableMapOf<Int, Counter>()

        list.forEachIndexed { index, cell ->

            if (cell.isX == null)
                map[(index + 1) % boardSize] = Counter(WinnerDirection.VERTICAL)
            else {
                var counter = map[(index + 1) % boardSize]

                if (counter == null || counter.isX != cell.isX)
                    counter = Counter(WinnerDirection.VERTICAL).apply { firstValue(cell.isX, index) }
                else
                    counter.increaseCounter(index)

                if (counter.counter == winCondition)
                    return GameResult.Winner(counter)

                map[(index + 1) % boardSize] = counter
            }
        }

        return validateTopStartTopEnd(list, winCondition)
    }

    private fun validateTopStartTopEnd(list: List<CellModel>, winCondition: Int): GameResult<Counter> {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        val max = boardSize - winCondition
        val map = mutableMapOf<Int, Counter>()

        var row = 0

        list.forEachIndexed { index, cell ->

            if (index >= boardSize * row)
                row += 1

            if (index <= max) {
                map[index] = Counter(WinnerDirection.START_TOP_END_BOTTOM).apply { firstValue(cell.isX, index) }
            } else if (row > 1) {
                kotlin.runCatching {
                    val indexCounter = index - (((row - 1) * boardSize) + row - 1)

                    val counter = map[indexCounter]

                    if (counter != null) {
                        map[indexCounter] = validateCell(cell.isX, counter, index)

                        if (map[indexCounter]?.counter == winCondition)
                            return GameResult.Winner(counter)
                    }
                }
            }

        }

        return validateTopEndTopStart(list, winCondition)
    }

    private fun validateTopEndTopStart(list: List<CellModel>, winCondition: Int): GameResult<Counter> {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        val map = mutableMapOf<Int, Counter>()
        var row = 1

        list.forEachIndexed { index, cell ->

            if (index >= boardSize * row)
                row += 1

            if (index in (winCondition - 1) until boardSize) {
                map[index] = Counter(WinnerDirection.BOTTOM_START_TOP_END).apply { firstValue(cell.isX, index) }
            } else if (row > 1) {
                kotlin.runCatching {

                    val indexCounter = index - (((row - 1) * boardSize) - (row - 1))

                    val counter = map[indexCounter]

                    if (counter != null) {
                        map[indexCounter] = validateCell(cell.isX, counter, index)

                        if (map[indexCounter]?.counter == winCondition)
                            return GameResult.Winner(counter)
                    }
                }
            }
        }

        return validateTopStartBottomStart(list, winCondition)
    }

    private fun validateTopStartBottomStart(list: List<CellModel>, winCondition: Int): GameResult<Counter> {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        val map = mutableMapOf<Int, Counter>()

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
                        map[index] = Counter(WinnerDirection.START_TOP_END_BOTTOM).apply { firstValue(cell.isX, index) }
                    } else {
                        kotlin.runCatching {

                            val indexCounter = index - ((boardSize + 1) * rowSupporter)
                            val diagonal = map[indexCounter]

                            if (diagonal != null) {
                                map[indexCounter] = validateCell(cell.isX, diagonal, index)

                                if (map[indexCounter]?.counter == winCondition)
                                    return GameResult.Winner(diagonal)
                            }
                        }
                    }
                }
            }

            nextRow++
        }

        return validateTopEndBottomEnd(list, winCondition)
    }

    private fun validateTopEndBottomEnd(list: List<CellModel>, winCondition: Int): GameResult<Counter> {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        val map = mutableMapOf<Int, Counter>()

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
                        map[index] = Counter(WinnerDirection.BOTTOM_START_TOP_END).apply { firstValue(cell.isX, index) }
                    } else {
                        kotlin.runCatching {

                            val indexCounter = index - ((boardSize - 1) * rowSupporter)
                            val diagonal = map[indexCounter]

                            if (diagonal != null) {
                                map[indexCounter] = validateCell(cell.isX, diagonal, index)

                                if (map[indexCounter]?.counter == winCondition)
                                    return GameResult.Winner(diagonal)
                            }
                        }
                    }

                }
            }
            nextRow++
        }

        return validateIfDraw(list)
    }

    private fun validateIfDraw(list: List<CellModel>): GameResult<Counter> {
        if (list.firstOrNull { it.isX == null } == null)
            return GameResult.Draw()

        return GameResult.Nothing()
    }
}

private fun validateCell(isX: Boolean?, counter: Counter, index: Int): Counter {
    if (isX == null)
        counter.reset()
    else if (counter.isX != isX)
        counter.firstValue(isX, index)
    else
        counter.increaseCounter(index)

    return counter
}