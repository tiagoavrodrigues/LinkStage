package turmaA.grupoB.LinkStage.viewmodel.orientador

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import turmaA.grupoB.LinkStage.data.remote.model.enums.EvaluationType
import turmaA.grupoB.LinkStage.data.remote.model.evaluation.CreateEvaluationInput
import turmaA.grupoB.LinkStage.data.remote.model.internship.CreateActivityLogInput
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.evaluation.EvaluationRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.student.StudentRepositoryInterface
import turmaA.grupoB.LinkStage.ui.aluno.activity.ActiveInternship
import turmaA.grupoB.LinkStage.ui.orientador.sampleEvaluationInstance
import turmaA.grupoB.LinkStage.ui.orientador.sampleMentorActivityLogsList
import turmaA.grupoB.LinkStage.ui.orientador.sampleMentorInternshipsList
import turmaA.grupoB.LinkStage.ui.orientador.sampleMentorStudentsList
import turmaA.grupoB.LinkStage.viewmodel.orientador.toActiveInternship
import turmaA.grupoB.LinkStage.viewmodel.orientador.toInternshipEvaluation
import turmaA.grupoB.LinkStage.viewmodel.orientador.toMentorActivityLogs
import turmaA.grupoB.LinkStage.viewmodel.orientador.toMentorInternships
import turmaA.grupoB.LinkStage.viewmodel.orientador.toMentorStudents
import java.time.LocalDate

class OrientadorDashboardViewModel(
    private val internshipRepository: InternshipRepositoryInterface,
    private val studentRepository: StudentRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface,
    private val offerRepository: OfferRepositoryInterface,
    private val institutionRepository: InstitutionRepositoryInterface,
    private val evaluationRepository: EvaluationRepositoryInterface,
) : ViewModel() {

    private val _uiState = MutableStateFlow<OrientadorDashboardUiState>(OrientadorDashboardUiState.Idle)
    val uiState: StateFlow<OrientadorDashboardUiState> = _uiState.asStateFlow()

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = OrientadorDashboardUiState.Loading
            _uiState.value = OrientadorDashboardUiState.Success(loadDashboardData())
        }
    }

    private suspend fun loadDashboardData(): OrientadorDashboardData {
        return try {
            val internships = internshipRepository.getInternships()
            val students = studentRepository.getStudents()
            val profiles = profileRepository.getProfiles().associateBy { it.id }
            val offers = offerRepository.getPublishedOffers().associateBy { it.id }
            val institutions = institutionRepository.getInstitutions().associateBy { it.id }
            val evaluations = evaluationRepository.getEvaluations()

            val mappedInternships = internships.toMentorInternships(
                studentsById = students.associateBy { it.id },
                profilesByUserId = profiles,
                offersById = offers,
                institutionsById = institutions,
            )
            val mappedStudents = students.toMentorStudents(
                profilesByUserId = profiles,
                internshipsByStudentId = internships.groupBy { it.studentId },
                offersById = offers,
            )
            val mappedActivityLogs = internships.flatMap { internship ->
                internshipRepository.getActivityLogsByInternship(internship.id)
            }.toMentorActivityLogs(
                studentsById = students.associateBy { it.id },
                profilesByUserId = profiles,
                institutionsById = institutions,
            )
            val mappedEvaluation = evaluations.toInternshipEvaluation()

            OrientadorDashboardData(
                internships = mappedInternships.ifEmpty { sampleMentorInternshipsList },
                students = mappedStudents.ifEmpty { sampleMentorStudentsList },
                activityLogs = mappedActivityLogs.ifEmpty { sampleMentorActivityLogsList },
                evaluation = mappedEvaluation ?: sampleEvaluationInstance,
            )
        } catch (error: Exception) {
            OrientadorDashboardData(
                internships = sampleMentorInternshipsList,
                students = sampleMentorStudentsList,
                activityLogs = sampleMentorActivityLogsList,
                evaluation = sampleEvaluationInstance,
            )
        }
    }
}

