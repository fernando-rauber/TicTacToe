package uk.fernando.tictactoe.di


import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import uk.fernando.logger.AndroidLogger
import uk.fernando.logger.MyLogger
import uk.fernando.tictactoe.BuildConfig
import uk.fernando.tictactoe.datastore.GamePrefsStore
import uk.fernando.tictactoe.datastore.GamePrefsStoreImpl
import uk.fernando.tictactoe.datastore.PrefsStore
import uk.fernando.tictactoe.datastore.PrefsStoreImpl
import uk.fernando.tictactoe.usecase.AiGameUseCase
import uk.fernando.tictactoe.usecase.GameUseCase
import uk.fernando.tictactoe.viewmodel.EatGameViewModel
import uk.fernando.tictactoe.viewmodel.CreateGameViewModel
import uk.fernando.tictactoe.viewmodel.TicGameViewModel

object KoinModule {

    /**
     * Keep the order applied
     * @return List<Module>
     */
    fun allModules(): List<Module> = listOf(coreModule, useCaseModule, viewModelModule)

    private val coreModule = module {

        single { getAndroidLogger() }
        single<PrefsStore> { PrefsStoreImpl(androidApplication()) }
        single<GamePrefsStore> { GamePrefsStoreImpl(androidApplication()) }
    }

    private val useCaseModule: Module
        get() = module {
            single { GameUseCase(get()) }
            single { AiGameUseCase(get()) }
//            single { PurchaseUseCase(androidApplication(), get(), get()) }
//            single { SetUpUseCase(get(), get(), get()) }
        }

    private val viewModelModule: Module
        get() = module {
            viewModel { CreateGameViewModel(get()) }
            viewModel { TicGameViewModel(get(), get(), get()) }
            viewModel { EatGameViewModel(get(), get(), get(), get()) }
        }

    fun getAndroidLogger(): MyLogger {
        return if (BuildConfig.BUILD_TYPE == "debug")
            AndroidLogger(MyLogger.LogLevel.DEBUG)
        else
            AndroidLogger(MyLogger.LogLevel.ERROR)
    }
}