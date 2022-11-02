package uk.fernando.tictactoe.ext

import androidx.compose.ui.geometry.Offset
import uk.fernando.tictactoe.R
import uk.fernando.tictactoe.enum.EatTacToeIcon
import uk.fernando.tictactoe.enum.EatTacToeIcon.Companion.getByValue
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

fun getRandomAvatar(value: Int = 0) =
    listOf(
        R.drawable.ic_cat,
        R.drawable.ic_chick,
        R.drawable.ic_crab,
        R.drawable.ic_dolphin,
        R.drawable.ic_elephant,
        R.drawable.ic_goat,
        R.drawable.ic_mouse,
        R.drawable.ic_octopus,
        R.drawable.ic_panda,
        R.drawable.ic_penguin,
        R.drawable.ic_pig,
        R.drawable.ic_rabbit,
        R.drawable.ic_sheep,
        R.drawable.ic_tiger
    ).filter { it != value }.shuffled().first()

fun Int.getIcon(isPLayer1 : Boolean): Int {
    return when(getByValue(this)){
        EatTacToeIcon.DOLL -> if(isPLayer1) R.drawable.doll_red else R.drawable.doll_green
        EatTacToeIcon.CUP -> if(isPLayer1) R.drawable.cup_red else R.drawable.cup_green
        else -> if(isPLayer1) R.drawable.img_x else R.drawable.img_o
    }
}