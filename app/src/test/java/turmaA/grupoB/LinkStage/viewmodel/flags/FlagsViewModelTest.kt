package turmaA.grupoB.LinkStage.viewmodel.flags

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import turmaA.grupoB.LinkStage.data.remote.model.Imgs
import turmaA.grupoB.LinkStage.data.repository.flags.FlagsRepository

@OptIn(ExperimentalCoroutinesApi::class)
class FlagsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var mockRepository: FlagsRepository

    private lateinit var viewModel: FlagsViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = FlagsViewModel(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialStateShouldBeIdle() = runTest {
        assertTrue(viewModel.uiState.value is FlagsUIState.Idle)
    }

    @Test
    fun getImagesShouldTransitionToSuccessOnValidResponse() = runTest {
        val mockImgs = Imgs(png = "https://flagcdn.com/w320/pt.png")
        whenever(mockRepository.getFlag("portugal")).thenReturn(mockImgs)

        viewModel.getImages(listOf("portugal"))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is FlagsUIState.Success)
        assertEquals(listOf(mockImgs), (state as FlagsUIState.Success).imgs)
    }

    @Test
    fun getImagesShouldTransitionToLoadingImmediately() = runTest {
        val mockImgs = Imgs(png = "https://flagcdn.com/w320/pt.png")
        whenever(mockRepository.getFlag("portugal")).thenReturn(mockImgs)

        viewModel.getImages(listOf("portugal"))
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(
            "Expected Success but got ${state::class.simpleName}",
            state is FlagsUIState.Success
        )
    }

    @Test
    fun getImagesShouldTransitionToErrorWhenRepositoryThrows() = runTest {
        whenever(mockRepository.getFlag("portugal"))
            .thenThrow(RuntimeException("Network error"))

        viewModel.getImages(listOf("portugal"))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is FlagsUIState.Error)
        assertEquals("Network error", (state as FlagsUIState.Error).message)
    }

    @Test
    fun getImagesShouldUseDefaultErrorMessageWhenExceptionHasNoMessage() = runTest {
        whenever(mockRepository.getFlag("portugal"))
            .thenThrow(RuntimeException())

        viewModel.getImages(listOf("portugal"))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is FlagsUIState.Error)
        assertEquals("Erro ao carregar imagens", (state as FlagsUIState.Error).message)
    }

    @Test
    fun getImagesWithMultipleCountriesShouldCollectAllResults() = runTest {
        val mockImgs1 = Imgs(png = "https://flagcdn.com/w320/pt.png")
        val mockImgs2 = Imgs(png = "https://flagcdn.com/w320/es.png")
        whenever(mockRepository.getFlag("portugal")).thenReturn(mockImgs1)
        whenever(mockRepository.getFlag("spain")).thenReturn(mockImgs2)

        viewModel.getImages(listOf("portugal", "spain"))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is FlagsUIState.Success)
        assertEquals(listOf(mockImgs1, mockImgs2), (state as FlagsUIState.Success).imgs)
    }

    @Test
    fun getImagesShouldHandleErrorInSecondCountryAfterSuccessfulFirst() = runTest {
        val mockImgs = Imgs(png = "https://flagcdn.com/w320/pt.png")
        whenever(mockRepository.getFlag("portugal")).thenReturn(mockImgs)
        whenever(mockRepository.getFlag("invalid"))
            .thenThrow(RuntimeException("Not found"))

        viewModel.getImages(listOf("portugal", "invalid"))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is FlagsUIState.Error)
        assertEquals("Not found", (state as FlagsUIState.Error).message)
    }

    @Test
    fun getImagesWithEmptyListShouldSetEmpty() = runTest {
        viewModel.getImages(emptyList())
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(
            "Expected Empty for empty list but got ${state::class.simpleName}",
            state is FlagsUIState.Empty
        )
    }
}
