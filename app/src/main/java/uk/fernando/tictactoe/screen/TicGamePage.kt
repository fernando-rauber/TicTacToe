package uk.fernando.tictactoe.screen

import android.media.MediaPlayer
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.getViewModel
import uk.fernando.tictactoe.R
import uk.fernando.tictactoe.component.NavigationTopBar
import uk.fernando.tictactoe.component.WinConditionIcon
import uk.fernando.tictactoe.enum.CellResult
import uk.fernando.tictactoe.enum.EatTacToeIcon
import uk.fernando.tictactoe.ext.getEndOffset
import uk.fernando.tictactoe.ext.getIcon
import uk.fernando.tictactoe.ext.getStartOffset
import uk.fernando.tictactoe.model.CellModel
import uk.fernando.tictactoe.theme.dark
import uk.fernando.tictactoe.theme.greenLight
import uk.fernando.tictactoe.viewmodel.TicGameViewModel
import uk.fernando.util.component.MyButton
import uk.fernando.util.ext.clickableSingle
import uk.fernando.util.ext.playAudio
import kotlin.math.ceil

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TicGamePage(
    navController: NavController = NavController(LocalContext.current),
    boardSize: Int,
    winCondition: Int,
    viewModel: TicGameViewModel = getViewModel()
) {

    val sheetState = rememberModalBottomSheetState(
        initialValue = if (viewModel.playerWinner.value != null) ModalBottomSheetValue.Expanded else ModalBottomSheetValue.Hidden,
        confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded }
    )

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            BottomSheetEndRound(
                viewModel = viewModel,
                onClose = { navController.popBackStack() }
            )
        },
        sheetBackgroundColor = Color.Transparent,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(Modifier.fillMaxSize()) {

            GameTopBar(
                gameType = 1,
                onClose = { navController.popBackStack() }
            )

            Column(
                Modifier.weight(1f),
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                WinConditionIcon(winCondition, 1)

                Board(viewModel, boardSize, EatTacToeIcon.CLASSIC.value)

                Text(
                    text = stringResource(R.string.current_round, viewModel.currentRound.value, viewModel.rounds.value),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )
            }

            BottomBar(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameTopBar(gameType: Int, onClose: () -> Unit) {
    NavigationTopBar(
        gameType = gameType,
        rightIcon = {
            Card(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp),
                onClick = onClose,
                shape = CircleShape,
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(Color.White),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    modifier = Modifier.padding(8.dp),
                    contentDescription = null,
                    tint = Color.Black.copy(.7f)
                )
            }
        })
}

@Composable
fun BottomSheetEndRound(viewModel: TicGameViewModel, onClose: () -> Unit) {
    Surface(
        shape = CutCornerShape(topEndPercent = 25),
        contentColor = dark,
        border = BorderStroke(2.dp, Color.White.copy(.4f)),
    ) {
        Column(Modifier.padding(16.dp)) {

            viewModel.playerWinner.value?.let { playerWinner ->

                val isEndGame = playerWinner.score >= ceil(viewModel.rounds.value / 2f)

                Row(verticalAlignment = Alignment.CenterVertically) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(.3f)
                            .padding(top = 15.dp)
                    ) {
                        Icon(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f),
                            painter = painterResource(playerWinner.avatar),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                        Icon(
                            modifier = Modifier
                                .fillMaxWidth(.45f)
                                .aspectRatio(1f)
                                .align(TopCenter)
                                .offset(y = (-25).dp),
                            painter = painterResource(R.drawable.ic_crown),
                            contentDescription = null,
                            tint = Color.Unspecified
                        )
                    }

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp),
                        text = stringResource(if (isEndGame) R.string.player_win else R.string.player_win_round, playerWinner.name),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }

                MyButton(
                    modifier = Modifier
                        .align(End)
                        .defaultMinSize(minHeight = 50.dp),
                    text = stringResource(if (isEndGame) R.string.close_action else R.string.next_round_action).uppercase(),
                    color = greenLight,
                    onClick = if (isEndGame) onClose else viewModel::startNextRound
                )
            }
        }
    }
}

@Composable
fun Board(viewModel: TicGameViewModel, boardSize: Int, iconType : Int, onSizeNoSelected: () -> Unit = {}) {
    val audio = MediaPlayer.create(LocalContext.current, R.raw.sound_finish)
    val audioWrong = MediaPlayer.create(LocalContext.current, R.raw.bip)

    LazyVerticalGrid(
        modifier = Modifier.padding(horizontal = 42.dp, vertical = 20.dp),
        columns = GridCells.Fixed(boardSize),
        content = {
            itemsIndexed(viewModel.gamePosition) { index, position ->
                GameCell(position, boardSize, iconType) {
                    when (viewModel.setCellValue(index)) {
                        CellResult.END_GAME -> audio.playAudio()
                        CellResult.ERROR -> audioWrong.playAudio()
                        CellResult.SIZE_NOT_SELECTED -> onSizeNoSelected()
                        else -> {}
                    }
                }
            }
        }
    )
}

