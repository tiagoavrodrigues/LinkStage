package turmaA.grupoB.LinkStage.viewmodel.student

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
import turmaA.grupoB.LinkStage.data.remote.model.user.StudentModel
import turmaA.grupoB.LinkStage.data.remote.model.user.CreateStudentInput
import turmaA.grupoB.LinkStage.data.repository.student.StudentRepositoryInterface

@OptIn(ExperimentalCoroutinesApi::class)
class StudentViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepository: FakeStudentRepository
    private lateinit var viewModel: StudentViewModel

    @Before
    fun setup() {
        fakeRepository = FakeStudentRepository()
        viewModel = StudentViewModel(fakeRepository)
    }

    @Test
    fun initialState_isIdle() {
        assertEquals(
            StudentUiState.Idle,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadStudents_whenStudentsExist_setsSuccessListState() = runTest {
        fakeRepository.students = listOf(testStudent)

        viewModel.loadStudents()

        advanceUntilIdle()

        assertEquals(
            StudentUiState.SuccessList(listOf(testStudent)),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadStudents_whenStudentsDoNotExist_setsEmptyState() = runTest {
        fakeRepository.students = emptyList()

        viewModel.loadStudents()

        advanceUntilIdle()

        assertEquals(
            StudentUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadStudents_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetStudents = true

        viewModel.loadStudents()

        advanceUntilIdle()

        assertEquals(
            StudentUiState.Error("Erro ao carregar estudantes."),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadStudentById_whenStudentExists_setsSuccessState() = runTest {
        fakeRepository.students = listOf(testStudent)

        viewModel.loadStudentById(testStudent.id)

        advanceUntilIdle()

        assertEquals(
            StudentUiState.Success(testStudent),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadStudentById_whenStudentDoesNotExist_setsEmptyState() = runTest {
        fakeRepository.students = emptyList()

        viewModel.loadStudentById("unknow-user-id")

        advanceUntilIdle()

        assertEquals(
            StudentUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadStudentById_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetStudentById = true

        viewModel.loadStudentById(testStudent.id)

        advanceUntilIdle()

        assertEquals(
            StudentUiState.Error("Erro ao carregar estudante."),
            viewModel.uiState.value
        )
    }


    @Test
    fun loadStudentByUserId_whenStudentExists_setsSuccessState() = runTest {
        fakeRepository.students = listOf(testStudent)

        viewModel.loadStudentByUserId(testStudent.userId)

        advanceUntilIdle()

        assertEquals(
            StudentUiState.Success(testStudent),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadStudentByUserId_whenStudentDoestNotExist_setsEmptyState() = runTest {
        fakeRepository.students = emptyList()

        viewModel.loadStudentByUserId("unknow-user-id")

        advanceUntilIdle()

        assertEquals(
            StudentUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadStudentByUserId_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetStudentByUserId = true

        viewModel.loadStudentByUserId(testStudent.id)

        advanceUntilIdle()

        assertEquals(
            StudentUiState.Error("Erro ao carregar estudante do utilizador."),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadStudentByNumber_whenStudentExists_setsSuccessState() = runTest {
        fakeRepository.students = listOf(testStudent)

        viewModel.loadStudentByNumber(testStudent.studentNumber)

        advanceUntilIdle()

        assertEquals(
            StudentUiState.Success(testStudent),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadStudentByNumber_whenStudentDoesNotExist_setsEmptyState() = runTest {
        fakeRepository.students = emptyList()

        viewModel.loadStudentByNumber("unknow_number")

        advanceUntilIdle()

        assertEquals(
            StudentUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadStudentByNumber_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetStudentByNumber = true

        viewModel.loadStudentByNumber(testStudent.studentNumber)

        advanceUntilIdle()

        assertEquals(
            StudentUiState.Error("Erro ao carregar estudante por número."),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadStudentsByCourse_whenStudentsExist_setsSuccessState() = runTest {
        fakeRepository.students = listOf(testStudent)

        viewModel.loadStudentsByCourse(testStudent.course)

        advanceUntilIdle()

        assertEquals(
            StudentUiState.SuccessList(listOf(testStudent)),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadStudentsByCourse_whenStudentsDoNotExist_setsEmptyState() = runTest {
        fakeRepository.students = emptyList()

        viewModel.loadStudentsByCourse("Curso Inexistente")

        advanceUntilIdle()

        assertEquals(
            StudentUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadStudentsByCourse_whenRepositoryThrows_setsEmptyState() = runTest {
        fakeRepository.shouldThrowOnGetStudentsByCourse = true

        viewModel.loadStudentsByCourse(testStudent.course)

        advanceUntilIdle()

        assertEquals(
            StudentUiState.Error("Erro ao carregar estudantes por curso."),
            viewModel.uiState.value
        )
    }

    @Test
    fun resetState_setsIdleState() = runTest {
        fakeRepository.students = listOf(testStudent)

        viewModel.loadStudents()

        advanceUntilIdle()

        viewModel.resetState()

        assertEquals(
            StudentUiState.Idle,
            viewModel.uiState.value
        )
    }

    private companion object {
        const val testUserId = "00000000-0000-0000-0000-000000000020"

        val testStudent = StudentModel(
            id = "00000000-0000-0000-0000-000000000001",
            userId = testUserId,
            studentNumber = "20260001",
            course = "Engenharia Informática",
            academicYear = "3",
            averageGrade = 15.8,
            cvData = null,
            createdAt = "2026-01-01T00:00:00Z",
            updatedAt = "2026-01-01T00:00:00Z"
        )
    }
}

private class FakeStudentRepository : StudentRepositoryInterface {
    var students: List<StudentModel> = emptyList()

    var shouldThrowOnGetStudents: Boolean = false
    var shouldThrowOnGetStudentById: Boolean = false
    var shouldThrowOnGetStudentByUserId: Boolean = false
    var shouldThrowOnGetStudentByNumber: Boolean = false
    var shouldThrowOnGetStudentsByCourse: Boolean = false
    var shouldThrowOnCreateStudent: Boolean = false

    override suspend fun getStudents(): List<StudentModel> {
        if (shouldThrowOnGetStudents) {
            throw IllegalStateException("Erro ao carregar estudantes.")
        }

        return students
    }

    override suspend fun getStudentById(studentId: String): StudentModel? {
        if (shouldThrowOnGetStudentById) {
            throw IllegalStateException("Erro ao carregar estudante.")
        }

        return students.firstOrNull { it.id == studentId }
    }

    override suspend fun getStudentByUserId(userId: String): StudentModel? {
        if (shouldThrowOnGetStudentByUserId) {
            throw IllegalStateException("Erro ao carregar estudante do utilizador.")
        }

        return students.firstOrNull { it.userId == userId }
    }

    override suspend fun getStudentByNumber(studentNumber: String): StudentModel? {
        if (shouldThrowOnGetStudentByNumber) {
            throw IllegalStateException("Erro ao carregar estudante por número.")
        }

        return students.firstOrNull { it.studentNumber == studentNumber }
    }

    override suspend fun getStudentsByCourse(course: String): List<StudentModel> {
        if (shouldThrowOnGetStudentsByCourse) {
            throw IllegalStateException("Erro ao carregar estudantes por curso.")
        }

        return students.filter { it.course == course }
    }

    override suspend fun createStudent(input: CreateStudentInput): StudentModel {
        TODO("Not yet implemented")
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description?) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        Dispatchers.resetMain()
    }
}

