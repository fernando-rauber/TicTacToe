package uk.fernando.tictactoe.datastore

interface GamePrefsStore {

    suspend fun getBoardSize(): Int
    suspend fun getWinCondition(): Int
    suspend fun getRounds(): Int
    suspend fun getGameType(): Int
    suspend fun getDifficulty(): Int

    suspend fun storeBoardSize(value: Int)
    suspend fun storeWinCondition(value: Int)
    suspend fun storeRounds(value: Int)
    suspend fun storeGameType(value: Int)
    suspend fun storeDifficulty(value: Int)
}