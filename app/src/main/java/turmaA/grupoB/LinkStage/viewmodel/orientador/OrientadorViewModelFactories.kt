package turmaA.grupoB.LinkStage.viewmodel.orientador

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepository
import turmaA.grupoB.LinkStage.data.repository.evaluation.EvaluationRepository
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepository
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepository
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepository
import turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepository
import turmaA.grupoB.LinkStage.data.repository.student.StudentRepository

class OrientadorDashboardViewModelFactory(
    private val internshipRepository: InternshipRepository = InternshipRepository(),
    private val studentRepository: StudentRepository = StudentRepository(),
    private val profileRepository: ProfileRepository = ProfileRepository(),
    private val offerRepository: OfferRepository = OfferRepository(),
    private val institutionRepository: InstitutionRepository = InstitutionRepository(),
    private val evaluationRepository: EvaluationRepository = EvaluationRepository(),
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrientadorDashboardViewModel::class.java)) {
            return OrientadorDashboardViewModel(
                internshipRepository = internshipRepository,
                studentRepository = studentRepository,
                profileRepository = profileRepository,
                offerRepository = offerRepository,
                institutionRepository = institutionRepository,
                evaluationRepository = evaluationRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class OrientadorStudentDetailViewModelFactory(
    private val internshipRepository: InternshipRepository = InternshipRepository(),
    private val studentRepository: StudentRepository = StudentRepository(),
    private val profileRepository: ProfileRepository = ProfileRepository(),
    private val offerRepository: OfferRepository = OfferRepository(),
    private val institutionRepository: InstitutionRepository = InstitutionRepository(),
    private val evaluationRepository: EvaluationRepository = EvaluationRepository(),
    private val authRepository: AuthRepository = AuthRepository(),
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrientadorStudentDetailViewModel::class.java)) {
            return OrientadorStudentDetailViewModel(
                internshipRepository = internshipRepository,
                studentRepository = studentRepository,
                profileRepository = profileRepository,
                offerRepository = offerRepository,
                institutionRepository = institutionRepository,
                evaluationRepository = evaluationRepository,
                authRepository = authRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class OrientadorInternshipDetailViewModelFactory(
    private val internshipRepository: InternshipRepository = InternshipRepository(),
    private val studentRepository: StudentRepository = StudentRepository(),
    private val profileRepository: ProfileRepository = ProfileRepository(),
    private val offerRepository: OfferRepository = OfferRepository(),
    private val institutionRepository: InstitutionRepository = InstitutionRepository(),
    private val evaluationRepository: EvaluationRepository = EvaluationRepository(),
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrientadorInternshipDetailViewModel::class.java)) {
            return OrientadorInternshipDetailViewModel(
                internshipRepository = internshipRepository,
                studentRepository = studentRepository,
                profileRepository = profileRepository,
                offerRepository = offerRepository,
                institutionRepository = institutionRepository,
                evaluationRepository = evaluationRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
