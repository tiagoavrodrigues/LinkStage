package turmaA.grupoB.LinkStage.viewmodel.offerform

sealed class OfferFormUiState {
    data object Idle : OfferFormUiState()
    data object Loading : OfferFormUiState()
    data class Success(val offerId: String) : OfferFormUiState()
    data class Error(val message: String) : OfferFormUiState()
}
