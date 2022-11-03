package uk.fernando.tictactoe.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import uk.fernando.tictactoe.ext.getIcon

@Composable
fun WinConditionIcon(winCondition: Int, gameIcon: Int) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.width(IntrinsicSize.Min),
            contentAlignment = Alignment.Center
        ) {
            Row(Modifier.padding(horizontal = 6.dp)) {
                for (i in 1..winCondition) {
                    Image(
                        modifier = Modifier
                            .padding(start = 1.dp)
                            .size(24.dp),
                        painter = painterResource(gameIcon.getIcon(true)),
                        contentDescription = null
                    )
                }
            }

            Divider(
                thickness = 2.dp,
                color = Color.White.copy(0.8f)
            )
        }
    }
}