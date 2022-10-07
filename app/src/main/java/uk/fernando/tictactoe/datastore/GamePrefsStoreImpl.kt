package uk.fernando.tictactoe.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val STORE_NAME = "tictactoe_game_data_store"

val Context.gameDataStore: DataStore<Preferences> by preferencesDataStore(STORE_NAME)

class GamePrefsStoreImpl(context: Context) : GamePrefsStore {

    private val dataStore = context.gameDataStore

    override fun getBoardSize(): Flow<Int> {
        return dataStore.data.map { prefs -> prefs[PreferencesKeys.BOARD_SIZE] ?: 5 }
    }

    override fun getWinCondition(): Flow<Int> {
        return dataStore.data.map { prefs -> prefs[PreferencesKeys.WIN_CONDITION] ?: 4 }
    }

    override fun getRounds(): Flow<Int> {
        return dataStore.data.map { prefs -> prefs[PreferencesKeys.ROUNDS] ?: 3 }
    }

    override fun getGameType(): Flow<Int> {
        return dataStore.data.map { prefs -> prefs[PreferencesKeys.GAME_TYPE] ?: 1 }
    }

    override fun getDifficulty(): Flow<Int> {
        return dataStore.data.map { prefs -> prefs[PreferencesKeys.DIFFICULTY] ?: 1 }
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

    override suspend fun storeDifficulty(value: Int) {
        dataStore.edit { prefs -> prefs[PreferencesKeys.DIFFICULTY] = value }
    }

    private object PreferencesKeys {
        val BOARD_SIZE = intPreferencesKey("board_size")
        val WIN_CONDITION = intPreferencesKey("win_condition")
        val ROUNDS = intPreferencesKey("rounds")
        val GAME_TYPE = intPreferencesKey("game_type")
        val DIFFICULTY = intPreferencesKey("difficulty")
    }
}