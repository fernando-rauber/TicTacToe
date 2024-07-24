package uk.fernando.tictactoe.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import uk.fernando.tictactoe.R
import uk.fernando.tictactoe.theme.gold
import uk.fernando.tictactoe.theme.greenLight
import uk.fernando.tictactoe.theme.red
import uk.fernando.uikit.component.MyIconButton

@Composable
fun NavigationTopBar(
    onBackClick: (() -> Unit)? = null,
    title: String? = null,
    gameType: Int? = null,
    rightIcon: (@Composable BoxScope.() -> Unit)? = null
) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {

        if (onBackClick != null)
            MyIconButton(
                icon = R.drawable.ic_arrow_back,
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = onBackClick,
                tint = MaterialTheme.colorScheme.onBackground
            )

        title?.let {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = title,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
        }

        gameType?.let {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(greenLight)) {
                        append(stringResource(if (gameType == 1) R.string.tic else R.string.eat))
                    }
                    append(" ")
                    withStyle(style = SpanStyle(gold)) {
                        append(stringResource(if (gameType == 1) R.string.tac else R.string.eat_tac))
                    }
                    append(" ")
                    withStyle(style = SpanStyle(red)) {
                        append(stringResource(R.string.toe))
                    }
                },
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (rightIcon != null)
            rightIcon()
    }
}