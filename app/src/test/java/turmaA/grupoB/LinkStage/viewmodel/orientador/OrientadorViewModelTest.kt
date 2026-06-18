package turmaA.grupoB.LinkStage.viewmodel.orientador

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import turmaA.grupoB.LinkStage.data.remote.model.enums.EvaluationType
import turmaA.grupoB.LinkStage.data.remote.model.enums.InternshipStatus
import turmaA.grupoB.LinkStage.data.remote.model.enums.UserRole
import turmaA.grupoB.LinkStage.data.remote.model.evaluation.EvaluationModel
import turmaA.grupoB.LinkStage.data.remote.model.institution.CreateInstitutionInput
import turmaA.grupoB.LinkStage.data.remote.model.institution.InstitutionModel
import turmaA.grupoB.LinkStage.data.remote.model.internship.ActivityLogModel
import turmaA.grupoB.LinkStage.data.remote.model.internship.InternshipModel
import turmaA.grupoB.LinkStage.data.remote.model.offer.InternshipOfferModel
import turmaA.grupoB.LinkStage.data.remote.model.user.ProfileModel
import turmaA.grupoB.LinkStage.data.remote.model.user.StudentModel
import turmaA.grupoB.LinkStage.data.repository.evaluation.EvaluationRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.student.StudentRepositoryInterface
import turmaA.grupoB.LinkStage.ui.aluno.activity.ActivityLogStatus
import turmaA.grupoB.LinkStage.ui.orientador.EvaluationState
import turmaA.grupoB.LinkStage.ui.orientador.InternshipType
import turmaA.grupoB.LinkStage.ui.orientador.sampleMentorInternshipsList
import turmaA.grupoB.LinkStage.ui.orientador.sampleMentorStudentsList

@OptIn(ExperimentalCoroutinesApi::class)
class OrientadorMappersTest {
    @Test
    fun `mentor internship mapper uses offer and institution details`() {
        val internship = internshipModel(
            id = "internship-1",
            studentId = "student-1",
            offerId = "offer-1",
            institutionId = "institution-1",
        )
        val offer = offerModel(id = "offer-1", title = "UX Design Internship", institutionId = "institution-1")
        val institution = institutionModel(id = "institution-1", name = "Creative Studio")

        val mapped = listOf(internship).toMentorInternships(
            studentsById = mapOf("student-1" to studentModel("student-1")),
            profilesByUserId = emptyMap(),
            offersById = mapOf(offer.id to offer),
            institutionsById = mapOf(institution.id to institution),
        ).single()

        assertEquals("UX Design Internship", mapped.offerTitle)
        assertEquals("Creative Studio", mapped.businessInstitutionName)
        assertEquals("C", mapped.logoInitial)
    }

    @Test
    fun `mentor internship mapper falls back to internship title and generic company name`() {
        val internship = internshipModel(
            id = "internship-2",
            studentId = "student-2",
            offerId = "missing-offer",
            institutionId = "missing-institution",
        )

        val mapped = listOf(internship).toMentorInternships(
            studentsById = mapOf("student-2" to studentModel("student-2")),
            profilesByUserId = emptyMap(),
            offersById = emptyMap(),
            institutionsById = emptyMap(),
        ).single()

        assertEquals("Remote Internship", mapped.offerTitle)
        assertEquals("Empresa", mapped.businessInstitutionName)
        assertEquals("R", mapped.logoInitial)
    }

    @Test
    fun `activity log mapper marks mentor-created logs as pending`() {
        val log = activityLogModel(id = "log-1", studentId = "student-1", internshipId = "internship-1", type = "MENTOR")

        val mapped = listOf(log).toMentorActivityLogs(
            studentsById = mapOf("student-1" to studentModel("student-1")),
            profilesByUserId = emptyMap(),
            institutionsById = emptyMap(),
        ).single()

        assertEquals("MENTOR", mapped.title)
        assertEquals(ActivityLogStatus.PENDING, mapped.status)
        assertTrue(mapped.hasSubmitted.not())
    }

