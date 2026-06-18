package turmaA.grupoB.LinkStage.viewmodel.supervisor

import turmaA.grupoB.LinkStage.data.remote.model.user.SupervisorModel
import turmaA.grupoB.LinkStage.data.remote.model.user.SupervisorSkillModel

sealed class SupervisorUiState {
    data object Idle : SupervisorUiState()
    data object Loading : SupervisorUiState()
    data class Success(val supervisor: SupervisorModel) : SupervisorUiState()
    data class SuccessList(val supervisors: List<SupervisorModel>) : SupervisorUiState()
    data class SkillsSuccess(val skills: List<SupervisorSkillModel>) : SupervisorUiState()

    data object Empty : SupervisorUiState()
    data class Error(val message: String) : SupervisorUiState()
}