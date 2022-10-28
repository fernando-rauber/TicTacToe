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
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.getViewModel
import uk.fernando.tictactoe.R
import uk.fernando.tictactoe.component.WinConditionIcon
import uk.fernando.tictactoe.viewmodel.EatGameViewModel
import uk.fernando.util.ext.clickableSingle

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EatGamePage(
    navController: NavController = NavController(LocalContext.current),
    boardSize: Int,
    winCondition: Int,
    viewModel: EatGameViewModel = getViewModel()
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

            GameTopBar(onClose = { navController.popBackStack() })

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 30.dp),
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                WinConditionIcon(winCondition)

                Board(viewModel, boardSize)

                Text(
                    text = stringResource(R.string.current_round, viewModel.currentRound.value, viewModel.rounds.value),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )

                DollCount(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 10.dp),
                    viewModel = viewModel
                )
            }

            BottomBar(viewModel)
        }
    }
}

@Composable
private fun DollCount(modifier: Modifier, viewModel: EatGameViewModel) {
    Row(modifier.fillMaxWidth()) {
        Column(Modifier.weight(1f)) {
            val playerRed = viewModel.playerRed.value
            Doll(R.drawable.eat_red, .9f, 99) { viewModel.setImageSize(1) }
            Doll(R.drawable.eat_red, 1.2f, playerRed.size2) { viewModel.setImageSize(2) }
            Doll(R.drawable.eat_red, 1.7f, playerRed.size3) { viewModel.setImageSize(3) }
        }
        Column(
            Modifier.weight(1f),
            horizontalAlignment = Alignment.End
        ) {
            val playerGreen = viewModel.playerGreen.value
            Doll(R.drawable.eat_green, 1f, 99, true) { viewModel.setImageSize(1) }
            Doll(R.drawable.eat_green, 1.3f, playerGreen.size2, true) { viewModel.setImageSize(2) }
            Doll(R.drawable.eat_green, 1.6f, playerGreen.size3, true) { viewModel.setImageSize(3) }
        }
    }
}

@Composable
private fun ColumnScope.Doll(@DrawableRes image: Int, weight: Float, quantity: Int, isEnd: Boolean = false, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .weight(weight)
            .padding(top = 4.dp)
            .clickableSingle { onClick() },
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

        Text(
            text =if(quantity == 99) "\u221e" else if (isEnd) "$quantity X" else "X $quantity",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

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