package turmaA.grupoB.LinkStage.data.remote.model.enums

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class InternshipStatus {
    @SerialName("PENDING_SUPERVISOR")
    PENDING_SUPERVISOR,

    @SerialName("IN_PROGRESS")
    IN_PROGRESS,

    @SerialName("COMPLETED")
    COMPLETED,

    @SerialName("EVALUATED")
    EVALUATED,

    @SerialName("CANCELLED")
    CANCELED
}