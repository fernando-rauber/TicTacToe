package uk.fernando.tictactoe.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import uk.fernando.tictactoe.R
import uk.fernando.tictactoe.navigation.Directions
import uk.fernando.tictactoe.theme.greenLight
import uk.fernando.util.component.MyButton
import uk.fernando.util.ext.safeNav

@Composable
fun HomePage(navController: NavController = NavController(LocalContext.current)) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = stringResource(R.string.home_subtext),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        MyButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp, top = 50.dp)
                .defaultMinSize(minHeight = 50.dp),
            text = stringResource(R.string.classic_action).uppercase(),
            color = greenLight,
            onClick = { navController.safeNav(Directions.createGame.withArgs("1")) }
        )

        MyButton(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 50.dp),
            text = stringResource(R.string.eat_action).uppercase(),
            color = greenLight,
            onClick = { navController.safeNav(Directions.createGame.withArgs("2")) }
        )
    }
}