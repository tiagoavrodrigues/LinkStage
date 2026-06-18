package turmaA.grupoB.LinkStage.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.student.StudentRepositoryInterface

class RegisterStudentViewModelFactory(
    private val authRepository: AuthRepositoryInterface,
    private val studentRepository: StudentRepositoryInterface
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RegisterStudentViewModel(
            authRepository = authRepository,
            studentRepository = studentRepository
        ) as T
    }

}