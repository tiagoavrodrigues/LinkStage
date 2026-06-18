package turmaA.grupoB.LinkStage.viewmodel.application

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import turmaA.grupoB.LinkStage.data.remote.model.application.ApplicationModel
import turmaA.grupoB.LinkStage.data.repository.application.ApplicationRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.student.StudentRepositoryInterface

class InstitutionApplicationsViewModel(
    private val applicationRepository: ApplicationRepositoryInterface,
    private val studentRepository: StudentRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface,
) : ViewModel() {

    private val _uiState = MutableStateFlow<InstitutionApplicationsUiState>(InstitutionApplicationsUiState.Idle)
    val uiState: StateFlow<InstitutionApplicationsUiState> = _uiState.asStateFlow()

    fun loadApplicationsByOffer(offerId: String) {
        viewModelScope.launch {
            _uiState.value = InstitutionApplicationsUiState.Loading

            try {
                val applications = applicationRepository.getApplicationsByOffer(offerId)

                _uiState.value = if (applications.isEmpty()) {
                    InstitutionApplicationsUiState.Empty
                } else {
                    InstitutionApplicationsUiState.SuccessList(applications.map { it.toDetails() })
                }
            } catch (e: Exception) {
                _uiState.value = InstitutionApplicationsUiState.Error(
                    e.message ?: "Erro ao carregar candidaturas da oferta."
                )
            }
        }
    }

    fun loadApplicationById(applicationId: String) {
        viewModelScope.launch {
            _uiState.value = InstitutionApplicationsUiState.Loading

            try {
                val application = applicationRepository.getApplicationById(applicationId)

                _uiState.value = if (application == null) {
                    InstitutionApplicationsUiState.Empty
                } else {
                    InstitutionApplicationsUiState.SuccessDetails(application.toDetails())
                }
            } catch (e: Exception) {
                _uiState.value = InstitutionApplicationsUiState.Error(
                    e.message ?: "Erro ao carregar candidatura."
                )
            }
        }
    }

    fun acceptApplication(applicationId: String) {
        viewModelScope.launch {
            _uiState.value = InstitutionApplicationsUiState.Loading

            try {
                val application = applicationRepository.acceptApplication(applicationId)
                _uiState.value = InstitutionApplicationsUiState.SuccessDetails(application.toDetails())
            } catch (e: Exception) {
                _uiState.value = InstitutionApplicationsUiState.Error(
                    e.message ?: "Erro ao aceitar candidatura."
                )
            }
        }
    }

    fun rejectApplication(applicationId: String, rejectionReason: String) {
        viewModelScope.launch {
            _uiState.value = InstitutionApplicationsUiState.Loading

            try {
                val application = applicationRepository.rejectApplication(applicationId, rejectionReason)
                _uiState.value = InstitutionApplicationsUiState.SuccessDetails(application.toDetails())
            } catch (e: Exception) {
                _uiState.value = InstitutionApplicationsUiState.Error(
                    e.message ?: "Erro ao rejeitar candidatura."
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = InstitutionApplicationsUiState.Idle
    }

    private suspend fun ApplicationModel.toDetails(): InstitutionApplicationDetails {
        val student = runCatching { studentRepository.getStudentById(studentId) }.getOrNull()
        val profile = student?.let {
            runCatching { profileRepository.getProfileById(it.userId) }.getOrNull()
        }
        return InstitutionApplicationDetails(application = this, student = student, profile = profile)
    }
}