    @Test
    fun `evaluation mapper uses institution evaluation to mark company school internship`() {
        val evaluations = listOf(
            evaluationModel(id = "school", internshipId = "internship-1", evaluatorType = EvaluationType.SUPERVISOR, grade = 14.0),
            evaluationModel(id = "company", internshipId = "internship-1", evaluatorType = EvaluationType.INSTITUTION, grade = 17.0),
        )

        val mapped = evaluations.toInternshipEvaluation(internshipId = "internship-1")!!

        assertEquals(InternshipType.COMPANY_SCHOOL, mapped.internshipType)
        assertEquals(14.0f, mapped.companyMentorGrade)
        assertEquals(17.0f, mapped.institutionGrade)
        assertEquals(EvaluationState.COMPLETED, mapped.state)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class OrientadorDashboardViewModelTest {
    @Test
    fun `load dashboard emits success state with mapped remote data`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val viewModel = OrientadorDashboardViewModel(
            internshipRepository = TestInternshipRepository(listOf(internshipModel())),
            studentRepository = TestStudentRepository(listOf(studentModel())),
            profileRepository = TestProfileRepository(listOf(profileModel())),
            offerRepository = TestOfferRepository(listOf(offerModel())),
            evaluationRepository = TestEvaluationRepository(emptyList()),
            institutionRepository = TestInstitutionRepository(listOf(institutionModel())),
        )

        viewModel.loadDashboard()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(viewModel.uiState.value.toString(), state is OrientadorDashboardUiState.Success)
        val data = (state as OrientadorDashboardUiState.Success).data
        assertEquals(1, data.internships.size)
        assertEquals(1, data.students.size)
        assertEquals("UX Design Internship", data.internships.single().offerTitle)
        assertEquals("Ana Student", data.students.single().name)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `load dashboard emits empty state when remote lists are empty`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)
        try {
            val viewModel = OrientadorDashboardViewModel(
            internshipRepository = TestInternshipRepository(emptyList()),
            studentRepository = TestStudentRepository(emptyList()),
            profileRepository = TestProfileRepository(emptyList()),
            offerRepository = TestOfferRepository(emptyList()),
            evaluationRepository = TestEvaluationRepository(emptyList()),
            institutionRepository = TestInstitutionRepository(emptyList()),
        )

        viewModel.loadDashboard()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(viewModel.uiState.value.toString(), state is OrientadorDashboardUiState.Success)
        val data = (state as OrientadorDashboardUiState.Success).data
        assertEquals(sampleMentorInternshipsList.size, data.internships.size)
        assertEquals(sampleMentorStudentsList.size, data.students.size)
        } finally {
            Dispatchers.resetMain()
        }
    }
}

private class TestInternshipRepository(
    private val internships: List<InternshipModel>,
) : InternshipRepositoryInterface {
    override suspend fun getInternships(): List<InternshipModel> = internships
    override suspend fun getInternshipById(internshipId: String): InternshipModel? = internships.firstOrNull { it.id == internshipId }
    override suspend fun getInternshipsByStudent(studentId: String): List<InternshipModel> = internships.filter { it.studentId == studentId }
    override suspend fun getInternshipsByInstitution(institutionId: String): List<InternshipModel> = internships.filter { it.institutionId == institutionId }
    override suspend fun getInternshipsByStatus(status: InternshipStatus): List<InternshipModel> = internships.filter { it.status == status }
    override suspend fun createInternship(input: turmaA.grupoB.LinkStage.data.remote.model.internship.CreateInternshipInput): InternshipModel = TODO()
    override suspend fun assignSupervisor(internshipId: String, input: turmaA.grupoB.LinkStage.data.remote.model.internship.AssignSupervisorInput): InternshipModel = TODO()
    override suspend fun getActivityLogsByInternship(internshipId: String): List<ActivityLogModel> = emptyList()
    override suspend fun createActivityLog(input: turmaA.grupoB.LinkStage.data.remote.model.internship.CreateActivityLogInput): ActivityLogModel = TODO()
}

private class TestStudentRepository(
    private val students: List<StudentModel>,
) : StudentRepositoryInterface {
    override suspend fun getStudents(): List<StudentModel> = students
    override suspend fun getStudentById(studentId: String): StudentModel? = students.firstOrNull { it.id == studentId }
    override suspend fun getStudentByUserId(userId: String): StudentModel? = students.firstOrNull { it.userId == userId }
    override suspend fun getStudentByNumber(studentNumber: String): StudentModel? = students.firstOrNull { it.studentNumber == studentNumber }
    override suspend fun getStudentsByCourse(course: String): List<StudentModel> = students.filter { it.course == course }
    override suspend fun createStudent(input: turmaA.grupoB.LinkStage.data.remote.model.user.CreateStudentInput): StudentModel = TODO()
}

private class TestProfileRepository(
    private val profiles: List<ProfileModel>,
) : ProfileRepositoryInterface {
    override suspend fun getProfiles(): List<ProfileModel> = profiles
    override suspend fun getProfileById(userId: String): ProfileModel? = profiles.firstOrNull { it.id == userId }
    override suspend fun getProfilesByRole(role: String): List<ProfileModel> = profiles.filter { it.role.name == role }
}

private class TestOfferRepository(
    private val offers: List<InternshipOfferModel>,
) : OfferRepositoryInterface {
    override suspend fun getPublishedOffers(): List<InternshipOfferModel> = offers
    override suspend fun getOffersByInstitution(institutionId: String): List<InternshipOfferModel> = offers.filter { it.institutionId == institutionId }
    override suspend fun getOfferById(offerId: String): InternshipOfferModel? = offers.firstOrNull { it.id == offerId }
    override suspend fun createOffer(input: turmaA.grupoB.LinkStage.data.remote.model.offer.CreateOfferInput): InternshipOfferModel = TODO()
    override suspend fun updateOffer(offerId: String, input: turmaA.grupoB.LinkStage.data.remote.model.offer.UpdateOfferInput): InternshipOfferModel = TODO()
    override suspend fun closeOffer(offerId: String): InternshipOfferModel = TODO()
    override suspend fun markOfferAsRemoved(offerId: String): InternshipOfferModel = TODO()
}

