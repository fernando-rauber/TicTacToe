package uk.fernando.tictactoe.usecase

import uk.fernando.tictactoe.enum.WinnerDirection
import uk.fernando.tictactoe.model.CellModel
import uk.fernando.tictactoe.model.Counter
import kotlin.math.sqrt

class GameUseCase {


    fun validateBoard(list: List<CellModel>, winCondition: Int): Counter? {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        var counter = Counter(WinnerDirection.HORIZONTAL)
        list.forEachIndexed { index, cell ->

            cell.image?.let {

                counter = validateCell(cell.image, counter, index)

                if (counter.counter == winCondition) // Winner
                    return counter
            }

            // reset counter - Horizontal
            if (cell.image == null || (index + 1) % boardSize == 0) {
                counter.value = 0
                counter.counter = 0
            }
        }

        return validateBoardVertical(list, winCondition)
    }

    private fun validateBoardVertical(list: List<CellModel>, winCondition: Int): Counter? {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        val map = mutableMapOf<Int, Counter>()

        list.forEachIndexed { index, cell ->

            if (cell.image == null)
                map[(index + 1) % boardSize] = Counter(WinnerDirection.VERTICAL)
            else {
                var counter = map[(index + 1) % boardSize]

                if (counter == null || counter.value != cell.image)
                    counter = Counter(WinnerDirection.VERTICAL).apply { firstValue(cell.image, index) }
                else
                    counter.increaseCounter(index)

                if (counter.counter == winCondition)
                    return counter

                map[(index + 1) % boardSize] = counter
            }
        }

        return validateTopStartTopEnd(list, winCondition)
    }

    private fun validateTopStartTopEnd(list: List<CellModel>, winCondition: Int): Counter? {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        val max = boardSize - winCondition
        val map = mutableMapOf<Int, Counter>()

        var row = 0

        list.forEachIndexed { index, cell ->

            if (index <= max) {
                map[index] = Counter(WinnerDirection.START_TOP_END_BOTTOM).apply { firstValue(cell.image, index) }
            } else if (row > 1) {
                kotlin.runCatching {
                    val indexCounter = index - (((row - 1) * boardSize) + row - 1)

                    val counter = map[indexCounter]

                    if (counter != null) {
                        map[indexCounter] = validateCell(cell.image, counter, index)

                        if (map[indexCounter]?.counter == winCondition)
                            return counter
                    }
                }
            }

            if (index >= boardSize * row)
                row += 1
        }

        return validateTopEndTopStart(list, winCondition)
    }

    private fun validateTopEndTopStart(list: List<CellModel>, winCondition: Int): Counter? {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        val map = mutableMapOf<Int, Counter>()
        var row = 1

        list.forEachIndexed { index, cell ->
            if (index >= boardSize * row)
                row += 1

            if (index in (winCondition - 1) until boardSize) {
                map[index] = Counter(WinnerDirection.BOTTOM_START_TOP_END).apply { firstValue(cell.image, index) }
            } else if (row > 1) {
                kotlin.runCatching {

                    val indexCounter = index - (((row - 1) * boardSize) - (row - 1))

                    val counter = map[indexCounter]

                    if (counter != null) {
                        map[indexCounter] = validateCell(cell.image, counter, index)

                        if (map[indexCounter]?.counter == winCondition)
                            return counter
                    }
                }
            }
        }

        return validateTopStartBottomStart(list, winCondition)
    }

    private fun validateTopStartBottomStart(list: List<CellModel>, winCondition: Int): Counter? {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        val map = mutableMapOf<Int, Counter>()

        var nextRow = 1

        while (nextRow <= boardSize) {

            var row = nextRow
            var rowSupporter = 0

            list.forEachIndexed { index, cell ->

                if (index >= (boardSize * nextRow) - 1) {

                    if (index % boardSize == 0) {
                        map[index] = Counter(WinnerDirection.START_TOP_END_BOTTOM).apply { firstValue(cell.image, index) }
                    } else {
                        kotlin.runCatching {

                            val indexCounter = index - ((boardSize + 1) * rowSupporter)
                            val diagonal = map[indexCounter]

                            if (diagonal != null) {
                                map[indexCounter] = validateCell(cell.image, diagonal, index)

                                if (map[indexCounter]?.counter == winCondition)
                                    return diagonal
                            }
                        }
                    }

                    if (index >= boardSize * row) {
                        row++
                        rowSupporter++
                    }
                }
            }

            nextRow++
        }

        return validateTopEndBottomEnd(list, winCondition)
    }

    private fun validateTopEndBottomEnd(list: List<CellModel>, winCondition: Int): Counter? {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        val map = mutableMapOf<Int, Counter>()

        var nextRow = 2

        while (nextRow <= boardSize) {

            var row = nextRow
            var rowSupporter = 0

            list.forEachIndexed { index, cell ->

                if (index >= (boardSize * nextRow) - 1) {

                    if (index % boardSize == (boardSize - 1)) {
                        map[index] = Counter(WinnerDirection.BOTTOM_START_TOP_END).apply { firstValue(cell.image, index) }
                    } else {
                        kotlin.runCatching {

                            val indexCounter = index - ((boardSize - 1) * rowSupporter)
                            val diagonal = map[indexCounter]

                            if (diagonal != null) {
                                map[indexCounter] = validateCell(cell.image, diagonal, index)

                                if (map[indexCounter]?.counter == winCondition)
                                    return diagonal
                            }
                        }
                    }

                    if (index >= boardSize * row) {
                        row++
                        rowSupporter++
                    }
                }
            }

            nextRow++
        }

        return null
    }
}

private fun validateCell(value: Int?, counter: Counter, index: Int): Counter {
    value?.let {
        if (counter.value != value)
            counter.firstValue(value, index)
        else
            counter.increaseCounter(index)
    }
    return counter
}