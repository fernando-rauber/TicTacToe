package uk.fernando.tictactoe.viewmodel

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTestRule
import org.koin.test.inject
import uk.fernando.tictactoe.KoinTestCase
import uk.fernando.tictactoe.MainCoroutineRule
import uk.fernando.tictactoe.di.singlePLayerMockedModules
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class TicGameUnitTest : KoinTestCase() {
    private val viewModel: TicGameViewModel by inject()

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @get:Rule
    override val koinRule = KoinTestRule.create {
        modules(singlePLayerMockedModules())
    }

    @Test
    fun `check if AI is on when game is single player`() {
        runTest {
            viewModel.init()

            delay(1000)

            assertEquals(true, viewModel.isAiOn)
            assertEquals("AI", viewModel.player2.value.name)
        }
    }

    @Test
    fun `check amount of cell is correct with board size`() {
        runTest {
            viewModel.init()

            delay(1000)

            assertEquals(25, viewModel.cellList.size)
        }
    }

    @Test
    fun `check if go to next round when finished`() {
        runTest {
            viewModel.init()

            delay(1000)

            assertEquals(1, viewModel.currentRound.value)

            viewModel.startNextRound()

            assertEquals(2, viewModel.currentRound.value)
        }
    }

    @Test
    fun `check if AI will play after player X`() {
        runTest {
            viewModel.init()

            delay(1_000)

            assertEquals(true, viewModel.isPLayer1Turn.value)

            viewModel.setCellValue(0)

            delay(1_000)

            assertEquals(true, viewModel.isPLayer1Turn.value)
            assertEquals(2, viewModel.cellList.filter { it.isX != null }.size)
        }
    }
}