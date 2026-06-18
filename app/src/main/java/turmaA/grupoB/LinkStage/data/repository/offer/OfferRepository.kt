package turmaA.grupoB.LinkStage.data.repository.offer

import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import turmaA.grupoB.LinkStage.data.remote.model.enums.OfferStatus
import turmaA.grupoB.LinkStage.data.remote.model.offer.CreateOfferInput
import turmaA.grupoB.LinkStage.data.remote.model.offer.InternshipOfferModel
import turmaA.grupoB.LinkStage.data.remote.model.offer.UpdateOfferInput
import turmaA.grupoB.LinkStage.data.remote.supabase.SupabaseClientProvider

class OfferRepository : OfferRepositoryInterface {
    private val supabase = SupabaseClientProvider.client

    override suspend fun getPublishedOffers(): List<InternshipOfferModel> {
        return supabase
            .from("internship_offers")
            .select {
                filter {
                    eq("status", OfferStatus.PUBLISHED.name)
                }
            }
            .decodeList<InternshipOfferModel>()
    }

    override suspend fun getOffersByInstitution(institutionId: String): List<InternshipOfferModel> {
        return supabase
            .from("internship_offers")
            .select {
                filter {
                    eq("institution_id", institutionId)
                }
            }
            .decodeList<InternshipOfferModel>()
    }

    override suspend fun getOfferById(offerId: String): InternshipOfferModel? {
        return supabase
            .from("internship_offers")
            .select {
                filter {
                    eq("id", offerId)
                }
            }
            .decodeList<InternshipOfferModel>()
            .firstOrNull()
    }

    override suspend fun createOffer(input: CreateOfferInput): InternshipOfferModel {
        return supabase
            .from("internship_offers")
            .insert(input) {
                select()
            }
            .decodeSingle<InternshipOfferModel>()
    }

    override suspend fun updateOffer(
        offerId: String,
        input: UpdateOfferInput
    ): InternshipOfferModel {
        val updateData = input.toJsonObject()

        require(updateData.isNotEmpty()) {
            "UpdateOfferInput não contém campos para atualizar."
        }

        return supabase
            .from("internship_offers")
            .update(updateData) {
                select()
                filter {
                    eq("id", offerId)
                }
            }
            .decodeSingle<InternshipOfferModel>()
    }

    override suspend fun closeOffer(offerId: String): InternshipOfferModel {
        return updateOffer(
            offerId = offerId,
            input = UpdateOfferInput(
                status = OfferStatus.CLOSED
            )
        )
    }

    override suspend fun markOfferAsRemoved(offerId: String): InternshipOfferModel {
        return updateOffer(
            offerId = offerId,
            input = UpdateOfferInput(
                status = OfferStatus.REMOVED
            )
        )
    }

    private fun UpdateOfferInput.toJsonObject(): JsonObject {
        return buildJsonObject {
            title?.let { put("title", JsonPrimitive(it)) }
            description?.let { put("description", JsonPrimitive(it)) }
            area?.let { put("area", JsonPrimitive(it)) }
            location?.let { put("location", JsonPrimitive(it)) }
            modality?.let { put("modality", JsonPrimitive(it)) }
            salary?.let { put("salary", JsonPrimitive(it)) }
            vacancies?.let { put("vacancies", JsonPrimitive(it)) }
            requirements?.let { put("requirements", JsonPrimitive(it)) }
            publishDate?.let { put("publish_date", JsonPrimitive(it)) }
            deadline?.let { put("deadline", JsonPrimitive(it)) }
            status?.let { put("status", JsonPrimitive(it.name)) }
        }
    }
}