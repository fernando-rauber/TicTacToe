package uk.fernando.tictactoe.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.inject
import uk.fernando.tictactoe.R
import uk.fernando.tictactoe.component.NavigationTopBar
import uk.fernando.tictactoe.component.WinConditionIcon
import uk.fernando.tictactoe.datastore.GamePrefsStore
import uk.fernando.tictactoe.theme.dark
import uk.fernando.tictactoe.theme.greyLight
import uk.fernando.tictactoe.viewmodel.HomeViewModel
import uk.fernando.util.component.MyIconButton

@Composable
fun HomePage(
    navController: NavController = NavController(LocalContext.current),
    viewModel: HomeViewModel = getViewModel()
) {
    val coroutine = rememberCoroutineScope()
    val gamePrefs: GamePrefsStore by inject()
    val boardSize = gamePrefs.getBoardSize().collectAsState(initial = 3)
    val winCondition = gamePrefs.getWinCondition().collectAsState(initial = 3)
    val rounds = gamePrefs.getRounds().collectAsState(initial = 3)
    val gameType = gamePrefs.getGameType().collectAsState(initial = 1)

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

        Column(modifier = Modifier.padding(16.dp)) {

            BoardSize(boardSize.value) { viewModel.setBoardSize(it) }

            MyDivider()

            WinCondition(
                viewModel = viewModel,
                selectedValue = winCondition.value,
                onSelected = { viewModel.setWinCondition(it) }
            )

            MyDivider()

            Rounds(
                selectedValue = rounds.value,
                onSelected = { coroutine.launch { gamePrefs.storeRounds(it) } }
            )

            MyDivider()

            GameType(
                selectedValue = gameType.value,
                onSelected = { coroutine.launch { gamePrefs.storeGameType(it) } }
            )

            MyDivider()
        }

    }
}

@Composable
private fun ColumnScope.BoardSize(size: Int, onSelected: (Int) -> Unit) {
    var boardSize by mutableStateOf(size.toFloat())

    Text(
        text = stringResource(R.string.board_size),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground
    )

    Slider(
        value = boardSize,
        colors = SliderDefaults.colors(dark, dark, Color.White, greyLight, dark),
        onValueChange = {
            boardSize = it
            onSelected(it.toInt())
        },
        steps = 6,
        valueRange = 3f..10f,
    )

    Text(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        text = "${boardSize.toInt()} X ${boardSize.toInt()}",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun WinCondition(viewModel: HomeViewModel, selectedValue: Int, onSelected: (Int) -> Unit) {
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
            items(viewModel.winCondition) { win ->
                MyChip(
                    text = "$win",
                    isSelected = selectedValue == win,
                    onClick = { onSelected(win) }
                )
            }
        }
    )

    WinConditionIcon(selectedValue)
}

@Composable
private fun Rounds(selectedValue: Int, onSelected: (Int) -> Unit) {
    val valuesList = listOf(1, 3, 5)

    Text(
        text = stringResource(R.string.rounds),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground
    )

    LazyVerticalGrid(
        columns = GridCells.Adaptive(64.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        content = {
            items(valuesList) { value ->
                MyChip(
                    text = "$value",
                    isSelected = selectedValue == value,
                    onClick = { onSelected(value) }
                )
            }
        }
    )
}

@Composable
private fun GameType(selectedValue: Int, onSelected: (Int) -> Unit) {

    Text(
        text = stringResource(R.string.game_type),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        content = {
            item {
                MyChip(
                    text = stringResource(R.string.single_player),
                    isSelected = selectedValue == 1,
                    onClick = { onSelected(1) }
                )
            }
            item {
                MyChip(
                    text = stringResource(R.string.multiplayer),
                    isSelected = selectedValue == 2,
                    onClick = { onSelected(2) }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(if (isSelected) dark else greyLight),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 3.dp),
            text = text,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MyDivider() {
    Divider(
        modifier = Modifier.padding(vertical = 8.dp),
        color = greyLight.copy(0.5f)
    )
}

