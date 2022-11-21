package uk.fernando.tictactoe.datastore

class GamePrefsStoreMock : GamePrefsStore {

    private var boardSize = 5
    private var winCondition = 3
    private var rounds = 3
    private var gameType = 1
    private var iconType = 2
    private var playerName = "Player 2"

    override suspend fun getBoardSize(): Int {
        return boardSize
    }

    override suspend fun getWinCondition(): Int {
        return winCondition
    }

    override suspend fun getRounds(): Int {
        return rounds
    }

    override suspend fun getGameType(): Int {
        return gameType
    }

    override suspend fun getIconType(): Int {
        return iconType
    }

    override suspend fun getPLayer2Name(): String {
        return playerName
    }

    override suspend fun storeBoardSize(value: Int) {
        boardSize = value
    }

    override suspend fun storeWinCondition(value: Int) {
        winCondition = value
    }

    override suspend fun storeRounds(value: Int) {
        rounds = value
    }

    override suspend fun storeGameType(value: Int) {
        gameType = gameType
    }

    override suspend fun storeIconType(value: Int) {
        iconType = value
    }

    override suspend fun storePLayer2Name(value: String) {
        playerName = value
    }
}