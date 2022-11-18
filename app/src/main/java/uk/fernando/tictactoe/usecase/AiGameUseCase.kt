package uk.fernando.tictactoe.usecase

import uk.fernando.logger.MyLogger
import uk.fernando.tictactoe.model.CellModel
import uk.fernando.util.ext.TAG
import kotlin.math.sqrt
import kotlin.random.Random

class AiGameUseCase(private val logger: MyLogger) {

    fun aiTurn(list: List<CellModel>, winCondition: Int): Int {
        kotlin.runCatching {
            // AI WIN

            // Check if AI can win
            val resultWin = checkNextToBlock(list, winCondition, false)
            if (resultWin >= 0) return resultWin

            // Check if AI can win between cells
            val betweenWin = checkEmptyVerticalHorizontal(list, winCondition, false)
            if (betweenWin >= 0) return betweenWin


            // BLOCK PLAYER

            // Check if can block player between cells
            val betweenBlock = checkEmptyVerticalHorizontal(list, winCondition, true)
            if (betweenBlock >= 0) return betweenBlock

            // Block next player move
            var condition = winCondition

            while (condition >= 0) {
                val resultBlock = checkNextToBlock(list, condition, true)
                if (resultBlock >= 0) return resultBlock
                condition--
            }

        }.onFailure {
            logger.e(TAG, it.message.toString())
            logger.addMessageToCrashlytics(TAG, "Error on validating board: msg: ${it.message}")
            logger.addExceptionToCrashlytics(it)
        }
        return 0
    }

    private fun checkNextToBlock(list: List<CellModel>, winCondition: Int, isXPlayer: Boolean): Int {
        return when (val order = Random.nextInt(5)) {
            0 -> checkHorizontal(list, winCondition, isXPlayer, order)
            1 -> checkVertical(list, winCondition, isXPlayer, order)
            2 -> validateTopStartTopEnd(list, winCondition, isXPlayer, order)
            3 -> validateTopEndTopStart(list, winCondition, isXPlayer, order)
            4 -> validateTopStartBottomStart(list, winCondition, isXPlayer, order)
            else -> validateTopEndBottomEnd(list, winCondition, isXPlayer, order)
        }
    }

    private fun checkHorizontal(list: List<CellModel>, winCondition: Int, isXPlayer: Boolean, order: Int): Int {

        val boardSize = sqrt(list.size.toDouble()).toInt()

        var counter = AiCounter()

        list.forEachIndexed { index, cell ->

            if (counter.counter == (winCondition - 1) && cell.isX == null) // after empty cell
                return index

            counter = validateCell(cell.isX, counter, index, isXPlayer)

            if (counter.counter == (winCondition - 1) && counter.index != null) // before empty cell
                return counter.index!!

            // reset counter when ends row
            if ((index + 1) % boardSize == 0)
                counter.reset()
        }

        return when (order) {
            1 -> -1
            else -> checkVertical(list, winCondition, isXPlayer, order)
        }
    }

    private fun checkVertical(list: List<CellModel>, winCondition: Int, isXPlayer: Boolean, order: Int): Int {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        val map = mutableMapOf<Int, AiCounter>()

        list.forEachIndexed { index, cell ->

            var counter = map[(index + 1) % boardSize]

            if (counter == null)
                counter = AiCounter()

            if (counter.counter == (winCondition - 1) && cell.isX == null) // after empty cell
                return index

            counter = validateCell(cell.isX, counter, index, isXPlayer)

            if (counter.counter == (winCondition - 1) && counter.index != null) // before empty cell
                return counter.index!!


            map[(index + 1) % boardSize] = counter
        }

        return when (order) {
            2 -> -1
            else -> validateTopStartTopEnd(list, winCondition, isXPlayer, order)
        }
    }

    private fun validateTopStartTopEnd(list: List<CellModel>, winCondition: Int, isXPlayer: Boolean, order: Int): Int {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        val max = boardSize - winCondition
        val map = mutableMapOf<Int, AiCounter>()

        var row = 0

        list.forEachIndexed { index, cell ->

            if (index >= boardSize * row)
                row += 1

            if (index <= max) {
                map[index] = validateCell(cell.isX, AiCounter(), index, isXPlayer)
            } else if (row > 1) {
                kotlin.runCatching {
                    val indexCounter = index - (((row - 1) * boardSize) + row - 1)

                    var counter = map[indexCounter]

                    if (counter != null) {

                        if (counter.counter == (winCondition - 1) && cell.isX == null) // after empty cell
                            return index

                        counter = validateCell(cell.isX, counter, index, isXPlayer)

                        if (counter.counter == (winCondition - 1) && counter.index != null) // before empty cell
                            return counter.index!!

                        map[indexCounter] = counter
                    }
                }
            }
        }

        return when (order) {
            3 -> -1
            else -> validateTopEndTopStart(list, winCondition, isXPlayer, order)
        }
    }

