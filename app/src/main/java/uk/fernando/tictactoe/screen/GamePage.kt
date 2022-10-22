package uk.fernando.tictactoe.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.getViewModel
import uk.fernando.tictactoe.R
import uk.fernando.tictactoe.component.NavigationTopBar
import uk.fernando.tictactoe.component.WinConditionIcon
import uk.fernando.tictactoe.ext.getRotation
import uk.fernando.tictactoe.model.CellModel
import uk.fernando.tictactoe.theme.dark
import uk.fernando.tictactoe.viewmodel.GameViewModel
import uk.fernando.util.component.MyAnimatedVisibility
import uk.fernando.util.ext.clickableSingle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamePage(
    navController: NavController = NavController(LocalContext.current),
    viewModel: GameViewModel = getViewModel()
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

@Composable
private fun Board(viewModel: GameViewModel) {
    LazyVerticalGrid(
        modifier = Modifier.padding(horizontal = 42.dp, vertical = 20.dp),
        columns = GridCells.Fixed(viewModel.boardSize.value ?: 3),
        content = {
            itemsIndexed(viewModel.gamePosition) { index, position ->
                GameCard(position) {
                    viewModel.onPositionClick(index)
                }
            }
        }
    )
}

@Composable
private fun BottomBar(viewModel: GameViewModel) {
    Box(
        Modifier
            .fillMaxHeight(0.20f)
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
                text = stringResource(R.string.score, 1, 0),
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
                        name = "YOU"
                    )
                }

                Spacer(Modifier.width(8.dp))

                Card() {

                }
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(dark, RoundedCornerShape(topStartPercent = 50))
                ) {

                    PLayerName(
                        modifier = Modifier.align(Alignment.CenterStart),
                        icon = R.drawable.img_o,
                        name = "AI"
                    )
                }
            }
        }

        Row(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxHeight(0.8f)
        ) {

            Icon(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f),
                painter = painterResource(R.drawable.ic_deer),
                contentDescription = null,
                tint = Color.Unspecified
            )

            Spacer(Modifier.weight(1f))

            Icon(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f),
                painter = painterResource(R.drawable.ic_unicorn),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
    }
}

@Composable
private fun PLayerName(modifier: Modifier, @DrawableRes icon: Int, name: String) {
    Column(
        modifier = modifier.fillMaxWidth(0.5f),
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
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun GameCard(position: CellModel, onClick: () -> Unit) {
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
            Box(
                Modifier
                    .rotate(it.getRotation())
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(Color.White.copy(.5f))
                    .align(Center)
            )
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