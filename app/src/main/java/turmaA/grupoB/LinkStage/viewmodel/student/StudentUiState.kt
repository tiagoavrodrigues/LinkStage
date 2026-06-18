package turmaA.grupoB.LinkStage.viewmodel.student

import turmaA.grupoB.LinkStage.data.remote.model.user.StudentModel

sealed class StudentUiState {
    data object Idle : StudentUiState()
    data object Loading : StudentUiState()
    data class Success(val student: StudentModel) : StudentUiState()
    data class SuccessList(val students: List<StudentModel>) : StudentUiState()
    data object Empty : StudentUiState()
    data class Error(val message: String) : StudentUiState()
}