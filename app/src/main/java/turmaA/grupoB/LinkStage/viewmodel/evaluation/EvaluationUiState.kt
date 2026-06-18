package turmaA.grupoB.LinkStage.viewmodel.evaluation

import turmaA.grupoB.LinkStage.data.remote.model.evaluation.EvaluationModel
import turmaA.grupoB.LinkStage.data.remote.model.evaluation.FinalGradeModel
import turmaA.grupoB.LinkStage.data.remote.model.institution.InstitutionModel
import turmaA.grupoB.LinkStage.data.remote.model.internship.InternshipModel
import turmaA.grupoB.LinkStage.data.remote.model.user.ProfileModel

sealed class EvaluationUiState {
    data object Idle: EvaluationUiState()
    data object Loading: EvaluationUiState()
    data class Success(val evaluation: EvaluationModel): EvaluationUiState()
    data class SuccessList(val evaluations: List<EvaluationModel>) : EvaluationUiState()
    data class FinalGradeSuccess(val finalGrade: FinalGradeModel) : EvaluationUiState()
    data class InternshipResultSuccess(
        val internship: InternshipModel,
        val institution: InstitutionModel?,
        val finalGrade: FinalGradeModel,
        val evaluations: List<EvaluationModel>,
        val evaluatorProfiles: Map<String, ProfileModel>,
    ) : EvaluationUiState()
    data object Empty: EvaluationUiState()
    data class Error(val message: String) : EvaluationUiState()
}
