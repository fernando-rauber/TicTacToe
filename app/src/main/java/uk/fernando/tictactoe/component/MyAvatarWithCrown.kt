package uk.fernando.tictactoe.component

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import uk.fernando.tictactoe.R
import uk.fernando.tictactoe.model.Player

@Composable
fun MyAvatarWithCrown(player: Player, showCrown: Boolean = true) {
    Box(
        modifier = Modifier
            .fillMaxWidth(.3f)
            .padding(top = 15.dp)
    )
    {

        Icon(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            painter = painterResource(player.avatar),
            contentDescription = null,
            tint = Color.Unspecified
        )

        if (showCrown)
            Icon(
                modifier = Modifier
                    .fillMaxWidth(.45f)
                    .aspectRatio(1f)
                    .align(Alignment.TopCenter)
                    .offset(y = (-25).dp),
                painter = painterResource(R.drawable.ic_crown),
                contentDescription = null,
                tint = Color.Unspecified
            )
    }
}