    private fun validateTopEndTopStart(list: List<CellModel>, winCondition: Int, isXPlayer: Boolean, order: Int): Int {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        val map = mutableMapOf<Int, AiCounter>()
        var row = 1

        list.forEachIndexed { index, cell ->

            if (index >= boardSize * row)
                row += 1

            if (index in (winCondition - 1) until boardSize) {
                map[index] = validateCell(cell.isX, AiCounter(), index, isXPlayer)
            } else if (row > 1) {
                kotlin.runCatching {

                    val indexCounter = index - (((row - 1) * boardSize) - (row - 1))

                    var counter = map[indexCounter]

                    if (counter != null) {
                        if (counter.counter == (winCondition - 1) && cell.isX == null) // after empty cell
                            return index

                        counter = validateCell(cell.isX, counter, index, isXPlayer)

                        if (counter.counter == (winCondition - 1) && counter.index != null) // before empty cell
                            return counter.index!!

                        map[indexCounter] = counter
                    }
                }
            }
        }

        return when (order) {
            4 -> -1
            else -> validateTopStartBottomStart(list, winCondition, isXPlayer, order)
        }
    }

    private fun validateTopStartBottomStart(list: List<CellModel>, winCondition: Int, isXPlayer: Boolean, order: Int): Int {
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
                        map[index] = validateCell(cell.isX, AiCounter(), index, isXPlayer)
                    } else {
                        kotlin.runCatching {

                            val indexCounter = index - ((boardSize + 1) * rowSupporter)
                            var counter = map[indexCounter]

                            if (counter != null) {
                                if (counter.counter == (winCondition - 1) && cell.isX == null) // after empty cell
                                    return index

                                counter = validateCell(cell.isX, counter, index, isXPlayer)

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

        return when (order) {
            5 -> -1
            else -> validateTopEndBottomEnd(list, winCondition, isXPlayer, order)
        }
    }

    private fun validateTopEndBottomEnd(list: List<CellModel>, winCondition: Int, isXPlayer: Boolean, order: Int): Int {
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
                        map[index] = validateCell(cell.isX, AiCounter(), index, isXPlayer)
                    } else {
                        kotlin.runCatching {

                            val indexCounter = index - ((boardSize - 1) * rowSupporter)
                            var counter = map[indexCounter]

                            if (counter != null) {
                                if (counter.counter == (winCondition - 1) && cell.isX == null) // after empty cell
                                    return index

                                counter = validateCell(cell.isX, counter, index, isXPlayer)

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

        return when (order) {
            0 -> -1
            else -> checkHorizontal(list, winCondition, isXPlayer, order)
        }
    }

    private fun validateCell(isX: Boolean?, counter: AiCounter, index: Int, isXPlayer: Boolean): AiCounter {
        if (isX == !isXPlayer)
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


//REGION Check between cells


private fun checkEmptyVerticalHorizontal(list: List<CellModel>, winCondition: Int, isXPlayer: Boolean): Int {
    val boardSize = sqrt(list.size.toDouble()).toInt()

    val counterHorizontal: MutableList<CellModel> = mutableListOf()
    val mapVertical = mutableMapOf<Int, MutableList<CellModel>>()

    list.forEachIndexed { index, cell ->

        // Horizontal
        counterHorizontal.add(cell)

        // reset counter when ends row
        if ((index + 1) % boardSize == 0) {
            val emptyBetween = checkBetweenEmptyCells(counterHorizontal, winCondition, isXPlayer)
            if (emptyBetween >= 0)
                return emptyBetween
            counterHorizontal.clear()
        }

        // Vertical
        var counterVertical = mapVertical[(index + 1) % boardSize]

        if (counterVertical == null)
            counterVertical = mutableListOf()

        counterVertical.add(cell)
        mapVertical[(index + 1) % boardSize] = counterVertical

        if ((index + 1) >= boardSize * (boardSize - 1)) {
            val emptyBetween = checkBetweenEmptyCells(counterVertical, winCondition, isXPlayer)
            if (emptyBetween >= 0) return emptyBetween
        }
    }

    return checkBetweenTopStartTopEnd(list, winCondition, isXPlayer)
}

private fun checkBetweenTopStartTopEnd(list: List<CellModel>, winCondition: Int, isXPlayer: Boolean): Int {
    val boardSize = sqrt(list.size.toDouble()).toInt()

    val max = boardSize - winCondition
    val map = mutableMapOf<Int, MutableList<CellModel>>()

    var row = 0
    // check between TopStart to TopEnd
    list.forEachIndexed { index, cell ->

        if (index >= boardSize * row)
            row++

        if (index <= max) {
            map[index] = mutableListOf()
            map[index]?.add(cell)
        } else if (row > 1) {
            runCatching {
                val indexCounter = index - (((row - 1) * boardSize) + row - 1)

                if (map[indexCounter] != null) {
                    map[indexCounter]?.add(cell)

                    if (row >= winCondition) {
                        val emptyBetween = checkBetweenEmptyCells(map[indexCounter] ?: emptyList(), winCondition, isXPlayer)
                        if (emptyBetween >= 0) return emptyBetween
                    }
                }
            }
        }
    }

    // check between TopEnd to TopStart
    map.clear()
    row = 1

    list.forEachIndexed { index, cell ->

        if (index >= boardSize * row)
            row += 1

        if (index in (winCondition - 1) until boardSize) {
            map[index] = mutableListOf()
            map[index]?.add(cell)
        } else if (row > 1) {
            runCatching {

                val indexCounter = index - (((row - 1) * boardSize) - (row - 1))

                if (map[indexCounter] != null) {
                    map[indexCounter]?.add(cell)

                    if (row >= winCondition) {
                        val emptyBetween = checkBetweenEmptyCells(map[indexCounter] ?: emptyList(), winCondition, isXPlayer)
                        if (emptyBetween >= 0) return emptyBetween
                    }
                }
            }
        }
    }

    return checkEmptyDiagonalBottom(list, winCondition, isXPlayer)
}

private fun checkEmptyDiagonalBottom(list: List<CellModel>, winCondition: Int, isXPlayer: Boolean): Int {
    val boardSize = sqrt(list.size.toDouble()).toInt()

    val map = mutableMapOf<Int, MutableList<CellModel>>()
    var nextRow = 1

    // check between TopStart to BottomStart
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
                    map[index] = mutableListOf()
                    map[index]?.add(cell)
                } else {
                    runCatching {

                        val indexCounter = index - ((boardSize + 1) * rowSupporter)

                        if (map[indexCounter] != null) {
                            map[indexCounter]?.add(cell)

                            if (row >= winCondition) {
                                val emptyBetween = checkBetweenEmptyCells(map[indexCounter] ?: emptyList(), winCondition, isXPlayer)
                                if (emptyBetween >= 0) return emptyBetween
                            }
                        }
                    }
                }
            }
        }

        nextRow++
    }

    // check between TopEnd to BottomEnd
    map.clear()
    nextRow = 2

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
                    map[index] = mutableListOf()
                    map[index]?.add(cell)
                } else {
                    runCatching {

                        val indexCounter = index - ((boardSize - 1) * rowSupporter)

                        if (map[indexCounter] != null) {
                            map[indexCounter]?.add(cell)

                            if (row >= winCondition) {
                                val emptyBetween = checkBetweenEmptyCells(map[indexCounter] ?: emptyList(), winCondition, isXPlayer)
                                if (emptyBetween >= 0) return emptyBetween
                            }
                        }
                    }
                }
            }
        }
        nextRow++
    }

    return -1
}

private fun checkBetweenEmptyCells(list: List<CellModel>, winCondition: Int, isXPlayer: Boolean): Int {
    var startIndex = 0

    while (startIndex < list.size - (winCondition - 1)) {
        var emptyIndex = -1
        var counter = 0

        list.subList(startIndex, winCondition + startIndex).forEach {
            if (it.isX == !isXPlayer) {
                emptyIndex = -1
                counter = 0
            } else if (it.isX == null) {
                if (counter > 0)
                    emptyIndex = it.position
            } else if (it.isX == isXPlayer)
                counter++

            if (counter == (winCondition - 1) && emptyIndex >= 0)
                return emptyIndex
        }
        startIndex++
    }
    return -1
}

//ENDREGION

data class AiCounter(
    var isX: Boolean? = null,
    var counter: Int = 0,
    var index: Int? = null
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