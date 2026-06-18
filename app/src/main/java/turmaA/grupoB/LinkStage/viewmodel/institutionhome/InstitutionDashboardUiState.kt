package turmaA.grupoB.LinkStage.viewmodel.institutionhome

sealed interface InstitutionDashboardUiState {
    data object Loading : InstitutionDashboardUiState
    data class Success(
        val activeOffersCount: Int,
        val applicationsCount: Int,
        val activeInternshipsCount: Int,
        val pendingEvaluationsCount: Int,
        val noMentorCount: Int,
    ) : InstitutionDashboardUiState
    data object Empty : InstitutionDashboardUiState
    data class Error(val message: String) : InstitutionDashboardUiState
}
