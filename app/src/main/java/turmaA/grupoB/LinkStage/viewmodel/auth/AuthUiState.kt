package turmaA.grupoB.LinkStage.viewmodel.auth

import turmaA.grupoB.LinkStage.data.remote.model.user.ProfileModel

sealed class AuthUiState {
    data object Idle : AuthUiState()
    data object Loading : AuthUiState()
    data class Success(val profile: ProfileModel) : AuthUiState()
    data object Unauthenticated : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}
