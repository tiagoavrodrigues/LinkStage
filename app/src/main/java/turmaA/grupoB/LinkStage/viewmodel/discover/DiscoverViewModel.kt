package turmaA.grupoB.LinkStage.viewmodel.discover

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import turmaA.grupoB.LinkStage.ui.aluno.offers.DiscoverFilters
import turmaA.grupoB.LinkStage.ui.aluno.offers.OfferItem

class DiscoverViewModel : ViewModel() {

    val availableAreas = listOf(
        "Tecnologia", "Design", "Marketing", "Engenharia",
        "Saúde", "Educação", "Finanças", "Hotelaria",
    )

    private val _filters = MutableStateFlow(DiscoverFilters())
    val filters: StateFlow<DiscoverFilters> = _filters.asStateFlow()

    private val _allOffers = MutableStateFlow(sampleOffers)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredOffers = MutableStateFlow(sampleOffers)
    val filteredOffers: StateFlow<List<OfferItem>> = _filteredOffers.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilters()
    }

    fun applyFilters(newFilters: DiscoverFilters = _filters.value) {
        _filters.value = newFilters
        val query = _searchQuery.value
        val filters = _filters.value

        _filteredOffers.value = _allOffers.value.filter { offer ->
            val matchesSearch = query.isEmpty() ||
                offer.title.contains(query, ignoreCase = true) ||
                offer.company.contains(query, ignoreCase = true)

            val matchesArea = filters.area.isEmpty() ||
                offer.area.contains(filters.area, ignoreCase = true)

            val matchesLocation = filters.location.isEmpty() ||
                offer.location.contains(filters.location, ignoreCase = true)

            val matchesWorkModel = filters.workModel.isEmpty() ||
                offer.type.contains(filters.workModel, ignoreCase = true)

            val matchesDuration = filters.duration.isEmpty() ||
                offer.duration.contains(filters.duration, ignoreCase = true)

            val matchesDeadline = filters.deadline.isEmpty() ||
                offer.deadline == filters.deadline

            matchesSearch && matchesArea && matchesLocation &&
                matchesWorkModel && matchesDuration && matchesDeadline
        }
    }

    fun clearFilters() {
        _filters.value = DiscoverFilters()
        applyFilters()
    }
}

private val sampleOffers = listOf(
    OfferItem("1", "Designer de Produto", "Continente – Tempo Inteiro", "Tempo Inteiro", "5h atrás", Color(0xFFE53935), "C", duration = "6 Meses", area = "Design", location = "Porto", deadline = "1 Mês"),
    OfferItem("2", "UI/UX Designer", "Viana S.T.Arts – Remoto", "Remoto", "2d atrás", Color(0xFF212121), "V", isFavourite = true, duration = "3 Meses", area = "Design", location = "Remoto", deadline = "2 Semanas"),
    OfferItem("3", "Aprendiz de cozinha", "McDonald's – Tempo parcial", "Tempo Parcial", "5d atrás", Color(0xFFFFCC00), "M", duration = "12 Meses", area = "Hotelaria", location = "Viana do Castelo", deadline = "3 Meses"),
    OfferItem("4", "Recepcionista", "B&B – Tempo Inteiro", "Tempo Inteiro", "1w atrás", Color(0xFF1565C0), "B", duration = "6 Meses", area = "Hotelaria", location = "Braga", deadline = "1 Semana"),
    OfferItem("5", "Programador Full-Stack", "IPVC Tech – Remoto", "Remoto", "2w atrás", Color(0xFF37474F), "I", duration = "9 Meses", area = "Tecnologia", location = "Remoto", deadline = "Sem limite"),
)
