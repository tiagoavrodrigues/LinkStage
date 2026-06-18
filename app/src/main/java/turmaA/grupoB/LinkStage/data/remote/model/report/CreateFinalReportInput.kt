package turmaA.grupoB.LinkStage.data.remote.model.report

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import turmaA.grupoB.LinkStage.data.remote.model.enums.ReportStatus

@Serializable
data class CreateFinalReportInput(
    @SerialName("internship_id")
    val internshipId: String,
    @SerialName("student_id")
    val studentId: String,
    val title: String,
    val content: String? = null,
    @SerialName("file_url")
    val fileUrl: String? = null,
    val status: ReportStatus = ReportStatus.DRAFT
)
