package turmaA.grupoB.LinkStage.viewmodel.evaluation

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
import turmaA.grupoB.LinkStage.data.remote.model.enums.EvaluationType
import turmaA.grupoB.LinkStage.data.remote.model.evaluation.CreateEvaluationInput
import turmaA.grupoB.LinkStage.data.remote.model.evaluation.EvaluationModel
import turmaA.grupoB.LinkStage.data.remote.model.evaluation.FinalGradeModel
import turmaA.grupoB.LinkStage.data.repository.evaluation.EvaluationRepositoryInterface
import turmaA.grupoB.LinkStage.viewmodel.application.MainDispatcherRule

@OptIn(ExperimentalCoroutinesApi::class)
class EvaluationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepository: FakeEvaluationRepository
    private lateinit var viewModel: EvaluationViewModel

    @Before
    fun setup() {
        fakeRepository = FakeEvaluationRepository()
        viewModel = EvaluationViewModel(fakeRepository)
    }

    @Test
    fun initialState_isIdle(){
        assertEquals(
            EvaluationUiState.Idle,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadEvaluations_WhenEvaluationsExist_setsSuccessListState() = runTest {
        fakeRepository.evaluations = listOf(testEvaluation)

        viewModel.loadEvaluations()

        advanceUntilIdle()

        assertEquals(
            EvaluationUiState.SuccessList(listOf(testEvaluation)),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadEvaluations_whenEvaluationsDoNotExist_setsEmptyState() = runTest {
        fakeRepository.evaluations = emptyList()

        viewModel.loadEvaluations()

        advanceUntilIdle()

        assertEquals(
            EvaluationUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadEvaluations_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetEvaluations = true

        viewModel.loadEvaluations()

        advanceUntilIdle()

        assertEquals(
            EvaluationUiState.Error("Erro ao carregar avaliações."),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadEvaluationsByInternship_whenEvaluationsExist_setsSuccessListState() = runTest {
        fakeRepository.evaluations = listOf(testEvaluation)

        viewModel.loadEvaluationsByInternship(testInternshipId)

        advanceUntilIdle()

        assertEquals(
            EvaluationUiState.SuccessList(listOf(testEvaluation)),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadEvaluationsByInternShip_whenEvaluationsDoNotExist_setsEmptyState() = runTest {
        fakeRepository.evaluations = emptyList()

        viewModel.loadEvaluationsByInternship(testInternshipId)

        advanceUntilIdle()

        assertEquals(
            EvaluationUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadEvaluationByInternshipAndType_whenEvaluationExists_setsSuccessState() = runTest {
        fakeRepository.evaluations = listOf(testEvaluation)

        viewModel.loadEvaluationByInternshipAndType(
            internshipId = testInternshipId,
            evaluationType = EvaluationType.INSTITUTION
        )

        advanceUntilIdle()

        assertEquals(
            EvaluationUiState.Success(testEvaluation),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadEvaluationByInternshipAndType_whenEvaluationDoesNotExist_setsEmptyState() = runTest {
        fakeRepository.evaluationToReturn = null

        viewModel.loadEvaluationByInternshipAndType(
            internshipId = testInternshipId,
            evaluationType = EvaluationType.INSTITUTION
        )

        advanceUntilIdle()

        assertEquals(
            EvaluationUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun createEvaluation_withValidData_setsSuccessState() = runTest {
        fakeRepository.evaluationToReturn = testEvaluation

        viewModel.createEvaluation(testCreateEvaluationInput)

        advanceUntilIdle()

        assertEquals(
            EvaluationUiState.Success(testEvaluation),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadGradingByInternship_whenFinalGradeExists_setsFinalGradeSuccessStatus() = runTest {
        fakeRepository.finalGrade = testFinalGrade

        viewModel.loadGradingByInternship(testInternshipId)

        advanceUntilIdle()

        assertEquals(
            EvaluationUiState.FinalGradeSuccess(testFinalGrade),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadGradingByInternship_whenFinalGradeDoesNotExist_setsEmptyState() = runTest {
        fakeRepository.finalGrade = null

        viewModel.loadGradingByInternship(testInternshipId)

        advanceUntilIdle()

        assertEquals(
            EvaluationUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun resetState_setsIdleSate() = runTest {
        fakeRepository.evaluations = listOf(testEvaluation)

        viewModel.loadEvaluations()

        advanceUntilIdle()

        viewModel.resetState()

        assertEquals(
            EvaluationUiState.Idle,
            viewModel.uiState.value
        )
    }

    private companion object {
        const val testInternshipId = "00000000-0000-0000-0000-000000000010"
        const val testEvaluatorUserId = "00000000-0000-0000-0000-000000000020"

        val testEvaluation = EvaluationModel(
            id = "00000000-0000-0000-0000-000000000001",
            internshipId = testInternshipId,
            evaluatorUserId = testEvaluatorUserId,
            evaluatorType = EvaluationType.INSTITUTION,
            grade = 17.5,
            comment = "Bom desempenho.",
            createdAt = "2026-01-01T00:00:00Z",
            updatedAt = "2026-01-01T00:00:00Z"
        )

        val testFinalGrade = FinalGradeModel(
            id = "00000000-0000-0000-0000-000000000002",
            internshipId = testInternshipId,
            grade = 18.0,
            comment = "Nota final calculada.",
            createdAt = "2026-01-01T00:00:00Z"
        )

        val testCreateEvaluationInput = CreateEvaluationInput(
            internshipId = testInternshipId,
            evaluatorUserId = testEvaluatorUserId,
            evaluatorType = EvaluationType.INSTITUTION,
            grade = testEvaluation.grade,
            comment = testEvaluation.comment
        )
    }
}

private class FakeEvaluationRepository : EvaluationRepositoryInterface {
    var evaluations: List<EvaluationModel> = emptyList()
    var evaluationToReturn: EvaluationModel? = null
    var finalGrade: FinalGradeModel? = null
    var shouldThrowOnGetEvaluations: Boolean = false
    var shouldThrowOnGetEvaluationsByInternship: Boolean = false
    var shouldThrowOnGetEvaluationByIntershipAndType: Boolean = false
    var shouldThrowOnCreateEvaluation: Boolean = false
    var shouldThrowOnGetFinalGradeByInternship: Boolean = false

    override suspend fun getEvaluations(): List<EvaluationModel> {
        if (shouldThrowOnGetEvaluations) {
            throw IllegalStateException("Erro ao carregar avaliações.")
        }

        return evaluations
    }

    override suspend fun getEvaluationsByInternship(internshipId: String): List<EvaluationModel> {
        if (shouldThrowOnGetEvaluationsByInternship) {
            throw IllegalStateException("Erro ao carregar avaliações do estagiágio.")
        }

        return evaluations.filter { it.internshipId == internshipId }
    }

    override suspend fun getEvaluationByInternshipAndType(
        internshipId: String,
        evaluatorType: EvaluationType
    ): EvaluationModel? {
        if (shouldThrowOnGetEvaluationByIntershipAndType) {
            throw IllegalStateException("Erro ao carregar avaliação por tipo.")
        }

        return evaluations.firstOrNull {
            it.internshipId == internshipId && it.evaluatorType == evaluatorType
        }
    }

    override suspend fun createEvaluation(input: CreateEvaluationInput): EvaluationModel {
        if (shouldThrowOnCreateEvaluation) {
            throw IllegalStateException("Erro ao criar avaliação.")
        }

        return evaluationToReturn ?: throw IllegalStateException("Avaliação não encontrada.")
    }

    override suspend fun getFinalGradeByInternship(internshipId: String): FinalGradeModel? {
        if (shouldThrowOnGetFinalGradeByInternship) {
            throw IllegalStateException("Erro ao carregar nota final.")
        }

        return finalGrade?.takeIf { it.internshipId == internshipId }
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