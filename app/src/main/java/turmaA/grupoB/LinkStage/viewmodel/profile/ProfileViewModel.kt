package turmaA.grupoB.LinkStage.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepositoryInterface

class ProfileViewModel(
    private val profileRepository: ProfileRepositoryInterface
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun loadProfiles() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading

            try {
                val profiles = profileRepository.getProfiles()

                _uiState.value = if (profiles.isEmpty()) {
                    ProfileUiState.Empty
                } else {
                    ProfileUiState.SuccessList(profiles)
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(
                    e.message ?: "Erro ao carregar perfis."
                )
            }
        }
    }

    fun loadProfileById(userId: String) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading

            try {
                val profile = profileRepository.getProfileById(userId)

                _uiState.value = if (profile != null) {
                    ProfileUiState.Success(profile)
                } else {
                    ProfileUiState.Empty
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(
                    e.message ?: "Erro ao carregar perfil"
                )

            }
        }
    }

    fun loadProfilesByRole(role: String) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading

            try {
                val profiles = profileRepository.getProfilesByRole(role)

                _uiState.value = if (profiles.isEmpty()) {
                    ProfileUiState.Empty
                } else {
                    ProfileUiState.SuccessList(profiles)
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(
                    e.message ?: "Erro ao carregar perfis por role"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = ProfileUiState.Idle
    }
}