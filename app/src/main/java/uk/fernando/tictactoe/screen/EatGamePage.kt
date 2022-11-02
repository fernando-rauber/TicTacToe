package uk.fernando.tictactoe.screen

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
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
import org.koin.androidx.compose.inject
import uk.fernando.tictactoe.R
import uk.fernando.tictactoe.component.MyDivider
import uk.fernando.tictactoe.component.WinConditionIcon
import uk.fernando.tictactoe.datastore.PrefsStore
import uk.fernando.tictactoe.ext.getIcon
import uk.fernando.tictactoe.model.SizeModel
import uk.fernando.tictactoe.theme.gold
import uk.fernando.tictactoe.viewmodel.EatGameViewModel
import uk.fernando.util.component.MyAnimatedVisibility
import uk.fernando.util.component.MyDialog
import uk.fernando.util.component.MyIconButton
import uk.fernando.util.ext.clickableSingle

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EatGamePage(
    navController: NavController = NavController(LocalContext.current),
    boardSize: Int,
    iconType: Int,
    winCondition: Int,
    viewModel: EatGameViewModel = getViewModel()
) {

    val prefs: PrefsStore by inject()
    val showTutorial = prefs.showTutorial().collectAsState(initial = false)

    val (highlightSize, setHighlightSize) = remember { mutableStateOf(false) }

    ModalBottomSheetLayout(
        sheetState = rememberModalBottomSheetState(
            initialValue = if (viewModel.playerWinner.value != null) ModalBottomSheetValue.Expanded else ModalBottomSheetValue.Hidden,
            confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded }
        ),
        sheetContent = {
            BottomSheetEndRound(
                viewModel = viewModel,
                onClose = { navController.popBackStack() }
            )
        },
        sheetBackgroundColor = Color.Transparent,
        modifier = Modifier.fillMaxSize()
    ) {
        Box {
            Column(Modifier.fillMaxSize()) {

                GameTopBar(
                    gameType = 2,
                    onClose = { navController.popBackStack() }
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 30.dp),
                    horizontalAlignment = CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    WinConditionIcon(winCondition, iconType)

                    Board(
                        viewModel = viewModel,
                        boardSize = boardSize,
                        iconType = iconType,
                        onSizeNoSelected = { setHighlightSize(true) }
                    )

                    ItemSizeCount(
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 10.dp),
                        viewModel = viewModel,
                        iconType =  iconType,
                        highlight = highlightSize,
                        onSelected = {
                            setHighlightSize(false)
                            viewModel.setImageSize(it)
                        }
                    )

                    Text(
                        text = stringResource(R.string.current_round, viewModel.currentRound.value, viewModel.rounds.value),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                BottomBar(viewModel)
            }

            MyAnimatedVisibility(showTutorial.value) {
                MyTutorialDialog(
                    iconType = iconType,
                    onClose = viewModel::closeTutorial
                )
            }
        }
    }
}

@Composable
private fun ItemSizeCount(modifier: Modifier, viewModel: EatGameViewModel, iconType: Int, highlight: Boolean, onSelected: (Int) -> Unit) {
    val isPlayer1 = viewModel.isPLayer1Turn.value

    Row(modifier.fillMaxWidth()) {
        Column(Modifier.weight(1f)) {
            SizeColumn(
                icon = iconType.getIcon(true) ,
                sizeCounter = viewModel.playerRed.value,
                sizeSelected = if (isPlayer1) viewModel.imageSize.value ?: 0 else 0,
                highlight = isPlayer1 && highlight,
                onSelected = onSelected
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End
        ) {
            SizeColumn(
                icon = iconType.getIcon(false),
                sizeCounter = viewModel.playerGreen.value,
                sizeSelected = if (!isPlayer1) viewModel.imageSize.value ?: 0 else 0,
                highlight = !isPlayer1 && highlight,
                isLeftSide = true,
                onSelected = onSelected
            )
        }
    }
}

@Composable
private fun SizeColumn(@DrawableRes icon: Int, sizeCounter: SizeModel, sizeSelected: Int, highlight: Boolean, isLeftSide: Boolean = false, onSelected: (Int) -> Unit) {
    val alpha: Float by animateFloatAsState(
        targetValue = if (highlight) 1f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800,  easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier.border(2.dp, gold.copy(alpha)),
        horizontalAlignment = if (isLeftSide) Alignment.End else Alignment.Start
    ) {
        SizeOption(icon, sizeSelected == 1, 1f, sizeCounter.size1, isLeftSide) {
            onSelected(1)
        }
        SizeOption(icon, sizeSelected == 2, 1.3f, sizeCounter.size2, isLeftSide) {
            onSelected(2)
        }
        SizeOption(icon, sizeSelected == 3, 1.6f, sizeCounter.size3, isLeftSide) {
            onSelected(3)
        }
    }
}

@Composable
private fun ColumnScope.SizeOption(@DrawableRes image: Int, isSelected: Boolean, weight: Float, quantity: Int, isEnd: Boolean = false, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .weight(weight)
            .padding(top = 4.dp)
            .clickableSingle(ripple = false) { if (quantity > 0) onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isEnd)
            Box(Modifier.fillMaxWidth(.3f)) {
                Image(
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Center)
                        .aspectRatio(1f),
                    painter = painterResource(image),
                    contentDescription = null,
                )
            }

//        if(isEnd && isSelected){
//            Image(
//                painter = painterResource(R.drawable.ic_arrow_drop),
//                contentDescription = null,
//            )
//        }

        Text(
            text = if (quantity == 99) "\u221e" else if (isEnd) "$quantity X" else "X $quantity",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) gold else Color.White,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

//        if(!isEnd && isSelected){
//            Image(
//                painter = painterResource(R.drawable.ic_arrow_drop),
//                contentDescription = null,
//            )
//        }

        if (isEnd)
            Box(Modifier.fillMaxWidth(.3f)) {
                Image(
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Center)
                        .aspectRatio(1f),
                    painter = painterResource(image),
                    contentDescription = null,
                )
            }
    }
}

