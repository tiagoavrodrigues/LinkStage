package turmaA.grupoB.LinkStage.viewmodel.admin

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import turmaA.grupoB.LinkStage.data.remote.model.application.ApplicationModel
import turmaA.grupoB.LinkStage.data.remote.model.enums.ApplicationStatus
import turmaA.grupoB.LinkStage.data.remote.model.enums.InternshipStatus
import turmaA.grupoB.LinkStage.data.remote.model.enums.UserRole
import turmaA.grupoB.LinkStage.data.remote.model.institution.CreateInstitutionInput
import turmaA.grupoB.LinkStage.data.remote.model.institution.InstitutionModel
import turmaA.grupoB.LinkStage.data.remote.model.internship.InternshipModel
import turmaA.grupoB.LinkStage.data.remote.model.offer.InternshipOfferModel
import turmaA.grupoB.LinkStage.data.remote.model.user.ProfileModel
import turmaA.grupoB.LinkStage.data.remote.model.user.StudentModel
import turmaA.grupoB.LinkStage.data.remote.model.user.CreateSupervisorInput
import turmaA.grupoB.LinkStage.data.remote.model.user.SupervisorModel
import turmaA.grupoB.LinkStage.data.remote.model.user.SupervisorSkillModel
import turmaA.grupoB.LinkStage.data.repository.application.ApplicationRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.student.StudentRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.supervisor.SupervisorRepositoryInterface
import turmaA.grupoB.LinkStage.ui.admin.InstitutionStatus

