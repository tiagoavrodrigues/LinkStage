package turmaA.grupoB.LinkStage.viewmodel.instituicao.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepository
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepository
import turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepository
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepository
import turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepository
import turmaA.grupoB.LinkStage.data.repository.student.StudentRepository
import turmaA.grupoB.LinkStage.data.repository.supervisor.SupervisorRepository

class InstitutionActivityViewModelFactory(
    private val internshipRepository: InternshipRepository = InternshipRepository(),
    private val studentRepository: StudentRepository = StudentRepository(),
    private val profileRepository: ProfileRepository = ProfileRepository(),
    private val offerRepository: OfferRepository = OfferRepository(),
    private val supervisorRepository: SupervisorRepository = SupervisorRepository(),
    private val institutionRepository: InstitutionRepository = InstitutionRepository(),
    private val authRepository: AuthRepository = AuthRepository(),
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return InstitutionActivityViewModel(
            internshipRepository,
            studentRepository,
            profileRepository,
            offerRepository,
            supervisorRepository,
            institutionRepository,
            authRepository,
        ) as T
    }
}
