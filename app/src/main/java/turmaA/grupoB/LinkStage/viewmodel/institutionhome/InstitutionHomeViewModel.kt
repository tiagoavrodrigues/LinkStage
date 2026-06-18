package turmaA.grupoB.LinkStage.viewmodel.institutionhome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import turmaA.grupoB.LinkStage.data.remote.model.enums.InternshipStatus
import turmaA.grupoB.LinkStage.data.remote.model.enums.OfferStatus
import turmaA.grupoB.LinkStage.data.repository.application.ApplicationRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepositoryInterface

class InstitutionHomeViewModel(
    private val authRepository: AuthRepositoryInterface,
    private val institutionRepository: InstitutionRepositoryInterface,
    private val offerRepository: OfferRepositoryInterface,
    private val applicationRepository: ApplicationRepositoryInterface,
    private val internshipRepository: InternshipRepositoryInterface,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {

    private val dashboardScope = viewModelScope + dispatcher

    private val fallbackDashboard = InstitutionDashboardUiState.Success(
        activeOffersCount = 5,
        applicationsCount = 12,
        activeInternshipsCount = 3,
        pendingEvaluationsCount = 1,
        noMentorCount = 1,
    )

    private val _hasDismissedEvaluationModal = MutableStateFlow(false)
    val hasDismissedEvaluationModal: StateFlow<Boolean> = _hasDismissedEvaluationModal.asStateFlow()

    private val _hasSeenEvaluations = MutableStateFlow(false)
    val hasSeenEvaluations: StateFlow<Boolean> = _hasSeenEvaluations.asStateFlow()

    private val _dashboardUiState = MutableStateFlow<InstitutionDashboardUiState>(fallbackDashboard)
    val dashboardUiState: StateFlow<InstitutionDashboardUiState> = _dashboardUiState.asStateFlow()

    fun setHasDismissedEvaluationModal(dismissed: Boolean) {
        _hasDismissedEvaluationModal.value = dismissed
    }

    fun setHasSeenEvaluations(seen: Boolean) {
        _hasSeenEvaluations.value = seen
        if (seen) {
            _hasDismissedEvaluationModal.value = true
        }
    }

    fun loadDashboardForCurrentUser() {
        dashboardScope.launch {
            _dashboardUiState.value = InstitutionDashboardUiState.Loading
            try {
                val userId = authRepository.getCurrentUserId()
                if (userId.isNullOrBlank()) {
                    _dashboardUiState.value = fallbackDashboard
                    return@launch
                }

                val institution = institutionRepository.getInstitutionByUserId(userId)
                if (institution == null) {
                    _dashboardUiState.value = fallbackDashboard
                    return@launch
                }

                val offers = offerRepository.getOffersByInstitution(institution.id)
                val activeOffersCount = offers.count { it.status == OfferStatus.PUBLISHED }
                val offerIds = offers.map { it.id }
                val applications = offerIds.flatMap { offerId ->
                    applicationRepository.getApplicationsByOffer(offerId)
                }
                val internships = internshipRepository.getInternshipsByInstitution(institution.id)
                val activeInternshipsCount = internships.count { it.status == InternshipStatus.IN_PROGRESS }
                val pendingEvaluationsCount = internships.count {
                    it.status == InternshipStatus.PENDING_SUPERVISOR
                }
                val noMentorCount = internships.count { internship ->
                    internship.supervisorId.isNullOrBlank() &&
                        internship.companySupervisorName.isNullOrBlank()
                }

                _dashboardUiState.value = if (
                    activeOffersCount == 0 &&
                    applications.isEmpty() &&
                    activeInternshipsCount == 0 &&
                    pendingEvaluationsCount == 0 &&
                    noMentorCount == 0
                ) {
                    InstitutionDashboardUiState.Empty
                } else {
                    InstitutionDashboardUiState.Success(
                        activeOffersCount = activeOffersCount,
                        applicationsCount = applications.size,
                        activeInternshipsCount = activeInternshipsCount,
                        pendingEvaluationsCount = pendingEvaluationsCount,
                        noMentorCount = noMentorCount,
                    )
                }
            } catch (e: Exception) {
                _dashboardUiState.value = fallbackDashboard
            }
        }
    }
}
