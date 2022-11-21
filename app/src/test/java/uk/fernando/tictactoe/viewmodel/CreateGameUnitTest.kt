package uk.fernando.tictactoe.viewmodel

import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTestRule
import org.koin.test.inject
import uk.fernando.tictactoe.KoinTestCase
import uk.fernando.tictactoe.di.allMockedModules
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class CreateGameUnitTest : KoinTestCase() {
    private val viewModel: CreateGameViewModel by inject()

    @get:Rule
    override val koinRule = KoinTestRule.create {
        modules(allMockedModules())
    }

    @OptIn(DelicateCoroutinesApi::class)
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // reset the main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @Test
    fun `set board size and check if win condition is lower`() {
        runTest {
            viewModel.setWinCondition(6)

            viewModel.setBoardSize(5)

            delay(1000)

            assertEquals(5, viewModel.winCondition.value)
            assertEquals(3, viewModel.winConditionList.size)
        }
    }

    @Test
    fun `set board size and check if win condition is correct to the board size`() {
        runBlocking {
            viewModel.setWinCondition(5)

            viewModel.setBoardSize(9)

            delay(1000)

            assertEquals(5, viewModel.winCondition.value)
            assertEquals(4, viewModel.winConditionList.size)
        }
    }

    @Test
    fun `check for empty name for opponent player`() {
        runBlocking {
            viewModel.setPlayerName("name")
            delay(1000)
            assertEquals("name", viewModel.playerName.value)

            viewModel.setPlayerName("")
            delay(1000)
            assertEquals("Player 2", viewModel.playerName.value)
        }
    }

}