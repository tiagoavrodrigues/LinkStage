package turmaA.grupoB.LinkStage.data.remote.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import turmaA.grupoB.LinkStage.data.remote.model.enums.UserRole

@Serializable
data class CreateProfileInput(
    val id: String,
    val name: String,
    val email: String,
    val phone: String? = null,

    @SerialName("photo_url")
    val photoUrl: String? = null,

    val role: UserRole,

    @SerialName("rgpd_consent")
    val rgpdConsent: Boolean,

    @SerialName("rgpd_consent_at")
    val rgpdConsentAt: String? = null
)
