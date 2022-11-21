package uk.fernando.tictactoe.datastore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PrefsStoreMock : PrefsStore {

    private var isSoundEnabled = true
    private var showTutorial = true

    override fun isSoundEnabled(): Flow<Boolean> {
        return flow { isSoundEnabled }
    }

    override fun showTutorial(): Flow<Boolean> {
        return flow { showTutorial }
    }

    override suspend fun storeSound(enabled: Boolean) {
        isSoundEnabled = enabled
    }

    override suspend fun storeTutorialStatus(show: Boolean) {
        showTutorial = show
    }
}