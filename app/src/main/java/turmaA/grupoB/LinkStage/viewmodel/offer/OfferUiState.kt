package turmaA.grupoB.LinkStage.viewmodel.offer

import turmaA.grupoB.LinkStage.data.remote.model.offer.InternshipOfferModel
import turmaA.grupoB.LinkStage.data.remote.model.institution.InstitutionModel

sealed class OfferUiState {
    data object Idle : OfferUiState()
    data object Loading : OfferUiState()
    data class Success(val offer: InternshipOfferModel) : OfferUiState()
    data class SuccessDetails(
        val offer: InternshipOfferModel,
        val institution: InstitutionModel?,
    ) : OfferUiState()
    data class SuccessList(
        val offers: List<InternshipOfferModel>,
        val institutionsById: Map<String, InstitutionModel> = emptyMap(),
    ) : OfferUiState() {
        val institution: InstitutionModel?
            get() = institutionsById.values.singleOrNull()
    }
    data object Empty : OfferUiState()
    data class Error(val message: String) : OfferUiState()
}
