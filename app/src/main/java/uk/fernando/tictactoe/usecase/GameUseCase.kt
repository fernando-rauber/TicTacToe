package uk.fernando.tictactoe.usecase

import android.util.Log
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
                if (index % boardSize == 0) {
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

    fun validateTopEndTopStart(list: List<CardModel>, winCondition: Int): Int {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        val map = mutableMapOf<Int, Counter>()
        var row = 1

        list.forEachIndexed { index, value ->
            if (index >= boardSize * row)
                row += 1

            if (index in (winCondition - 1) until boardSize) {
                map[index] = (Counter(lastValue = value.image, if (value.image != null) 1 else 0)) //3
            } else if (row > 1) {
                kotlin.runCatching {

                    val indexCounter = index - (((row - 1) * boardSize) - (row - 1))

                    val counter = map[indexCounter]

                    if (counter != null) {
                        map[indexCounter] = validateCell(value, counter)

                        if (map[indexCounter]?.counter == winCondition)
                            return counter.lastValue!!
                    }
                }
            }
        }

        return 0
    }

    // 0  1  2  3  4
    // 5  6  7  8  9
    // 10 11 12 13 14
    // 15 16 17 18 19
    // 20 21 22 23 24

    // 0  1   2  3  4  5
    // 6  7   8  9 10 11
    // 12 13 14 15 16 17
    // 18 19 20 21 22 23
    // 24 25 26 27 28 29
    // 30 31 32 33 34 35

    // 0  1   2  3  4  5  6
    // 7  8   9 10 11 12 13
    // 14 15 16 17 18 19 20
    // 21 22 23 24 25 26 27
    // 28 29 30 31 32 33 34
    // 35 36 37 38 39 40 41
    // 42 43 44 45 46 47 48

    // 13 18 23 28
    // 9  13 17 21
    // 25 30 35 40
    // 31 37 43

    //9             // 13         5       *   3            3  = -4
    //9             // 17         5       *   4            4  = -8
    //9             // 21         5       *   5            4  = -12

    //11            // 16         6       *   3            3  = -5
    //11            // 21         6       *   4            4  = -10
    //11            // 26         6       *   5            5  = -15

    //23            // 28         6       *   5            5  = -5
    //23            // 33         6       *   6            6  = -5


    //13            // 19         7       *   3            3  = -6
    //13            // 25         7       *   4            4  = -12
    //13            // 31         7       *   5            5  = -18

    //34            // 40         7       *   6            6  = -6
    //34            // 46         7       *   7            7  = -12


    //9                  // 13         4            *   1  = -4
    //9                  // 17         4            *   2  = -8
    //9                  // 21         4            *   3  = -12

    //14                 // 18         4            *   2  = -8
    //14                 // 22         4            *   3  = -12

    fun validateTopEndBottomEnd(list: List<CardModel>, winCondition: Int): Int {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        val map = mutableMapOf<Int, Counter>()

        var nextRow = 2

        while (nextRow <= boardSize) {

            var row = nextRow
            var rowSupporter = 0

            list.forEachIndexed { index, value ->

                if (index >= (boardSize * nextRow) - 1) {

                    if (index % boardSize == (boardSize - 1)) {
                        map[index] = Counter(lastValue = value.image, if (value.image != null) 1 else 0)
                    } else {
                        kotlin.runCatching {

                            val indexCounter = index - ((boardSize - 1) * rowSupporter)
                            val diagonal = map[indexCounter]

                            if (diagonal != null) {
                                map[indexCounter] = validateCell(value, diagonal)

                                if (map[indexCounter]?.counter == winCondition)
                                    return diagonal.lastValue!!
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