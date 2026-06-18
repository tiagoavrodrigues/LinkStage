package turmaA.grupoB.LinkStage.viewmodel.application

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import turmaA.grupoB.LinkStage.data.repository.application.ApplicationRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.student.StudentRepositoryInterface

class InstitutionApplicationsViewModelFactory(
    private val applicationRepository: ApplicationRepositoryInterface,
    private val studentRepository: StudentRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return InstitutionApplicationsViewModel(
            applicationRepository,
            studentRepository,
            profileRepository,
        ) as T
    }
}
