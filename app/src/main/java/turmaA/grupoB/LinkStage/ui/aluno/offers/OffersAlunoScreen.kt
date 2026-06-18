package turmaA.grupoB.LinkStage.ui.aluno.offers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.data.remote.model.offer.InternshipOfferModel
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepository
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepository
import turmaA.grupoB.LinkStage.ui.common.CommonTopBar
import turmaA.grupoB.LinkStage.ui.common.LinkStageDialog
import turmaA.grupoB.LinkStage.ui.common.SectionLabel
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.BorderGrey
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.ui.theme.Red
import turmaA.grupoB.LinkStage.viewmodel.discover.DiscoverViewModel
import turmaA.grupoB.LinkStage.viewmodel.offer.OfferUiState
import turmaA.grupoB.LinkStage.viewmodel.offer.OfferViewModel
import turmaA.grupoB.LinkStage.viewmodel.offer.OfferViewModelFactory
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// region Data models

data class OfferItem(
    val id: String,
    val title: String,
    val company: String,
    val type: String,
    val publishedAgo: String,
    val logoColor: Color,
    val logoInitial: String,
    val isFavourite: Boolean = false,
    val duration: String = "",
    val area: String = "",
    val location: String = "",
    val deadline: String = "",
)

data class DiscoverFilters(
    val area: String = "",
    val location: String = "",
    val workModel: String = "",
    val duration: String = "",
    val deadline: String = "",
)

// endregion


@Composable
fun OffersAlunoScreen(
    modifier: Modifier = Modifier,
    onOfferClick: (String) -> Unit = {},
    discoverViewModel: DiscoverViewModel = viewModel(),
    offerViewModel: OfferViewModel = viewModel(
        factory = OfferViewModelFactory(
            OfferRepository(),
            InstitutionRepository(),
        )
    ),
) {
    var searchQuery by remember { mutableStateOf("") }
    var currentFilters by remember { mutableStateOf(DiscoverFilters()) }
    val hasActiveFilters = currentFilters != DiscoverFilters()

    val offerUiState by offerViewModel.uiState.collectAsState()

    val allOffers: List<OfferItem> = when (val state = offerUiState) {
        is OfferUiState.SuccessList -> state.offers.map { offer: InternshipOfferModel ->
            offer.toOfferItem(state.institutionsById[offer.institutionId]?.name)
        }

        else -> emptyList()
    }

    val filteredOffers = allOffers.filter { offer ->
        val matchesSearch = searchQuery.isBlank() ||
                offer.title.contains(searchQuery, ignoreCase = true) ||
                offer.company.contains(searchQuery, ignoreCase = true) ||
                offer.area.contains(searchQuery, ignoreCase = true)

        val matchesArea = currentFilters.area.isBlank() ||
                offer.area.contains(currentFilters.area, ignoreCase = true)

        val matchesLocation = currentFilters.location.isBlank() ||
                offer.location.contains(currentFilters.location, ignoreCase = true)

        val matchesWorkModel = currentFilters.workModel.isBlank() ||
                offer.type.contains(currentFilters.workModel, ignoreCase = true)

        val matchesDuration = currentFilters.duration.isBlank() ||
                offer.duration.contains(currentFilters.duration, ignoreCase = true)

        matchesSearch &&
                matchesArea &&
                matchesLocation &&
                matchesWorkModel &&
                matchesDuration
    }

    var showFilterModal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        offerViewModel.loadPublishedOffers()
    }

    Scaffold(
        modifier = modifier,
        containerColor = BackgroundLight,
        topBar = {
            Column(modifier = Modifier.background(BackgroundLight)) {
                CommonTopBar()
                Text(
                    text = stringResource(R.string.discover_title),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue,
                    ),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                )
                SearchBarWithFilter(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    hasActiveFilters = hasActiveFilters,
                    onFilterClick = { showFilterModal = true },
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            when (val state = offerUiState) {
                is OfferUiState.Loading -> {
                    item {
                        Text(
                            text = stringResource(R.string.offers_loading),
                            modifier = Modifier.padding(20.dp),
                            color = DarkGrey
                        )
                    }
                }

                is OfferUiState.Error -> {
                    item {
                        Text(
                            text = state.message,
                            modifier = Modifier.padding(20.dp),
                            color = Red
                        )
                    }
                }

                OfferUiState.Empty -> {
                    item {
                        Text(
                            text = stringResource(R.string.offers_empty),
                            modifier = Modifier.padding(20.dp),
                            color = DarkGrey
                        )
                    }
                }

                else -> {
                    items(filteredOffers, key = { it.id }) { offer ->
                        OfferCard(
                            offer = offer,
                            onClick = { onOfferClick(offer.id) },
                        )
                    }
                }
            }
        }
    }

    if (showFilterModal) {
        FilterModal(
            currentFilters = currentFilters,
            onApply = { filters ->
                currentFilters = filters
                showFilterModal = false
            },
            onDismiss = { showFilterModal = false },
        )
    }
}

