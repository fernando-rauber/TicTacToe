package uk.fernando.tictactoe.di


import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import uk.fernando.tictactoe.datastore.GamePrefsStore
import uk.fernando.tictactoe.datastore.GamePrefsStoreImpl
import uk.fernando.tictactoe.datastore.PrefsStore
import uk.fernando.tictactoe.datastore.PrefsStoreImpl
import uk.fernando.tictactoe.viewmodel.HomeViewModel

object KoinModule {

    /**
     * Keep the order applied
     * @return List<Module>
     */
    fun allModules(): List<Module> = listOf(coreModule, useCaseModule, viewModelModule)

    private val coreModule = module {

//        single { getAndroidLogger() }
        single<PrefsStore> { PrefsStoreImpl(androidApplication()) }
        single<GamePrefsStore> { GamePrefsStoreImpl(androidApplication()) }
    }

    private val useCaseModule: Module
        get() = module {
//            single { GetCategoryListUseCase(get()) }
//            single { UpdateLevelUseCase(get(), get()) }
//            single { PurchaseUseCase(androidApplication(), get(), get()) }
//            single { SetUpUseCase(get(), get(), get()) }
        }

    private val viewModelModule: Module
        get() = module {

            viewModel { HomeViewModel(get()) }
        }

//    fun getAndroidLogger(): MyLogger {
//        return if (BuildConfig.BUILD_TYPE == "debug")
//            AndroidLogger(MyLogger.LogLevel.DEBUG)
//        else
//            AndroidLogger(MyLogger.LogLevel.ERROR)
//    }
}