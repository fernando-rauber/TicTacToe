package uk.fernando.tictactoe.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.androidx.compose.getViewModel
import uk.fernando.tictactoe.R
import uk.fernando.tictactoe.component.NavigationTopBar
import uk.fernando.tictactoe.model.CardModel
import uk.fernando.tictactoe.viewmodel.GameViewModel

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

        //Board(viewModel)
    }
}

@Composable
private fun Board(viewModel: GameViewModel) {
    LazyVerticalGrid(
        modifier = Modifier.padding(46.dp),
        columns = GridCells.Fixed(viewModel.boardSize.value),
        content = {
            items(viewModel.gamePosition) { position ->
                GameCard(position)
            }
        }
    )
}

@Composable
private fun GameCard(position: CardModel) {
    Box(
        Modifier
            .fillMaxSize()
            .aspectRatio(1f)
    ) {


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