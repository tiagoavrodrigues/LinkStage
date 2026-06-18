package turmaA.grupoB.LinkStage.data.remote.model.institution

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateInstitutionInput(
    @SerialName("user_id")
    val userId: String,

    val name: String,
    val address: String? = null,
    val website: String? = null,
    val description: String? = null,
    val sector: String? = null,
)
