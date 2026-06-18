package turmaA.grupoB.LinkStage.data.repository.evaluation

import io.github.jan.supabase.postgrest.from
import turmaA.grupoB.LinkStage.data.remote.model.enums.EvaluationType
import turmaA.grupoB.LinkStage.data.remote.model.evaluation.CreateEvaluationInput
import turmaA.grupoB.LinkStage.data.remote.model.evaluation.EvaluationModel
import turmaA.grupoB.LinkStage.data.remote.model.evaluation.FinalGradeModel
import turmaA.grupoB.LinkStage.data.remote.supabase.SupabaseClientProvider

class EvaluationRepository : EvaluationRepositoryInterface {

    private val supabase = SupabaseClientProvider.client

    override suspend fun getEvaluations(): List<EvaluationModel> {
        return supabase
            .from("evaluations")
            .select()
            .decodeList<EvaluationModel>()
    }

    override suspend fun getEvaluationsByInternship(internshipId: String): List<EvaluationModel> {
        return supabase
            .from("evaluations")
            .select {
                filter {
                    eq("internship_id", internshipId)
                }
            }
            .decodeList<EvaluationModel>()
    }

    override suspend fun getEvaluationByInternshipAndType(
        internshipId: String,
        evaluatorType: EvaluationType
    ): EvaluationModel? {
        return supabase
            .from("evaluations")
            .select {
                filter {
                    eq("internship_id", internshipId)
                    eq("evaluator_type", evaluatorType.name)
                }
            }
            .decodeList<EvaluationModel>()
            .firstOrNull()
    }

    override suspend fun createEvaluation(input: CreateEvaluationInput): EvaluationModel {
        return supabase
            .from("evaluations")
            .insert(input) {
                select()
            }
            .decodeSingle<EvaluationModel>()
    }

    override suspend fun getFinalGradeByInternship(internshipId: String): FinalGradeModel? {
        return supabase
            .from("final_grades")
            .select {
                filter {
                    eq("internship_id", internshipId)
                }
            }
            .decodeList<FinalGradeModel>()
            .firstOrNull()
    }
}