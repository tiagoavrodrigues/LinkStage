package turmaA.grupoB.LinkStage.data.repository.report

import turmaA.grupoB.LinkStage.data.remote.model.enums.ReportStatus
import turmaA.grupoB.LinkStage.data.remote.model.report.CreateFinalReportInput
import turmaA.grupoB.LinkStage.data.remote.model.report.FinalReportModel
import turmaA.grupoB.LinkStage.data.remote.model.report.UpdateFinalReportInput

interface ReportRepositoryInterface {
    suspend fun getReports(): List<FinalReportModel>
    suspend fun getReportById(reportId: String): FinalReportModel?
    suspend fun getReportByInternship(internshipId: String): FinalReportModel?
    suspend fun getReportsByStudent(studentId: String): List<FinalReportModel>
    suspend fun getReportsByStatus(status: ReportStatus): List<FinalReportModel>
    suspend fun createReport(input: CreateFinalReportInput): FinalReportModel
    suspend fun updateReport(
        reportId: String,
        input: UpdateFinalReportInput
    ): FinalReportModel
    suspend fun submitReport(reportId: String): FinalReportModel
}
