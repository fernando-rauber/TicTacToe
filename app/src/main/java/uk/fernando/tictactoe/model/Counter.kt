package uk.fernando.tictactoe.model

import uk.fernando.tictactoe.enum.WinnerDirection

data class Counter(
    val direction: WinnerDirection,
    var isX: Boolean? = null,
    var counter: Int = 0,
    var ids: MutableList<Int> = mutableListOf()
) {
    fun firstValue(newValue: Boolean?, index: Int) {
        isX = newValue
        ids.clear()

        if (newValue != null) {
            counter = 1
            ids.add(index)
        } else
            counter = 0
    }

    fun reset() {
        isX = null
        ids.clear()
        counter = 0
    }

    fun increaseCounter(index: Int) {
        counter++
        ids.add(index)
    }
}