@OptIn(ExperimentalCoroutinesApi::class)
class AdminViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var studentRepository: FakeStudentRepository
    private lateinit var supervisorRepository: FakeSupervisorRepository
    private lateinit var institutionRepository: FakeInstitutionRepository
    private lateinit var profileRepository: FakeProfileRepository
    private lateinit var applicationRepository: FakeApplicationRepository
    private lateinit var internshipRepository: FakeInternshipRepository
    private lateinit var offerRepository: FakeOfferRepository
    private lateinit var authRepository: FakeAuthRepository

    @Before
    fun setup() {
        studentRepository = FakeStudentRepository()
        supervisorRepository = FakeSupervisorRepository()
        institutionRepository = FakeInstitutionRepository()
        profileRepository = FakeProfileRepository()
        applicationRepository = FakeApplicationRepository()
        internshipRepository = FakeInternshipRepository()
        offerRepository = FakeOfferRepository()
        authRepository = FakeAuthRepository()

        studentRepository.students = listOf(testStudent)
        supervisorRepository.supervisors = listOf(testSupervisor)
        institutionRepository.institutions = listOf(testInstitution)
        profileRepository.profiles = listOf(testStudentProfile, testSupervisorProfile, testInstitutionProfile)
        applicationRepository.applications = listOf(testApplication)
        internshipRepository.internships = listOf(testInternship)
        offerRepository.offers = listOf(testOffer)
        supervisorRepository.skillsBySupervisorId[testSupervisor.id] = listOf(testSkill)
    }

    @Test
    fun dashboard_initialState_isIdle() {
        val viewModel = AdminDashboardViewModel(
            studentRepository,
            supervisorRepository,
            institutionRepository,
            profileRepository,
            applicationRepository,
            internshipRepository,
        )

        assertEquals(AdminDashboardUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun dashboard_loadDashboard_whenRepositoriesReturnData_setsSuccessState() = runTest {
        val viewModel = AdminDashboardViewModel(
            studentRepository,
            supervisorRepository,
            institutionRepository,
            profileRepository,
            applicationRepository,
            internshipRepository,
        )

        viewModel.loadDashboard()
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdminDashboardUiState.Success
        assertEquals(1, state.data.totalStudents)
        assertEquals(1, state.data.totalMentors)
        assertEquals(1, state.data.totalInstitutions)
        assertEquals("Ana Silva", state.data.recentStudents.first().name)
        assertEquals("Mentor Silva", state.data.recentMentors.first().name)
        assertEquals("Escola Superior de Tecnologia e Gestão", state.data.recentInstitutions.first().name)
    }

    @Test
    fun dashboard_loadDashboard_whenRepositoriesThrow_usesHardcodedFallback() = runTest {
        studentRepository.shouldThrowOnGetStudents = true
        supervisorRepository.shouldThrowOnGetSupervisors = true
        institutionRepository.shouldThrowOnGetInstitutions = true
        profileRepository.shouldThrowOnGetProfiles = true
        applicationRepository.shouldThrowOnGetApplicationsByStatus = true
        internshipRepository.shouldThrowOnGetInternshipsByStatus = true
        val viewModel = AdminDashboardViewModel(
            studentRepository,
            supervisorRepository,
            institutionRepository,
            profileRepository,
            applicationRepository,
            internshipRepository,
        )

        viewModel.loadDashboard()
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdminDashboardUiState.Success
        assertTrue(state.data.recentStudents.isNotEmpty())
        assertTrue(state.data.recentMentors.isNotEmpty())
        assertTrue(state.data.recentInstitutions.isNotEmpty())
    }

    @Test
    fun dashboard_loadDashboard_whenRepositoriesReturnEmpty_usesHardcodedFallback() = runTest {
        studentRepository.students = emptyList()
        supervisorRepository.supervisors = emptyList()
        institutionRepository.institutions = emptyList()
        val viewModel = AdminDashboardViewModel(
            studentRepository,
            supervisorRepository,
            institutionRepository,
            profileRepository,
            applicationRepository,
            internshipRepository,
        )

        viewModel.loadDashboard()
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdminDashboardUiState.Success
        assertTrue(state.data.recentStudents.isNotEmpty())
        assertTrue(state.data.recentMentors.isNotEmpty())
        assertTrue(state.data.recentInstitutions.isNotEmpty())
    }

    @Test
    fun users_loadUsers_whenRepositoriesReturnData_setsSuccessState() = runTest {
        val viewModel = AdminUsersViewModel(
            studentRepository,
            supervisorRepository,
            institutionRepository,
            profileRepository,
            internshipRepository,
        )

        viewModel.loadUsers()
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdminUsersUiState.Success
        assertEquals(1, state.students.size)
        assertEquals(1, state.mentors.size)
        assertEquals("Ana Silva", state.students.first().name)
        assertEquals("Mentor Silva", state.mentors.first().name)
    }

    @Test
    fun users_filterStudents_whenQueryMatches_setsFilteredStudentsState() = runTest {
        val viewModel = AdminUsersViewModel(
            studentRepository,
            supervisorRepository,
            institutionRepository,
            profileRepository,
            internshipRepository,
        )
        viewModel.loadUsers()
        advanceUntilIdle()

        viewModel.filterStudents(query = "Ana")
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdminUsersUiState.StudentsSuccess
        assertEquals(listOf("Ana Silva"), state.students.map { it.name })
    }

    @Test
    fun users_loadUsers_whenRepositoriesThrow_usesHardcodedFallback() = runTest {
        studentRepository.shouldThrowOnGetStudents = true
        supervisorRepository.shouldThrowOnGetSupervisors = true
        val viewModel = AdminUsersViewModel(
            studentRepository,
            supervisorRepository,
            institutionRepository,
            profileRepository,
            internshipRepository,
        )

        viewModel.loadUsers()
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdminUsersUiState.Success
        assertTrue(state.students.isNotEmpty())
        assertTrue(state.mentors.isNotEmpty())
    }

    @Test
    fun studentDetail_loadStudent_whenStudentExists_setsSuccessState() = runTest {
        val viewModel = AdminStudentDetailViewModel(
            studentRepository,
            profileRepository,
            internshipRepository,
            applicationRepository,
            institutionRepository,
            offerRepository,
            authRepository,
        )

        viewModel.loadStudent(testStudent.id)
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdminStudentDetailUiState.Success
        assertEquals("Ana Silva", state.data.student.name)
        assertEquals("Escola Superior de Tecnologia e Gestão", state.data.student.internshipCompany)
        assertEquals(listOf(testApplication), state.data.applications)
        assertEquals(listOf(testInternship), state.data.internships)
    }

    @Test
    fun studentDetail_loadStudent_whenStudentMissing_usesHardcodedFallback() = runTest {
        studentRepository.students = emptyList()
        val viewModel = AdminStudentDetailViewModel(
            studentRepository,
            profileRepository,
            internshipRepository,
            applicationRepository,
            institutionRepository,
            offerRepository,
            authRepository,
        )

        viewModel.loadStudent("missing-student")
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdminStudentDetailUiState.Success
        assertTrue(state.data.student.id.isNotEmpty())
    }

    @Test
    fun mentorDetail_loadMentor_whenMentorExists_setsSuccessState() = runTest {
        val viewModel = AdminMentorDetailViewModel(
            supervisorRepository,
            profileRepository,
            internshipRepository,
            studentRepository,
            authRepository,
        )

        viewModel.loadMentor(testSupervisor.id)
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdminMentorDetailUiState.Success
        assertEquals("Mentor Silva", state.data.mentor.name)
        assertEquals(1, state.data.mentor.activeStudentsCount)
        assertTrue(state.data.supervisedStudents.isNotEmpty())
    }

    @Test
    fun mentorDetail_loadMentor_whenMentorMissing_usesHardcodedFallback() = runTest {
        supervisorRepository.supervisors = emptyList()
        val viewModel = AdminMentorDetailViewModel(
            supervisorRepository,
            profileRepository,
            internshipRepository,
            studentRepository,
            authRepository,
        )

        viewModel.loadMentor("missing-mentor")
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdminMentorDetailUiState.Success
        assertTrue(state.data.mentor.id.isNotEmpty())
    }

    @Test
    fun institutions_loadInstitutions_whenRepositoriesReturnData_setsSuccessState() = runTest {
        val viewModel = AdminInstitutionsViewModel(
            institutionRepository,
            profileRepository,
            studentRepository,
            supervisorRepository,
            internshipRepository,
        )

        viewModel.loadInstitutions()
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdminInstitutionsUiState.Success
        assertEquals(1, state.approvedInstitutions.size)
        assertTrue(state.pendingInstitutions.isEmpty())
        assertEquals("Escola Superior de Tecnologia e Gestão", state.approvedInstitutions.first().name)
    }

    @Test
    fun institutions_filterInstitutions_whenLocationMatches_setsFilteredSuccessState() = runTest {
        val viewModel = AdminInstitutionsViewModel(
            institutionRepository,
            profileRepository,
            studentRepository,
            supervisorRepository,
            internshipRepository,
        )
        viewModel.loadInstitutions()
        advanceUntilIdle()

        viewModel.filterInstitutions(location = "Viana do Castelo")
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdminInstitutionsUiState.Success
        assertEquals(listOf("Escola Superior de Tecnologia e Gestão"), state.approvedInstitutions.map { it.name })
    }

    @Test
    fun institutions_loadInstitutions_whenRepositoriesThrow_usesHardcodedFallback() = runTest {
        institutionRepository.shouldThrowOnGetInstitutions = true
        profileRepository.shouldThrowOnGetProfiles = true
        val viewModel = AdminInstitutionsViewModel(
            institutionRepository,
            profileRepository,
            studentRepository,
            supervisorRepository,
            internshipRepository,
        )

        viewModel.loadInstitutions()
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdminInstitutionsUiState.Success
        assertTrue(state.approvedInstitutions.isNotEmpty())
    }

    @Test
    fun institutionDetail_loadInstitution_whenInstitutionExists_setsSuccessState() = runTest {
        val viewModel = AdminInstitutionDetailViewModel(
            institutionRepository,
            profileRepository,
            studentRepository,
            supervisorRepository,
            internshipRepository,
            authRepository,
        )

        viewModel.loadInstitution(testInstitution.id)
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdminInstitutionDetailUiState.Success
        assertEquals("Escola Superior de Tecnologia e Gestão", state.data.institution.name)
        assertTrue(state.data.relatedStudents.isNotEmpty())
        assertTrue(state.data.relatedMentors.isNotEmpty())
    }

    @Test
    fun institutionDetail_loadInstitution_whenInstitutionMissing_usesHardcodedFallback() = runTest {
        institutionRepository.institutions = emptyList()
        val viewModel = AdminInstitutionDetailViewModel(
            institutionRepository,
            profileRepository,
            studentRepository,
            supervisorRepository,
            internshipRepository,
            authRepository,
        )

        viewModel.loadInstitution("missing-institution")
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdminInstitutionDetailUiState.Success
        assertTrue(state.data.institution.id.isNotEmpty())
    }

    @Test
    fun internshipDetail_loadInternship_whenInternshipExists_setsSuccessState() = runTest {
        val viewModel = AdminInternshipDetailViewModel(
            internshipRepository,
            offerRepository,
            institutionRepository,
            studentRepository,
            supervisorRepository,
            profileRepository,
            applicationRepository,
        )

        viewModel.loadInternship(testInternship.id)
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdminInternshipDetailUiState.Success
        assertEquals(testOffer.title, state.data.offerDetail.title)
        assertEquals("Escola Superior de Tecnologia e Gestão", state.data.institutionName)
        assertEquals("Ana Silva", state.data.student.name)
        assertEquals("Mentor Silva", state.data.mentor?.name)
    }

    @Test
    fun internshipDetail_loadInternship_whenInternshipMissing_usesHardcodedFallback() = runTest {
        internshipRepository.internships = emptyList()
        val viewModel = AdminInternshipDetailViewModel(
            internshipRepository,
            offerRepository,
            institutionRepository,
            studentRepository,
            supervisorRepository,
            profileRepository,
            applicationRepository,
        )

        viewModel.loadInternship("missing-internship")
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdminInternshipDetailUiState.Success
        assertEquals("missing-internship", state.data.offerDetail.id)
        assertTrue(state.data.offerDetail.title.isNotEmpty())
    }

    @Test
    fun institutionStatusMapping_setsApprovedInstitutionStatus() = runTest {
        val viewModel = AdminInstitutionsViewModel(
            institutionRepository,
            profileRepository,
            studentRepository,
            supervisorRepository,
            internshipRepository,
        )

        viewModel.loadInstitutions()
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdminInstitutionsUiState.Success
        assertEquals(InstitutionStatus.APPROVED, state.approvedInstitutions.first().status)
        assertTrue(state.approvedInstitutions.all { it.status == InstitutionStatus.APPROVED })
        assertTrue(state.pendingInstitutions.all { it.status == InstitutionStatus.PENDING_APPROVAL })
    }

    @Test
    fun users_filterMentors_whenQueryMatches_setsFilteredMentorsState() = runTest {
        val viewModel = AdminUsersViewModel(
            studentRepository,
            supervisorRepository,
            institutionRepository,
            profileRepository,
            internshipRepository,
        )
        viewModel.loadUsers()
        advanceUntilIdle()

        viewModel.filterMentors(query = "Silva")
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdminUsersUiState.MentorsSuccess
        assertEquals(listOf("Mentor Silva"), state.mentors.map { it.name })
    }

    @Test
    fun studentDetail_loadStudent_whenStudentHasInternship_marksActiveInternship() = runTest {
        val viewModel = AdminStudentDetailViewModel(
            studentRepository,
            profileRepository,
            internshipRepository,
            applicationRepository,
            institutionRepository,
            offerRepository,
            authRepository,
        )

        viewModel.loadStudent(testStudent.id)
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdminStudentDetailUiState.Success
        assertTrue(state.data.student.hasActiveInternship)
    }

    @Test
    fun internshipDetail_loadInternship_whenMentorMissing_keepsRealOfferAndStudent() = runTest {
        supervisorRepository.supervisors = emptyList()
        val viewModel = AdminInternshipDetailViewModel(
            internshipRepository,
            offerRepository,
            institutionRepository,
            studentRepository,
            supervisorRepository,
            profileRepository,
            applicationRepository,
        )

        viewModel.loadInternship(testInternship.id)
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdminInternshipDetailUiState.Success
        assertEquals(testOffer.title, state.data.offerDetail.title)
        assertEquals("Ana Silva", state.data.student.name)
        assertTrue(state.data.mentor == null || state.data.mentor?.id?.isNotEmpty() == true)
    }

    @Test
    fun institutions_loadInstitutions_whenInstitutionsEmpty_usesHardcodedFallback() = runTest {
        institutionRepository.institutions = emptyList()
        val viewModel = AdminInstitutionsViewModel(
            institutionRepository,
            profileRepository,
            studentRepository,
            supervisorRepository,
            internshipRepository,
        )

        viewModel.loadInstitutions()
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdminInstitutionsUiState.Success
        assertFalse(state.approvedInstitutions.isEmpty())
    }

    @Test
    fun institutionDetail_loadInstitution_whenRelatedDataEmpty_usesFallbackLists() = runTest {
        studentRepository.students = emptyList()
        supervisorRepository.supervisors = emptyList()
        val viewModel = AdminInstitutionDetailViewModel(
            institutionRepository,
            profileRepository,
            studentRepository,
            supervisorRepository,
            internshipRepository,
            authRepository,
        )

        viewModel.loadInstitution(testInstitution.id)
        advanceUntilIdle()

        val state = viewModel.uiState.value as AdminInstitutionDetailUiState.Success
        assertFalse(state.data.relatedStudents.isEmpty())
        assertFalse(state.data.relatedMentors.isEmpty())
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}

private const val now = "2026-06-01T00:00:00Z"

private val testStudentProfile = ProfileModel(
    id = "user-student",
    name = "Ana Silva",
    email = "ana.silva@example.com",
    phone = "910000001",
    role = UserRole.STUDENT,
    createdAt = now,
    updatedAt = now,
)

private val testSupervisorProfile = ProfileModel(
    id = "user-mentor",
    name = "Mentor Silva",
    email = "mentor.silva@example.com",
    phone = "910000002",
    role = UserRole.SUPERVISOR,
    createdAt = now,
    updatedAt = now,
)

private val testInstitutionProfile = ProfileModel(
    id = "user-institution",
    name = "ESTG IPVC",
    email = "estg@example.com",
    phone = "258000000",
    role = UserRole.INSTITUTION,
    createdAt = now,
    updatedAt = now,
)

private val testStudent = StudentModel(
    id = "student-1",
    userId = "user-student",
    studentNumber = "12345",
    course = "Engenharia Informática",
    averageGrade = 16.0,
    createdAt = now,
    updatedAt = now,
)

private val testSupervisor = SupervisorModel(
    id = "mentor-1",
    userId = "user-mentor",
    department = "Informática",
    specialty = "Mobile",
    maxInternships = 5,
    acceptsNewInternships = true,
    createdAt = now,
    updatedAt = now,
)

private val testInstitution = InstitutionModel(
    id = "institution-1",
    userId = "user-institution",
    name = "Escola Superior de Tecnologia e Gestão",
    address = "Viana do Castelo",
    website = "https://estg.ipvc.pt",
    sector = "Instituição de Ensino",
    createdAt = now,
    updatedAt = now,
)

private val testOffer = InternshipOfferModel(
    id = "offer-1",
    institutionId = "institution-1",
    title = "Mobile App",
    description = "Criar uma aplicação mobile.",
    area = "Mobile",
    location = "Viana do Castelo",
    modality = "Presencial",
    salary = 700.0,
    vacancies = 1,
    requirements = "Kotlin\nAndroid",
    createdAt = now,
    updatedAt = now,
)

private val testInternship = InternshipModel(
    id = "internship-1",
    applicationId = "application-1",
    offerId = "offer-1",
    studentId = "student-1",
    institutionId = "institution-1",
    supervisorId = "mentor-1",
    title = "Mobile App",
    status = InternshipStatus.IN_PROGRESS,
    createdAt = now,
    updatedAt = now,
)

private val testApplication = ApplicationModel(
    id = "application-1",
    offerId = "offer-1",
    studentId = "student-1",
    status = ApplicationStatus.ACCEPTED,
    createdAt = now,
    updatedAt = now,
)

private val testSkill = SupervisorSkillModel(
    id = "skill-1",
    supervisorId = "mentor-1",
    skill = "Mobile",
    createdAt = now,
)

private class FakeStudentRepository : StudentRepositoryInterface {
    var students: List<StudentModel> = emptyList()
    var shouldThrowOnGetStudents = false
    var shouldThrowOnGetStudentById = false

    override suspend fun getStudents(): List<StudentModel> {
        if (shouldThrowOnGetStudents) error("students")
        return students
    }

    override suspend fun getStudentById(studentId: String): StudentModel? {
        if (shouldThrowOnGetStudentById) error("student by id")
        return students.firstOrNull { it.id == studentId }
    }

    override suspend fun getStudentByUserId(userId: String): StudentModel? = students.firstOrNull { it.userId == userId }

    override suspend fun getStudentByNumber(studentNumber: String): StudentModel? = students.firstOrNull { it.studentNumber == studentNumber }

    override suspend fun getStudentsByCourse(course: String): List<StudentModel> = students.filter { it.course == course }

    override suspend fun createStudent(input: turmaA.grupoB.LinkStage.data.remote.model.user.CreateStudentInput): StudentModel = error("not implemented")
}

private class FakeSupervisorRepository : SupervisorRepositoryInterface {
    var supervisors: List<SupervisorModel> = emptyList()
    var skillsBySupervisorId: MutableMap<String, List<SupervisorSkillModel>> = mutableMapOf()
    var shouldThrowOnGetSupervisors = false
    var shouldThrowOnGetSupervisorById = false

    override suspend fun getSupervisors(): List<SupervisorModel> {
        if (shouldThrowOnGetSupervisors) error("supervisors")
        return supervisors
    }

    override suspend fun getSupervisorById(supervisorId: String): SupervisorModel? {
        if (shouldThrowOnGetSupervisorById) error("supervisor by id")
        return supervisors.firstOrNull { it.id == supervisorId }
    }

    override suspend fun getSupervisorByUserId(userId: String): SupervisorModel? = supervisors.firstOrNull { it.userId == userId }

    override suspend fun getAvailableSupervisors(): List<SupervisorModel> = supervisors.filter { it.acceptsNewInternships }

    override suspend fun getSupervisorsByDepartment(department: String): List<SupervisorModel> = supervisors.filter { it.department == department }

    override suspend fun getSupervisorSkills(supervisorId: String): List<SupervisorSkillModel> = skillsBySupervisorId[supervisorId].orEmpty()

    override suspend fun createSupervisor(input: CreateSupervisorInput): SupervisorModel = error("not implemented")
}

private class FakeInstitutionRepository : InstitutionRepositoryInterface {
    var institutions: List<InstitutionModel> = emptyList()
    var shouldThrowOnGetInstitutions = false
    var shouldThrowOnGetInstitutionById = false

    override suspend fun getInstitutions(): List<InstitutionModel> {
        if (shouldThrowOnGetInstitutions) error("institutions")
        return institutions
    }

    override suspend fun getInstitutionById(institutionId: String): InstitutionModel? {
        if (shouldThrowOnGetInstitutionById) error("institution by id")
        return institutions.firstOrNull { it.id == institutionId }
    }

    override suspend fun getInstitutionByUserId(userId: String): InstitutionModel? = institutions.firstOrNull { it.userId == userId }

    override suspend fun createInstitution(input: CreateInstitutionInput): InstitutionModel = error("not implemented")
}

private class FakeProfileRepository : ProfileRepositoryInterface {
    var profiles: List<ProfileModel> = emptyList()
    var shouldThrowOnGetProfiles = false
    var shouldThrowOnGetProfileById = false

    override suspend fun getProfiles(): List<ProfileModel> {
        if (shouldThrowOnGetProfiles) error("profiles")
        return profiles
    }

    override suspend fun getProfileById(userId: String): ProfileModel? {
        if (shouldThrowOnGetProfileById) error("profile by id")
        return profiles.firstOrNull { it.id == userId }
    }

    override suspend fun getProfilesByRole(role: String): List<ProfileModel> = profiles.filter { it.role.name == role }
}

private class FakeApplicationRepository : ApplicationRepositoryInterface {
    var applications: List<ApplicationModel> = emptyList()
    var shouldThrowOnGetApplicationsByStatus = false

    override suspend fun getApplicationById(applicationId: String): ApplicationModel? = applications.firstOrNull { it.id == applicationId }

    override suspend fun getApplicationsByOffer(offerId: String): List<ApplicationModel> = applications.filter { it.offerId == offerId }

    override suspend fun getApplicationsByStudent(studentId: String): List<ApplicationModel> = applications.filter { it.studentId == studentId }

    override suspend fun getApplicationsByStatus(status: ApplicationStatus): List<ApplicationModel> {
        if (shouldThrowOnGetApplicationsByStatus) error("applications by status")
        return applications.filter { it.status == status }
    }

    override suspend fun createApplication(input: turmaA.grupoB.LinkStage.data.remote.model.application.CreateApplicationInput): ApplicationModel = error("not implemented")

    override suspend fun updateApplicationDecision(applicationId: String, input: turmaA.grupoB.LinkStage.data.remote.model.application.UpdateApplicationDecisionInput): ApplicationModel = error("not implemented")

    override suspend fun acceptApplication(applicationId: String): ApplicationModel = error("not implemented")

    override suspend fun rejectApplication(applicationId: String, rejectionReason: String): ApplicationModel = error("not implemented")
}

private class FakeInternshipRepository : InternshipRepositoryInterface {
    var internships: List<InternshipModel> = emptyList()
    var shouldThrowOnGetInternshipsByStatus = false

    override suspend fun getInternships(): List<InternshipModel> = internships

    override suspend fun getInternshipById(internshipId: String): InternshipModel? = internships.firstOrNull { it.id == internshipId }

    override suspend fun getInternshipsByStudent(studentId: String): List<InternshipModel> = internships.filter { it.studentId == studentId }

    override suspend fun getInternshipsByInstitution(institutionId: String): List<InternshipModel> = internships.filter { it.institutionId == institutionId }

    override suspend fun getInternshipsByStatus(status: InternshipStatus): List<InternshipModel> {
        if (shouldThrowOnGetInternshipsByStatus) error("internships by status")
        return internships.filter { it.status == status }
    }

    override suspend fun createInternship(input: turmaA.grupoB.LinkStage.data.remote.model.internship.CreateInternshipInput): InternshipModel = error("not implemented")

    override suspend fun assignSupervisor(internshipId: String, input: turmaA.grupoB.LinkStage.data.remote.model.internship.AssignSupervisorInput): InternshipModel = error("not implemented")

    override suspend fun getActivityLogsByInternship(internshipId: String): List<turmaA.grupoB.LinkStage.data.remote.model.internship.ActivityLogModel> = emptyList()

    override suspend fun createActivityLog(input: turmaA.grupoB.LinkStage.data.remote.model.internship.CreateActivityLogInput): turmaA.grupoB.LinkStage.data.remote.model.internship.ActivityLogModel = error("not implemented")
}

private class FakeOfferRepository : OfferRepositoryInterface {
    var offers: List<InternshipOfferModel> = emptyList()
    var shouldThrowOnGetOfferById = false

    override suspend fun getPublishedOffers(): List<InternshipOfferModel> = offers

    override suspend fun getOffersByInstitution(institutionId: String): List<InternshipOfferModel> = offers.filter { it.institutionId == institutionId }

    override suspend fun getOfferById(offerId: String): InternshipOfferModel? {
        if (shouldThrowOnGetOfferById) error("offer by id")
        return offers.firstOrNull { it.id == offerId }
    }

    override suspend fun createOffer(input: turmaA.grupoB.LinkStage.data.remote.model.offer.CreateOfferInput): InternshipOfferModel = error("not implemented")

    override suspend fun updateOffer(offerId: String, input: turmaA.grupoB.LinkStage.data.remote.model.offer.UpdateOfferInput): InternshipOfferModel = error("not implemented")

    override suspend fun closeOffer(offerId: String): InternshipOfferModel = error("not implemented")

    override suspend fun markOfferAsRemoved(offerId: String): InternshipOfferModel = error("not implemented")
}

private class FakeAuthRepository : turmaA.grupoB.LinkStage.data.repository.auth.AuthRepositoryInterface {
    val deactivatedUserIds = mutableListOf<String>()

    override suspend fun signUp(input: turmaA.grupoB.LinkStage.data.remote.model.auth.SignUpInput): ProfileModel = error("not implemented")

    override suspend fun signIn(input: turmaA.grupoB.LinkStage.data.remote.model.auth.SignInInput) = error("not implemented")

    override suspend fun signOut() = error("not implemented")

    override fun getCurrentUserId(): String? = null

    override fun isUserLoggedIn(): Boolean = false

    override suspend fun getCurrentUserProfile(): ProfileModel? = null

    override suspend fun updateProfile(userId: String, input: turmaA.grupoB.LinkStage.data.remote.model.user.UpdateProfileInput): ProfileModel = error("not implemented")

    override suspend fun updatePassword(newPassword: String) = error("not implemented")

    override suspend fun createManagedAccount(input: turmaA.grupoB.LinkStage.data.remote.model.auth.SignUpInput): ProfileModel = error("not implemented")

    override suspend fun setProfileActive(userId: String, active: Boolean) {
        if (!active) deactivatedUserIds.add(userId)
    }
}
