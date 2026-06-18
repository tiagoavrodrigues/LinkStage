package turmaA.grupoB.LinkStage.viewmodel.application

import turmaA.grupoB.LinkStage.data.remote.model.application.ApplicationModel

sealed class ApplicationUiState {
    data object Idle : ApplicationUiState()
    data object Loading : ApplicationUiState()
    data class Success(val applications: ApplicationModel) : ApplicationUiState()
    data class SuccessList(val applications: List<ApplicationModel>) : ApplicationUiState()
    data object Empty : ApplicationUiState()
    data class Error(val message: String) : ApplicationUiState()
}