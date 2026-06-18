package turmaA.grupoB.LinkStage.data.remote.model.offer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import turmaA.grupoB.LinkStage.data.remote.model.enums.OfferStatus

@Serializable
data class InternshipOfferModel(
    val id: String,
    @SerialName("institution_id")
    val institutionId: String,
    val title: String,
    val description: String,
    val area: String,
    val location: String? = null,
    val modality: String? = null,
    val salary: Double? = null,
    val vacancies: Int = 1,
    val requirements: String? = null,

    @SerialName("publish_date")
    val publishDate: String? = null,

    val deadline: String? = null,
    val status: OfferStatus = OfferStatus.DRAFT,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("updated_at")
    val updatedAt: String
)
