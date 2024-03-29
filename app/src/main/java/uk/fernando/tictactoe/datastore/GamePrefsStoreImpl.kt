package uk.fernando.tictactoe.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val STORE_NAME = "tictactoe_game_data_store"

val Context.gameDataStore: DataStore<Preferences> by preferencesDataStore(STORE_NAME)

class GamePrefsStoreImpl(context: Context) : GamePrefsStore {

    private val dataStore = context.gameDataStore

    override suspend fun getBoardSize(): Int {
        return dataStore.data.map { prefs -> prefs[PreferencesKeys.BOARD_SIZE] ?: 5 }.first()
    }

    override suspend fun getWinCondition(): Int {
        return dataStore.data.map { prefs -> prefs[PreferencesKeys.WIN_CONDITION] ?: 4 }.first()
    }

    override suspend fun getRounds(): Int {
        return dataStore.data.map { prefs -> prefs[PreferencesKeys.ROUNDS] ?: 3 }.first()
    }

    override suspend fun getGameType(): Int {
        return dataStore.data.map { prefs -> prefs[PreferencesKeys.GAME_TYPE] ?: 1 }.first()
    }

    override suspend fun getIconType(): Int {
        return dataStore.data.map { prefs -> prefs[PreferencesKeys.ICON_TYPE] ?: 2 }.first()
    }

    override suspend fun getPLayer2Name(): String {
        return dataStore.data.map { prefs -> prefs[PreferencesKeys.PLAYER2_NAME] ?: "Player 2" }.first()
    }

    override suspend fun storeBoardSize(value: Int) {
        dataStore.edit { prefs -> prefs[PreferencesKeys.BOARD_SIZE] = value }
    }

    override suspend fun storeWinCondition(value: Int) {
        dataStore.edit { prefs -> prefs[PreferencesKeys.WIN_CONDITION] = value }
    }

    override suspend fun storeRounds(value: Int) {
        dataStore.edit { prefs -> prefs[PreferencesKeys.ROUNDS] = value }
    }

    override suspend fun storeGameType(value: Int) {
        dataStore.edit { prefs -> prefs[PreferencesKeys.GAME_TYPE] = value }
    }

    override suspend fun storeIconType(value: Int) {
        dataStore.edit { prefs -> prefs[PreferencesKeys.ICON_TYPE] = value }
    }

    override suspend fun storePLayer2Name(value: String) {
        dataStore.edit { prefs -> prefs[PreferencesKeys.PLAYER2_NAME] = value }
    }

    private object PreferencesKeys {
        val BOARD_SIZE = intPreferencesKey("board_size")
        val WIN_CONDITION = intPreferencesKey("win_condition")
        val ROUNDS = intPreferencesKey("rounds")
        val GAME_TYPE = intPreferencesKey("game_type")
        val ICON_TYPE = intPreferencesKey("icon_type")
        val PLAYER2_NAME = stringPreferencesKey("player2_name")
    }
}