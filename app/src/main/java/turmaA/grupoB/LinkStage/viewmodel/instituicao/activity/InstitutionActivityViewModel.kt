package turmaA.grupoB.LinkStage.viewmodel.instituicao.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import turmaA.grupoB.LinkStage.data.remote.model.enums.InternshipStatus as RemoteInternshipStatus
import turmaA.grupoB.LinkStage.data.remote.model.internship.AssignSupervisorInput
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.student.StudentRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.supervisor.SupervisorRepositoryInterface
import turmaA.grupoB.LinkStage.ui.instituicao.toAdminMentor
import turmaA.grupoB.LinkStage.ui.instituicao.toInstitutionInternship
import turmaA.grupoB.LinkStage.ui.instituicao.toInstitutionMentorItem

class InstitutionActivityViewModel(
    private val internshipRepository: InternshipRepositoryInterface,
    private val studentRepository: StudentRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface,
    private val offerRepository: OfferRepositoryInterface,
    private val supervisorRepository: SupervisorRepositoryInterface,
    private val institutionRepository: InstitutionRepositoryInterface,
    private val authRepository: AuthRepositoryInterface,
) : ViewModel() {

    private val _uiState = MutableStateFlow<InstitutionActivityUiState>(InstitutionActivityUiState.Idle)
    val uiState: StateFlow<InstitutionActivityUiState> = _uiState.asStateFlow()

    private val _assignState = MutableStateFlow<AssignSupervisorUiState>(AssignSupervisorUiState.Idle)
    val assignState: StateFlow<AssignSupervisorUiState> = _assignState.asStateFlow()

    fun loadActivityForCurrentInstitution() {
        viewModelScope.launch {
            _uiState.value = InstitutionActivityUiState.Loading
            try {
                val userId = authRepository.getCurrentUserId()
                val institution = userId?.let { institutionRepository.getInstitutionByUserId(it) }
                if (institution == null) {
                    _uiState.value = InstitutionActivityUiState.Error("Não foi possível identificar a instituição.")
                    return@launch
                }

                val internships = internshipRepository.getInternshipsByInstitution(institution.id)

                val studentsById = internships
                    .mapNotNull { internship ->
                        runCatching { studentRepository.getStudentById(internship.studentId) }.getOrNull()
                    }
                    .associateBy { it.id }

                val supervisorIds = internships.mapNotNull { it.supervisorId }.distinct()
                val supervisors = supervisorIds.mapNotNull { supervisorId ->
                    runCatching { supervisorRepository.getSupervisorById(supervisorId) }.getOrNull()
                }
                val supervisorsById = supervisors.associateBy { it.id }

                val userIds = (studentsById.values.map { it.userId } + supervisors.map { it.userId }).distinct()
                val profilesByUserId = userIds
                    .mapNotNull { userId2 ->
                        runCatching { profileRepository.getProfileById(userId2) }.getOrNull()?.let { userId2 to it }
                    }
                    .toMap()

                val offerIds = internships.map { it.offerId }.distinct()
                val offersById = offerIds
                    .mapNotNull { offerId ->
                        runCatching { offerRepository.getOfferById(offerId) }.getOrNull()?.let { offerId to it }
                    }
                    .toMap()

                val institutionInternships = internships.map { internship ->
                    val student = studentsById[internship.studentId]
                    val studentProfile = student?.let { profilesByUserId[it.userId] }
                    val offer = offersById[internship.offerId]
                    val supervisor = internship.supervisorId?.let { supervisorsById[it] }
                    val supervisorProfile = supervisor?.let { profilesByUserId[it.userId] }
                    internship.toInstitutionInternship(studentProfile, offer, supervisorProfile)
                }

                val mentors = supervisors.map { supervisor ->
                    val activeCount = internships.count {
                        it.supervisorId == supervisor.id && it.status == RemoteInternshipStatus.IN_PROGRESS
                    }
                    supervisor.toInstitutionMentorItem(profilesByUserId[supervisor.userId], activeCount)
                }

                val adminMentors = supervisors.map { supervisor ->
                    val activeCount = internships.count {
                        it.supervisorId == supervisor.id && it.status == RemoteInternshipStatus.IN_PROGRESS
                    }
                    supervisor.toAdminMentor(profilesByUserId[supervisor.userId], activeCount)
                }

                _uiState.value = if (institutionInternships.isEmpty() && mentors.isEmpty()) {
                    InstitutionActivityUiState.Empty
                } else {
                    InstitutionActivityUiState.Success(
                        InstitutionActivityData(
                            internships = institutionInternships,
                            mentors = mentors,
                            adminMentors = adminMentors,
                        )
                    )
                }
            } catch (e: Exception) {
                _uiState.value = InstitutionActivityUiState.Error(e.message ?: "Erro ao carregar atividade da instituição.")
            }
        }
    }

    fun assignSupervisor(internshipId: String, supervisorId: String) {
        viewModelScope.launch {
            _assignState.value = AssignSupervisorUiState.Loading
            try {
                internshipRepository.assignSupervisor(
                    internshipId,
                    AssignSupervisorInput(supervisorId = supervisorId),
                )
                _assignState.value = AssignSupervisorUiState.Success
            } catch (e: Exception) {
                _assignState.value = AssignSupervisorUiState.Error(e.message ?: "Não foi possível atribuir o mentor.")
            }
        }
    }

    fun resetAssignState() {
        _assignState.value = AssignSupervisorUiState.Idle
    }
}
