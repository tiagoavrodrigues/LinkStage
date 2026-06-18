package turmaA.grupoB.LinkStage.viewmodel.Institution

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepositoryInterface

class InstitutionViewModel(
    private val institutionRepository: InstitutionRepositoryInterface
) : ViewModel() {

    private val _uiState = MutableStateFlow<InstitutionUiState>(InstitutionUiState.Idle)
    val uiState: StateFlow<InstitutionUiState> = _uiState.asStateFlow()

    fun loadInstitutions() {
        viewModelScope.launch {
            _uiState.value = InstitutionUiState.Idle

            try {
                val institutions = institutionRepository.getInstitutions()

                _uiState.value = if (institutions.isEmpty()) {
                    InstitutionUiState.Empty
                } else {
                    InstitutionUiState.SuccessList(institutions)
                }
            } catch (e: Exception) {
                _uiState.value = InstitutionUiState.Error(
                    e.message ?: "Erro ao carregar instituições."
                )
            }
        }
    }

    fun loadInstitutionById(institutionId: String) {
        viewModelScope.launch {
            _uiState.value = InstitutionUiState.Idle

            try {
                val institution = institutionRepository.getInstitutionById(institutionId)

                _uiState.value = if (institution != null) {
                    InstitutionUiState.Success(institution)
                } else {
                    InstitutionUiState.Empty
                }

            } catch (e: Exception) {
                _uiState.value = InstitutionUiState.Error(
                    e.message ?: "Erro ao carregar instituição."
                )
            }
        }
    }

    fun loadInstitutionByUserId(userId: String) {
        viewModelScope.launch {
            _uiState.value = InstitutionUiState.Idle

            try {
                val institution = institutionRepository.getInstitutionByUserId(userId)

                _uiState.value = if (institution != null) {
                    InstitutionUiState.Success(institution)
                } else {
                    InstitutionUiState.Idle
                }
            } catch (e: Exception) {
                _uiState.value = InstitutionUiState.Error(
                    e.message ?: "Erro ao carregar instituição do utilizador."
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = InstitutionUiState.Idle
    }
}