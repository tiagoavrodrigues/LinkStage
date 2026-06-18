package turmaA.grupoB.LinkStage.data.remote.model.communication

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageThreadParticipantModel(
    val id: String,

    @SerialName("thread_id")
    val threadId: String,

    @SerialName("user_id")
    val userId: String,

    @SerialName("created_at")
    val createdAt: String
)
