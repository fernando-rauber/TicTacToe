package uk.fernando.tictactoe.enum

enum class GameIcon(val value: Int) {
    CLASSIC(1),
    CUP(2),
    DOLL(3);

    companion object {
        fun getByValue(value: Int) = values().firstOrNull { it.value == value }
    }
}