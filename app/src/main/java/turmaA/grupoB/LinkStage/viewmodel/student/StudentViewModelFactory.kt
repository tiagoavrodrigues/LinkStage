package turmaA.grupoB.LinkStage.viewmodel.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import turmaA.grupoB.LinkStage.data.repository.student.StudentRepositoryInterface

class StudentViewModelFactory(
    private val studentRepository: StudentRepositoryInterface
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StudentViewModel(studentRepository) as T
    }

}