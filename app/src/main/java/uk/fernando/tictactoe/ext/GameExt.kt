package uk.fernando.tictactoe.ext

import androidx.compose.ui.geometry.Offset
import uk.fernando.tictactoe.R
import uk.fernando.tictactoe.enum.WinnerDirection

fun WinnerDirection.getStartOffset(width: Float, padding: Boolean): Offset {
    val start = if (padding) width / 4f else 0f
    return when (this) {
        WinnerDirection.HORIZONTAL -> Offset(x = 0f + start, y = width / 2)
        WinnerDirection.VERTICAL -> Offset(x = width / 2, y = 0f + start)
        WinnerDirection.START_TOP_END_BOTTOM -> Offset(x = 0f + start, y = 0f + start)
        else -> Offset(x = width - start, y = 0f + start)
    }
}

fun WinnerDirection.getEndOffset(width: Float, padding: Boolean): Offset {
    val end = if (padding) width / 4f else 0f
    return when (this) {
        WinnerDirection.HORIZONTAL -> Offset(x = width - end, y = width / 2)
        WinnerDirection.VERTICAL -> Offset(x = width / 2, y = width - end)
        WinnerDirection.START_TOP_END_BOTTOM -> Offset(x = width - end, y = width - end)
        else -> Offset(x = 0f + end, y = width - end)
    }
}

fun Int.isPlayerX(): Boolean {
    return when (this) {
        R.drawable.img_x -> true
        else -> false
    }
}