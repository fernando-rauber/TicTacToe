package uk.fernando.tictactoe.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.koin.androidx.compose.getViewModel
import uk.fernando.tictactoe.R
import uk.fernando.tictactoe.component.MyDivider
import uk.fernando.tictactoe.component.NavigationTopBar
import uk.fernando.tictactoe.component.WinConditionIcon
import uk.fernando.tictactoe.navigation.Directions
import uk.fernando.tictactoe.theme.*
import uk.fernando.tictactoe.viewmodel.HomeViewModel
import uk.fernando.util.component.MyAnimatedVisibility
import uk.fernando.util.component.MyButton
import uk.fernando.util.component.MyIconButton
import uk.fernando.util.ext.safeNav

@Composable
fun HomePage(
    navController: NavController = NavController(LocalContext.current),
    viewModel: HomeViewModel = getViewModel()
) {

    Column(modifier = Modifier.fillMaxSize()) {

        NavigationTopBar(
            rightIcon = {
                MyIconButton(
                    icon = R.drawable.ic_settings,
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = { },
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        )

        Spacer(Modifier.height(30.dp))

        Column(
            modifier = Modifier
                .padding(16.dp)
                .weight(1f)
            //.verticalScroll(rememberScrollState())
        ) {

            BoardSize(viewModel)

            MyDivider()

            WinCondition(viewModel)

            MyDivider()

            Rounds(viewModel)

            MyDivider()

            GameType(viewModel)

            MyDivider()

            AIDifficulty(viewModel)

        }

        MyButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .defaultMinSize(minHeight = 50.dp),
            text = stringResource(R.string.start_action).uppercase(),
            fontSize = 20.sp,
            color = greenLight,
            onClick = { navController.safeNav(Directions.game.path) }
        )
    }
}

@Composable
private fun ColumnScope.BoardSize(viewModel: HomeViewModel) {
    Text(
        text = stringResource(R.string.board_size),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground
    )

    Slider(
        value = viewModel.boardSize.value.toFloat(),
        colors = SliderDefaults.colors(dark, dark, Color.White, greyLight, dark),
        onValueChange = { viewModel.setBoardSize(it.toInt()) },
        steps = 6,
        valueRange = 3f..10f,
    )

    Text(
        modifier = Modifier.align(CenterHorizontally),
        text = "${viewModel.boardSize.value}X${viewModel.boardSize.value}",
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun WinCondition(viewModel: HomeViewModel) {
    Text(
        text = stringResource(R.string.win_condition),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground
    )

    LazyVerticalGrid(
        columns = GridCells.Adaptive(64.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        content = {
            items(viewModel.winConditionList) { win ->
                MyChip(
                    text = "$win",
                    isSelected = viewModel.winCondition.value == win,
                    onClick = { viewModel.setWinCondition(win) }
                )
            }
        }
    )

    WinConditionIcon(viewModel.winCondition.value)
}

@Composable
private fun Rounds(viewModel: HomeViewModel) {
    val valuesList = listOf(1, 3, 5)

    RowContent(title = R.string.rounds) {
        valuesList.forEach { value ->
            MyChip(
                text = "$value",
                isSelected = viewModel.rounds.value == value,
                onClick = { viewModel.setRounds(value) }
            )
        }
    }
}

@Composable
private fun GameType(viewModel: HomeViewModel) {
    RowContent(title = R.string.game_type) {
        MyChip(
            text = stringResource(R.string.single_player),
            isSelected = viewModel.gameType.value == 1,
            onClick = { viewModel.setGameType(1) }
        )
        MyChip(
            text = stringResource(R.string.multiplayer),
            isSelected = viewModel.gameType.value == 2,
            onClick = { viewModel.setGameType(2) }
        )
    }
}

@Composable
private fun AIDifficulty(viewModel: HomeViewModel) {
    MyAnimatedVisibility(viewModel.gameType.value == 1) {

        Column {
            RowContent(title = R.string.difficulty) {
                MyChip(
                    text = stringResource(R.string.easy),
                    isSelected = viewModel.difficulty.value == 1,
                    color = greenLight,
                    onClick = { viewModel.setDifficulty(1) }
                )
                MyChip(
                    text = stringResource(R.string.medium),
                    isSelected = viewModel.difficulty.value == 2,
                    color = gold,
                    onClick = { viewModel.setDifficulty(2) }
                )
                MyChip(
                    text = stringResource(R.string.hard),
                    isSelected = viewModel.difficulty.value == 3,
                    color = red,
                    onClick = { viewModel.setDifficulty(3) }
                )
            }
        }
    }
}

@Composable
private fun RowContent(@StringRes title: Int, content: @Composable () -> Unit) {
    Text(
        text = stringResource(title),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground
    )
    Row(
        modifier = Modifier.padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyChip(text: String, isSelected: Boolean, color: Color = dark, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(if (isSelected) color else greyLight),
    ) {
        Text(
            modifier = Modifier
                .align(CenterHorizontally)
                .padding(horizontal = 24.dp, vertical = 3.dp),
            text = text,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

