package turmaA.grupoB.LinkStage.viewmodel.application

import turmaA.grupoB.LinkStage.data.remote.model.application.ApplicationModel

data class StudentApplicationDetails(
    val application: ApplicationModel,
    val offerTitle: String,
    val institutionName: String,
)

sealed class StudentApplicationsUiState {
    data object Idle : StudentApplicationsUiState()
    data object Loading : StudentApplicationsUiState()
    data class SuccessList(
        val applications: List<StudentApplicationDetails>
    ) : StudentApplicationsUiState()
    data object Empty : StudentApplicationsUiState()
    data class Error(val message: String) : StudentApplicationsUiState()
}
