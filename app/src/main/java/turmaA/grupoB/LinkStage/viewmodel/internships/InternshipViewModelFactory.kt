package turmaA.grupoB.LinkStage.viewmodel.internships

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.internship.LocalActivityRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.storage.StorageRepositoryInterface

class InternshipViewModelFactory(
    private val internshipRepository: InternshipRepositoryInterface,
    private val localActivityRepository: LocalActivityRepositoryInterface? = null,
    private val storageRepository: StorageRepositoryInterface? = null,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        return InternshipViewModel(
            internshipRepository,
            localActivityRepository,
            storageRepository,
        ) as T
    }
}
