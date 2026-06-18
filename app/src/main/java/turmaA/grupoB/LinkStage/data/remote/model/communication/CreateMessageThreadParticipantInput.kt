package turmaA.grupoB.LinkStage.data.remote.model.communication

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateMessageThreadParticipantInput(
    @SerialName("thread_id")
    val threadId: String,

    @SerialName("user_id")
    val userId: String,
)
