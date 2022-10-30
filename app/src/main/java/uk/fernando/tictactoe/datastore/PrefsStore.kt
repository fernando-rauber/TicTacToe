package uk.fernando.tictactoe.datastore

import kotlinx.coroutines.flow.Flow

interface PrefsStore {

    fun isSoundEnabled(): Flow<Boolean>
    fun showTutorial(): Flow<Boolean>

    suspend fun storeSound(enabled: Boolean)
    suspend fun storeTutorialStatus(show: Boolean)
}