package uk.fernando.tictactoe.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uk.fernando.tictactoe.theme.greyLight

@Composable
fun MyDivider(modifier: Modifier = Modifier.padding(vertical = 8.dp)) {
    HorizontalDivider(
        modifier = modifier,
        color = greyLight.copy(0.3f)
    )
}