@Composable
private fun MyTutorialDialog( iconType:Int, onClose: () -> Unit) {
    MyDialog {
        Box(Modifier.border(2.dp, Color.White.copy(.3f), MaterialTheme.shapes.small)) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = CenterHorizontally
            ) {

                Text(
                    text = stringResource(id = R.string.tutorial_title),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Text(
                    modifier = Modifier.padding(bottom = 12.dp, top = 32.dp),
                    text = stringResource(id = R.string.tutorial_1),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                val icon = painterResource(iconType.getIcon(true))

                Row(verticalAlignment = Alignment.Bottom) {
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = icon,
                        contentDescription = null
                    )
                    Image(
                        modifier = Modifier.size(36.dp),
                        painter = icon,
                        contentDescription = null
                    )
                    Image(
                        modifier = Modifier.size(48.dp),
                        painter = icon,
                        contentDescription = null
                    )
                }

                MyDivider(Modifier.padding(vertical = 16.dp))

                Text(
                    text = stringResource(id = R.string.tutorial_2),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Column {

                    val offset by rememberInfiniteTransition().animateValue(
                        initialValue = 42.dp,
                        targetValue = (-32).dp,
                        typeConverter = Dp.VectorConverter,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, 1500, easing = LinearOutSlowInEasing),
                            repeatMode = RepeatMode.Restart
                        )
                    )

                    Row(
                        modifier = Modifier.padding(top = 12.dp, start = 6.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Image(
                            modifier = Modifier.size(24.dp),
                            painter = icon,
                            contentDescription = null
                        )
                        Image(
                            modifier = Modifier
                                .size(36.dp)
                                .offset(offset),
                            painter = icon,
                            contentDescription = null
                        )
                    }

                    val offset2 by rememberInfiniteTransition().animateValue(
                        initialValue = 30.dp,
                        targetValue = (-44).dp,
                        typeConverter = Dp.VectorConverter,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1_000, 1500, easing = LinearOutSlowInEasing),
                            repeatMode = RepeatMode.Restart
                        )
                    )

                    Row(
                        modifier = Modifier.padding(top = 5.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Image(
                            modifier = Modifier.size(36.dp),
                            painter = icon,
                            contentDescription = null
                        )
                        Image(
                            modifier = Modifier
                                .size(48.dp)
                                .offset(offset2),
                            painter = icon,
                            contentDescription = null
                        )
                    }
                }

                MyDivider(Modifier.padding(vertical = 16.dp))

                Text(
                    modifier = Modifier.padding(bottom = 12.dp),
                    text = stringResource(id = R.string.tutorial_3),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier.width(IntrinsicSize.Min),
                    contentAlignment = Center
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier.size(48.dp),
                            painter = icon,
                            contentDescription = null
                        )
                        Image(
                            modifier = Modifier.size(24.dp),
                            painter = icon,
                            contentDescription = null
                        )
                        Image(
                            modifier = Modifier.size(36.dp),
                            painter = icon,
                            contentDescription = null
                        )
                    }
                    Divider(
                        thickness = 3.dp,
                        color = Color.White.copy(0.8f)
                    )
                }

            }
            MyIconButton(
                icon = R.drawable.ic_close,
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = onClose,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}