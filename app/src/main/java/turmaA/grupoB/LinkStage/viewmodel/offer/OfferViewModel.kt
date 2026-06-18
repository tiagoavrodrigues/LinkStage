package turmaA.grupoB.LinkStage.viewmodel.offer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import turmaA.grupoB.LinkStage.data.remote.model.offer.CreateOfferInput
import turmaA.grupoB.LinkStage.data.remote.model.offer.UpdateOfferInput
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepositoryInterface

class OfferViewModel(
    private val offerRepository: OfferRepositoryInterface,
    private val institutionRepository: InstitutionRepositoryInterface? = null,
    private val authRepository: AuthRepositoryInterface? = null,
) : ViewModel() {

    private val _uiState = MutableStateFlow<OfferUiState>(OfferUiState.Idle)
    val uiState: StateFlow<OfferUiState> = _uiState.asStateFlow()

    fun loadPublishedOffers() {
        viewModelScope.launch {
            _uiState.value = OfferUiState.Loading

            try {
                val offers = offerRepository.getPublishedOffers()
                val institutionsById = institutionRepository?.let { repository ->
                    offers.map { it.institutionId }
                        .distinct()
                        .mapNotNull { institutionId ->
                            runCatching { repository.getInstitutionById(institutionId) }
                                .getOrNull()
                                ?.let { institutionId to it }
                        }
                        .toMap()
                }.orEmpty()

                _uiState.value = if (offers.isEmpty()) {
                    OfferUiState.Empty
                } else {
                    OfferUiState.SuccessList(offers, institutionsById)
                }

            } catch (e: Exception) {
                _uiState.value = OfferUiState.Error(
                    e.message ?: "Erro ao carregar ofertas publicadas."
                )
            }
        }
    }

    fun loadOffersByInstitution(institutionId: String) {
        viewModelScope.launch {
            _uiState.value = OfferUiState.Loading

            try {
                val offers = offerRepository.getOffersByInstitution((institutionId))

                _uiState.value = if (offers.isEmpty()) {
                    OfferUiState.Empty
                } else {
                    OfferUiState.SuccessList(offers)
                }
            } catch (e: Exception) {
                _uiState.value = OfferUiState.Error(
                    e.message ?: "Erro ao carregar ofertas da instituição."
                )
            }
        }
    }

    fun loadOffersForCurrentInstitution() {
        viewModelScope.launch {
            _uiState.value = OfferUiState.Loading

            try {
                val userId = authRepository?.getCurrentUserId()
                val institution = userId?.let { institutionRepository?.getInstitutionByUserId(it) }

                if (institution == null) {
                    _uiState.value = OfferUiState.Error(
                        "Não foi possível identificar a instituição."
                    )
                    return@launch
                }

                val offers = offerRepository.getOffersByInstitution(institution.id)

                _uiState.value = if (offers.isEmpty()) {
                    OfferUiState.Empty
                } else {
                    OfferUiState.SuccessList(
                        offers = offers,
                        institutionsById = mapOf(institution.id to institution),
                    )
                }
            } catch (e: Exception) {
                _uiState.value = OfferUiState.Error(
                    e.message ?: "Erro ao carregar ofertas da instituição."
                )
            }
        }
    }

    fun loadOfferById(offerId: String) {
        viewModelScope.launch {
            _uiState.value = OfferUiState.Loading

            try {
                val offer = offerRepository.getOfferById(offerId)

                _uiState.value = if (offer != null) {
                    OfferUiState.Success(offer)
                } else {
                    OfferUiState.Empty
                }

            } catch (e: Exception) {
                _uiState.value = OfferUiState.Error(
                    e.message ?: "Erro ao carregar oferta."
                )
            }
        }
    }

    fun loadOfferDetailsById(offerId: String) {
        viewModelScope.launch {
            _uiState.value = OfferUiState.Loading

            try {
                val offer = offerRepository.getOfferById(offerId)

                if (offer == null) {
                    _uiState.value = OfferUiState.Empty
                    return@launch
                }

                val institution = institutionRepository?.let { repository ->
                    runCatching {
                        repository.getInstitutionById(offer.institutionId)
                    }.getOrNull()
                }

                _uiState.value = OfferUiState.SuccessDetails(offer, institution)
            } catch (e: Exception) {
                _uiState.value = OfferUiState.Error(
                    e.message ?: "Erro ao carregar detalhes da oferta."
                )
            }
        }
    }

    fun createOffer(input: CreateOfferInput) {
        viewModelScope.launch {
            _uiState.value = OfferUiState.Loading

            try {
                val offer = offerRepository.createOffer(input)
                _uiState.value = OfferUiState.Success(offer)
            } catch (e: Exception) {
                _uiState.value = OfferUiState.Error(
                    e.message ?: "Erro ao criar oferta."
                )
            }
        }
    }

    fun updateOffer(offerId: String, input: UpdateOfferInput) {
        viewModelScope.launch {
            _uiState.value = OfferUiState.Loading

            try {
                val offer = offerRepository.updateOffer(offerId, input)

                _uiState.value = OfferUiState.Success(offer)
            } catch (e: Exception) {
                _uiState.value = OfferUiState.Error(
                    e.message ?: "Erro ao atualizar oferta."
                )
            }
        }
    }

    fun closeOffer(offerId: String) {
        viewModelScope.launch {
            _uiState.value = OfferUiState.Loading

            try {
                val offer = offerRepository.closeOffer(offerId)
                _uiState.value = OfferUiState.Success(offer)
            } catch (e: Exception) {
                _uiState.value = OfferUiState.Error(
                    e.message ?: "Erro ao fechar oferta."
                )
            }
        }
    }

    fun markOfferAsRemoved(offerId: String) {
        viewModelScope.launch {
            _uiState.value = OfferUiState.Loading

            try {
                val offer = offerRepository.markOfferAsRemoved(offerId)
                _uiState.value = OfferUiState.Success(offer)
            } catch (e: Exception) {
                _uiState.value = OfferUiState.Error(
                    e.message ?: "Erro ao remover oferta."
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = OfferUiState.Idle
    }
}
