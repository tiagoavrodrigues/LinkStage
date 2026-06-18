package turmaA.grupoB.LinkStage.viewmodel.institutionhome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import turmaA.grupoB.LinkStage.data.repository.application.ApplicationRepository
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepository
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepository
import turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepository
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepository

class InstitutionHomeViewModelFactory(
    private val authRepository: AuthRepository = AuthRepository(),
    private val institutionRepository: InstitutionRepository = InstitutionRepository(),
    private val offerRepository: OfferRepository = OfferRepository(),
    private val applicationRepository: ApplicationRepository = ApplicationRepository(),
    private val internshipRepository: InternshipRepository = InternshipRepository(),
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass == InstitutionHomeViewModel::class.java) {
            "Unknown ViewModel class: ${modelClass.name}"
        }
        return InstitutionHomeViewModel(
            authRepository = authRepository,
            institutionRepository = institutionRepository,
            offerRepository = offerRepository,
            applicationRepository = applicationRepository,
            internshipRepository = internshipRepository,
        ) as T
    }
}
