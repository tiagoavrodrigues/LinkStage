package turmaA.grupoB.LinkStage.ui.admin.institutions

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.admin.AdminInstitution
import turmaA.grupoB.LinkStage.ui.admin.AdminRoutes
import turmaA.grupoB.LinkStage.ui.admin.InstitutionStatus
import turmaA.grupoB.LinkStage.ui.admin.sampleInstitutionsList
import turmaA.grupoB.LinkStage.ui.common.CommonTopBar
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminAccountUiState
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminAccountViewModel
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminAccountViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminInstitutionsUiState
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminInstitutionsViewModel
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminInstitutionsViewModelFactory
import turmaA.grupoB.LinkStage.ui.common.LinkStageDialog
import turmaA.grupoB.LinkStage.ui.common.LinkStageTabRow
import turmaA.grupoB.LinkStage.ui.common.SectionLabel
import turmaA.grupoB.LinkStage.ui.common.TemporaryPasswordDialog
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.BorderGrey
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.ui.theme.MediumBlue
import turmaA.grupoB.LinkStage.ui.theme.Red

@Composable
fun InstitutionsAdminScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var newAccountEmail by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var filterType by rememberSaveable { mutableStateOf("") }
    var filterLocation by rememberSaveable { mutableStateOf("") }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val institutionsViewModel: AdminInstitutionsViewModel = viewModel(factory = AdminInstitutionsViewModelFactory())
    val institutionsUiState by institutionsViewModel.uiState.collectAsState()
    val accountViewModel: AdminAccountViewModel = viewModel(factory = AdminAccountViewModelFactory())
    val accountUiState by accountViewModel.uiState.collectAsState()
    val approvedInstitutions = when (val state = institutionsUiState) {
        is AdminInstitutionsUiState.Success -> state.approvedInstitutions
        else -> emptyList()
    }
    val pendingInstitutions = when (val state = institutionsUiState) {
        is AdminInstitutionsUiState.Success -> state.pendingInstitutions
        else -> sampleInstitutionsList.filter { it.status == InstitutionStatus.PENDING_APPROVAL }
    }

    LaunchedEffect(Unit) {
        institutionsViewModel.loadInstitutions()
    }

    LaunchedEffect(searchQuery, filterType, filterLocation) {
        institutionsViewModel.filterInstitutions(
            query = searchQuery,
            type = filterType,
            location = filterLocation,
        )
    }

    val currentList = if (selectedTab == 0) approvedInstitutions else pendingInstitutions

    val filtered = currentList.filter { institution ->
        val matchesSearch = searchQuery.isBlank() ||
            institution.name.contains(searchQuery, ignoreCase = true) ||
            institution.code.contains(searchQuery, ignoreCase = true) ||
            institution.location.contains(searchQuery, ignoreCase = true)
        val matchesType = filterType.isEmpty() || institution.type == filterType
        val matchesLocation = filterLocation.isEmpty() ||
            institution.location.contains(filterLocation, ignoreCase = true)
        matchesSearch && matchesType && matchesLocation
    }

    val accountSuccess = accountUiState as? AdminAccountUiState.Success
    if (showAddDialog && accountSuccess == null) {
        AddInstitutionDialog(
            onDismiss = {
                showAddDialog = false
                accountViewModel.resetState()
            },
            onConfirm = { name, email, sector, location ->
                newAccountEmail = email
                accountViewModel.createInstitutionAccount(name, email, sector, location)
            },
            isSubmitting = accountUiState is AdminAccountUiState.Loading,
            errorMessage = (accountUiState as? AdminAccountUiState.Error)?.message,
        )
    }
    if (accountSuccess != null) {
        TemporaryPasswordDialog(
            email = newAccountEmail,
            password = accountSuccess.temporaryPassword,
            onDismiss = {
                showAddDialog = false
                accountViewModel.resetState()
                institutionsViewModel.loadInstitutions()
            },
        )
    }

    if (showFilterDialog) {
        InstitutionFilterDialog(
            currentType = filterType,
            currentLocation = filterLocation,
            locationOptions = filteredInstitutionLocations(institutionsUiState),
            onApply = { type, location ->
                filterType = type
                filterLocation = location
                showFilterDialog = false
            },
            onDismiss = { showFilterDialog = false },
        )
    }

    val pendingLabel = if (pendingInstitutions.isNotEmpty()) stringResource(R.string.admin_institution_tab_pending, pendingInstitutions.size) else stringResource(R.string.admin_institution_badge_pending)

    Scaffold(
        modifier = modifier,
        containerColor = BackgroundLight,
        topBar = {
            Column(modifier = Modifier.background(BackgroundLight)) {
                CommonTopBar()
                Text(
                    text = stringResource(R.string.admin_institutions_title),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue,
                    ),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                )
                SearchBarWithFilter(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onFilterClick = { showFilterDialog = true },
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinkStageTabRow(
                    tabs = listOf(stringResource(R.string.admin_institution_tab_approved), pendingLabel),
                    selectedIndex = selectedTab,
                    onTabSelected = { selectedTab = it },
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = LightBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.admin_inst_add))
            }
        },
    ) { innerPadding ->
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "tab_content",
            modifier = Modifier.padding(innerPadding),
        ) { tab ->
            if (filtered.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = if (tab == 0) Icons.Outlined.AccountBalance else Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        tint = BorderGrey,
                        modifier = Modifier.size(64.dp),
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (tab == 0) stringResource(R.string.admin_inst_empty_approved)
                        else stringResource(R.string.admin_inst_empty_pending),
                        color = DarkGrey,
                        fontSize = 16.sp,
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filtered, key = { it.id }) { institution ->
                        if (tab == 0) {
                            InstitutionListItem(
                                institution = institution,
                                onClick = {
                                    navController.navigate(AdminRoutes.institutionDetail(institution.id))
                                },
                            )
                        } else {
                            PendingInstitutionListItem(
                                institution = institution,
                                onClick = {
                                    navController.navigate(AdminRoutes.institutionDetail(institution.id))
                                },
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

private fun filteredInstitutionLocations(uiState: AdminInstitutionsUiState): List<String> = when (uiState) {
    is AdminInstitutionsUiState.Success -> (uiState.approvedInstitutions + uiState.pendingInstitutions)
        .map { it.location }
        .distinct()
    else -> sampleInstitutionsList.map { it.location }.distinct()
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
            placeholder = { Text(stringResource(R.string.admin_inst_search), color = DarkGrey) },
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
private fun InstitutionFilterDialog(
    currentType: String,
    currentLocation: String,
    locationOptions: List<String>,
    onApply: (type: String, location: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var type by remember { mutableStateOf(currentType) }
    var location by remember { mutableStateOf(currentLocation) }
    var typeExpanded by remember { mutableStateOf(false) }

    val allLabel = stringResource(R.string.admin_inst_filter_all)
    val schoolLabel = stringResource(R.string.admin_inst_filter_school)
    val companyLabel = stringResource(R.string.admin_inst_filter_company)
    val typeOptions = listOf(
        "" to allLabel,
        "Instituição de Ensino" to schoolLabel,
        "Instituição Empresarial" to companyLabel,
    )
    val locationOptions = locationOptions.ifEmpty { sampleInstitutionsList.map { it.location }.distinct() }

    LinkStageDialog(
        title = stringResource(R.string.filter_title),
        onConfirm = {
            onApply(type, location)
        },
        onDismiss = onDismiss,
        confirmText = stringResource(R.string.filter_apply),
        dismissText = stringResource(R.string.common_cancel),
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionLabel(stringResource(R.string.admin_inst_filter_type))
                    ExposedDropdownMenuBox(
                        expanded = typeExpanded,
                        onExpandedChange = { typeExpanded = it },
                    ) {
                        OutlinedTextField(
                            value = typeOptions.firstOrNull { it.first == type }?.second ?: allLabel,
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
                            typeOptions.forEach { (key, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        type = key
                                        typeExpanded = false
                                    },
                                )
                            }
                        }
                    }
                }

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
            }
        },
    )
}

@Composable
private fun InstitutionListItem(
    institution: AdminInstitution,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(institution.logoColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = institution.logoInitial,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = institution.name,
                    color = DarkBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                )
                Text(
                    text = institution.location,
                    color = DarkGrey,
                    fontSize = 12.sp,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = stringResource(R.string.admin_inst_students_count, institution.studentsCount),
                        color = MediumBlue,
                        fontSize = 11.sp,
                    )
                    Text(
                        text = stringResource(R.string.admin_inst_internships_count, institution.activeInternshipsCount),
                        color = LightBlue,
                        fontSize = 11.sp,
                    )
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(LightBlue.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = institution.type,
                    color = DarkBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                )
            }
        }
    }
}

