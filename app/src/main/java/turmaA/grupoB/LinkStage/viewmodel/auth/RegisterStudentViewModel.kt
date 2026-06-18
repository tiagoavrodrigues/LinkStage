package turmaA.grupoB.LinkStage.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import turmaA.grupoB.LinkStage.viewmodel.auth.RegisterStudentInput
import turmaA.grupoB.LinkStage.data.remote.model.auth.SignUpInput
import turmaA.grupoB.LinkStage.data.remote.model.enums.UserRole
import turmaA.grupoB.LinkStage.data.remote.model.user.CreateStudentInput
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.student.StudentRepositoryInterface

class RegisterStudentViewModel(
    private val authRepository: AuthRepositoryInterface,
    private val studentRepository: StudentRepositoryInterface
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterStudentUiState>(RegisterStudentUiState.Idle)
    val uiState: StateFlow<RegisterStudentUiState> = _uiState.asStateFlow()

    fun registerStudent(input: RegisterStudentInput) {
        viewModelScope.launch {
            _uiState.value = RegisterStudentUiState.Loading

            try {
                validateRegisterStudentInput(input)

                val profile = authRepository.signUp(
                    SignUpInput(
                        name = input.name.trim(),
                        email = input.email.trim(),
                        password = input.password,
                        phone = input.phone?.trim()?.takeIf { it.isNotBlank() },
                        role = UserRole.STUDENT,
                        rgpdConsent = input.rgpdConsent
                    )
                )

                studentRepository.createStudent(
                    CreateStudentInput(
                        userId = profile.id,
                        studentNumber = input.studentNumber.trim(),
                        course = input.course.trim(),
                        academicYear = input.academicYear?.trim()?.takeIf { it.isNotBlank() }
                    )
                )

                _uiState.value = RegisterStudentUiState.Success(profile)
            } catch (e: Exception) {
                _uiState.value = RegisterStudentUiState.Error(
                    e.message ?: "Erro ao registar estudante."
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = RegisterStudentUiState.Idle
    }

    private fun validateRegisterStudentInput(input: RegisterStudentInput) {
        require(input.name.isNotBlank()) {
            "O nome é obrigatório."
        }

        require(input.email.isNotBlank()) {
            "O email é obrigatório."
        }

        require(input.password.length >= 6) {
            "A palavra-passe deve ter pelo menos 6 caracteres."
        }

        require(input.studentNumber.isNotBlank()) {
            "O número de estudante é obrigatório."
        }

        require(input.course.isNotBlank()) {
            "O curso é obrigatório."
        }

        require(input.rgpdConsent) {
            "É necessário aceitar o consentimento RGPD."
        }
    }
}