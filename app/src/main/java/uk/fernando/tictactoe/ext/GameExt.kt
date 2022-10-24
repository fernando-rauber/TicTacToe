package uk.fernando.tictactoe.ext

import uk.fernando.tictactoe.R
import uk.fernando.tictactoe.enum.WinnerDirection

fun WinnerDirection.getRotation() : Float{
    return when(this){
        WinnerDirection.HORIZONTAL -> 0f
        WinnerDirection.VERTICAL -> 90f
        WinnerDirection.START_TOP_END_BOTTOM -> 45f
        else -> 135f
    }
}

fun Int.isPlayerX() : Boolean{
    return when(this){
        R.drawable.img_x -> true
        else -> false
    }
}