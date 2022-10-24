package uk.fernando.tictactoe.ext

import androidx.compose.ui.geometry.Offset
import uk.fernando.tictactoe.R
import uk.fernando.tictactoe.enum.WinnerDirection

fun WinnerDirection.getStartOffset(width: Float) : Offset{
    return when(this){
        WinnerDirection.HORIZONTAL -> Offset(x = 0f, y = width/2)
        WinnerDirection.VERTICAL -> Offset(x = width/2, y = width)
        WinnerDirection.START_TOP_END_BOTTOM -> Offset(x = 0f, y = 0f)
        else -> Offset(x = width, y = 0f)
    }
}

fun WinnerDirection.getEndOffset(width: Float) : Offset{
    return when(this){
        WinnerDirection.HORIZONTAL -> Offset(x = width, y = width/2)
        WinnerDirection.VERTICAL -> Offset(x = width/2, y = 0f)
        WinnerDirection.START_TOP_END_BOTTOM -> Offset(x = width, y = width)
        else -> Offset(x = 0f, y = width)
    }
}

fun Int.isPlayerX() : Boolean{
    return when(this){
        R.drawable.img_x -> true
        else -> false
    }
}