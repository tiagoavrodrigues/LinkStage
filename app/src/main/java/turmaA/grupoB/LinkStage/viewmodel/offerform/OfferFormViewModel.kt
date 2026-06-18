package turmaA.grupoB.LinkStage.viewmodel.offerform

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import turmaA.grupoB.LinkStage.data.remote.model.enums.OfferStatus
import turmaA.grupoB.LinkStage.data.remote.model.offer.CreateOfferInput
import turmaA.grupoB.LinkStage.data.remote.model.offer.UpdateOfferInput
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepositoryInterface

class OfferFormViewModel(
    private val offerRepository: OfferRepositoryInterface? = null,
    private val institutionRepository: InstitutionRepositoryInterface? = null,
    private val authRepository: AuthRepositoryInterface? = null,
) : ViewModel() {

    // Step 0
    var schoolMentorId by mutableStateOf("")
    var schoolMentorName by mutableStateOf("")
    var companyMentorId by mutableStateOf("")
    var companyMentorName by mutableStateOf("")
    var title by mutableStateOf("")
    var category by mutableStateOf("")
    var description by mutableStateOf("")
    var isCompanyOffer by mutableStateOf(false)

    // Step 1
    var requirements by mutableStateOf("")
    var location by mutableStateOf("")
    var deadline by mutableStateOf("")

    var currentStep by mutableStateOf(0)

    private val _uiState = MutableStateFlow<OfferFormUiState>(OfferFormUiState.Idle)
    val uiState: StateFlow<OfferFormUiState> = _uiState.asStateFlow()

    val mentorSummary: String
        get() = when {
            isCompanyOffer -> listOfNotNull(
                schoolMentorName.ifBlank { null },
                companyMentorName.ifBlank { null },
            ).joinToString(" · ").ifEmpty { "" }
            else -> schoolMentorName
        }

    fun validateStep0(): Boolean =
        title.isNotBlank() && category.isNotBlank() && description.isNotBlank()

    fun validateStep1(): Boolean =
        location.isNotBlank() && deadline.isNotBlank() && isValidDate(deadline)

    fun isValidDate(date: String): Boolean {
        val regex = Regex("""^\d{2}/\d{2}/\d{4}$""")
        return regex.matches(date)
    }

    fun loadOfferForEdit(offerId: String) {
        if (offerId == "new" || offerRepository == null) return

        viewModelScope.launch {
            _uiState.value = OfferFormUiState.Loading

            try {
                val offer = offerRepository.getOfferById(offerId)

                if (offer == null) {
                    _uiState.value = OfferFormUiState.Error("Oferta não encontrada.")
                    return@launch
                }

                title = offer.title
                category = offer.area
                description = offer.description
                requirements = offer.requirements.orEmpty()
                location = offer.location.orEmpty()
                deadline = offer.deadline?.toDisplayDate().orEmpty()

                _uiState.value = OfferFormUiState.Idle
            } catch (e: Exception) {
                _uiState.value = OfferFormUiState.Error(
                    e.message ?: "Erro ao carregar oferta."
                )
            }
        }
    }

    fun submitOffer(offerId: String) {
        if (offerRepository == null) {
            _uiState.value = OfferFormUiState.Error("Não foi possível submeter a oferta.")
            return
        }

        viewModelScope.launch {
            _uiState.value = OfferFormUiState.Loading

            try {
                val offer = if (offerId == "new") {
                    val userId = authRepository?.getCurrentUserId()
                    val institution = userId?.let { institutionRepository?.getInstitutionByUserId(it) }

                    if (institution == null) {
                        _uiState.value = OfferFormUiState.Error("Não foi possível identificar a instituição.")
                        return@launch
                    }

                    offerRepository.createOffer(
                        CreateOfferInput(
                            institutionId = institution.id,
                            title = title,
                            description = description,
                            area = category,
                            location = location.ifBlank { null },
                            requirements = requirements.ifBlank { null },
                            deadline = deadline.toIsoDate(),
                            status = OfferStatus.PUBLISHED,
                        )
                    )
                } else {
                    offerRepository.updateOffer(
                        offerId,
                        UpdateOfferInput(
                            title = title,
                            description = description,
                            area = category,
                            location = location.ifBlank { null },
                            requirements = requirements.ifBlank { null },
                            deadline = deadline.toIsoDate(),
                            status = OfferStatus.PUBLISHED,
                        )
                    )
                }

                _uiState.value = OfferFormUiState.Success(offer.id)
            } catch (e: Exception) {
                _uiState.value = OfferFormUiState.Error(
                    e.message ?: "Erro ao submeter oferta."
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = OfferFormUiState.Idle
    }

    private fun String.toIsoDate(): String? {
        val parts = split("/")
        if (parts.size != 3) return null
        val (day, month, year) = parts
        return "$year-$month-$day"
    }

    private fun String.toDisplayDate(): String? {
        val parts = take(10).split("-")
        if (parts.size != 3) return null
        val (year, month, day) = parts
        return "$day/$month/$year"
    }
}
