package turmaA.grupoB.LinkStage.viewmodel.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import turmaA.grupoB.LinkStage.data.remote.model.user.ProfileModel
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepository
import turmaA.grupoB.LinkStage.ui.aluno.settings.LoggedUser

sealed class PasswordChangeState {
    data object Idle : PasswordChangeState()
    data object Loading : PasswordChangeState()
    data object Success : PasswordChangeState()
    data class Error(val message: String) : PasswordChangeState()
}

class SettingsViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val profile = runCatching { authRepository.getCurrentUserProfile() }.getOrNull()
            _user.value = profile.toLoggedUser()
        }
    }

    private fun ProfileModel?.toLoggedUser(): LoggedUser {
        if (this == null) return LoggedUser(name = "", email = "")
        return LoggedUser(name = name.ifBlank { email }, email = email)
    }

    private val _currentLanguage = MutableStateFlow(resolveCurrentLanguage())
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _notifCandidaturas = MutableStateFlow(true)
    val notifCandidaturas: StateFlow<Boolean> = _notifCandidaturas.asStateFlow()

    private val _notifMensagens = MutableStateFlow(true)
    val notifMensagens: StateFlow<Boolean> = _notifMensagens.asStateFlow()

    private val _notifLembretes = MutableStateFlow(false)
    val notifLembretes: StateFlow<Boolean> = _notifLembretes.asStateFlow()

    private val _notifOrientador = MutableStateFlow(true)
    val notifOrientador: StateFlow<Boolean> = _notifOrientador.asStateFlow()

    private val _notifAvaliacao = MutableStateFlow(true)
    val notifAvaliacao: StateFlow<Boolean> = _notifAvaliacao.asStateFlow()

    private val _notifAtividade = MutableStateFlow(true)
    val notifAtividade: StateFlow<Boolean> = _notifAtividade.asStateFlow()

    private val _user = MutableStateFlow(
        LoggedUser(
            name = "",
            email = "",
        )
    )
    val user: StateFlow<LoggedUser> = _user.asStateFlow()

    private val _passwordChangeState = MutableStateFlow<PasswordChangeState>(PasswordChangeState.Idle)
    val passwordChangeState: StateFlow<PasswordChangeState> = _passwordChangeState.asStateFlow()

    fun changeLanguage(lang: String) {
        _currentLanguage.value = lang
        val tag = when (lang) {
            "portugal", "PT" -> "pt"
            "gb", "EN" -> "en"
            else -> if (lang == "PT") "pt" else "en"
        }
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
    }

    fun toggleNotifications(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun toggleNotifCandidaturas(enabled: Boolean) {
        _notifCandidaturas.value = enabled
    }

    fun toggleNotifMensagens(enabled: Boolean) {
        _notifMensagens.value = enabled
    }

    fun toggleNotifLembretes(enabled: Boolean) {
        _notifLembretes.value = enabled
    }

    fun toggleNotifOrientador(enabled: Boolean) {
        _notifOrientador.value = enabled
    }

    fun toggleNotifAvaliacao(enabled: Boolean) {
        _notifAvaliacao.value = enabled
    }

    fun toggleNotifAtividade(enabled: Boolean) {
        _notifAtividade.value = enabled
    }

    fun changePassword(newPassword: String) {
        viewModelScope.launch {
            _passwordChangeState.value = PasswordChangeState.Loading
            try {
                authRepository.updatePassword(newPassword)
                _passwordChangeState.value = PasswordChangeState.Success
            } catch (e: Exception) {
                _passwordChangeState.value = PasswordChangeState.Error(
                    e.message ?: "Não foi possível alterar a password."
                )
            }
        }
    }

    fun resetPasswordChangeState() {
        _passwordChangeState.value = PasswordChangeState.Idle
    }

    fun logout() {
        // Supabase auth sign out will be implemented here
    }

    companion object {
        private fun resolveCurrentLanguage(): String {
            val locales = AppCompatDelegate.getApplicationLocales()
            if (locales.isEmpty) return "EN"
            val tag = locales[0]?.language ?: return "EN"
            return if (tag == "pt") "PT" else "EN"
        }
    }
}
