package turmaA.grupoB.LinkStage.data.remote.model.offer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import turmaA.grupoB.LinkStage.data.remote.model.enums.OfferStatus

@Serializable
data class UpdateOfferInput(
    val title: String? = null,
    val description: String? = null,
    val area: String? = null,
    val location: String? = null,
    val salary: Double? = null,
    val modality: String? = null,
    val vacancies: Int? = null,
    val requirements: String? = null,

    @SerialName("publish_date")
    val publishDate: String? = null,

    val deadline: String? = null,
    val status: OfferStatus? = null

)