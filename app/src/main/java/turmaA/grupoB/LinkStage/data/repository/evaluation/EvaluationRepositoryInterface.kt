package turmaA.grupoB.LinkStage.data.repository.evaluation

import turmaA.grupoB.LinkStage.data.remote.model.enums.EvaluationType
import turmaA.grupoB.LinkStage.data.remote.model.evaluation.CreateEvaluationInput
import turmaA.grupoB.LinkStage.data.remote.model.evaluation.EvaluationModel
import turmaA.grupoB.LinkStage.data.remote.model.evaluation.FinalGradeModel

interface EvaluationRepositoryInterface {
    suspend fun getEvaluations(): List<EvaluationModel>
    suspend fun getEvaluationsByInternship(internshipId: String): List<EvaluationModel>
    suspend fun getEvaluationByInternshipAndType(
        internshipId: String,
        evaluatorType: EvaluationType
    ): EvaluationModel?
    suspend fun createEvaluation(input: CreateEvaluationInput): EvaluationModel
    suspend fun getFinalGradeByInternship(internshipId:String): FinalGradeModel?
}