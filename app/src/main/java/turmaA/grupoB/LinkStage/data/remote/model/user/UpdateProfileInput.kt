package turmaA.grupoB.LinkStage.data.remote.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileInput(
    val name: String? = null,
    val phone: String? = null,

    @SerialName("photo_url")
    val photoUrl: String? = null
)
