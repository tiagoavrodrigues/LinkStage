package turmaA.grupoB.LinkStage.data.repository.report

import android.provider.SyncStateContract.Helpers.insert
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import turmaA.grupoB.LinkStage.data.remote.model.enums.ReportStatus
import turmaA.grupoB.LinkStage.data.remote.model.report.CreateFinalReportInput
import turmaA.grupoB.LinkStage.data.remote.model.report.FinalReportModel
import turmaA.grupoB.LinkStage.data.remote.model.report.UpdateFinalReportInput
import turmaA.grupoB.LinkStage.data.remote.supabase.SupabaseClientProvider
import java.time.Instant

class ReportRepository : ReportRepositoryInterface {

    private val supabase = SupabaseClientProvider.client

    override suspend fun getReports(): List<FinalReportModel> {
        return supabase
            .from("final_reports")
            .select()
            .decodeList<FinalReportModel>()
    }

    override suspend fun getReportById(reportId: String): FinalReportModel? {
        return supabase
            .from("final_reports")
            .select {
                filter {
                    eq("id", reportId)
                }
            }
            .decodeList<FinalReportModel>()
            .firstOrNull()
    }

    override suspend fun getReportByInternship(internshipId: String): FinalReportModel? {
        return supabase
            .from("final_reports")
            .select {
                filter {
                    eq("internship_id", internshipId)
                }
            }
            .decodeList<FinalReportModel>()
            .firstOrNull()
    }

    override suspend fun getReportsByStudent(studentId: String): List<FinalReportModel> {
        return supabase
            .from("final_reports")
            .select {
                filter {
                    eq("student_id", studentId)
                }
            }
            .decodeList<FinalReportModel>()
    }

    override suspend fun getReportsByStatus(status: ReportStatus): List<FinalReportModel> {
        return supabase
            .from("final_reports")
            .select {
                filter {
                    eq("status", status.name)
                }
            }
            .decodeList<FinalReportModel>()
    }

    override suspend fun createReport(input: CreateFinalReportInput): FinalReportModel {
        val now = Instant.now().toString()

        val newReport = CreateFinalReportPayload(
            internshipId = input.internshipId,
            studentId = input.studentId,
            title = input.title,
            content = input.content,
            fileUrl = input.fileUrl,
            status = input.status,
            createAt = now,
            updatedAt = now
        )

        return supabase
            .from("final_reports")
            .insert(input) {
                select()
            }
            .decodeSingle<FinalReportModel>()
    }

    override suspend fun updateReport(
        reportId: String,
        input: UpdateFinalReportInput
    ): FinalReportModel {
        return supabase
            .from("final_reports")
            .update(input) {
                filter {
                    eq("id", reportId)
                }
                select()
            }
            .decodeSingle<FinalReportModel>()
    }

    override suspend fun submitReport(reportId: String): FinalReportModel {
        val now = Instant.now().toString()

        val input = UpdateFinalReportInput(
            status = ReportStatus.SUBMITTED,
            updatedAt = now
        )

        return updateReport(
            reportId = reportId,
            input = input
        )
    }
}

@Serializable
private data class CreateFinalReportPayload(
    @SerialName("internship_id")
    val internshipId: String,
    @SerialName("student_id")
    val studentId: String,
    val title: String,
    val content: String? = null,
    @SerialName("file_url")
    val fileUrl: String? = null,
    val status: ReportStatus = ReportStatus.DRAFT,
    @SerialName("created_at")
    val createAt: String,
    @SerialName("updated_at")
    val updatedAt: String

)