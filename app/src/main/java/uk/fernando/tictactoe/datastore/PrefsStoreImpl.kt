package uk.fernando.tictactoe.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private const val STORE_NAME = "tictactoe_data_store"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(STORE_NAME)

class PrefsStoreImpl(context: Context) : PrefsStore {

    private val dataStore = context.dataStore
    override suspend fun getVersion(): Int {
        return dataStore.data.map { prefs -> prefs[PreferencesKeys.VERSION] ?: 1 }.first()
    }

    override fun isPremium(): Flow<Boolean> {
        return dataStore.data.map { prefs -> prefs[PreferencesKeys.PREMIUM] ?: false }
    }

    override fun isSoundEnabled(): Flow<Boolean> {
        return dataStore.data.map { prefs -> prefs[PreferencesKeys.SOUND_ENABLED] ?: true }
    }

    override fun getStarCount(): Flow<Int> {
        return dataStore.data.map { prefs -> prefs[PreferencesKeys.STAR] ?: 0 }
    }

    override suspend fun storeVersion(value: Int) {
        dataStore.edit { prefs -> prefs[PreferencesKeys.VERSION] = value }
    }

    override suspend fun storePremium(value: Boolean) {
        dataStore.edit { prefs -> prefs[PreferencesKeys.PREMIUM] = value }
    }

    override suspend fun storeSound(enabled: Boolean) {
        dataStore.edit { prefs -> prefs[PreferencesKeys.SOUND_ENABLED] = enabled }
    }

    override suspend fun storeStar(value: Int) {
        dataStore.edit { prefs -> prefs[PreferencesKeys.STAR] = getStarCount().first() + value }
    }

    private object PreferencesKeys {
        val VERSION = intPreferencesKey("version")
        val PREMIUM = booleanPreferencesKey("premium")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val STAR = intPreferencesKey("star")
    }
}