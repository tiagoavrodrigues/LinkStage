package turmaA.grupoB.LinkStage.viewmodel.application

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import turmaA.grupoB.LinkStage.data.repository.application.ApplicationRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepositoryInterface

class StudentApplicationsViewModelFactory(
    private val applicationRepository: ApplicationRepositoryInterface,
    private val offerRepository: OfferRepositoryInterface,
    private val institutionRepository: InstitutionRepositoryInterface,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StudentApplicationsViewModel(
            applicationRepository,
            offerRepository,
            institutionRepository,
        ) as T
    }
}
