package turmaA.grupoB.LinkStage.viewmodel.application

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import turmaA.grupoB.LinkStage.data.remote.model.application.CreateApplicationInput
import turmaA.grupoB.LinkStage.data.remote.model.application.UpdateApplicationDecisionInput
import turmaA.grupoB.LinkStage.data.remote.model.enums.ApplicationStatus
import turmaA.grupoB.LinkStage.data.repository.application.ApplicationRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.storage.StorageRepositoryInterface

class ApplicationViewModel(
    private val applicationRepository: ApplicationRepositoryInterface,
    private val storageRepository: StorageRepositoryInterface? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ApplicationUiState>(ApplicationUiState.Idle)
    val uiState: StateFlow<ApplicationUiState> = _uiState.asStateFlow()

    fun loadApplicationById(applicationId: String) {
        viewModelScope.launch {
            _uiState.value = ApplicationUiState.Loading

            try {
                val application = applicationRepository.getApplicationById(applicationId)

                _uiState.value = if (application != null) {
                    ApplicationUiState.Success(application)
                } else {
                    ApplicationUiState.Empty
                }
            } catch (e: Exception) {
                _uiState.value = ApplicationUiState.Error(
                    e.message ?: "Erro ao carregar candidatura."
                )
            }
        }
    }

    fun loadApplicationsByOffer(offerId: String) {
        viewModelScope.launch {
            _uiState.value = ApplicationUiState.Loading

            try {
                val applications = applicationRepository.getApplicationsByOffer(offerId)

                _uiState.value = if (applications.isEmpty()) {
                    ApplicationUiState.Empty
                } else {
                    ApplicationUiState.SuccessList(applications)
                }
            } catch (e: Exception) {
                _uiState.value = ApplicationUiState.Error(
                    e.message ?: "Erro ao carregar candidaturas da oferta."
                )
            }
        }
    }

    fun loadApplicationsByStudent(studentId: String) {
        viewModelScope.launch {
            _uiState.value = ApplicationUiState.Loading

            try {
                val applications = applicationRepository.getApplicationsByStudent(studentId)

                _uiState.value = if (applications.isEmpty()) {
                    ApplicationUiState.Empty
                } else {
                    ApplicationUiState.SuccessList(applications)
                }
            } catch (e: Exception) {
                _uiState.value = ApplicationUiState.Error(
                    e.message ?: "Erro ao carregar candidaturas do estudante."
                )
            }
        }
    }

    fun loadApplicationsByStatus(status: ApplicationStatus) {
        viewModelScope.launch {
            _uiState.value = ApplicationUiState.Loading

            try {
                val applications = applicationRepository.getApplicationsByStatus(status)

                _uiState.value = if (applications.isEmpty()) {
                    ApplicationUiState.Empty
                } else {
                    ApplicationUiState.SuccessList(applications)
                }
            } catch (e: Exception) {
                _uiState.value = ApplicationUiState.Error(
                    e.message ?: "Erro ao carregar candidaturas por estado."
                )
            }
        }
    }

    fun createApplication(input: CreateApplicationInput) {
        viewModelScope.launch {
            _uiState.value = ApplicationUiState.Loading

            try {
                val application = applicationRepository.createApplication(input)

                _uiState.value = ApplicationUiState.Success(application)
            } catch (e: Exception) {
                _uiState.value = ApplicationUiState.Error(
                    e.message ?: "Erro ao criar candidatura."
                )
            }
        }
    }

    fun createApplication(
        input: CreateApplicationInput,
        motivationLetterPath: String,
        motivationLetterBytes: ByteArray,
    ) {
        viewModelScope.launch {
            _uiState.value = ApplicationUiState.Loading

            try {
                val uploadedPath = requireNotNull(storageRepository).uploadFile(
                    bucket = "motivation-letters",
                    path = motivationLetterPath,
                    bytes = motivationLetterBytes,
                )
                val application = applicationRepository.createApplication(
                    input.copy(motivationLetter = uploadedPath)
                )
                _uiState.value = ApplicationUiState.Success(application)
            } catch (e: Exception) {
                _uiState.value = ApplicationUiState.Error(
                    e.message ?: "Erro ao criar candidatura."
                )
            }
        }
    }

    fun updateApplicationDecision(
        applicationId: String,
        input: UpdateApplicationDecisionInput
    ) {
        viewModelScope.launch {
            _uiState.value = ApplicationUiState.Loading

            try {
                val application = applicationRepository.updateApplicationDecision(
                    applicationId,
                    input
                )

                _uiState.value = ApplicationUiState.Success(application)
            } catch (e: Exception) {
                _uiState.value = ApplicationUiState.Error(
                    e.message ?: "Erro ao atualizar decisão da candidatura."
                )
            }
        }
    }

    fun acceptApplication(
        applicationid: String
    ) {
        viewModelScope.launch {
            _uiState.value  = ApplicationUiState.Loading

            try {
                val application = applicationRepository.acceptApplication(applicationid)

                _uiState.value = ApplicationUiState.Success(application)
            } catch (e: Exception) {
                _uiState.value = ApplicationUiState.Error(
                    e.message ?: "Erro ao aceitar candidatura."
                )
            }
        }
    }

    fun rejectApplication(
        applicationId: String,
        rejectionReason: String
    ) {
        viewModelScope.launch {
            _uiState.value = ApplicationUiState.Loading

            try {
                val application = applicationRepository.rejectApplication(
                    applicationId,
                    rejectionReason
                )

                _uiState.value = ApplicationUiState.Success(application)
            } catch (e: Exception) {
                _uiState.value = ApplicationUiState.Error(
                    e.message ?: "Erro ao rejeitar candidatura."
                )
            }

        }
    }

    fun resetState() {
        _uiState.value = ApplicationUiState.Idle
    }
}
