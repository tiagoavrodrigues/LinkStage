package turmaA.grupoB.LinkStage.viewmodel.instituicao.activity

import turmaA.grupoB.LinkStage.ui.admin.AdminMentor
import turmaA.grupoB.LinkStage.ui.instituicao.InstitutionInternship
import turmaA.grupoB.LinkStage.ui.instituicao.InstitutionMentorItem

data class InstitutionActivityData(
    val internships: List<InstitutionInternship>,
    val mentors: List<InstitutionMentorItem>,
    val adminMentors: List<AdminMentor>,
)

sealed class InstitutionActivityUiState {
    data object Idle : InstitutionActivityUiState()
    data object Loading : InstitutionActivityUiState()
    data class Success(val data: InstitutionActivityData) : InstitutionActivityUiState()
    data object Empty : InstitutionActivityUiState()
    data class Error(val message: String) : InstitutionActivityUiState()
}

sealed class AssignSupervisorUiState {
    data object Idle : AssignSupervisorUiState()
    data object Loading : AssignSupervisorUiState()
    data object Success : AssignSupervisorUiState()
    data class Error(val message: String) : AssignSupervisorUiState()
}
