package uk.fernando.tictactoe.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import io.mockk.mockk
import org.koin.core.qualifier.StringQualifier
import org.koin.dsl.module
import uk.fernando.tictactoe.datastore.GamePrefsStore
import uk.fernando.tictactoe.datastore.GamePrefsStoreMock
import uk.fernando.tictactoe.datastore.PrefsStore
import uk.fernando.tictactoe.datastore.PrefsStoreMock
import uk.fernando.tictactoe.di.KoinModule.allModules

val mockModule = module {
    single { mockk<Application>() }
    single { mockk<Context>() }
    single<PrefsStore> { PrefsStoreMock() }
    single<GamePrefsStore> { GamePrefsStoreMock() }
    single { KoinModule.getAndroidLogger() }
    factory(qualifier = StringQualifier("common")) { mockk<SharedPreferences>() }
}

fun allMockedModules() = allModules() + mockModule