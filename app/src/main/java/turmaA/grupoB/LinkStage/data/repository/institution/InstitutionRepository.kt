package turmaA.grupoB.LinkStage.data.repository.institution

import io.github.jan.supabase.postgrest.from
import turmaA.grupoB.LinkStage.data.remote.model.institution.CreateInstitutionInput
import turmaA.grupoB.LinkStage.data.remote.model.institution.InstitutionModel
import turmaA.grupoB.LinkStage.data.remote.supabase.SupabaseClientProvider

class InstitutionRepository : InstitutionRepositoryInterface{
    private val supabase = SupabaseClientProvider.client

    override suspend fun getInstitutions(): List<InstitutionModel> {
        return supabase
            .from("institutions")
            .select()
            .decodeList<InstitutionModel>()
    }

    override suspend fun getInstitutionById(institutionId: String): InstitutionModel? {
        return supabase
            .from("institutions")
            .select {
                filter {
                    eq("id", institutionId)
                }
            }
            .decodeList<InstitutionModel>()
            .firstOrNull()
    }

    override suspend fun getInstitutionByUserId(userId: String): InstitutionModel? {
        return supabase
            .from("institutions")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<InstitutionModel>()
            .firstOrNull()
    }

    override suspend fun createInstitution(input: CreateInstitutionInput): InstitutionModel {
        return supabase
            .from("institutions")
            .insert(input) {
                select()
            }
            .decodeSingle<InstitutionModel>()
    }
}