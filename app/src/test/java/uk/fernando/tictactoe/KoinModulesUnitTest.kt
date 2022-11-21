package uk.fernando.tictactoe

import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.koin.test.AutoCloseKoinTest
import org.koin.test.category.CheckModuleTest
import org.koin.test.check.checkModules
import org.koin.test.mock.MockProviderRule
import uk.fernando.tictactoe.di.KoinModule
import uk.fernando.tictactoe.di.mockModule

@Category(CheckModuleTest::class)
class KoinModulesUnitTest : AutoCloseKoinTest() {

    @get:Rule
    val mockProvider = MockProviderRule.create { _ ->
        mockk()
    }

    @Test
    fun `can resolve dependency tree`() {
        checkModules {
            modules(KoinModule.allModules() + mockModule)
        }
    }
}