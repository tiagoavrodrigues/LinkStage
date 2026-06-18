package turmaA.grupoB.LinkStage.viewmodel.supervisor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import turmaA.grupoB.LinkStage.data.repository.supervisor.SupervisorRepositoryInterface

class SupervisorViewModelFactory(
    private val supervisorRepository: SupervisorRepositoryInterface
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SupervisorViewModel(supervisorRepository) as T
    }
}