class OrientadorStudentDetailViewModel(
    private val internshipRepository: InternshipRepositoryInterface,
    private val studentRepository: StudentRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface,
    private val offerRepository: OfferRepositoryInterface,
    private val institutionRepository: InstitutionRepositoryInterface,
    private val evaluationRepository: EvaluationRepositoryInterface,
    private val authRepository: AuthRepositoryInterface,
) : ViewModel() {

    private val _uiState = MutableStateFlow<OrientadorStudentDetailUiState>(OrientadorStudentDetailUiState.Idle)
    val uiState: StateFlow<OrientadorStudentDetailUiState> = _uiState.asStateFlow()

    private val _submitGradeState = MutableStateFlow<SubmitFinalGradeUiState>(SubmitFinalGradeUiState.Idle)
    val submitGradeState: StateFlow<SubmitFinalGradeUiState> = _submitGradeState.asStateFlow()

    fun loadStudent(studentId: String) {
        viewModelScope.launch {
            _uiState.value = OrientadorStudentDetailUiState.Loading
            _uiState.value = OrientadorStudentDetailUiState.Success(loadStudentData(studentId))
        }
    }

    fun createCheckpoint(internshipId: String, studentId: String, title: String, description: String, date: LocalDate) {
        viewModelScope.launch {
            try {
                internshipRepository.createActivityLog(
                    CreateActivityLogInput(
                        internshipId = internshipId,
                        studentId = studentId,
                        description = if (description.isBlank()) title else "$title: $description",
                        activityDate = date.toString(),
                        type = "MENTOR",
                    )
                )
                loadStudent(studentId)
            } catch (_: Exception) {
                // Ignore - checkpoint creation is best-effort from the UI.
            }
        }
    }

    fun submitFinalGrade(internshipId: String, grade: Double, comment: String?) {
        viewModelScope.launch {
            _submitGradeState.value = SubmitFinalGradeUiState.Loading
            try {
                val evaluatorUserId = authRepository.getCurrentUserId()
                    ?: throw IllegalStateException("Utilizador não autenticado.")
                evaluationRepository.createEvaluation(
                    CreateEvaluationInput(
                        internshipId = internshipId,
                        evaluatorUserId = evaluatorUserId,
                        evaluatorType = EvaluationType.SUPERVISOR,
                        grade = grade,
                        comment = comment?.takeIf { it.isNotBlank() },
                    )
                )
                _submitGradeState.value = SubmitFinalGradeUiState.Success
            } catch (e: Exception) {
                _submitGradeState.value = SubmitFinalGradeUiState.Error(
                    e.message ?: "Erro ao submeter a nota final."
                )
            }
        }
    }

    fun resetSubmitGradeState() {
        _submitGradeState.value = SubmitFinalGradeUiState.Idle
    }

    private suspend fun loadStudentData(studentId: String): OrientadorStudentDetailData {
        return try {
            val student = studentRepository.getStudentById(studentId)
            val fallbackStudent = sampleMentorStudentsList.firstOrNull { it.id == studentId } ?: sampleMentorStudentsList.first()
            val internships = student?.let { internshipRepository.getInternshipsByStudent(student.id) }
                ?: internshipRepository.getInternships().filter { it.studentId == studentId }
            val profiles = profileRepository.getProfiles().associateBy { it.id }
            val offers = offerRepository.getPublishedOffers().associateBy { it.id }
            val institutions = institutionRepository.getInstitutions().associateBy { it.id }

            val mappedStudent = student?.toMentorStudent(
                profile = profiles[student.userId],
                internships = internships,
                offersById = offers,
            ) ?: fallbackStudent

            val activityLogs = internships.flatMap { internship ->
                internshipRepository.getActivityLogsByInternship(internship.id)
            }.toMentorActivityLogs(
                studentsById = mapOf(studentId to student).filterValues { it != null }.mapValues { it.value!! },
                profilesByUserId = profiles,
                institutionsById = institutions,
            )
            val activeInternship = internships.firstOrNull()?.toActiveInternship(
                activityLogs = activityLogs,
                offer = internships.firstOrNull()?.offerId?.let { offers[it] },
            )
            val mappedEvaluation = evaluationRepository.getEvaluationsByInternship(internships.firstOrNull()?.id.orEmpty())
                .toInternshipEvaluation()

            OrientadorStudentDetailData(
                student = mappedStudent,
                internships = internships,
                activeInternship = activeInternship,
                activityLogs = activityLogs.ifEmpty { sampleMentorActivityLogsList },
                evaluation = mappedEvaluation ?: sampleEvaluationInstance,
            )
        } catch (error: Exception) {
            val fallbackStudent = sampleMentorStudentsList.firstOrNull { it.id == studentId } ?: sampleMentorStudentsList.first()
            OrientadorStudentDetailData(
                student = fallbackStudent,
                internships = emptyList(),
                activeInternship = null,
                activityLogs = sampleMentorActivityLogsList,
                evaluation = sampleEvaluationInstance,
            )
        }
    }
}

