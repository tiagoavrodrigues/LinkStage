package turmaA.grupoB.LinkStage.data.repository.supervisor

import io.github.jan.supabase.postgrest.from
import turmaA.grupoB.LinkStage.data.remote.model.user.CreateSupervisorInput
import turmaA.grupoB.LinkStage.data.remote.model.user.SupervisorModel
import turmaA.grupoB.LinkStage.data.remote.model.user.SupervisorSkillModel
import turmaA.grupoB.LinkStage.data.remote.supabase.SupabaseClientProvider

class SupervisorRepository : SupervisorRepositoryInterface{

    private val supabase = SupabaseClientProvider.client

    override suspend fun getSupervisors(): List<SupervisorModel> {
        return supabase
            .from("supervisors")
            .select()
            .decodeList<SupervisorModel>()
    }

    override suspend fun getSupervisorById(supervisorId: String): SupervisorModel? {
        return supabase
            .from("supervisors")
            .select {
                filter {
                    eq("id", supervisorId)
                }
            }
            .decodeList<SupervisorModel>()
            .firstOrNull()
    }

    override suspend fun getSupervisorByUserId(userId: String): SupervisorModel? {
        return supabase
            .from("supervisors")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<SupervisorModel>()
            .firstOrNull()
    }

    override suspend fun getAvailableSupervisors(): List<SupervisorModel> {
        return supabase
            .from("supervisors")
            .select {
                filter {
                    eq("accepts_new_internships", true)
                }
            }
            .decodeList<SupervisorModel>()
    }

    override suspend fun getSupervisorsByDepartment(department:String): List<SupervisorModel> {
        return supabase
            .from("supervisors")
            .select {
                filter {
                    eq("department", department)
                }
            }
            .decodeList<SupervisorModel>()
    }

    override suspend fun getSupervisorSkills(supervisorId: String): List<SupervisorSkillModel> {
        return supabase
            .from("supervisor_skills")
            .select {
                filter {
                    eq("supervisor_id", supervisorId)
                }
            }
            .decodeList<SupervisorSkillModel>()
    }

    override suspend fun createSupervisor(input: CreateSupervisorInput): SupervisorModel {
        return supabase
            .from("supervisors")
            .insert(input) {
                select()
            }
            .decodeSingle<SupervisorModel>()
    }

}