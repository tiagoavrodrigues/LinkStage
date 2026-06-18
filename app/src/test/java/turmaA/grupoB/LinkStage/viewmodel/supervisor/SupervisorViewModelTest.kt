package turmaA.grupoB.LinkStage.viewmodel.supervisor

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
import turmaA.grupoB.LinkStage.data.remote.model.user.CreateSupervisorInput
import turmaA.grupoB.LinkStage.data.remote.model.user.SupervisorModel
import turmaA.grupoB.LinkStage.data.remote.model.user.SupervisorSkillModel
import turmaA.grupoB.LinkStage.data.repository.supervisor.SupervisorRepositoryInterface

@OptIn(ExperimentalCoroutinesApi::class)
class SupervisorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepository: FakeSupervisorRepository
    private lateinit var viewModel: SupervisorViewModel

    @Before
    fun setup() {
        fakeRepository = FakeSupervisorRepository()
        viewModel = SupervisorViewModel(fakeRepository)
    }

    @Test
    fun initialState_isIdle() {
        assertEquals(
            SupervisorUiState.Idle,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadSupervisors_whenSupervisorsExist_setsSuccessListState() = runTest {
        fakeRepository.supervisors = listOf(testSupervisor)

        viewModel.loadSupervisors()

        advanceUntilIdle()

        assertEquals(
            SupervisorUiState.SuccessList(listOf(testSupervisor)),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadSupervisors_whenSupervisorsDoNotExist_setsEmptyState() = runTest {
        fakeRepository.supervisors = emptyList()

        viewModel.loadSupervisors()

        advanceUntilIdle()

        assertEquals(
            SupervisorUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadSupervisors_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.ShouldThrowOnGetSupervisors = true

        viewModel.loadSupervisors()

        advanceUntilIdle()

        assertEquals(
            SupervisorUiState.Error("Erro ao carregar orientadores."),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadSupervisorById_whenSupervisorExists_setsSuccessState() = runTest {
        fakeRepository.supervisors = listOf(testSupervisor)

        viewModel.loadSupervisorById(testSupervisor.id)

        advanceUntilIdle()

        assertEquals(
            SupervisorUiState.Success(testSupervisor),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadSupervisorById_whenSupervisorDoesNotExist_setsEmptyState() = runTest {
        fakeRepository.supervisors = emptyList()

        viewModel.loadSupervisorById("unknow-user-id")

        advanceUntilIdle()

        assertEquals(
            SupervisorUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadSupervisorById_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.ShouldThrowOnGetSupervisorById = true

        viewModel.loadSupervisorById(testSupervisor.id)

        advanceUntilIdle()

        assertEquals(
            SupervisorUiState.Error("Erro ao carregar orientador."),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadSupervisorByUserId_whenSupervisorExists_setsSuccessState() = runTest {
        fakeRepository.supervisors = listOf(testSupervisor)

        viewModel.loadSupervisorByUserId(testUserId)

        advanceUntilIdle()

        assertEquals(
            SupervisorUiState.Success(testSupervisor),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadSupervisorByUserId_whenSupervisorDoesNotExist_setsEmptyState() = runTest {
        fakeRepository.supervisors = emptyList()

        viewModel.loadSupervisorByUserId(testUserId)

        advanceUntilIdle()

        assertEquals(
            SupervisorUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadSupervisorByUserId_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.ShouldThrowOnGetSupervisorByUserId = true

        viewModel.loadSupervisorByUserId(testUserId)

        advanceUntilIdle()

        assertEquals(
            SupervisorUiState.Error("Erro ao carregar orientador do utilizador."),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadAvailableSupervisors_whenSupervisorsExist_setsSuccessState() = runTest {
        fakeRepository.supervisors = listOf(testSupervisor)

        viewModel.loadAvailableSupervisors()

        advanceUntilIdle()

        assertEquals(
            SupervisorUiState.SuccessList(listOf(testSupervisor)),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadAvailableSupervisors_whenSupervisorsDoNotExist_setsSemptyState() = runTest {
        fakeRepository.supervisors = emptyList()

        viewModel.loadAvailableSupervisors()

        advanceUntilIdle()

        assertEquals(
            SupervisorUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadAvailableSupervisors_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.ShouldThrowOnGetAvailableSupervisors = true

        viewModel.loadAvailableSupervisors()

        advanceUntilIdle()

        assertEquals(
            SupervisorUiState.Error("Erro ao carregar orientadores disponíveis."),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadSupervisorsByDepartment_whenSupervisorsExist_setsSuccessListState() = runTest {
        fakeRepository.supervisors = listOf(testSupervisor)

        viewModel.loadSupervisorsByDepartment("Informática")

        advanceUntilIdle()

        assertEquals(
            SupervisorUiState.SuccessList(listOf(testSupervisor)),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadSupervisorsByDepartment_whenSupervisorsDoNotExist_setsEmptyState() = runTest {
        fakeRepository.supervisors = emptyList()

        viewModel.loadSupervisorsByDepartment("Informática")

        advanceUntilIdle()

        assertEquals(
            SupervisorUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadSupervisorsByDepartment_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.ShouldThrowOnGetSupervisorsByDepartment = true

        viewModel.loadSupervisorsByDepartment("Informática")

        advanceUntilIdle()

        assertEquals(
            SupervisorUiState.Error("Erro ao carregar orientadores por departamento."),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadSupervisorSkills_whenSkillsExist_setsSkillsSuccessState() = runTest {
        fakeRepository.skills = listOf(testSkill)

        viewModel.loadSupervisorSkills(testSupervisor.id)

        advanceUntilIdle()

        assertEquals(
            SupervisorUiState.SkillsSuccess(listOf(testSkill)),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadSupervisorSkills_whenSkillsDoNotExist_setsEmptyState() = runTest {
        fakeRepository.skills = emptyList()

        viewModel.loadSupervisorSkills(testSupervisor.id)

        advanceUntilIdle()

        assertEquals(
            SupervisorUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadSupervisorSkills_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.ShouldThrowOnGetSupervisorSkills = true

        viewModel.loadSupervisorSkills(testSupervisor.id)

        advanceUntilIdle()

        assertEquals(
            SupervisorUiState.Error("Erro ao carregar competências do orientador."),
            viewModel.uiState.value
        )
    }

    @Test
    fun resetState_setsIdleState() = runTest {
        fakeRepository.supervisors = listOf(testSupervisor)

        viewModel.loadSupervisors()

        advanceUntilIdle()

        viewModel.resetState()

        assertEquals(
            SupervisorUiState.Idle,
            viewModel.uiState.value
        )
    }

    private companion object {
        const val testUserId = "00000000-0000-0000-0000-000000000020"

        val testSupervisor = SupervisorModel(
            id = "00000000-0000-0000-0000-000000000001",
            userId = testUserId,
            department = "Informática",
            specialty = "Desenvolvimento Android",
            maxInternships = 5,
            acceptsNewInternships = true,
            createdAt = "2026-01-01T00:00:00Z",
            updatedAt = "2026-01-01T00:00:00Z"
        )

        val testSkill = SupervisorSkillModel(
            id = "00000000-0000-0000-0000-000000000002",
            supervisorId = testSupervisor.id,
            skill = "Kotlin",
            createdAt = "2026-01-01T00:00:00Z"
        )
    }
}

private class FakeSupervisorRepository : SupervisorRepositoryInterface {
    var supervisors: List<SupervisorModel> = emptyList()
    var skills: List<SupervisorSkillModel> = emptyList()

    var ShouldThrowOnGetSupervisors: Boolean = false
    var ShouldThrowOnGetSupervisorById: Boolean = false
    var ShouldThrowOnGetSupervisorByUserId: Boolean = false
    var ShouldThrowOnGetAvailableSupervisors: Boolean = false
    var ShouldThrowOnGetSupervisorsByDepartment: Boolean = false
    var ShouldThrowOnGetSupervisorSkills: Boolean = false

    override suspend fun getSupervisors(): List<SupervisorModel> {
        if (ShouldThrowOnGetSupervisors) {
            throw IllegalStateException("Erro ao carregar orientadores.")
        }

        return supervisors
    }

    override suspend fun getSupervisorById(supervisorId: String): SupervisorModel? {
        if (ShouldThrowOnGetSupervisorById) {
            throw IllegalStateException("Erro ao carregar orientador.")
        }

        return supervisors.firstOrNull { it.id == supervisorId }
    }

    override suspend fun getSupervisorByUserId(userId: String): SupervisorModel? {
        if (ShouldThrowOnGetSupervisorByUserId) {
            throw IllegalStateException("Erro ao carregar orientador do utilizador.")
        }

        return supervisors.firstOrNull { it.userId == userId}
    }

    override suspend fun getAvailableSupervisors(): List<SupervisorModel> {
        if (ShouldThrowOnGetAvailableSupervisors) {
            throw IllegalStateException("Erro ao carregar orientadores disponíveis.")
        }

        return supervisors.filter { it.acceptsNewInternships }
    }

    override suspend fun getSupervisorsByDepartment(department: String): List<SupervisorModel> {
        if (ShouldThrowOnGetSupervisorsByDepartment) {
            throw IllegalStateException("Erro ao carregar orientadores por departamento.")
        }

        return supervisors.filter { it.department == department }
    }

    override suspend fun getSupervisorSkills(supervisorId: String): List<SupervisorSkillModel> {
        if (ShouldThrowOnGetSupervisorSkills) {
            throw IllegalStateException("Erro ao carregar competências do orientador.")
        }

        return skills.filter { it.supervisorId == supervisorId }
    }

    override suspend fun createSupervisor(input: CreateSupervisorInput): SupervisorModel = error("not implemented")
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