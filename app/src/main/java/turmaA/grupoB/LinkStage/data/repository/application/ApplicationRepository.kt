package turmaA.grupoB.LinkStage.data.repository.application

import io.github.jan.supabase.postgrest.from
import turmaA.grupoB.LinkStage.data.remote.model.application.ApplicationModel
import turmaA.grupoB.LinkStage.data.remote.model.application.CreateApplicationInput
import turmaA.grupoB.LinkStage.data.remote.model.application.UpdateApplicationDecisionInput
import turmaA.grupoB.LinkStage.data.remote.model.enums.ApplicationStatus
import turmaA.grupoB.LinkStage.data.remote.supabase.SupabaseClientProvider
import java.time.Instant

class ApplicationRepository : ApplicationRepositoryInterface {

    private val supabase = SupabaseClientProvider.client

    override suspend fun getApplicationById(applicationId: String): ApplicationModel? {
        return supabase
            .from("applications")
            .select {
                filter {
                    eq("id", applicationId)
                }
            }
            .decodeList<ApplicationModel>()
            .firstOrNull()
    }

    override suspend fun getApplicationsByOffer(offerId: String): List<ApplicationModel> {
        return supabase
            .from("applications")
            .select {
                filter {
                    eq("offer_id", offerId)
                }
            }
            .decodeList<ApplicationModel>()
    }

    override suspend fun getApplicationsByStudent(studentId: String): List<ApplicationModel> {
        return supabase
            .from("applications")
            .select {
                filter {
                    eq("student_id", studentId)
                }
            }
            .decodeList<ApplicationModel>()
    }

    override suspend fun getApplicationsByStatus(status: ApplicationStatus): List<ApplicationModel> {
        return supabase
            .from("applications")
            .select {
                filter {
                    eq("status", status.name)
                }
            }
            .decodeList<ApplicationModel>()
    }

    override suspend fun createApplication(input: CreateApplicationInput): ApplicationModel {
        return supabase
            .from("applications")
            .insert(input) {
                select()
            }
            .decodeSingle<ApplicationModel>()
    }

    override suspend fun updateApplicationDecision(
        applicationId: String,
        input: UpdateApplicationDecisionInput
    ): ApplicationModel {
        return supabase
            .from("applications")
            .update(input) {
                select()
                filter {
                    eq("id", applicationId)
                }
            }
            .decodeSingle<ApplicationModel>()
    }

    override suspend fun acceptApplication(
        applicationId: String
    ): ApplicationModel {
        return updateApplicationDecision(
            applicationId=applicationId,
            input=UpdateApplicationDecisionInput(
                status=ApplicationStatus.ACCEPTED,
                rejectionReason=null,
                decisionAt=Instant.now().toString()
            )
        )
    }

    override suspend fun rejectApplication(
        applicationId: String,
        rejectionReason: String,
    ): ApplicationModel {
        return updateApplicationDecision(
            applicationId=applicationId,
            input=UpdateApplicationDecisionInput(
                status=ApplicationStatus.REJECTED,
                rejectionReason=rejectionReason,
                decisionAt=Instant.now().toString()
            )
        )
    }
}