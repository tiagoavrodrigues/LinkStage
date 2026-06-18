package turmaA.grupoB.LinkStage.viewmodel.institution

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import turmaA.grupoB.LinkStage.data.remote.model.institution.CreateInstitutionInput
import turmaA.grupoB.LinkStage.data.remote.model.institution.InstitutionModel
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepositoryInterface
import turmaA.grupoB.LinkStage.viewmodel.Institution.InstitutionUiState
import turmaA.grupoB.LinkStage.viewmodel.Institution.InstitutionViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class InstitutionViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepository: FakeInstitutionRepository
    private lateinit var viewModel: InstitutionViewModel

    @Before
    fun setup() {
        fakeRepository = FakeInstitutionRepository()
        viewModel = InstitutionViewModel(fakeRepository)
    }

    @Test
    fun initialState_isIdle(){
        assertEquals(
            InstitutionUiState.Idle,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadInstitutions_whenInstitutionsExist_setsSuccessListState() = runTest {
        fakeRepository.institutions = listOf(testInstitution)

        viewModel.loadInstitutions()

        advanceUntilIdle()

        assertEquals(
            InstitutionUiState.SuccessList(listOf(testInstitution)),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadInstitutions_whenInstitutionsDoNotExist_setsEmptyState() = runTest {
        fakeRepository.institutions = emptyList()

        viewModel.loadInstitutions()

        advanceUntilIdle()

        assertEquals(
            InstitutionUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadInstitutions_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetInstitutions = true

        viewModel.loadInstitutions()

        advanceUntilIdle()

        assertEquals(
            InstitutionUiState.Error("Erro ao carregar instituições."),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadInstitutionById_whenInstitutionExists_setsSuccessState() = runTest {
        fakeRepository.institutions = listOf(testInstitution)

        viewModel.loadInstitutionById(testInstitution.id)

        advanceUntilIdle()

        assertEquals(
            InstitutionUiState.Success(testInstitution),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadInstitutionById_whenInstitutionDoesNotExist_setsEmptyState() = runTest {
        fakeRepository.institutions = emptyList()

        viewModel.loadInstitutionById("unknown-user-id")

        advanceUntilIdle()

        assertEquals(
            InstitutionUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun resetState_setsIdleState() = runTest {
        fakeRepository.institutions = listOf(testInstitution)

        viewModel.loadInstitutions()

        advanceUntilIdle()

        viewModel.resetState()

        assertEquals(
            InstitutionUiState.Idle,
            viewModel.uiState.value
        )
    }

    private companion object {
        const val testUserId = "00000000-0000-0000-0000-000000000020"

        val testInstitution = InstitutionModel(
            id = "00000000-0000-0000-0000-000000000001",
            userId = testUserId,
            name = "Instituição Teste",
            address = "Rua de Teste",
            website = "https://example.com",
            description = "Descrição Teste",
            sector = "Setor de Teste",
            createdAt = "2026-01-01T00:00:00Z",
            updatedAt = "2026-01-01T00:00:00Z"
        )
    }
}

private class FakeInstitutionRepository: InstitutionRepositoryInterface {
    var institutions: List<InstitutionModel> = emptyList()

    var shouldThrowOnGetInstitutions: Boolean = false
    var shouldThrowOnGetInstitutionsById: Boolean = false
    var shouldThrowOnGetInstitutionsByUserId: Boolean = false

    override suspend fun getInstitutions(): List<InstitutionModel> {
        if (shouldThrowOnGetInstitutions) {
            throw IllegalStateException("Erro ao carregar instituições.")
        }

        return institutions
    }

    override suspend fun getInstitutionById(institutionId: String): InstitutionModel? {
        if (shouldThrowOnGetInstitutionsById) {
            throw IllegalStateException("Erro ao carregar instituições.")
        }

        return institutions.firstOrNull { it.id == institutionId }
    }

    override suspend fun getInstitutionByUserId(userId: String): InstitutionModel? {
        if (shouldThrowOnGetInstitutionsByUserId) {
            throw IllegalStateException("Erro ao carregar instituição do utilizador.")
        }

        return institutions.firstOrNull { it.userId == userId }
    }

    override suspend fun createInstitution(input: CreateInstitutionInput): InstitutionModel {
        throw NotImplementedError("Not used in these tests.")
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}