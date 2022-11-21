package uk.fernando.tictactoe.viewmodel

import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTestRule
import org.koin.test.inject
import uk.fernando.tictactoe.KoinTestCase
import uk.fernando.tictactoe.MainCoroutineRule
import uk.fernando.tictactoe.di.multiplayerMockedModules
import uk.fernando.tictactoe.util.GameResult
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class EatGameUnitTest : KoinTestCase() {
    private val viewModel: TicGameViewModel by inject()

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @get:Rule
    override val koinRule = KoinTestRule.create {
        modules(multiplayerMockedModules())
    }

    @Test
    fun `check if is multiplayer`() {
        runTest {
            viewModel.init()

            delay(1000)

            assertEquals(false, viewModel.isAiOn)
            assertEquals("Player 2", viewModel.player2.value.name)
        }
    }

    @Test
    fun `check amount of cell is correct with board size`() {
        runTest {
            viewModel.init()

            delay(1000)

            assertEquals(9, viewModel.cellList.size)
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
    fun `check if player O can play after player X`() {
        runTest {
            viewModel.init()

            delay(1_000)

            assertEquals(true, viewModel.isPLayer1Turn.value)

            viewModel.setCellValue(0)

            delay(1_000)

            assertEquals(false, viewModel.isPLayer1Turn.value)
        }
    }

    @Test
    fun `check if player X is winner`() {
        runTest {
            viewModel.init()

            delay(1000)

            viewModel.setCellValue(0) // x
            viewModel.setCellValue(3) // o
            viewModel.setCellValue(1) // x
            viewModel.setCellValue(4) // 0
            viewModel.setCellValue(2) // x

            delay(1_000)

            val winner = (viewModel.roundResult.value as GameResult.Winner).result

            assertEquals(viewModel.player1.value.avatar, winner.avatar)
            assertEquals(1, viewModel.player1.value.score)

        }
    }

    @Test
    fun `check if is a draw`() {
        runTest {
            viewModel.init()

            delay(1000)

            // x o x
            // x o o
            // o x x

            viewModel.setCellValue(0) // x
            viewModel.setCellValue(1) // 0
            viewModel.setCellValue(2) // x

            viewModel.setCellValue(4) // o
            viewModel.setCellValue(3) // x
            viewModel.setCellValue(5) // o

            viewModel.setCellValue(7) // x
            viewModel.setCellValue(6) // o
            viewModel.setCellValue(8) // x

            delay(1_000)

            val isDraw = (viewModel.roundResult.value is GameResult.Draw)

            assertEquals(true, isDraw)
        }
    }
}