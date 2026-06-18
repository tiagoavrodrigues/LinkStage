package turmaA.grupoB.LinkStage.data.remote.model.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    @SerialName("ADMIN")
    ADMIN,

    @SerialName("STUDENT")
    STUDENT,

    @SerialName("SUPERVISOR")
    SUPERVISOR,

    @SerialName("INSTITUTION")
    INSTITUTION
}