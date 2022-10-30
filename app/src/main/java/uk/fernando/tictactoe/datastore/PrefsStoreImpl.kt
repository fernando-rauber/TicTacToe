package uk.fernando.tictactoe.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val STORE_NAME = "tictactoe_data_store"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(STORE_NAME)

class PrefsStoreImpl(context: Context) : PrefsStore {

    private val dataStore = context.dataStore

    override fun isSoundEnabled(): Flow<Boolean> {
        return dataStore.data.map { prefs -> prefs[PreferencesKeys.SOUND_ENABLED] ?: true }
    }

    override fun showTutorial(): Flow<Boolean> {
        return dataStore.data.map { prefs -> prefs[PreferencesKeys.TUTORIAL] ?: true }
    }

    override suspend fun storeSound(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[PreferencesKeys.SOUND_ENABLED] = enabled }
    }

    override suspend fun storeTutorialStatus(show: Boolean) {
        dataStore.edit { prefs -> prefs[PreferencesKeys.TUTORIAL] = show }
    }

    private object PreferencesKeys {
        val TUTORIAL = booleanPreferencesKey("show_tutorial")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
    }
}