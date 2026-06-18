package turmaA.grupoB.LinkStage.viewmodel.internship

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
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import turmaA.grupoB.LinkStage.data.remote.model.enums.InternshipStatus
import turmaA.grupoB.LinkStage.data.remote.model.internship.ActivityLogModel
import turmaA.grupoB.LinkStage.data.remote.model.internship.AssignSupervisorInput
import turmaA.grupoB.LinkStage.data.remote.model.internship.CreateActivityLogInput
import turmaA.grupoB.LinkStage.data.remote.model.internship.CreateInternshipInput
import turmaA.grupoB.LinkStage.data.remote.model.internship.InternshipModel
import turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepositoryInterface
import turmaA.grupoB.LinkStage.viewmodel.internships.InternshipUiState
import turmaA.grupoB.LinkStage.viewmodel.internships.InternshipViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class InternshipViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepository: FakeInternshipRepository
    private lateinit var viewModel: InternshipViewModel

    @Before
    fun setup() {
        fakeRepository = FakeInternshipRepository()
        viewModel = InternshipViewModel(fakeRepository)
    }

    @Test
    fun initialState_isIdle() {
        assertEquals(
            InternshipUiState.Idle,
            viewModel.uiState.value
        )
    }

    // --- getInternships() tests ---

    @Test
    fun getInternships_whenInternshipsExist_setsSuccessListState() = runTest {
        fakeRepository.internships = listOf(testInternship)

        viewModel.getInternships()

        advanceUntilIdle()

        assertEquals(
            InternshipUiState.SuccessList(listOf(testInternship)),
            viewModel.uiState.value
        )
    }

    @Test
    fun getInternships_whenInternshipsDoNotExist_setsEmptyState() = runTest {
        fakeRepository.internships = emptyList()

        viewModel.getInternships()

        advanceUntilIdle()

        assertEquals(
            InternshipUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun getInternships_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetInternships = true

        viewModel.getInternships()

        advanceUntilIdle()

        assertEquals(
            InternshipUiState.Error("Erro ao carregar estágios."),
            viewModel.uiState.value
        )
    }

    // --- getInternshipById() tests ---

    @Test
    fun getInternshipById_whenInternshipExists_setsSuccessState() = runTest {
        fakeRepository.internships = listOf(testInternship)

        viewModel.getInternshipById(testInternship.id)

        advanceUntilIdle()

        assertEquals(
            InternshipUiState.Success(testInternship),
            viewModel.uiState.value
        )
    }

    @Test
    fun getInternshipById_whenInternshipDoesNotExist_setsEmptyState() = runTest {
        fakeRepository.internships = emptyList()

        viewModel.getInternshipById("00000000-0000-0000-0000-000000000099")

        advanceUntilIdle()

        assertEquals(
            InternshipUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun getInternshipById_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetInternshipsById = true

        viewModel.getInternshipById(testInternship.id)

        advanceUntilIdle()

        assertEquals(
            InternshipUiState.Error("Erro ao carregar estágios."),
            viewModel.uiState.value
        )
    }

    // --- getInternshipByStudent() tests ---

    @Test
    fun getInternshipByStudent_whenInternshipsExist_setsSuccessListState() = runTest {
        fakeRepository.internships = listOf(testInternship)

        viewModel.getInternshipByStudent(testInternship.studentId)

        advanceUntilIdle()

        assertEquals(
            InternshipUiState.SuccessList(listOf(testInternship)),
            viewModel.uiState.value
        )
    }

    @Test
    fun getInternshipByStudent_whenInternshipsDoNotExist_setsEmptyState() = runTest {
        fakeRepository.internships = emptyList()

        viewModel.getInternshipByStudent(testInternship.studentId)

        advanceUntilIdle()

        assertEquals(
            InternshipUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun getInternshipByStudent_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetInternshipsByStudent = true

        viewModel.getInternshipByStudent(testInternship.studentId)

        advanceUntilIdle()

        assertEquals(
            InternshipUiState.Error("Erro ao carregar estágios."),
            viewModel.uiState.value
        )
    }

    // --- getInternshipsByInstitution() tests ---

    @Test
    fun getInternshipsByInstitution_whenInternshipsExist_setsSuccessListState() = runTest {
        fakeRepository.internships = listOf(testInternship)

        viewModel.getInternshipsByInstitution(testInternship.institutionId)

        advanceUntilIdle()

        assertEquals(
            InternshipUiState.SuccessList(listOf(testInternship)),
            viewModel.uiState.value
        )
    }

    @Test
    fun getInternshipsByInstitution_whenInternshipsDoNotExist_setsEmptyState() = runTest {
        fakeRepository.internships = emptyList()

        viewModel.getInternshipsByInstitution(testInternship.institutionId)

        advanceUntilIdle()

        assertEquals(
            InternshipUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun getInternshipsByInstitution_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetInternshipsByInstitution = true

        viewModel.getInternshipsByInstitution(testInternship.institutionId)

        advanceUntilIdle()

        assertEquals(
            InternshipUiState.Error("Erro ao carregar estágios."),
            viewModel.uiState.value
        )
    }

    // --- getInternshipsByStatus() tests ---

    @Test
    fun getInternshipsByStatus_whenInternshipsExist_setsSuccessListState() = runTest {
        fakeRepository.internships = listOf(testInternship.copy(status = InternshipStatus.IN_PROGRESS))

        viewModel.getInternshipsByStatus(InternshipStatus.IN_PROGRESS)

        advanceUntilIdle()

        assertEquals(
            InternshipUiState.SuccessList(
                listOf(testInternship.copy(status = InternshipStatus.IN_PROGRESS))
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun getInternshipsByStatus_whenInternshipsDoNotExist_setsEmptyState() = runTest {
        fakeRepository.internships = emptyList()

        viewModel.getInternshipsByStatus(InternshipStatus.IN_PROGRESS)

        advanceUntilIdle()

        assertEquals(
            InternshipUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun getInternshipsByStatus_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetInternshipsByStatus = true

        viewModel.getInternshipsByStatus(InternshipStatus.IN_PROGRESS)

        advanceUntilIdle()

        assertEquals(
            InternshipUiState.Error("Erro ao carregar estágios."),
            viewModel.uiState.value
        )
    }

    // --- assignSupervisor() tests ---

    @Test
    fun assignSupervisor_whenInternshipExists_setsSuccessState() = runTest {
        fakeRepository.internships = listOf(testInternship)

        viewModel.assignSupervisor(
            testInternship.id,
            AssignSupervisorInput(
                supervisorId = "00000000-0000-0000-0000-000000000002",
                status = InternshipStatus.IN_PROGRESS
            )
        )

        advanceUntilIdle()

        assertEquals(
            InternshipUiState.Success(testInternship),
            viewModel.uiState.value
        )
    }

    @Test
    fun assignSupervisor_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnAssignSupervisor = true

        viewModel.assignSupervisor(
            testInternship.id,
            AssignSupervisorInput(
                supervisorId = "00000000-0000-0000-0000-000000000002"
            )
        )

        advanceUntilIdle()

        assertEquals(
            InternshipUiState.Error("Erro ao carregar estágios."),
            viewModel.uiState.value
        )
    }

    // --- createActivityLog() tests ---

    @Ignore("Temporariamente desativado")
    @Test
    fun createActivityLog_whenActivityCreated_setsSuccessActivityState() = runTest {
        fakeRepository.activities = listOf(testActivityLog)

        viewModel.createActivityLog(
            CreateActivityLogInput(
                internshipId = testInternship.id,
                studentId = testInternship.studentId,
                description = "Worked on feature X",
                activityDate = "2026-01-15",
                hours = 4.0
            )
        )

        advanceUntilIdle()

        assertEquals(
            InternshipUiState.SuccessActivity(testActivityLog),
            viewModel.uiState.value
        )
    }

    @Ignore("Temporariamente desativado")
    @Test
    fun createActivityLog_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnCreateActivityLog = true

        viewModel.createActivityLog(
            CreateActivityLogInput(
                internshipId = testInternship.id,
                studentId = testInternship.studentId,
                description = "Worked on feature X",
                activityDate = "2026-01-15"
            )
        )

        advanceUntilIdle()

        assertEquals(
            InternshipUiState.Error("Erro ao carregar estágios."),
            viewModel.uiState.value
        )
    }

    // --- getActivityLogsByInternship() tests ---

    @Test
    fun getActivityLogsByInternship_whenActivitiesExist_setsSuccessActivityListState() = runTest {
        fakeRepository.activities = listOf(testActivityLog)

        viewModel.getActivityLogsByInternship(testInternship.id)

        advanceUntilIdle()

        assertEquals(
            InternshipUiState.SuccessActivityList(
                listOf(testActivityLog).filter { it.internshipId == testInternship.id }
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun getActivityLogsByInternship_whenActivitiesDoNotExist_setsEmptyState() = runTest {
        fakeRepository.activities = emptyList()

        viewModel.getActivityLogsByInternship(testInternship.id)

        advanceUntilIdle()

        assertEquals(
            InternshipUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun getActivityLogsByInternship_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetActivityLogsByInternship = true

        viewModel.getActivityLogsByInternship(testInternship.id)

        advanceUntilIdle()

        assertEquals(
            InternshipUiState.Error("Erro ao carregar estágios."),
            viewModel.uiState.value
        )
    }

    @Test
    fun createInternship_whenInternshipCreated_setsSuccessState() = runTest {
        val input = CreateInternshipInput(
            applicationId = "application-id",
            offerId = "offer-id",
            studentId = "student-id",
            institutionId = "institution-id",
            supervisorId = "supervisor-id",
            title = "Estágio Android",
            companySupervisorName = "Supervisor Empresa",
            startDate = "2025-01-01",
            endDate = "2025-06-30",
            status = InternshipStatus.PENDING_SUPERVISOR,
            workPlan = "Plano de trabalho",
            objectives = "Objetivos do estágio"
        )

        val expectedInternship = InternshipModel(
            id = "internship-id",
            applicationId = input.applicationId,
            offerId = input.offerId,
            studentId = input.studentId,
            institutionId = input.institutionId,
            supervisorId = input.supervisorId,
            title = input.title,
            companySupervisorName = input.companySupervisorName,
            startDate = input.startDate,
            endDate = input.endDate,
            status = input.status,
            workPlan = input.workPlan,
            objectives = input.objectives,
            createdAt = "2025-01-01T00:00:00Z",
            updatedAt = "2025-01-01T00:00:00Z"
        )

        viewModel.createInternship(input)
        advanceUntilIdle()

        assertEquals(
            InternshipUiState.Success(expectedInternship),
            viewModel.uiState.value
        )
    }


    private companion object {
        val testInternship = InternshipModel(
            id = "00000000-0000-0000-0000-000000000001",
            applicationId = "00000000-0000-0000-0000-000000000001",
            offerId = "00000000-0000-0000-0000-000000000001",
            studentId = "00000000-0000-0000-0000-000000000001",
            institutionId = "00000000-0000-0000-0000-000000000001",
            supervisorId = null,
            title = "Test Internship",
            status = InternshipStatus.PENDING_SUPERVISOR,
            createdAt = "2026-01-01T00:00:00Z",
            updatedAt = "2026-01-01T00:00:00Z"
        )

        val testActivityLog = ActivityLogModel(
            id = "00000000-0000-0000-0000-000000000010",
            internshipId = "00000000-0000-0000-0000-000000000001",
            studentId = "00000000-0000-0000-0000-000000000001",
            description = "Worked on feature X",
            activityDate = "2026-01-15",
            hours = 4.0,
            createdAt = "2026-01-15T00:00:00Z"
        )
    }
}

private class FakeInternshipRepository : InternshipRepositoryInterface {

    var internships: List<InternshipModel> = emptyList()
    var activities: List<ActivityLogModel> = emptyList()

    var shouldThrowOnGetInternships: Boolean = false
    var shouldThrowOnGetInternshipsById: Boolean = false
    var shouldThrowOnGetInternshipsByStudent: Boolean = false
    var shouldThrowOnGetInternshipsByInstitution: Boolean = false
    var shouldThrowOnGetInternshipsByStatus: Boolean = false
    var shouldThrowOnCreateInternship: Boolean = false
    var shouldThrowOnAssignSupervisor: Boolean = false
    var shouldThrowOnGetActivityLogsByInternship: Boolean = false
    var shouldThrowOnCreateActivityLog: Boolean = false

    override suspend fun getInternships(): List<InternshipModel> {
        if (shouldThrowOnGetInternships) {
            throw IllegalStateException("Erro ao carregar estágios.")
        }
        return internships
    }

    override suspend fun getInternshipById(internshipId: String): InternshipModel? {
        if (shouldThrowOnGetInternshipsById) {
            throw IllegalStateException("Erro ao carregar estágios.")
        }
        return internships.firstOrNull { it.id == internshipId }
    }

    override suspend fun getInternshipsByStudent(studentId: String): List<InternshipModel> {
        if (shouldThrowOnGetInternshipsByStudent) {
            throw IllegalStateException("Erro ao carregar estágios.")
        }
        return internships.filter { it.studentId == studentId }
    }

    override suspend fun getInternshipsByInstitution(institutionId: String): List<InternshipModel> {
        if (shouldThrowOnGetInternshipsByInstitution) {
            throw IllegalStateException("Erro ao carregar estágios.")
        }
        return internships.filter { it.institutionId == institutionId }
    }

    override suspend fun getInternshipsByStatus(status: InternshipStatus): List<InternshipModel> {
        if (shouldThrowOnGetInternshipsByStatus) {
            throw IllegalStateException("Erro ao carregar estágios.")
        }
        return internships.filter { it.status == status }
    }

    override suspend fun createInternship(input: CreateInternshipInput): InternshipModel {
        if (shouldThrowOnGetInternships) {
            throw IllegalStateException("Erro ao criar estágio.")
        }

        return InternshipModel(
            id = "internship-id",
            applicationId = input.applicationId,
            offerId = input.offerId,
            studentId = input.studentId,
            institutionId = input.institutionId,
            supervisorId = input.supervisorId,
            title = input.title,
            companySupervisorName = input.companySupervisorName,
            startDate = input.startDate,
            endDate = input.endDate,
            status = input.status,
            workPlan = input.workPlan,
            objectives = input.objectives,
            createdAt = "2025-01-01T00:00:00Z",
            updatedAt = "2025-01-01T00:00:00Z"
        )
    }

    override suspend fun assignSupervisor(
        internshipId: String,
        input: AssignSupervisorInput
    ): InternshipModel {
        if (shouldThrowOnAssignSupervisor) {
            throw IllegalStateException("Erro ao carregar estágios.")
        }
        return internships.first { it.id == internshipId }
    }

    override suspend fun getActivityLogsByInternship(internshipId: String): List<ActivityLogModel> {
        if (shouldThrowOnGetActivityLogsByInternship) {
            throw IllegalStateException("Erro ao carregar estágios.")
        }
        return activities.filter { it.internshipId == internshipId }
    }

    override suspend fun createActivityLog(input: CreateActivityLogInput): ActivityLogModel {
        if (shouldThrowOnCreateActivityLog) {
            throw IllegalStateException("Erro ao carregar estágios.")
        }
        return ActivityLogModel(
            id = "00000000-0000-0000-0000-000000000010",
            internshipId = input.internshipId,
            studentId = input.studentId,
            description = input.description,
            activityDate = input.activityDate,
            hours = input.hours,
            type = input.type,
            location = input.location,
            createdAt = "2026-01-15T00:00:00Z"
        )
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
