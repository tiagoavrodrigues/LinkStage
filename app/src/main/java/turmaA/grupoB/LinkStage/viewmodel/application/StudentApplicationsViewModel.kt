package turmaA.grupoB.LinkStage.viewmodel.application

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import turmaA.grupoB.LinkStage.data.remote.model.institution.InstitutionModel
import turmaA.grupoB.LinkStage.data.remote.model.offer.InternshipOfferModel
import turmaA.grupoB.LinkStage.data.repository.application.ApplicationRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepositoryInterface

class StudentApplicationsViewModel(
    private val applicationRepository: ApplicationRepositoryInterface,
    private val offerRepository: OfferRepositoryInterface,
    private val institutionRepository: InstitutionRepositoryInterface,
) : ViewModel() {

    private val _uiState = MutableStateFlow<StudentApplicationsUiState>(
        StudentApplicationsUiState.Idle
    )
    val uiState: StateFlow<StudentApplicationsUiState> = _uiState.asStateFlow()

    fun loadApplicationsByStudent(studentId: String) {
        viewModelScope.launch {
            _uiState.value = StudentApplicationsUiState.Loading

            try {
                val applications = applicationRepository.getApplicationsByStudent(studentId)

                if (applications.isEmpty()) {
                    _uiState.value = StudentApplicationsUiState.Empty
                    return@launch
                }

                val offers = mutableMapOf<String, InternshipOfferModel?>()
                val institutions = mutableMapOf<String, InstitutionModel?>()

                val enrichedApplications = applications.map { application ->
                    val offer = offers.getOrPut(application.offerId) {
                        offerRepository.getOfferById(application.offerId)
                    }
                    val institution = offer?.let {
                        institutions.getOrPut(it.institutionId) {
                            institutionRepository.getInstitutionById(it.institutionId)
                        }
                    }

                    StudentApplicationDetails(
                        application = application,
                        offerTitle = offer?.title.orEmpty(),
                        institutionName = institution?.name.orEmpty(),
                    )
                }

                _uiState.value = StudentApplicationsUiState.SuccessList(enrichedApplications)
            } catch (e: Exception) {
                _uiState.value = StudentApplicationsUiState.Error(
                    e.message ?: "Erro ao carregar detalhes das candidaturas."
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = StudentApplicationsUiState.Idle
    }
}
