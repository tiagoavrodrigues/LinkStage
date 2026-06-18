package turmaA.grupoB.LinkStage.viewmodel.supervisor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import turmaA.grupoB.LinkStage.data.repository.supervisor.SupervisorRepositoryInterface

class SupervisorViewModel(
    private val supervisorRepository: SupervisorRepositoryInterface
) : ViewModel() {

    private val _uiState = MutableStateFlow<SupervisorUiState>(SupervisorUiState.Idle)
    val uiState: StateFlow<SupervisorUiState> = _uiState.asStateFlow()

    fun loadSupervisors() {
        viewModelScope.launch {
            _uiState.value = SupervisorUiState.Loading

            try {
                val supervisors = supervisorRepository.getSupervisors()

                _uiState.value = if (supervisors.isEmpty()) {
                    SupervisorUiState.Empty
                } else {
                    SupervisorUiState.SuccessList(supervisors)
                }

            } catch (e: Exception) {
                _uiState.value = SupervisorUiState.Error(
                    e.message ?: "Erro ao carregar orientadores."
                )
            }
        }
    }

    fun loadSupervisorById(supervisorId: String) {
        viewModelScope.launch {
            _uiState.value = SupervisorUiState.Loading

            try {
                val supervisor = supervisorRepository.getSupervisorById(supervisorId)

                _uiState.value = if (supervisor != null) {
                    SupervisorUiState.Success(supervisor)
                } else {
                    SupervisorUiState.Empty
                }
            } catch (e: Exception) {
                _uiState.value = SupervisorUiState.Error(
                    e.message ?: "Erro ao carregar orientador."
                )
            }
        }
    }

    fun loadSupervisorByUserId(userId: String) {
        viewModelScope.launch {
            _uiState.value = SupervisorUiState.Loading

            try {
                val supervisor = supervisorRepository.getSupervisorByUserId(userId)

                _uiState.value = if (supervisor != null) {
                    SupervisorUiState.Success(supervisor)
                } else {
                    SupervisorUiState.Empty
                }
            } catch (e: Exception) {
                _uiState.value = SupervisorUiState.Error(
                    e.message ?: "Erro ao carregar orientador do utilizador."
                )
            }
        }
    }

    fun loadAvailableSupervisors() {
        viewModelScope.launch {
            _uiState.value = SupervisorUiState.Loading

            try {
                val supervisors = supervisorRepository.getAvailableSupervisors()

                _uiState.value = if (supervisors.isEmpty()) {
                    SupervisorUiState.Empty
                } else {
                    SupervisorUiState.SuccessList(supervisors)
                }
            } catch (e: Exception) {
                _uiState.value = SupervisorUiState.Error(
                    e.message ?: "Erro ao carregar orientadores disponíveis."
                )
            }
        }
    }

    fun loadSupervisorsByDepartment(department: String) {
        viewModelScope.launch {
            _uiState.value = SupervisorUiState.Loading

            try {
                val supervisors = supervisorRepository.getSupervisorsByDepartment(department)

                _uiState.value = if (supervisors.isEmpty()) {
                    SupervisorUiState.Empty
                } else {
                    SupervisorUiState.SuccessList(supervisors)
                }
            } catch (e: Exception) {
                _uiState.value = SupervisorUiState.Error(
                    e.message ?: "Erro ao carregar orientadores por departamento."
                )
            }
        }
    }

    fun loadSupervisorSkills(supervisorId: String) {
        viewModelScope.launch {
            _uiState.value = SupervisorUiState.Loading

            try {
                val skills = supervisorRepository.getSupervisorSkills(supervisorId)

                _uiState.value = if (skills.isEmpty()) {
                    SupervisorUiState.Empty
                } else {
                    SupervisorUiState.SkillsSuccess(skills)
                }
            } catch (e: Exception) {
                _uiState.value = SupervisorUiState.Error(
                    e.message ?: "Erro ao carregar competências do orientador."
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = SupervisorUiState.Idle
    }
}