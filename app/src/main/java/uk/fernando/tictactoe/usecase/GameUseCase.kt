package uk.fernando.tictactoe.usecase

import uk.fernando.tictactoe.model.CardModel
import kotlin.math.sqrt

class GameUseCase() {

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

        return 0
    }

    fun validateBoardVertical(list: List<CardModel>, winCondition: Int): Int {
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

                if(counter.counter == winCondition)
                    return counter.lastValue!!

                map[(index + 1) % boardSize] = counter
            }
        }

        return 0
    }

    fun validateBoardDiagonal(list: List<CardModel>, winCondition: Int): Int {
        val boardSize = sqrt(list.size.toDouble()).toInt()

        list.forEachIndexed { index, value ->

        }

        return 0
    }

}

data class Counter(
    var lastValue: Int? = null,
    var counter: Int = 0
)