@Composable
private fun PendingInstitutionListItem(
    institution: AdminInstitution,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, Color(0xFFF5C518).copy(alpha = 0.5f)),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(institution.logoColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = institution.logoInitial,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = institution.name,
                    color = DarkBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                )
                Text(
                    text = institution.location,
                    color = DarkGrey,
                    fontSize = 12.sp,
                )
                Text(
                    text = stringResource(R.string.admin_inst_submitted, institution.submittedAt),
                    color = DarkGrey,
                    fontSize = 11.sp,
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(LightBlue.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = institution.type,
                        color = DarkBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF5C518).copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = stringResource(R.string.admin_institution_badge_pending),
                        color = Color(0xFFF5C518),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddInstitutionDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, email: String, sector: String, location: String) -> Unit,
    isSubmitting: Boolean,
    errorMessage: String?,
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var typeExpanded by remember { mutableStateOf(false) }

    val types = listOf(
        "Instituição de Ensino" to stringResource(R.string.admin_inst_filter_school),
        "Instituição Empresarial" to stringResource(R.string.admin_inst_filter_company),
    )

    LinkStageDialog(
        onDismiss = onDismiss,
        title = stringResource(R.string.admin_inst_add_title),
        onConfirm = { onConfirm(name, email, selectedType, location) },
        confirmText = stringResource(R.string.admin_inst_add_button),
        dismissText = stringResource(R.string.common_cancel),
        confirmEnabled = !isSubmitting && name.isNotBlank() && email.isNotBlank() && selectedType.isNotBlank(),
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.admin_users_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                )
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it },
                ) {
                    OutlinedTextField(
                        value = types.firstOrNull { it.first == selectedType }?.second ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.admin_inst_filter_type)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(10.dp),
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false },
                    ) {
                        types.forEach { (key, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    selectedType = key
                                    typeExpanded = false
                                },
                            )
                        }
                    }
                }
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text(stringResource(R.string.filter_location)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(R.string.admin_inst_add_email)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                )
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = Red,
                        fontSize = 12.sp,
                    )
                }
            }
        }
    )
}

@Preview(showSystemUi = true)
@Composable
private fun InstitutionsAdminScreenPreview() {
    MaterialTheme {
        InstitutionsAdminScreen(navController = rememberNavController())
    }
}