class OrientadorInternshipDetailViewModel(
    private val internshipRepository: InternshipRepositoryInterface,
    private val studentRepository: StudentRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface,
    private val offerRepository: OfferRepositoryInterface,
    private val institutionRepository: InstitutionRepositoryInterface,
    private val evaluationRepository: EvaluationRepositoryInterface,
) : ViewModel() {

    private val _uiState = MutableStateFlow<OrientadorInternshipDetailUiState>(OrientadorInternshipDetailUiState.Idle)
    val uiState: StateFlow<OrientadorInternshipDetailUiState> = _uiState.asStateFlow()

    fun loadInternship(internshipId: String) {
        viewModelScope.launch {
            _uiState.value = OrientadorInternshipDetailUiState.Loading
            _uiState.value = OrientadorInternshipDetailUiState.Success(loadInternshipData(internshipId))
        }
    }

    private suspend fun loadInternshipData(internshipId: String): OrientadorInternshipDetailData {
        return try {
            val internships = internshipRepository.getInternships()
            val fallbackInternship = sampleMentorInternshipsList.firstOrNull { it.id == internshipId }
                ?: sampleMentorInternshipsList.first()
            val internship = internships.firstOrNull { it.id == internshipId }
                ?: internships.firstOrNull()
                ?: return OrientadorInternshipDetailData(
                    internship = fallbackInternship,
                    student = sampleMentorStudentsList.first(),
                    activeInternship = null,
                    activityLogs = sampleMentorActivityLogsList,
                    evaluation = sampleEvaluationInstance,
                )

            val students = studentRepository.getStudents()
            val student = students.firstOrNull { it.id == internship.studentId }
            val profiles = profileRepository.getProfiles().associateBy { it.id }
            val offers = offerRepository.getPublishedOffers().associateBy { it.id }
            val institutions = institutionRepository.getInstitutions().associateBy { it.id }
            val offer = offers[internship.offerId]
            val institution = institutions[internship.institutionId]
            val mappedInternship = listOf(internship).toMentorInternships(
                studentsById = students.associateBy { it.id },
                profilesByUserId = profiles,
                offersById = offers,
                institutionsById = institutions,
            ).first()
            val mappedStudent = student?.toMentorStudent(
                profile = profiles[student.userId],
                internships = listOf(internship),
                offersById = offers,
            ) ?: sampleMentorStudentsList.first()
            val activityLogs = internshipRepository.getActivityLogsByInternship(internship.id)
                .toMentorActivityLogs(
                    studentsById = students.associateBy { it.id },
                    profilesByUserId = profiles,
                    institutionsById = institutions,
                )
            val activeInternship = internship.toActiveInternship(
                activityLogs = activityLogs,
                offer = offer,
            )
            val mappedEvaluation = evaluationRepository.getEvaluationsByInternship(internship.id)
                .toInternshipEvaluation()

            OrientadorInternshipDetailData(
                internship = mappedInternship,
                student = mappedStudent,
                activeInternship = activeInternship,
                activityLogs = activityLogs.ifEmpty { sampleMentorActivityLogsList },
                evaluation = mappedEvaluation ?: sampleEvaluationInstance,
            )
        } catch (error: Exception) {
            val fallbackInternship = sampleMentorInternshipsList.firstOrNull { it.id == internshipId }
                ?: sampleMentorInternshipsList.first()
            OrientadorInternshipDetailData(
                internship = fallbackInternship,
                student = sampleMentorStudentsList.first(),
                activeInternship = null,
                activityLogs = sampleMentorActivityLogsList,
                evaluation = sampleEvaluationInstance,
            )
        }
    }
}
