package turmaA.grupoB.LinkStage.viewmodel.Institution

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepositoryInterface

class InstitutionViewModelFactory(
    private val institutionRepository: InstitutionRepositoryInterface
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return InstitutionViewModel(institutionRepository) as T
    }
}