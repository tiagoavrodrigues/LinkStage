package turmaA.grupoB.LinkStage.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import turmaA.grupoB.LinkStage.data.remote.model.auth.SignInInput
import turmaA.grupoB.LinkStage.data.remote.model.auth.SignUpInput
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepositoryInterface


class AuthViewModel(
    private val authRepository: AuthRepositoryInterface
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun loadCurrentUserProfile() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            try {
                val profile = authRepository.getCurrentUserProfile()

                _uiState.value = if (profile != null) {
                    AuthUiState.Success(profile)
                } else {
                    AuthUiState.Unauthenticated
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(
                    e.message ?: "Erro ao carregar perfil."
                )
            }
        }
    }

    fun signUp(input: SignUpInput) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            try {
                val profile = authRepository.signUp(input)
                _uiState.value = AuthUiState.Success(profile)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(
                    e.message ?: "Erro ao criar conta."
                )
            }
        }
    }

    fun signIn(input: SignInInput) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            try {
                authRepository.signIn(input)

                val profile = authRepository.getCurrentUserProfile()

                _uiState.value = if (profile != null) {
                    AuthUiState.Success(profile)
                } else {
                    AuthUiState.Unauthenticated
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(
                    e.message ?: "Erro ao iniciar sessão"
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            try {
                authRepository.signOut()
                _uiState.value = AuthUiState.Unauthenticated
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(
                    e.message ?: "Erro ao terminar sessão."
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

}