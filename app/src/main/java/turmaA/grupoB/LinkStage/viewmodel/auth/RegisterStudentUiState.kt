package turmaA.grupoB.LinkStage.viewmodel.auth

import turmaA.grupoB.LinkStage.data.remote.model.user.ProfileModel

sealed class RegisterStudentUiState {
    data object Idle: RegisterStudentUiState()
    data object Loading: RegisterStudentUiState()
    data class Success(val profile: ProfileModel) : RegisterStudentUiState()
    data class Error(val message: String) : RegisterStudentUiState()
}