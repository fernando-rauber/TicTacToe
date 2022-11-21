package uk.fernando.tictactoe

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.mockkClass
import org.junit.Rule
import org.koin.core.context.unloadKoinModules
import org.koin.test.AutoCloseKoinTest
import org.koin.test.KoinTestRule
import org.koin.test.mock.MockProviderRule
import uk.fernando.tictactoe.di.allMockedModules

abstract class KoinTestCase : AutoCloseKoinTest() {

    @get:Rule
    abstract val koinRule: KoinTestRule

    @get:Rule
    open val mockProvider = MockProviderRule.create { clazz ->
        mockkClass(clazz)
    }

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    protected fun freeUpModules() {
        unloadKoinModules(allMockedModules())
    }
}