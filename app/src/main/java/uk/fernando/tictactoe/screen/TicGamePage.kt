package uk.fernando.tictactoe.screen

import android.media.MediaPlayer
import android.util.Log
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
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.inject
import uk.fernando.advertising.AdInterstitial
import uk.fernando.tictactoe.R
import uk.fernando.tictactoe.activity.MainActivity
import uk.fernando.tictactoe.component.MyAvatarWithCrown
import uk.fernando.tictactoe.component.NavigationTopBar
import uk.fernando.tictactoe.component.WinConditionIcon
import uk.fernando.tictactoe.datastore.PrefsStore
import uk.fernando.tictactoe.enum.CellResult
import uk.fernando.tictactoe.enum.GameIcon
import uk.fernando.tictactoe.ext.getEndOffset
import uk.fernando.tictactoe.ext.getIcon
import uk.fernando.tictactoe.ext.getStartOffset
import uk.fernando.tictactoe.model.CellModel
import uk.fernando.tictactoe.model.Player
import uk.fernando.tictactoe.theme.dark
import uk.fernando.tictactoe.theme.greenLight
import uk.fernando.tictactoe.util.GameResult
import uk.fernando.tictactoe.viewmodel.TicGameViewModel
import uk.fernando.uikit.component.MyButton
import uk.fernando.uikit.ext.clickableSingle
import uk.fernando.uikit.ext.playAudio
import kotlin.math.ceil

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TicGamePage(
    navController: NavController = NavController(LocalContext.current),
    boardSize: Int,
    winCondition: Int,
    viewModel: TicGameViewModel = getViewModel()
) {
    LaunchedEffect(Unit) { viewModel.init() }

    val fullScreenAd = AdInterstitial(LocalContext.current as MainActivity, stringResource(R.string.ad_interstitial_end_game))

    val sheetState =
        rememberModalBottomSheetState(initialValue = if (viewModel.roundResult.value != null) ModalBottomSheetValue.Expanded else ModalBottomSheetValue.Hidden,
            confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded }
        )

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            BottomSheetEndRound(
                viewModel = viewModel,
                onClose = {
                    fullScreenAd.showAdvert()
                    navController.popBackStack()
                }
            )
        },
        sheetBackgroundColor = Color.Transparent,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(Modifier.fillMaxSize()) {

            GameTopBar(
                viewModel = viewModel,
                onClose = {
                    fullScreenAd.showAdvert()
                    navController.popBackStack()
                }
            )

            Column(
                Modifier.weight(1f),
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                WinConditionIcon(winCondition, GameIcon.CLASSIC.value)

                Board(
                    modifier = Modifier.padding(vertical = 30.dp),
                    viewModel = viewModel,
                    boardSize = boardSize,
                    gameIcon = GameIcon.CLASSIC.value
                )
            }

            BottomBar(viewModel, GameIcon.CLASSIC.value)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameTopBar(viewModel: TicGameViewModel, onClose: () -> Unit) {
    NavigationTopBar(
        title = stringResource(R.string.current_round, viewModel.currentRound.intValue, viewModel.rounds.intValue),
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
        }
    )
}

