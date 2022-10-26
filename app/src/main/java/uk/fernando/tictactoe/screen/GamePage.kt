package uk.fernando.tictactoe.screen

import android.media.MediaPlayer
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import uk.fernando.tictactoe.component.MyDivider
import uk.fernando.tictactoe.component.NavigationTopBar
import uk.fernando.tictactoe.component.WinConditionIcon
import uk.fernando.tictactoe.ext.getEndOffset
import uk.fernando.tictactoe.ext.getStartOffset
import uk.fernando.tictactoe.model.CellModel
import uk.fernando.tictactoe.theme.dark
import uk.fernando.tictactoe.theme.greenLight
import uk.fernando.tictactoe.viewmodel.GameViewModel
import uk.fernando.util.component.MyAnimatedVisibility
import uk.fernando.util.component.MyButton
import uk.fernando.util.ext.clickableSingle
import uk.fernando.util.ext.playAudio
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun GamePage(
    navController: NavController = NavController(LocalContext.current),
    viewModel: GameViewModel = getViewModel()
) {

    val sheetState = rememberModalBottomSheetState(
        initialValue = if (viewModel.endRoundDialog.value) ModalBottomSheetValue.Expanded else ModalBottomSheetValue.Hidden,
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

            NavigationTopBar(rightIcon = {
                Card(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 8.dp),
                    onClick = { navController.popBackStack() },
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

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Center
            ) {

                MyAnimatedVisibility(viewModel.boardSize.value != null) {
                    Column(
                        horizontalAlignment = CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        WinConditionIcon(viewModel.winCondition.value)

                        Board(viewModel)

                        Text(
                            text = stringResource(R.string.current_round, viewModel.currentRound.value, viewModel.rounds.value),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            BottomBar(viewModel)
        }
    }
}

@Composable
private fun BottomSheetEndRound(viewModel: GameViewModel, onClose: () -> Unit) {
    val isEndGame = viewModel.playerWinner.value.score >= ceil(viewModel.rounds.value / 2f)

    Surface(
        shape = RoundedCornerShape(topStartPercent = 15, topEndPercent = 15),
        contentColor = dark
    ) {
        Column(Modifier.padding(16.dp)) {

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
                        painter = painterResource(viewModel.playerWinner.value.avatar),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                    Icon(
                        modifier = Modifier
                            .fillMaxWidth(.5f)
                            .aspectRatio(1f)
                            .align(TopCenter)
                            .offset(y = (-20).dp),
                        painter = painterResource(R.drawable.ic_crown),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                }

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(if (isEndGame) R.string.player_win else R.string.player_win_round, viewModel.playerWinner.value.name),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }

            MyDivider(Modifier.padding(vertical = 16.dp))

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

@Composable
private fun Board(viewModel: GameViewModel) {
    val audio = MediaPlayer.create(LocalContext.current, R.raw.sound_finish)

    LazyVerticalGrid(
        modifier = Modifier.padding(horizontal = 42.dp, vertical = 20.dp),
        columns = GridCells.Fixed(viewModel.boardSize.value ?: 3),
        content = {
            itemsIndexed(viewModel.gamePosition) { index, position ->
                GameCell(position) {
                    val isEndRound = viewModel.onPositionClick(index)
                    if (isEndRound) audio.playAudio()
                }
            }
        }
    )
}

@Composable
private fun BottomBar(viewModel: GameViewModel) {
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
                        .shadow(4.dp, RoundedCornerShape(topEndPercent = 50))
                        .background(dark, RoundedCornerShape(topEndPercent = 50))
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
                        .background(dark, RoundedCornerShape(topStartPercent = 50))
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
                .fillMaxHeight(1f)
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
private fun GameCell(position: CellModel, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .aspectRatio(1f)
            .clickableSingle { onClick() }
    ) {


        position.image?.let { image ->
            Image(
                modifier = Modifier
                    .align(Center)
                    .fillMaxSize(.6f),
                painter = painterResource(image),
                contentDescription = null,
            )
        }

        position.direction?.let {
            Canvas(Modifier.fillMaxSize()) {
                drawLine(
                    start = it.getStartOffset(size.width, position.paddingStart),
                    end = it.getEndOffset(size.width, position.paddingEnd),
                    color = Color.White,
                    strokeWidth = 10f,
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