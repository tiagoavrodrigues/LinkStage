package turmaA.grupoB.LinkStage.data.remote.model.internship

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateActivityLogInput(
    @SerialName("internship_id")
    val internshipId: String,

    @SerialName("student_id")
    val studentId: String,

    val description: String,

    @SerialName("activity_date")
    val activityDate: String,

    val hours: Double? = null,

    val type: String? = null,

    val location: String? = null,

    @SerialName("attachment_url")
    val attachmentUrl: String? = null,
)
