package uk.fernando.tictactoe.datastore

import kotlinx.coroutines.flow.Flow

interface GamePrefsStore {

    fun getBoardSize(): Flow<Int>
    fun getWinCondition(): Flow<Int>
    fun getRounds(): Flow<Int>
    fun getGameType(): Flow<Int>
    fun getDifficulty(): Flow<Int>

    suspend fun storeBoardSize(value: Int)
    suspend fun storeWinCondition(value: Int)
    suspend fun storeRounds(value: Int)
    suspend fun storeGameType(value: Int)
    suspend fun storeDifficulty(value: Int)
}