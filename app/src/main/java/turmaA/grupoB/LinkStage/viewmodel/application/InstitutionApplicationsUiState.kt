package turmaA.grupoB.LinkStage.viewmodel.application

import turmaA.grupoB.LinkStage.data.remote.model.application.ApplicationModel
import turmaA.grupoB.LinkStage.data.remote.model.user.ProfileModel
import turmaA.grupoB.LinkStage.data.remote.model.user.StudentModel

data class InstitutionApplicationDetails(
    val application: ApplicationModel,
    val student: StudentModel?,
    val profile: ProfileModel?,
)

sealed class InstitutionApplicationsUiState {
    data object Idle : InstitutionApplicationsUiState()
    data object Loading : InstitutionApplicationsUiState()
    data class SuccessList(val applications: List<InstitutionApplicationDetails>) : InstitutionApplicationsUiState()
    data class SuccessDetails(val details: InstitutionApplicationDetails) : InstitutionApplicationsUiState()
    data object Empty : InstitutionApplicationsUiState()
    data class Error(val message: String) : InstitutionApplicationsUiState()
}
