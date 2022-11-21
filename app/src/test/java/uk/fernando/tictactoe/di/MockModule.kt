package uk.fernando.tictactoe.di

import android.app.Application
import android.content.Context
import io.mockk.mockk
import org.koin.dsl.module
import uk.fernando.tictactoe.datastore.*
import uk.fernando.tictactoe.di.KoinModule.allModules

val mockModule = module {
    single { mockk<Application>() }
    single { mockk<Context>() }
    single<PrefsStore> { PrefsStoreMock() }
    single { KoinModule.getAndroidLogger() }
}

val mockSinglePLayer = module {
    single<GamePrefsStore> { GameSinglePrefsMock() }
}

val mockMultiplayerPLayer = module {
    single<GamePrefsStore> { GameMultiplayerPrefsMock() }
}

fun allMockedModules() = allModules() + mockModule

fun singlePLayerMockedModules() = allMockedModules() + mockSinglePLayer
fun multiplayerMockedModules() = allMockedModules() + mockMultiplayerPLayer