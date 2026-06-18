package turmaA.grupoB.LinkStage.viewmodel.report

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
import org.junit.runner.Description
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import turmaA.grupoB.LinkStage.data.remote.model.enums.ReportStatus
import turmaA.grupoB.LinkStage.data.remote.model.report.CreateFinalReportInput
import turmaA.grupoB.LinkStage.data.remote.model.report.FinalReportModel
import turmaA.grupoB.LinkStage.data.remote.model.report.UpdateFinalReportInput
import turmaA.grupoB.LinkStage.data.repository.report.ReportRepositoryInterface

@OptIn(ExperimentalCoroutinesApi::class)
class ReportViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepository: FakeReportRepository
    private lateinit var viewModel: ReportViewModel

    @Before
    fun setup() {
        fakeRepository = FakeReportRepository()
        viewModel = ReportViewModel(fakeRepository)
    }

    @Test
    fun initialState_isIdle() {
        assertEquals(
            ReportUiState.Idle,
            viewModel.uiState.value
        )
    }

    // --- loadReports ---

    @Test
    fun loadReports_whenReportsExist_setsSuccessListState() = runTest {
        fakeRepository.reports = listOf(testReport)

        viewModel.loadReports()

        advanceUntilIdle()

        assertEquals(
            ReportUiState.SuccessList(listOf(testReport)),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadReports_whenReportsDoNotExist_setsEmptyState() = runTest {
        fakeRepository.reports = emptyList()

        viewModel.loadReports()

        advanceUntilIdle()

        assertEquals(
            ReportUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadReports_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetReports = true

        viewModel.loadReports()

        advanceUntilIdle()

        assertEquals(
            ReportUiState.Error("Erro ao carregar relatórios."),
            viewModel.uiState.value
        )
    }

    // --- loadReportById ---

    @Test
    fun loadReportById_whenReportExists_setsSuccessState() = runTest {
        fakeRepository.reportById = testReport

        viewModel.loadReportById(testReport.id)

        advanceUntilIdle()

        assertEquals(
            ReportUiState.Success(testReport),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadReportById_whenReportDoesNotExist_setsEmptyState() = runTest {
        fakeRepository.reportById = null

        viewModel.loadReportById(testReport.id)

        advanceUntilIdle()

        assertEquals(
            ReportUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadReportById_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetReportById = true

        viewModel.loadReportById(testReport.id)

        advanceUntilIdle()

        assertEquals(
            ReportUiState.Error("Erro ao carregar relatório."),
            viewModel.uiState.value
        )
    }

    // --- loadReportByInternship ---

    @Test
    fun loadReportByInternship_whenReportExists_setsSuccessState() = runTest {
        fakeRepository.reportByInternship = testReport

        viewModel.loadReportByInternship(testReport.internshipId)

        advanceUntilIdle()

        assertEquals(
            ReportUiState.Success(testReport),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadReportByInternship_whenReportDoesNotExist_setsEmptyState() = runTest {
        fakeRepository.reportByInternship = null

        viewModel.loadReportByInternship(testReport.internshipId)

        advanceUntilIdle()

        assertEquals(
            ReportUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadReportByInternship_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetReportByInternship = true

        viewModel.loadReportByInternship(testReport.internshipId)

        advanceUntilIdle()

        assertEquals(
            ReportUiState.Error("Erro ao carregar relatório por estágio."),
            viewModel.uiState.value
        )
    }

    // --- loadReportsByStudent ---

    @Test
    fun loadReportsByStudent_whenReportsExist_setsSuccessListState() = runTest {
        fakeRepository.reportsByStudent = listOf(testReport)

        viewModel.loadReportsByStudent(testReport.studentId)

        advanceUntilIdle()

        assertEquals(
            ReportUiState.SuccessList(listOf(testReport)),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadReportsByStudent_whenReportsDoNotExist_setsEmptyState() = runTest {
        fakeRepository.reportsByStudent = emptyList()

        viewModel.loadReportsByStudent(testReport.studentId)

        advanceUntilIdle()

        assertEquals(
            ReportUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadReportsByStudent_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetReportsByStudent = true

        viewModel.loadReportsByStudent(testReport.studentId)

        advanceUntilIdle()

        assertEquals(
            ReportUiState.Error("Erro ao carregar relatórios por estudante."),
            viewModel.uiState.value
        )
    }

    // --- loadReportsByStatus ---

    @Test
    fun loadReportsByStatus_whenReportsExist_setsSuccessListState() = runTest {
        fakeRepository.reportsByStatus = listOf(testReport)

        viewModel.loadReportsByStatus(ReportStatus.DRAFT)

        advanceUntilIdle()

        assertEquals(
            ReportUiState.SuccessList(listOf(testReport)),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadReportsByStatus_whenReportsDoNotExist_setsEmptyState() = runTest {
        fakeRepository.reportsByStatus = emptyList()

        viewModel.loadReportsByStatus(ReportStatus.DRAFT)

        advanceUntilIdle()

        assertEquals(
            ReportUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadReportsByStatus_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetReportsByStatus = true

        viewModel.loadReportsByStatus(ReportStatus.DRAFT)

        advanceUntilIdle()

        assertEquals(
            ReportUiState.Error("Erro ao carregar relatórios por estado."),
            viewModel.uiState.value
        )
    }

    @Test
    fun createReport_withValidData_setsSuccessState() = runTest {
        fakeRepository.reportToReturn = testReport

        viewModel.createReport(testCreateFinalReportModel)

        advanceUntilIdle()

        assertEquals(
            ReportUiState.Success(testReport),
            viewModel.uiState.value
        )
    }

    @Test
    fun updateReport_withValidData_setsSuccessState() = runTest {
        fakeRepository.reportToReturn = updatedReport

        viewModel.updateReport(
            reportId = testReport.id,
            input = testUpdateFinalReportInput
        )

        advanceUntilIdle()

        assertEquals(
            ReportUiState.Success(updatedReport),
            viewModel.uiState.value
        )
    }

    @Test
    fun submitReport_setsSuccessState() = runTest {
        fakeRepository.reportToReturn = submittedReport

        viewModel.submitReport(testReport.id)

        advanceUntilIdle()

        assertEquals(
            ReportUiState.Success(submittedReport),
            viewModel.uiState.value
        )
    }

    // --- resetState ---

    @Test
    fun resetState_setsIdleState() = runTest {
        fakeRepository.reports = listOf(testReport)

        viewModel.loadReports()

        advanceUntilIdle()

        viewModel.resetState()

        assertEquals(
            ReportUiState.Idle,
            viewModel.uiState.value
        )
    }

    private companion object {
        val testReport = FinalReportModel(
            id = "00000000-0000-0000-0000-000000000001",
            internshipId = "00000000-0000-0000-0000-000000000002",
            studentId = "00000000-0000-0000-0000-000000000003",
            title = "Test Report",
            content = "Test report content",
            fileUrl = null,
            status = ReportStatus.DRAFT,
            createdAt = "2026-01-01T00:00:00Z",
            updatedAt = "2026-01-01T00:00:00Z"
        )

        val updatedReport = testReport.copy(
            title = "Updated Test Report",
            content = "updated report content",
            updatedAt = "2026-01-02T00:00:00Z"
        )

        val submittedReport = testReport.copy(
            status = ReportStatus.SUBMITTED,
            updatedAt = "2026-01-02T00:00:00Z"
        )

        val testCreateFinalReportModel = CreateFinalReportInput(
            internshipId = testReport.internshipId,
            studentId = testReport.studentId,
            title = testReport.title,
            content = testReport.content,
            fileUrl = testReport.fileUrl,
            status = testReport.status,
        )

        val testUpdateFinalReportInput = UpdateFinalReportInput(
            title = updatedReport.title,
            content = updatedReport.content,
            fileUrl = updatedReport.fileUrl,
            status = updatedReport.status,
            updatedAt = updatedReport.updatedAt
        )

    }
}

private class FakeReportRepository : ReportRepositoryInterface {

    var reports: List<FinalReportModel> = emptyList()
    var reportById: FinalReportModel? = null
    var reportByInternship: FinalReportModel? = null
    var reportsByStudent: List<FinalReportModel> = emptyList()
    var reportsByStatus: List<FinalReportModel> = emptyList()
    var createdReport: FinalReportModel? = null
    var updatedReport: FinalReportModel? = null
    var submittedReport: FinalReportModel? = null

    var reportToReturn: FinalReportModel? = null
    var shouldThrowOnGetReports: Boolean = false
    var shouldThrowOnGetReportById: Boolean = false
    var shouldThrowOnGetReportByInternship: Boolean = false
    var shouldThrowOnGetReportsByStudent: Boolean = false
    var shouldThrowOnGetReportsByStatus: Boolean = false
    var shouldThrowOnCreateReport: Boolean = false
    var shouldThrowOnUpdateReport: Boolean = false
    var shouldThrowOnSubmitReport: Boolean = false

    override suspend fun getReports(): List<FinalReportModel> {
        if (shouldThrowOnGetReports) {
            throw IllegalStateException("Erro ao carregar relatórios.")
        }
        return reports
    }

    override suspend fun getReportById(reportId: String): FinalReportModel? {
        if (shouldThrowOnGetReportById) {
            throw IllegalStateException("Erro ao carregar relatório.")
        }
        return reportById
    }

    override suspend fun getReportByInternship(internshipId: String): FinalReportModel? {
        if (shouldThrowOnGetReportByInternship) {
            throw IllegalStateException("Erro ao carregar relatório por estágio.")
        }
        return reportByInternship
    }

    override suspend fun getReportsByStudent(studentId: String): List<FinalReportModel> {
        if (shouldThrowOnGetReportsByStudent) {
            throw IllegalStateException("Erro ao carregar relatórios por estudante.")
        }
        return reportsByStudent
    }

    override suspend fun getReportsByStatus(status: ReportStatus): List<FinalReportModel> {
        if (shouldThrowOnGetReportsByStatus) {
            throw IllegalStateException("Erro ao carregar relatórios por estado.")
        }
        return reportsByStatus
    }

    override suspend fun createReport(input: CreateFinalReportInput) : FinalReportModel {
        if (shouldThrowOnCreateReport) {
            throw IllegalStateException("Erro ao criar relatório.")
        }

        return reportToReturn ?: throw IllegalStateException("Relatório não encontrado.")
    }

    override suspend fun updateReport(
        reportId: String,
        input: UpdateFinalReportInput
    ): FinalReportModel {
        if (shouldThrowOnUpdateReport) {
            throw IllegalStateException("Erro ao atualizar relatório.")
        }
        return reportToReturn ?: throw IllegalStateException("Relatório não encontrado.")
    }

    override suspend fun submitReport(reportId: String): FinalReportModel {
        if (shouldThrowOnUpdateReport) {
            throw IllegalStateException("Erro ao submeter relatório.")
        }
        return reportToReturn ?: throw IllegalStateException("Relatório não encontrado.")
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
