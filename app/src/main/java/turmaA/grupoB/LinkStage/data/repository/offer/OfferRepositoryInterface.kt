package turmaA.grupoB.LinkStage.data.repository.offer

import turmaA.grupoB.LinkStage.data.remote.model.offer.CreateOfferInput
import turmaA.grupoB.LinkStage.data.remote.model.offer.InternshipOfferModel
import turmaA.grupoB.LinkStage.data.remote.model.offer.UpdateOfferInput

interface OfferRepositoryInterface {
    suspend fun getPublishedOffers(): List<InternshipOfferModel>
    suspend fun getOffersByInstitution(institutionId: String): List<InternshipOfferModel>
    suspend fun getOfferById(offerId: String): InternshipOfferModel?
    suspend fun createOffer(input: CreateOfferInput): InternshipOfferModel
    suspend fun updateOffer(offerId: String, input: UpdateOfferInput): InternshipOfferModel
    suspend fun closeOffer(offerId: String): InternshipOfferModel
    suspend fun markOfferAsRemoved(offerId: String): InternshipOfferModel
}