package turmaA.grupoB.LinkStage.viewmodel.evaluation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import turmaA.grupoB.LinkStage.data.repository.evaluation.EvaluationRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepositoryInterface

class EvaluationViewModelFactory(
    private val evaluationRepository: EvaluationRepositoryInterface,
    private val internshipRepository: InternshipRepositoryInterface? = null,
    private val profileRepository: ProfileRepositoryInterface? = null,
    private val institutionRepository: InstitutionRepositoryInterface? = null,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EvaluationViewModel(
            evaluationRepository,
            internshipRepository,
            profileRepository,
            institutionRepository,
        ) as T
    }
}
