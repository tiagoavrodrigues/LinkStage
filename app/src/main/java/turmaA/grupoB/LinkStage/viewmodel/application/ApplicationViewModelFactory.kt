package turmaA.grupoB.LinkStage.viewmodel.application

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import turmaA.grupoB.LinkStage.data.repository.application.ApplicationRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.storage.StorageRepositoryInterface

class ApplicationViewModelFactory(
    private val applicationRepository: ApplicationRepositoryInterface,
    private val storageRepository: StorageRepositoryInterface? = null,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>) : T {
        return ApplicationViewModel(applicationRepository, storageRepository) as T
    }

}
