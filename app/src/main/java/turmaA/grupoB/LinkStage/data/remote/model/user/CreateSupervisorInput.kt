package turmaA.grupoB.LinkStage.data.remote.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateSupervisorInput(
    @SerialName("user_id")
    val userId: String,

    val department: String? = null,
    val specialty: String? = null,

    @SerialName("max_internships")
    val maxInternships: Int = 5,

    @SerialName("accepts_new_internships")
    val acceptsNewInternships: Boolean = true,
)