@Composable
fun BottomSheetEndRound(viewModel: TicGameViewModel, onClose: () -> Unit) {
    Surface(
        shape = CutCornerShape(topEndPercent = 25),
        contentColor = dark,
        border = BorderStroke(2.dp, Color.White.copy(.4f)),
    ) {
        Column(Modifier.padding(16.dp)) {

            viewModel.roundResult.value?.let { roundResult ->

                var isDraw = false
                var playerWinner = Player(1, "")

                when (roundResult) {
                    is GameResult.Winner -> playerWinner = roundResult.result
                    is GameResult.Draw -> {
                        playerWinner = viewModel.player1.value
                        isDraw = true
                    }

                    else -> {}
                }


                var isEndGame = playerWinner.score >= ceil(viewModel.rounds.intValue / 2f)
                if (!isEndGame && viewModel.currentRound.intValue == viewModel.rounds.intValue) {
                    isEndGame = true
                }

                Row(verticalAlignment = Alignment.CenterVertically) {

                    MyAvatarWithCrown(playerWinner, !isDraw)

                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 30.dp),
                        text = stringResource(if (isDraw) R.string.draw else if (isEndGame) R.string.player_win else R.string.player_win_round, playerWinner.name),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )

                    if (isDraw)
                        MyAvatarWithCrown(viewModel.player2.value, false)
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
fun Board(modifier: Modifier, viewModel: TicGameViewModel, boardSize: Int, gameIcon: Int, onSizeNoSelected: (() -> Unit)? = null) {
    val prefs: PrefsStore by inject()
    val isSoundEnable = prefs.isSoundEnabled().collectAsState(initial = true)
    val audio = MediaPlayer.create(LocalContext.current, R.raw.sound_finish)
    val audioWrong = MediaPlayer.create(LocalContext.current, R.raw.bip)
    val audioClick = MediaPlayer.create(LocalContext.current, R.raw.click)
    val audioDraw = MediaPlayer.create(LocalContext.current, R.raw.sound_draw)
    val coroutine = rememberCoroutineScope()

    LazyVerticalGrid(
        modifier = modifier.fillMaxWidth(.8f),
        columns = GridCells.Fixed(boardSize),
        content = {
            itemsIndexed(viewModel.cellList) { index, position ->
                GameCell(position, boardSize, gameIcon) {
                    coroutine.launch {
                        if (viewModel.isAiOn && onSizeNoSelected == null)
                            audioClick.playAudio(isSoundEnable.value)
                        when (viewModel.setCellValue(index)) {
                            CellResult.END_GAME -> audio.playAudio(isSoundEnable.value)
                            CellResult.ERROR -> audioWrong.playAudio(isSoundEnable.value)
                            CellResult.DRAW -> audioDraw.playAudio(isSoundEnable.value)
                            CellResult.SIZE_NOT_SELECTED -> onSizeNoSelected?.invoke()
                            CellResult.DO_NOTHING -> audioClick.playAudio(isSoundEnable.value)
                            else -> {}
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun BottomBar(viewModel: TicGameViewModel, gameIcon: Int) {
    Box(
        Modifier
            .fillMaxHeight(0.2f)
            .fillMaxWidth()
    ) {

        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxHeight(0.8f)
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
                        icon = gameIcon.getIcon(true),
                        name = stringResource(id = R.string.you)
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
                        icon = gameIcon.getIcon(false),
                        name = viewModel.player2.value.name,
                        isLeft = true
                    )
                }
            }
        }

        Row(
            Modifier
                .align(TopCenter)
                .fillMaxHeight(0.8f)
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

        val translation by rememberInfiniteTransition(label = "").animateValue(
            initialValue = 10.dp,
            targetValue = (-10).dp,
            typeConverter = Dp.VectorConverter,
            animationSpec = infiniteRepeatable(
                animation = tween(350, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ), label = ""
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
private fun PLayerName(modifier: Modifier, @DrawableRes icon: Int, name: String, isLeft: Boolean = false) {
    Row(
        modifier = modifier
            .fillMaxWidth(0.7f)
            .padding(horizontal = 10.dp)
            .padding(top = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (isLeft)
            Image(
                modifier = Modifier.size(24.dp),
                painter = painterResource(icon),
                contentDescription = null,
            )
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 3.dp),
            text = name,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        if (!isLeft)
            Image(
                modifier = Modifier.size(24.dp),
                painter = painterResource(icon),
                contentDescription = null,
            )
    }
}

@Composable
private fun GameCell(position: CellModel, boardSize: Int, gameIcon: Int, onClick: () -> Unit) {
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
                painter = painterResource(gameIcon.getIcon(isX)),
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