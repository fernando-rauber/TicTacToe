package uk.fernando.tictactoe.datastore

interface GamePrefsStore {

    suspend fun getBoardSize(): Int
    suspend fun getWinCondition(): Int
    suspend fun getRounds(): Int
    suspend fun getGameType(): Int
    suspend fun getIconType(): Int
    suspend fun getDifficulty(): Int
    suspend fun getPLayer2Name(): String

    suspend fun storeBoardSize(value: Int)
    suspend fun storeWinCondition(value: Int)
    suspend fun storeRounds(value: Int)
    suspend fun storeGameType(value: Int)
    suspend fun storeIconType(value: Int)
    suspend fun storeDifficulty(value: Int)
    suspend fun storePLayer2Name(value: String)
}