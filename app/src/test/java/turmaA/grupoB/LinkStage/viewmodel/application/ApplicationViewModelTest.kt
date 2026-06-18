package turmaA.grupoB.LinkStage.viewmodel.application

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
import turmaA.grupoB.LinkStage.data.remote.model.application.ApplicationModel
import turmaA.grupoB.LinkStage.data.remote.model.application.CreateApplicationInput
import turmaA.grupoB.LinkStage.data.remote.model.application.UpdateApplicationDecisionInput
import turmaA.grupoB.LinkStage.data.remote.model.enums.ApplicationStatus
import turmaA.grupoB.LinkStage.data.repository.application.ApplicationRepositoryInterface

@OptIn(ExperimentalCoroutinesApi::class)
class ApplicationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepository: FakeApplicationRepository
    private lateinit var viewModel: ApplicationViewModel

    @Before
    fun setup() {
        fakeRepository = FakeApplicationRepository()
        viewModel = ApplicationViewModel(fakeRepository)
    }

    @Test
    fun initialState_isIdle() {
        assertEquals(
            ApplicationUiState.Idle,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadApplicationById_whenApplicationExists_setsSuccessState() = runTest {
        fakeRepository.applications = listOf(testApplication)

        viewModel.loadApplicationById(testApplication.id)

        advanceUntilIdle()

        assertEquals(
            ApplicationUiState.Success(testApplication),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadApplicationById_WhenApplicationDoesNotExist_setsEmptyState() = runTest {
        fakeRepository.applications = emptyList()

        viewModel.loadApplicationById("unknown-applicaiton-id")

        advanceUntilIdle()

        assertEquals(
            ApplicationUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadApplicationsByOffer_whenApplicationsExist_setsSuccessListState() = runTest {
        fakeRepository.applications = listOf(testApplication)

        viewModel.loadApplicationsByOffer(testOfferId)

        advanceUntilIdle()

        assertEquals(
            ApplicationUiState.SuccessList(listOf(testApplication)),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadApplicationsByOffer_whenApplicationsDonotExist_setsEmptyState() = runTest {
        fakeRepository.applications = emptyList()

        viewModel.loadApplicationsByOffer(testOfferId)

        advanceUntilIdle()

        assertEquals(
            ApplicationUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadApplicationsByStudent_WhenApplicaitonsExist_setsSuccessListState() = runTest {
        fakeRepository.applications = listOf(testApplication)

        viewModel.loadApplicationsByStudent(testStudentId)

        advanceUntilIdle()

        assertEquals(
            ApplicationUiState.SuccessList(listOf(testApplication)),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadApplicationsByStatus_whenApplicationsExist_setsSuccessListState() = runTest {
        fakeRepository.applications = listOf(testApplication)

        viewModel.loadApplicationsByStatus(ApplicationStatus.PENDING)

        advanceUntilIdle()

        assertEquals(
            ApplicationUiState.SuccessList(listOf(testApplication)),
            viewModel.uiState.value
        )
    }

    @Test
    fun createApplication_withValidData_setsSuccessState() = runTest {
        fakeRepository.applicationToReturn = testApplication

        viewModel.createApplication(testCreateApplicationInput)

        advanceUntilIdle()

        assertEquals(
            ApplicationUiState.Success(testApplication),
            viewModel.uiState.value
        )
    }

    @Test
    fun updateApplicationDecision_withValidData_setsSuccessState() = runTest {
        fakeRepository.applicationToReturn = acceptedApplication

        viewModel.updateApplicationDecision(
            applicationId = testApplication.id,
            input = testUpdateApplicationDecisionInput
        )

        advanceUntilIdle()

        assertEquals(
            ApplicationUiState.Success(acceptedApplication),
            viewModel.uiState.value
        )
    }

    @Test
    fun acceptApplication_setsSuccessState() = runTest {
        fakeRepository.applicationToReturn = acceptedApplication

        viewModel.acceptApplication(testApplication.id)

        advanceUntilIdle()

        assertEquals(
            ApplicationUiState.Success(acceptedApplication),
            viewModel.uiState.value
        )
    }

    @Test
    fun rejectApplication_setsSuccessState() = runTest {
        fakeRepository.applicationToReturn = rejectedApplication

        viewModel.rejectApplication(
            applicationId = testApplication.id,
            rejectionReason = "Perfil não corresponde aos requisitos."
        )

        advanceUntilIdle()

        assertEquals(
            ApplicationUiState.Success(rejectedApplication),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadApplicationsByStudent_whenRepositoryThrows_SetsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetApplicationsByStudent = true

        viewModel.loadApplicationsByStudent(testStudentId)

        advanceUntilIdle()

        assertEquals(
            ApplicationUiState.Error("Erro ao carregar candidaturas do estudante."),
            viewModel.uiState.value
        )
    }

    @Test
    fun resetState_setsIdleState() = runTest {
        fakeRepository.applications = listOf(testApplication)

        viewModel.loadApplicationsByStudent(testStudentId)

        advanceUntilIdle()

        viewModel.resetState()

        assertEquals(
            ApplicationUiState.Idle,
            viewModel.uiState.value
        )
    }

    private companion object {
        const val testApplicationId = "00000000-0000-0000-0000-000000000001"
        const val testOfferId = "00000000-0000-0000-0000-000000000010"
        const val testStudentId = "00000000-0000-0000-0000-000000000020"

        val testApplication = ApplicationModel(
            id = testApplicationId,
            offerId = testOfferId,
            studentId = testStudentId,
            status = ApplicationStatus.PENDING,
            motivationLetter = "Tenho interesse nesta oportunidade.",
            rejectionReason = null,
            decisionAt = null,
            createdAt = "2026-01-01T00:00:00Z",
            updatedAt = "2026-01-01T00:00:00Z"
        )

        val acceptedApplication = testApplication.copy(
            status = ApplicationStatus.ACCEPTED,
            decisionAt = "2026-01-02T00:00:00Z"
        )

        val rejectedApplication = testApplication.copy(
            status = ApplicationStatus.REJECTED,
            rejectionReason = "Perfil não corresponde aos requisitos.",
            decisionAt = "2026-01-02T00:00:00Z"
        )

        val testCreateApplicationInput = CreateApplicationInput(
            offerId = testOfferId,
            studentId = testStudentId,
            motivationLetter = testApplication.motivationLetter
        )

        val testUpdateApplicationDecisionInput = UpdateApplicationDecisionInput(
            status = ApplicationStatus.ACCEPTED,
            rejectionReason = null,
            decisionAt = "2026-01-02T00:00:00Z"
        )
    }
}

private class FakeApplicationRepository : ApplicationRepositoryInterface {

    var applications: List<ApplicationModel> = emptyList()
    var applicationToReturn: ApplicationModel? = null

    var shouldThrowOnGetApplicationById: Boolean = false
    var shouldThrowOnGetApplicationsByOffer: Boolean = false
    var shouldThrowOnGetApplicationsByStudent: Boolean = false
    var shouldThrowOnGetApplicationsByStatus: Boolean = false
    var shouldThrowOnCreateApplication: Boolean = false
    var shouldThrowOnUpdateApplicationDecision: Boolean = false
    var shouldThrowOnAcceptApplication: Boolean = false
    var shouldThrowOnRejectApplication: Boolean = false

    override suspend fun getApplicationById(applicationId: String): ApplicationModel? {
        if(shouldThrowOnUpdateApplicationDecision) {
            throw IllegalStateException("Erro ao carregar candidatura.")
        }

        return applications.firstOrNull { it.id == applicationId }
    }

    override suspend fun getApplicationsByOffer(offerId: String): List<ApplicationModel> {
        if (shouldThrowOnGetApplicationsByOffer) {
            throw IllegalStateException("Erro ao carregar candidaturas da oferta.")
        }

        return applications.filter { it.offerId == offerId }
    }

    override suspend fun getApplicationsByStudent(studentId: String): List<ApplicationModel> {
        if (shouldThrowOnGetApplicationsByStudent) {
            throw IllegalStateException("Erro ao carregar candidaturas do estudante.")
        }

        return applications.filter { it.studentId == studentId }
    }

    override suspend fun getApplicationsByStatus(status: ApplicationStatus): List<ApplicationModel> {
        if (shouldThrowOnGetApplicationsByStatus) {
            throw IllegalStateException("Erro ao carregar candidaturas por estado.")
        }

        return applications.filter { it.status == status }
    }

    override suspend fun createApplication(input: CreateApplicationInput): ApplicationModel {
        if (shouldThrowOnCreateApplication) {
            throw IllegalStateException("Erro ao criar candidatura.")
        }

        return applicationToReturn ?: throw IllegalStateException("Candidatura não encontrada.")
    }

    override suspend fun updateApplicationDecision(
        applicationId: String,
        input: UpdateApplicationDecisionInput
    ): ApplicationModel {
        if (shouldThrowOnUpdateApplicationDecision) {
            throw IllegalStateException("Erro ao atualizar decisão da candidatura.")
        }

        return applicationToReturn ?: throw IllegalStateException("Candidatura não encontrada.")
    }

    override suspend fun acceptApplication(applicationId: String): ApplicationModel {
        if (shouldThrowOnAcceptApplication) {
            throw IllegalStateException("Erro ao aceitar candidatura.")
        }

        return applicationToReturn ?: throw IllegalStateException("Candidatura não encontrada.")
    }

    override suspend fun rejectApplication(
        applicationId: String,
        rejectionReason: String
    ): ApplicationModel {
        if (shouldThrowOnRejectApplication) {
            throw IllegalStateException("Erro ao rejeitar candidatura")
        }

        return applicationToReturn ?: throw IllegalStateException("Candidatura não encontrada.")
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