package uk.fernando.tictactoe.usecase

import uk.fernando.tictactoe.model.CardModel
import kotlin.math.sqrt

class GameUseCase {


    fun validateBoard(list: List<CardModel>, winCondition: Int): Int {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        val counter = Counter()
        list.forEachIndexed { index, value ->

            value.image?.let {

                if (value.image != counter.lastValue) {
                    counter.lastValue = value.image
                    counter.counter = 1
                } else {
                    counter.counter += 1

                    if (counter.counter == winCondition) // Winner
                        return counter.lastValue!!
                }
            }

            // reset counter - Horizontal
            if (value.image == null || (index + 1) % boardSize == 0) {
                counter.lastValue = 0
                counter.counter = 0
            }
        }

        return validateBoardVertical(list, winCondition)
    }

    private fun validateBoardVertical(list: List<CardModel>, winCondition: Int): Int {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        val map = mutableMapOf<Int, Counter>()

        list.forEachIndexed { index, value ->

            if (value.image == null)
                map[(index + 1) % boardSize] = Counter()
            else {
                var counter = map[(index + 1) % boardSize]
                if (counter == null || counter.lastValue != value.image)
                    counter = Counter(value.image, 1)
                else
                    counter.counter += 1

                if (counter.counter == winCondition)
                    return counter.lastValue!!

                map[(index + 1) % boardSize] = counter
            }
        }

        return validateTopStartTopEnd(list, winCondition)
    }

    private fun validateTopStartTopEnd(list: List<CardModel>, winCondition: Int): Int {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        val max = boardSize - winCondition
        val map = mutableMapOf<Int, Counter>()

        var row = 0

        list.forEachIndexed { index, value ->

            if (index <= max) {
                map[index] = (Counter(lastValue = value.image, if (value.image != null) 1 else 0)) //3
            } else if (row > 1) {
                kotlin.runCatching {
                    val indexCounter = index - (((row - 1) * boardSize) + row - 1)

                    val counter = map[indexCounter]

                    if (counter != null) {
                        map[indexCounter] = validateCell(value, counter)

                        if (map[indexCounter]?.counter == winCondition)
                            return counter.lastValue!!
                    }
                }
            }

            if (index >= boardSize * row)
                row += 1
        }

        return validateTopStartBottomStart(list, winCondition)
    }

    private fun validateTopStartBottomStart(list: List<CardModel>, winCondition: Int): Int {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        val map = mutableMapOf<Int, Counter>()

        var row = 1

        list.forEachIndexed { index, value ->
            if (index >= boardSize * row)
                row += 1

            if (row > 1) {
                if (index % ((row - 1) * boardSize) == 0) {
                    map[index] = (Counter(lastValue = value.image, if (value.image != null) 1 else 0))
                } else {

                    kotlin.runCatching {
                        val indexCounterTop = index - ((boardSize * (row - 2)) + row - 2)
                        val counterTop = map[indexCounterTop]

                        if (counterTop != null) {
                            map[indexCounterTop] = validateCell(value, counterTop)

                            if (map[indexCounterTop]?.counter == winCondition)
                                return counterTop.lastValue!!
                        }

                        val indexCounterBottom = index - (6 * (row - 3))
                        val counterBottom = map[indexCounterBottom]

                        if (counterBottom != null) {
                            map[indexCounterBottom] = validateCell(value, counterBottom)

                            if (map[indexCounterBottom]?.counter == winCondition)
                                return counterBottom.lastValue!!
                        }
                    }
                }
            }
        }

        return 0
    }
}

private fun validateCell(cell: CardModel, counter: Counter): Counter {
    return if (counter.lastValue != cell.image)
        Counter(cell.image, 1)
    else {
        counter.counter += 1
        counter
    }
}


data class Counter(
    var lastValue: Int? = null,
    var counter: Int = 0
)