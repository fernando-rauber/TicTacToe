package uk.fernando.tictactoe.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import org.koin.androidx.compose.inject
import uk.fernando.tictactoe.datastore.PrefsStore

@Composable
fun SettingsPage(
    navController: NavController = NavController(LocalContext.current),
) {
    val context = LocalContext.current
    val prefs: PrefsStore by inject()
    val isSoundEnable = prefs.isSoundEnabled().collectAsState(initial = true)
    val isPremium = prefs.isPremium().collectAsState(initial = false)

    Box {
        Column(Modifier.fillMaxSize()) {

        }
    }
}