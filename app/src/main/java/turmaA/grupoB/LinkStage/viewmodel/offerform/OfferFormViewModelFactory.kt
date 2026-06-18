package turmaA.grupoB.LinkStage.viewmodel.offerform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepositoryInterface

class OfferFormViewModelFactory(
    private val offerRepository: OfferRepositoryInterface,
    private val institutionRepository: InstitutionRepositoryInterface? = null,
    private val authRepository: AuthRepositoryInterface? = null,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OfferFormViewModel(offerRepository, institutionRepository, authRepository) as T
    }
}
