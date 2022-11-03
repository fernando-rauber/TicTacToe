package uk.fernando.tictactoe.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.getViewModel
import uk.fernando.tictactoe.R
import uk.fernando.tictactoe.component.MyDivider
import uk.fernando.tictactoe.component.MyTextField
import uk.fernando.tictactoe.component.NavigationTopBar
import uk.fernando.tictactoe.component.WinConditionIcon
import uk.fernando.tictactoe.enum.GameIcon
import uk.fernando.tictactoe.navigation.Directions
import uk.fernando.tictactoe.theme.*
import uk.fernando.tictactoe.viewmodel.CreateGameViewModel
import uk.fernando.util.component.MyAnimatedVisibility
import uk.fernando.util.component.MyButton
import uk.fernando.util.component.MyIconButton
import uk.fernando.util.ext.safeNav

@Composable
fun CreateGamePage(
    navController: NavController = NavController(LocalContext.current),
    gameType: Int,
    viewModel: CreateGameViewModel = getViewModel()
) {

    Column(modifier = Modifier.fillMaxSize()) {

        NavigationTopBar(
            onBackClick = { navController.popBackStack() },
            gameType = gameType,
            rightIcon = {
                MyIconButton(
                    icon = R.drawable.ic_settings,
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = { },
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(20.dp))

            BoardSize(viewModel)

            MyDivider()

            WinCondition(viewModel, gameType)

            MyDivider()

            if (gameType == 2) {
                IconChoice(viewModel)
                MyDivider()
            }

            Rounds(viewModel)

            MyDivider()

            GameType(viewModel)

            MyDivider()

            Box(Modifier.padding(bottom = 16.dp)) {
                AIDifficulty(viewModel)
                Player2Name(viewModel)
            }
        }

        MyButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, bottom = 16.dp, end = 16.dp)
                .defaultMinSize(minHeight = 50.dp),
            text = stringResource(R.string.start_action).uppercase(),
            color = greenLight,
            onClick = { navController.safeNav(Directions.game.withArgs("${viewModel.boardSize.value}", "${viewModel.winCondition.value}", "$gameType", "${viewModel.iconType.value}")) }
        )
    }
}

@Composable
private fun ColumnScope.BoardSize(viewModel: CreateGameViewModel) {
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
private fun WinCondition(viewModel: CreateGameViewModel, gameType: Int) {
    Text(
        text = stringResource(R.string.win_condition),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onBackground
    )

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        viewModel.winConditionList.forEach { win ->
            MyChip(
                text = "$win",
                isSelected = viewModel.winCondition.value == win,
                onClick = { viewModel.setWinCondition(win) }
            )
        }
    }

    WinConditionIcon(
        winCondition = viewModel.winCondition.value,
        iconType = if (gameType == 1) GameIcon.CLASSIC.value else viewModel.iconType.value
    )
}

@Composable
private fun Rounds(viewModel: CreateGameViewModel) {
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
private fun IconChoice(viewModel: CreateGameViewModel) {
    RowContent(title = R.string.icon_choice) {
        MyChip(
            icon = R.drawable.doll_red,
            isSelected = viewModel.iconType.value == GameIcon.DOLL.value,
            onClick = { viewModel.setIconType(GameIcon.DOLL.value) }
        )
        MyChip(
            icon = R.drawable.cup_red,
            isSelected = viewModel.iconType.value == GameIcon.CUP.value,
            onClick = { viewModel.setIconType(GameIcon.CUP.value) }
        )
    }
}

@Composable
private fun GameType(viewModel: CreateGameViewModel) {
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
private fun AIDifficulty(viewModel: CreateGameViewModel) {
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
private fun Player2Name(viewModel: CreateGameViewModel) {
    MyAnimatedVisibility(viewModel.gameType.value == 2) {

        Column {
            Text(
                modifier = Modifier.padding(bottom = 5.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                text = stringResource(R.string.player_name)
            )
            MyTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewModel.playerName.value,
                onValueChange = {
                    if (it.length <= 15) {
                        viewModel.setPlayerName(it)
                        viewModel.playerName.value = it
                    }
                }
            )
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
private fun MyChip(text: String? = null, icon: Int? = null, isSelected: Boolean, color: Color = dark, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(if (isSelected) color else greyLight),
    ) {
        text?.let {
            Text(
                modifier = Modifier
                    .align(CenterHorizontally)
                    .padding(horizontal = 24.dp, vertical = 3.dp),
                text = text,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
        icon?.let {
            Image(
                modifier = Modifier
                    .align(CenterHorizontally)
                    .padding(horizontal = 24.dp, vertical = 3.dp)
                    .size(32.dp),
                painter = painterResource(icon),
                contentDescription = null
            )
        }
    }
}