private class TestEvaluationRepository(
    private val evaluations: List<EvaluationModel>,
) : EvaluationRepositoryInterface {
    override suspend fun getEvaluations(): List<EvaluationModel> = evaluations
    override suspend fun getEvaluationsByInternship(internshipId: String): List<EvaluationModel> = evaluations.filter { it.internshipId == internshipId }
    override suspend fun getEvaluationByInternshipAndType(internshipId: String, evaluatorType: EvaluationType): EvaluationModel? =
        evaluations.firstOrNull { it.internshipId == internshipId && it.evaluatorType == evaluatorType }
    override suspend fun createEvaluation(input: turmaA.grupoB.LinkStage.data.remote.model.evaluation.CreateEvaluationInput): EvaluationModel = TODO()
    override suspend fun getFinalGradeByInternship(internshipId: String): turmaA.grupoB.LinkStage.data.remote.model.evaluation.FinalGradeModel? = null
}

private class TestInstitutionRepository(
    private val institutions: List<InstitutionModel>,
) : InstitutionRepositoryInterface {
    override suspend fun getInstitutions(): List<InstitutionModel> = institutions
    override suspend fun getInstitutionById(institutionId: String): InstitutionModel? = institutions.firstOrNull { it.id == institutionId }
    override suspend fun getInstitutionByUserId(userId: String): InstitutionModel? = institutions.firstOrNull { it.userId == userId }
    override suspend fun createInstitution(input: CreateInstitutionInput): InstitutionModel = error("not implemented")
}

private fun internshipModel(
    id: String = "internship-1",
    studentId: String = "student-1",
    offerId: String = "offer-1",
    institutionId: String = "institution-1",
): InternshipModel = InternshipModel(
    id = id,
    applicationId = "application-$id",
    offerId = offerId,
    studentId = studentId,
    institutionId = institutionId,
    title = "Remote Internship",
    status = InternshipStatus.IN_PROGRESS,
    createdAt = "2026-06-01T00:00:00Z",
    updatedAt = "2026-06-01T00:00:00Z",
)

private fun studentModel(id: String = "student-1", userId: String = "user-student-1"): StudentModel = StudentModel(
    id = id,
    userId = userId,
    studentNumber = "A$id",
    course = "Design",
    averageGrade = 15.0,
    createdAt = "2026-06-01T00:00:00Z",
    updatedAt = "2026-06-01T00:00:00Z",
)

private fun profileModel(id: String = "user-student-1", name: String = "Ana Student"): ProfileModel = ProfileModel(
    id = id,
    name = name,
    email = "$name@example.com",
    role = UserRole.STUDENT,
    createdAt = "2026-06-01T00:00:00Z",
    updatedAt = "2026-06-01T00:00:00Z",
)

private fun offerModel(id: String = "offer-1", institutionId: String = "institution-1", title: String = "UX Design Internship"): InternshipOfferModel = InternshipOfferModel(
    id = id,
    institutionId = institutionId,
    title = title,
    description = "Design internship",
    area = "Design",
    location = "Remote",
    modality = "Remote",
    salary = 800.0,
    requirements = "Figma\nPrototyping",
    createdAt = "2026-06-01T00:00:00Z",
    updatedAt = "2026-06-01T00:00:00Z",
)

private fun institutionModel(id: String = "institution-1", name: String = "Creative Studio"): InstitutionModel = InstitutionModel(
    id = id,
    userId = "user-institution-1",
    name = name,
    description = "Creative company",
    sector = "Design",
    createdAt = "2026-06-01T00:00:00Z",
    updatedAt = "2026-06-01T00:00:00Z",
)

private fun evaluationModel(id: String, internshipId: String, evaluatorType: EvaluationType, grade: Double): EvaluationModel = EvaluationModel(
    id = id,
    internshipId = internshipId,
    evaluatorUserId = "evaluator-$id",
    evaluatorType = evaluatorType,
    grade = grade,
    comment = "Good work",
    createdAt = "2026-06-01T00:00:00Z",
    updatedAt = "2026-06-01T00:00:00Z",
)

private fun activityLogModel(
    id: String = "log-1",
    internshipId: String = "internship-1",
    studentId: String = "student-1",
    type: String? = "STUDENT",
): ActivityLogModel = ActivityLogModel(
    id = id,
    internshipId = internshipId,
    studentId = studentId,
    description = "Submitted checkpoint",
    activityDate = "2026-06-01",
    type = type,
    createdAt = "2026-06-01T00:00:00Z",
)
