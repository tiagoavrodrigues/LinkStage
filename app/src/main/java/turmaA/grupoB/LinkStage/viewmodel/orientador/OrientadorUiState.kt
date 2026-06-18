package turmaA.grupoB.LinkStage.viewmodel.orientador

sealed class OrientadorDashboardUiState {
    data object Idle : OrientadorDashboardUiState()
    data object Loading : OrientadorDashboardUiState()
    data class Success(val data: OrientadorDashboardData) : OrientadorDashboardUiState()
    data object Empty : OrientadorDashboardUiState()
    data class Error(val message: String) : OrientadorDashboardUiState()
}

sealed class OrientadorStudentDetailUiState {
    data object Idle : OrientadorStudentDetailUiState()
    data object Loading : OrientadorStudentDetailUiState()
    data class Success(val data: OrientadorStudentDetailData) : OrientadorStudentDetailUiState()
    data object Empty : OrientadorStudentDetailUiState()
    data class Error(val message: String) : OrientadorStudentDetailUiState()
}

sealed class OrientadorInternshipDetailUiState {
    data object Idle : OrientadorInternshipDetailUiState()
    data object Loading : OrientadorInternshipDetailUiState()
    data class Success(val data: OrientadorInternshipDetailData) : OrientadorInternshipDetailUiState()
    data object Empty : OrientadorInternshipDetailUiState()
    data class Error(val message: String) : OrientadorInternshipDetailUiState()
}

sealed class SubmitFinalGradeUiState {
    data object Idle : SubmitFinalGradeUiState()
    data object Loading : SubmitFinalGradeUiState()
    data object Success : SubmitFinalGradeUiState()
    data class Error(val message: String) : SubmitFinalGradeUiState()
}
