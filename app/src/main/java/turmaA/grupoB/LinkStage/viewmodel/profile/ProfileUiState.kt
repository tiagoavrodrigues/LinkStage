package turmaA.grupoB.LinkStage.viewmodel.profile

import turmaA.grupoB.LinkStage.data.remote.model.user.ProfileModel

sealed class ProfileUiState {
    data object Idle : ProfileUiState()
    data object Loading : ProfileUiState()
    data class Success(val profile: ProfileModel) : ProfileUiState()
    data class SuccessList(val profiles: List<ProfileModel>) : ProfileUiState()

    data object Empty : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}