@Composable
fun BottomBar(viewModel: TicGameViewModel) {
    Box(
        Modifier
            .fillMaxHeight(0.25f)
            .fillMaxWidth()
    ) {

        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxHeight(0.6f)
        ) {

            Text(
                modifier = Modifier
                    .align(CenterHorizontally)
                    .padding(bottom = 5.dp),
                text = stringResource(R.string.score, viewModel.player1.value.score, viewModel.player2.value.score),
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )

            Row {
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .offset(-(4).dp, (4).dp)
                        .border(2.dp, Color.White.copy(.4f), CutCornerShape(topEndPercent = 35))
                        .shadow(6.dp, CutCornerShape(topEndPercent = 35))
                        .background(dark, CutCornerShape(topEndPercent = 35))
                ) {
                    PLayerName(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        icon = R.drawable.img_x,
                        name = viewModel.player1.value.name
                    )
                }

                Spacer(Modifier.width(8.dp))

                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .offset((4).dp, (4).dp)
                        .border(2.dp, Color.White.copy(.4f), CutCornerShape(topStartPercent = 35))
                        .shadow(6.dp, CutCornerShape(topStartPercent = 35))
                        .background(dark, CutCornerShape(topStartPercent = 35))
                ) {
                    PLayerName(
                        modifier = Modifier.align(Alignment.CenterStart),
                        icon = R.drawable.img_o,
                        name = viewModel.player2.value.name
                    )
                }
            }
        }

        Row(
            Modifier
                .align(TopCenter)
                .fillMaxHeight(0.9f)
        ) {
            BottomBarAvatar(viewModel.player1.value.avatar, viewModel.isPLayer1Turn.value)

            Spacer(Modifier.weight(1f))

            BottomBarAvatar(viewModel.player2.value.avatar, !viewModel.isPLayer1Turn.value)
        }
    }
}

@Composable
private fun BottomBarAvatar(avatar: Int, isPlayerTurn: Boolean) {
    Column(horizontalAlignment = CenterHorizontally) {

        val translation by rememberInfiniteTransition().animateValue(
            initialValue = 10.dp,
            targetValue = (-10).dp,
            typeConverter = Dp.VectorConverter,
            animationSpec = infiniteRepeatable(
                animation = tween(450, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        Icon(
            modifier = Modifier
                .align(CenterHorizontally)
                .offset(y = translation),
            painter = painterResource(R.drawable.ic_arrow_drop),
            contentDescription = null,
            tint = if (isPlayerTurn) Color.White else Color.Transparent
        )

        Icon(
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f),
            painter = painterResource(avatar),
            contentDescription = null,
            tint = Color.Unspecified
        )
    }
}

@Composable
private fun PLayerName(modifier: Modifier, @DrawableRes icon: Int, name: String) {
    Column(
        modifier = modifier
            .fillMaxWidth(0.5f)
            .padding(horizontal = 10.dp),
        horizontalAlignment = CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(18.dp),
            painter = painterResource(icon),
            contentDescription = null,
        )
        Text(
            modifier = Modifier.padding(top = 5.dp),
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun GameCell(position: CellModel, boardSize: Int, iconType : Int, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .clickableSingle { onClick() }
    ) {

        position.isX?.let { isX ->
            Image(
                modifier = Modifier
                    .align(Center)
                    .fillMaxSize(if (position.size == null) .6f else (.2f * position.size) + .1f),
                painter = painterResource(iconType.getIcon(isX)),
                contentDescription = null,
            )
        }

        position.direction?.let {
            Canvas(Modifier.fillMaxSize()) {
                drawLine(
                    start = it.getStartOffset(size.width, position.paddingStart),
                    end = it.getEndOffset(size.width, position.paddingEnd),
                    color = Color.White,
                    strokeWidth = 20f - boardSize,
                    alpha = .8f
                )
            }
        }

        if (position.showBarBottom)
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(Color.White.copy(.5f))
                    .align(Alignment.BottomCenter)
            )

        if (position.showBarLeft)
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(2.dp)
                    .background(Color.White.copy(.5f))
                    .align(Alignment.BottomEnd)
            )
    }
}