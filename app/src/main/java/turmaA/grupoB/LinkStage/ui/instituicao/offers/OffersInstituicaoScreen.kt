package turmaA.grupoB.LinkStage.ui.instituicao.offers

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.data.remote.model.offer.InternshipOfferModel
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepository
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepository
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepository
import turmaA.grupoB.LinkStage.ui.aluno.offers.OfferItem
import turmaA.grupoB.LinkStage.ui.common.CommonTopBar
import turmaA.grupoB.LinkStage.ui.common.LinkStageDialog
import turmaA.grupoB.LinkStage.ui.common.SectionLabel
import turmaA.grupoB.LinkStage.ui.instituicao.InstituicaoRoutes
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.BorderGrey
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.ui.theme.Red
import turmaA.grupoB.LinkStage.viewmodel.offer.OfferUiState
import turmaA.grupoB.LinkStage.viewmodel.offer.OfferViewModel
import turmaA.grupoB.LinkStage.viewmodel.offer.OfferViewModelFactory

private fun InternshipOfferModel.toOfferItem(institutionName: String): OfferItem {
    return OfferItem(
        id = id,
        title = title,
        company = institutionName,
        type = modality.orEmpty(),
        publishedAgo = (publishDate ?: createdAt).take(10),
        logoColor = Color(0xFF1565C0),
        logoInitial = institutionName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
        duration = "",
        area = area,
        location = location.orEmpty(),
        deadline = deadline.orEmpty(),
    )
}

@Composable
fun OffersInstituicaoScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    offerViewModel: OfferViewModel = viewModel(
        factory = OfferViewModelFactory(
            offerRepository = OfferRepository(),
            institutionRepository = InstitutionRepository(),
            authRepository = AuthRepository(),
        )
    ),
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var offerToDelete by remember { mutableStateOf<OfferItem?>(null) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var filterType by rememberSaveable { mutableStateOf("") }
    var filterArea by rememberSaveable { mutableStateOf("") }
    var filterLocation by rememberSaveable { mutableStateOf("") }

    val offerUiState by offerViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        offerViewModel.loadOffersForCurrentInstitution()
    }

    val institutionName = (offerUiState as? OfferUiState.SuccessList)?.institution?.name.orEmpty()

    val offers = when (val state = offerUiState) {
        is OfferUiState.SuccessList -> state.offers.map { it.toOfferItem(institutionName) }
        else -> emptyList()
    }

    val filteredOffers = offers.filter { offer ->
        val matchesSearch = searchQuery.isEmpty() ||
            offer.title.contains(searchQuery, ignoreCase = true) ||
            offer.company.contains(searchQuery, ignoreCase = true)
        val matchesType = filterType.isEmpty() ||
            offer.type.contains(filterType, ignoreCase = true)
        val matchesArea = filterArea.isEmpty() ||
            offer.area.contains(filterArea, ignoreCase = true)
        val matchesLocation = filterLocation.isEmpty() ||
            offer.location.contains(filterLocation, ignoreCase = true)
        matchesSearch && matchesType && matchesArea && matchesLocation
    }

    if (offerToDelete != null) {
        DeleteOfferDialog(
            offerTitle = offerToDelete!!.title,
            onConfirm = {
                offerViewModel.markOfferAsRemoved(offerToDelete!!.id)
                offerToDelete = null
                offerViewModel.loadOffersForCurrentInstitution()
            },
            onDismiss = { offerToDelete = null },
        )
    }

    if (showFilterDialog) {
        OfferFilterDialog(
            currentType = filterType,
            currentArea = filterArea,
            currentLocation = filterLocation,
            onApply = { type, area, location ->
                filterType = type
                filterArea = area
                filterLocation = location
                showFilterDialog = false
            },
            onDismiss = { showFilterDialog = false },
        )
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(InstituicaoRoutes.offerFormRoute("new")) },
                containerColor = LightBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.offers_create))
            }
        },
        containerColor = BackgroundLight,
        topBar = {
            Column(modifier = Modifier.background(BackgroundLight)) {
                CommonTopBar()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = stringResource(R.string.offers_manage_title),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkBlue,
                        ),
                    )
                }
                SearchBarWithFilter(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onFilterClick = { showFilterDialog = true },
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            items(filteredOffers, key = { it.id }) { offer ->
                InstitutionOfferCard(
                    offer = offer,
                    onClick = { navController.navigate(InstituicaoRoutes.offerDetailRoute(offer.id)) },
                    onEdit = { navController.navigate(InstituicaoRoutes.offerFormRoute(offer.id)) },
                    onDelete = { offerToDelete = offer },
                )
            }
        }
    }
}

