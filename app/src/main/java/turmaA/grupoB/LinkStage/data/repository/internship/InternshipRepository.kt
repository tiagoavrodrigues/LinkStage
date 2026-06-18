package turmaA.grupoB.LinkStage.data.repository.internship

import io.github.jan.supabase.postgrest.from
import turmaA.grupoB.LinkStage.data.remote.model.enums.InternshipStatus
import turmaA.grupoB.LinkStage.data.remote.model.internship.ActivityLogModel
import turmaA.grupoB.LinkStage.data.remote.model.internship.AssignSupervisorInput
import turmaA.grupoB.LinkStage.data.remote.model.internship.CreateActivityLogInput
import turmaA.grupoB.LinkStage.data.remote.model.internship.CreateInternshipInput
import turmaA.grupoB.LinkStage.data.remote.model.internship.InternshipModel
import turmaA.grupoB.LinkStage.data.remote.supabase.SupabaseClientProvider

class InternshipRepository : InternshipRepositoryInterface {

    private val supabase = SupabaseClientProvider.client

    override suspend fun getInternships(): List<InternshipModel> {
        return supabase
            .from("internships")
            .select()
            .decodeList<InternshipModel>()
    }

    override suspend fun getInternshipById(internshipId: String): InternshipModel? {
        return supabase
            .from("internships")
            .select {
                filter {
                    eq("id", internshipId)
                }
            }
            .decodeList<InternshipModel>()
            .firstOrNull()
    }

    override suspend fun getInternshipsByStudent(studentId: String): List<InternshipModel> {
        return supabase
            .from("internships")
            .select {
                filter {
                    eq("student_id", studentId)
                }
            }
            .decodeList<InternshipModel>()
    }

    override suspend fun getInternshipsByInstitution(institutionId: String): List<InternshipModel> {
        return supabase
            .from("internships")
            .select {
                filter {
                    eq("institution_id", institutionId)
                }
            }
            .decodeList<InternshipModel>()
    }

    override suspend fun getInternshipsByStatus(status: InternshipStatus): List<InternshipModel> {
        return supabase
            .from("internships")
            .select {
                filter {
                    eq("status", status.name)
                }
            }
            .decodeList<InternshipModel>()
    }

    override suspend fun createInternship(input: CreateInternshipInput): InternshipModel {
        return supabase
            .from("internships")
            .insert(input) {
                select()
            }
            .decodeSingle<InternshipModel>()
    }

    override suspend fun assignSupervisor(
        internshipId: String,
        input: AssignSupervisorInput
    ): InternshipModel {
        return supabase
            .from("internships")
            .update(input) {
                select()
                filter {
                    eq("id", internshipId)
                }
            }
            .decodeSingle<InternshipModel>()
    }

    override suspend fun getActivityLogsByInternship(internshipId: String): List<ActivityLogModel> {
        return supabase
            .from("activity_logs")
            .select {
                filter {
                    eq("internship_id", internshipId)
                }
            }
            .decodeList<ActivityLogModel>()
    }

    override suspend fun getActivityLogById(activityLogId: String): ActivityLogModel? {
        return supabase
            .from("activity_logs")
            .select {
                filter {
                    eq("id", activityLogId)
                }
            }
            .decodeList<ActivityLogModel>()
            .firstOrNull()
    }

    override suspend fun createActivityLog(input: CreateActivityLogInput): ActivityLogModel {
        return supabase
            .from("activity_logs")
            .insert(input) {
                select()
            }
            .decodeSingle<ActivityLogModel>()
    }

}
