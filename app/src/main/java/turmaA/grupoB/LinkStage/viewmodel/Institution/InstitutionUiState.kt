package turmaA.grupoB.LinkStage.viewmodel.Institution

import turmaA.grupoB.LinkStage.data.remote.model.institution.InstitutionModel

sealed class InstitutionUiState {
    data object Idle : InstitutionUiState()
    data object Loading : InstitutionUiState()
    data class Success(val institution: InstitutionModel) : InstitutionUiState()
    data class SuccessList(val institutions: List<InstitutionModel>) : InstitutionUiState()
    data object Empty : InstitutionUiState()
    data class Error(val message: String) : InstitutionUiState()
}