@Composable
private fun SearchBarWithFilter(
    query: String,
    onQueryChange: (String) -> Unit,
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
            placeholder = { Text(stringResource(R.string.common_search_placeholder), color = DarkGrey) },
            leadingIcon = {
                Icon(Icons.Outlined.Search, contentDescription = null, tint = DarkGrey)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OfferFilterDialog(
    currentType: String,
    currentArea: String,
    currentLocation: String,
    onApply: (type: String, area: String, location: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var type by remember { mutableStateOf(currentType) }
    var area by remember { mutableStateOf(currentArea) }
    var location by remember { mutableStateOf(currentLocation) }
    var typeExpanded by remember { mutableStateOf(false) }

    val allLabel = stringResource(R.string.filter_all)
    val typeOptions = listOf(allLabel, stringResource(R.string.offer_filter_full_time), stringResource(R.string.offer_filter_part_time), stringResource(R.string.offer_filter_remote), stringResource(R.string.offer_filter_hybrid))

    LinkStageDialog(
        title = stringResource(R.string.filter_title),
        onConfirm = {
            onApply(
                if (type == allLabel || type.isEmpty()) "" else type,
                area,
                location,
            )
        },
        onDismiss = onDismiss,
        confirmText = stringResource(R.string.filter_apply),
        dismissText = stringResource(R.string.dialog_cancel),
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionLabel(stringResource(R.string.offers_filter_work_model))
                    ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = { typeExpanded = it },
                    ) {
                        OutlinedTextField(
                            value = type.ifEmpty { allLabel },
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LightBlue,
                                unfocusedBorderColor = BorderGrey,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                            ),
                            singleLine = true,
                        )
                        ExposedDropdownMenu(
                            expanded = typeExpanded,
                            onDismissRequest = { typeExpanded = false },
                        ) {
                            typeOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        type = if (option == allLabel) "" else option
                                        typeExpanded = false
                                    },
                                )
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionLabel(stringResource(R.string.offers_filter_area))
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

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionLabel(stringResource(R.string.offers_filter_location))
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
            }
        },
    )
}

@Composable
private fun InstitutionOfferCard(
    offer: OfferItem,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
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
                        fontSize = 18.sp,
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = offer.title,
                        color = DarkBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = offer.company,
                        color = DarkGrey,
                        fontSize = 13.sp,
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = stringResource(R.string.common_edit),
                            tint = LightBlue,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = stringResource(R.string.common_delete_label),
                            tint = Red,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }

            HorizontalDivider(
                color = BorderGrey,
                modifier = Modifier.padding(top = 8.dp),
            )

            Row(
                modifier = Modifier.padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = DarkGrey,
                    modifier = Modifier.size(13.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.offers_published, offer.publishedAgo),
                    color = DarkGrey,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
private fun DeleteOfferDialog(
    offerTitle: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    LinkStageDialog(
        title = stringResource(R.string.offers_delete_title),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        confirmText = stringResource(R.string.offers_delete_confirm),
        dismissText = stringResource(R.string.dialog_cancel),
    ) {
        Text(
            text = stringResource(R.string.offers_delete_body, offerTitle),
            color = DarkGrey,
            fontSize = 14.sp,
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun OffersInstituicaoScreenPreview() {
    MaterialTheme {
        OffersInstituicaoScreen(navController = rememberNavController())
    }
}

@Preview(showSystemUi = true, device = "spec:width=411dp,height=891dp,orientation=landscape")
@Composable
private fun OffersInstituicaoScreenLandscapePreview() {
    MaterialTheme {
        OffersInstituicaoScreen(navController = rememberNavController())
    }
}