// region Filter Modal


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDropdown(
    value: String,
    placeholder: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(placeholder, color = DarkGrey, fontSize = 14.sp) },
            trailingIcon = {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = DarkGrey)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LightBlue,
                unfocusedBorderColor = BorderGrey,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = DarkBlue,
                unfocusedTextColor = DarkBlue,
            ),
            singleLine = true,
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = DarkBlue) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
fun FilterModal(
    currentFilters: DiscoverFilters,
    onApply: (DiscoverFilters) -> Unit,
    onDismiss: () -> Unit,
) {
    var area by remember { mutableStateOf(currentFilters.area) }
    var location by remember { mutableStateOf(currentFilters.location) }
    var workModel by remember { mutableStateOf(currentFilters.workModel) }
    var duration by remember { mutableStateOf(currentFilters.duration) }
    var deadline by remember { mutableStateOf(currentFilters.deadline) }

    val workModelOptions = listOf(
        stringResource(R.string.filter_fulltime),
        stringResource(R.string.filter_parttime),
        stringResource(R.string.filter_remote),
        stringResource(R.string.filter_hybrid),
    )
    val durationOptions = listOf(
        stringResource(R.string.filter_3months),
        stringResource(R.string.filter_6months),
        stringResource(R.string.filter_9months),
        stringResource(R.string.filter_12months),
        stringResource(R.string.filter_12months_plus),
    )
    val deadlineOptions = listOf(
        stringResource(R.string.filter_1week),
        stringResource(R.string.filter_2weeks),
        stringResource(R.string.filter_1month),
        stringResource(R.string.filter_3months),
        stringResource(R.string.filter_no_limit),
    )

    LinkStageDialog(
        title = stringResource(R.string.filter_title),
        onConfirm = {
            onApply(
                DiscoverFilters(
                    area = area,
                    location = location,
                    workModel = workModel,
                    duration = duration,
                    deadline = deadline,
                )
            )
        },
        onDismiss = onDismiss,
        confirmText = stringResource(R.string.filter_apply),
        dismissText = stringResource(R.string.dialog_cancel),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Campo 1 — Área de atuação
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionLabel(stringResource(R.string.filter_area))
                    OutlinedTextField(
                        value = area,
                        onValueChange = { area = it },
                        placeholder = { Text(stringResource(R.string.common_write_here), color = DarkGrey, fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LightBlue,
                            unfocusedBorderColor = BorderGrey,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                        ),
                        singleLine = true,
                    )
                }

                // Campo 2 — Localização
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionLabel(stringResource(R.string.filter_location))
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        placeholder = { Text(stringResource(R.string.common_write_here), color = DarkGrey, fontSize = 14.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LightBlue,
                            unfocusedBorderColor = BorderGrey,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                        ),
                        singleLine = true,
                    )
                }

                // Campo 3 — Modelo de trabalho
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionLabel(stringResource(R.string.filter_work_model))
                    FilterDropdown(
                        value = workModel,
                        placeholder = stringResource(R.string.filter_select_model),
                        options = workModelOptions,
                        onOptionSelected = { workModel = it },
                    )
                }

                // Campo 4 — Duração
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionLabel(stringResource(R.string.filter_duration))
                    FilterDropdown(
                        value = duration,
                        placeholder = stringResource(R.string.filter_select_duration),
                        options = durationOptions,
                        onOptionSelected = { duration = it },
                    )
                }

                // Campo 5 — Data Limite
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionLabel(stringResource(R.string.filter_deadline))
                    FilterDropdown(
                        value = deadline,
                        placeholder = stringResource(R.string.filter_select_duration),
                        options = deadlineOptions,
                        onOptionSelected = { deadline = it },
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun FilterModalPreview() {
    MaterialTheme {
        FilterModal(
            currentFilters = DiscoverFilters(),
            onApply = {},
            onDismiss = {},
        )
    }
}

// endregion

// region Components

@Composable
private fun SearchBarWithFilter(
    query: String,
    onQueryChange: (String) -> Unit,
    hasActiveFilters: Boolean,
    onFilterClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text(stringResource(R.string.common_search), color = DarkGrey) },
            leadingIcon = {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = null,
                    tint = DarkGrey,
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = BorderGrey,
                focusedBorderColor = DarkBlue,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
            ),
            singleLine = true,
        )

        BadgedBox(
            badge = {
                if (hasActiveFilters) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Red),
                    )
                }
            },
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkBlue)
                    .clickable { onFilterClick() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.FilterList,
                    contentDescription = stringResource(R.string.discover_filters),
                    tint = Color.White,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}


@Composable
private fun OfferCard(
    offer: OfferItem,
    onClick: () -> Unit,
) {
    var isFav by remember { mutableStateOf(offer.isFavourite) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(offer.logoColor),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = offer.logoInitial,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = offer.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = offer.company,
                        style = MaterialTheme.typography.bodySmall,
                        color = DarkGrey,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                IconButton(
                    onClick = { isFav = !isFav },
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFav) stringResource(R.string.offer_remove_favorite) else stringResource(R.string.offer_add_favorite),
                        tint = if (isFav) LightBlue else DarkGrey,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = DarkGrey,
                    modifier = Modifier.size(13.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.discover_published_ago, offer.publishedAgo),
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkGrey,
                )

                if (offer.duration.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "• ${offer.duration}",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkGrey,
                    )
                }
                if (offer.location.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "• ${offer.location}",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkGrey,
                    )
                }
            }
        }
    }
}

private fun InternshipOfferModel.toOfferItem(institutionName: String?): OfferItem {
    val resolvedInstitutionName = institutionName.orEmpty()

    return OfferItem(
        id = id,
        title = title,
        company = resolvedInstitutionName,
        type = modality.orEmpty(),
        publishedAgo = publishDate.toDisplayDate(),
        logoColor = Color(0xFF212121),
        logoInitial = resolvedInstitutionName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
        duration = "",
        area = area,
        location = location.orEmpty(),
        deadline = deadline.orEmpty()
    )
}

private fun String?.toDisplayDate(): String {
    if (this.isNullOrBlank()) return ""

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    return runCatching { LocalDate.parse(this).format(formatter) }
        .recoverCatching { LocalDateTime.parse(this).toLocalDate().format(formatter) }
        .recoverCatching { OffsetDateTime.parse(this).toLocalDate().format(formatter) }
        .recoverCatching { Instant.parse(this).atZone(ZoneId.systemDefault()).toLocalDate().format(formatter) }
        .getOrDefault("")
}

// endregion
