package uk.fernando.tictactoe.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import org.koin.androidx.compose.getViewModel
import uk.fernando.tictactoe.viewmodel.HomeViewModel

@Composable
fun HomePage(
    navController: NavController = NavController(LocalContext.current),
    viewModel: HomeViewModel = getViewModel()
) {

    Box {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {

        }
    }
}