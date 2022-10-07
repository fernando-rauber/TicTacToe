package uk.fernando.tictactoe.datastore

import kotlinx.coroutines.flow.Flow

interface PrefsStore {

    suspend fun getVersion(): Int
    fun isPremium(): Flow<Boolean>
    fun isSoundEnabled(): Flow<Boolean>
    fun getStarCount(): Flow<Int>

    suspend fun storeVersion(value: Int)
    suspend fun storePremium(value: Boolean)
    suspend fun storeSound(enabled: Boolean)
    suspend fun storeStar(value: Int)
}