package turmaA.grupoB.LinkStage.viewmodel.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import turmaA.grupoB.LinkStage.data.repository.student.StudentRepositoryInterface

class StudentViewModel(
    private val studentRepository: StudentRepositoryInterface
) : ViewModel() {
    private val _uiState = MutableStateFlow<StudentUiState>(StudentUiState.Idle)
    val uiState: StateFlow<StudentUiState> = _uiState.asStateFlow()

    fun loadStudents() {
        viewModelScope.launch {
            _uiState.value = StudentUiState.Loading

            try {
                val students = studentRepository.getStudents()

                _uiState.value = if (students.isEmpty()) {
                    StudentUiState.Empty
                } else {
                    StudentUiState.SuccessList(students)
                }
            } catch (e: Exception) {
                _uiState.value = StudentUiState.Error(
                    e.message ?: "Erro ao carregar estudantes."
                )
            }
        }
    }

    fun loadStudentById(studentId: String) {
        viewModelScope.launch {
            _uiState.value = StudentUiState.Loading

            try {
                val student = studentRepository.getStudentById(studentId)

                _uiState.value = if (student != null) {
                    StudentUiState.Success(student)
                } else {
                    StudentUiState.Empty
                }
            } catch (e: Exception) {
                _uiState.value = StudentUiState.Error(
                    e.message ?: "Erro ao carregar estudante."
                )
            }
        }
    }

    fun loadStudentByUserId(userId: String) {
        viewModelScope.launch {
            _uiState.value = StudentUiState.Loading

            try {
                val student = studentRepository.getStudentByUserId(userId)

                _uiState.value = if (student != null) {
                    StudentUiState.Success(student)
                } else {
                    StudentUiState.Empty
                }
            } catch (e: Exception) {
                _uiState.value = StudentUiState.Error(
                    e.message ?: "Erro ao carregar estudante do utilizador."
                )
            }
        }
    }

    fun loadStudentByNumber(studentNumber: String) {
        viewModelScope.launch {
            _uiState.value = StudentUiState.Loading

            try {
                val student = studentRepository.getStudentByNumber(studentNumber)

                _uiState.value = if (student != null) {
                    StudentUiState.Success(student)
                } else {
                    StudentUiState.Empty
                }
            } catch (e: Exception) {
                _uiState.value = StudentUiState.Error(
                    e.message ?: "Erro ao carregar estudante por número."
                )
            }
        }
    }

    fun loadStudentsByCourse(course: String) {
        viewModelScope.launch {
            _uiState.value = StudentUiState.Loading

            try {
                val students = studentRepository.getStudentsByCourse(course)

                _uiState.value = if (students.isEmpty()) {
                    StudentUiState.Empty
                } else {
                    StudentUiState.SuccessList(students)
                }
            } catch (e: Exception) {
                _uiState.value = StudentUiState.Error(
                    e.message ?: "Erro ao carregar alunos por curso."
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = StudentUiState.Idle
    }
}