package uk.fernando.tictactoe.util

sealed class GameResult<T> {
    class Winner<T>(val result: T) : GameResult<T>()
    class Draw<T> : GameResult<T>()
    class Nothing<T> : GameResult<T>()
}