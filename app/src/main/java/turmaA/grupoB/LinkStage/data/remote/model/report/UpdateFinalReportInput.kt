package turmaA.grupoB.LinkStage.data.remote.model.report

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import turmaA.grupoB.LinkStage.data.remote.model.enums.ReportStatus

@Serializable
data class UpdateFinalReportInput(
    val title: String? = null,
    val content: String? = null,
    @SerialName("file_url")
    val fileUrl: String? = null,
    val status: ReportStatus? = null,
    @SerialName("updated_at")
    val updatedAt: String
)
