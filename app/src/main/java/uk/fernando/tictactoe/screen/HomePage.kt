package uk.fernando.tictactoe.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import org.koin.androidx.compose.getViewModel
import uk.fernando.tictactoe.R
import uk.fernando.tictactoe.component.NavigationTopBar
import uk.fernando.tictactoe.viewmodel.HomeViewModel
import uk.fernando.util.component.MyIconButton

@Composable
fun HomePage(
    navController: NavController = NavController(LocalContext.current),
    viewModel: HomeViewModel = getViewModel()
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {

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
    }
}

