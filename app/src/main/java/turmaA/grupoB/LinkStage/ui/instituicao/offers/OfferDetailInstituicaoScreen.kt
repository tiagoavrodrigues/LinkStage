package turmaA.grupoB.LinkStage.ui.instituicao.offers

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.data.remote.model.enums.OfferStatus
import turmaA.grupoB.LinkStage.data.remote.model.institution.InstitutionModel
import turmaA.grupoB.LinkStage.data.remote.model.offer.InternshipOfferModel
import turmaA.grupoB.LinkStage.data.repository.application.ApplicationRepository
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepository
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepository
import turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepository
import turmaA.grupoB.LinkStage.data.repository.student.StudentRepository
import turmaA.grupoB.LinkStage.ui.aluno.chat.avatarColors
import turmaA.grupoB.LinkStage.ui.aluno.home.ApplicationStatus
import turmaA.grupoB.LinkStage.ui.aluno.offers.OfferDetail
import turmaA.grupoB.LinkStage.ui.aluno.offers.ResponsibilityItem
import turmaA.grupoB.LinkStage.ui.common.CheckItem
import turmaA.grupoB.LinkStage.ui.common.ContentSection
import turmaA.grupoB.LinkStage.ui.common.ContentSectionColored
import turmaA.grupoB.LinkStage.ui.common.ConfirmationDialog
import turmaA.grupoB.LinkStage.ui.common.LinkStageButton
import turmaA.grupoB.LinkStage.ui.common.LinkStageOutlinedButton
import turmaA.grupoB.LinkStage.ui.common.LinkStageTabRow
import turmaA.grupoB.LinkStage.ui.instituicao.InstituicaoRoutes
import turmaA.grupoB.LinkStage.ui.instituicao.InstitutionApplication
import turmaA.grupoB.LinkStage.ui.instituicao.toInstitutionApplication
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.BorderGrey
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.ui.theme.Fade1
import turmaA.grupoB.LinkStage.ui.theme.Fade2
import turmaA.grupoB.LinkStage.ui.theme.MediumBlue
import turmaA.grupoB.LinkStage.ui.theme.Red
import turmaA.grupoB.LinkStage.viewmodel.application.InstitutionApplicationsUiState
import turmaA.grupoB.LinkStage.viewmodel.application.InstitutionApplicationsViewModel
import turmaA.grupoB.LinkStage.viewmodel.application.InstitutionApplicationsViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.offer.OfferUiState
import turmaA.grupoB.LinkStage.viewmodel.offer.OfferViewModel
import turmaA.grupoB.LinkStage.viewmodel.offer.OfferViewModelFactory
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private fun InternshipOfferModel.toOfferDetail(institution: InstitutionModel?, applicantsCount: Int): OfferDetail {
    val requirementsList = requirements?.split("\n", ";")?.map { it.trim() }?.filter { it.isNotBlank() }.orEmpty()
    return OfferDetail(
        id = id,
        title = title,
        company = institution?.name.orEmpty(),
        logoInitial = institution?.name?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
        logoColor = Color(0xFF1565C0),
        location = location.orEmpty(),
        duration = "",
        type = modality.orEmpty(),
        aboutCompany = institution?.description.orEmpty(),
        responsibilities = listOfNotNull(description.takeIf { it.isNotBlank() }),
        requirements = requirementsList,
        benefits = emptyList(),
        deadlineDays = deadline.toDeadlineDays(),
        applicantsCount = applicantsCount,
    )
}

private fun String?.toDeadlineDays(): Int {
    val deadlineDate = this?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: return 0
    return ChronoUnit.DAYS.between(LocalDate.now(), deadlineDate).coerceAtLeast(0).toInt()
}

private fun emptyOfferDetail(offerId: String) = OfferDetail(
    id = offerId, title = "", company = "", logoInitial = "?", logoColor = Color(0xFF1565C0),
    location = "", duration = "", type = "", aboutCompany = "",
    responsibilities = emptyList(), requirements = emptyList(), benefits = emptyList(),
    deadlineDays = 0, applicantsCount = 0,
)

@Composable
fun OfferDetailInstituicaoScreen(
    offerId: String,
    navController: NavController,
    modifier: Modifier = Modifier,
    offerViewModel: OfferViewModel = viewModel(
        factory = OfferViewModelFactory(
            offerRepository = OfferRepository(),
            institutionRepository = InstitutionRepository(),
        )
    ),
    applicationsViewModel: InstitutionApplicationsViewModel = viewModel(
        factory = InstitutionApplicationsViewModelFactory(
            applicationRepository = ApplicationRepository(),
            studentRepository = StudentRepository(),
            profileRepository = ProfileRepository(),
        )
    ),
) {
    val offerUiState by offerViewModel.uiState.collectAsState()
    val applicationsUiState by applicationsViewModel.uiState.collectAsState()

    LaunchedEffect(offerId) {
        offerViewModel.loadOfferDetailsById(offerId)
        applicationsViewModel.loadApplicationsByOffer(offerId)
    }

    LaunchedEffect(offerUiState) {
        val state = offerUiState
        if (state is OfferUiState.Success) {
            if (state.offer.status == OfferStatus.REMOVED) {
                navController.popBackStack()
            } else {
                offerViewModel.loadOfferDetailsById(offerId)
            }
        }
    }

    val applications = (applicationsUiState as? InstitutionApplicationsUiState.SuccessList)
        ?.applications?.map { it.toInstitutionApplication() }.orEmpty()

    val details = offerUiState as? OfferUiState.SuccessDetails
    val offer = details?.offer?.toOfferDetail(details.institution, applications.size) ?: emptyOfferDetail(offerId)

    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var showCloseDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showCloseDialog) {
        ConfirmationDialog(
            title = stringResource(R.string.offer_detail_close_title),
            body = stringResource(R.string.offer_detail_close_body),
            confirmLabel = stringResource(R.string.offer_detail_close_confirm),
            onConfirm = {
                showCloseDialog = false
                offerViewModel.closeOffer(offerId)
            },
            onDismiss = { showCloseDialog = false },
        )
    }

    if (showDeleteDialog) {
        ConfirmationDialog(
            title = stringResource(R.string.offer_detail_delete_title),
            body = stringResource(R.string.offer_detail_delete_body),
            confirmLabel = stringResource(R.string.offer_detail_delete_confirm),
            isDanger = false,
            onConfirm = {
                showDeleteDialog = false
                offerViewModel.markOfferAsRemoved(offerId)
            },
            onDismiss = { showDeleteDialog = false },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundLight,
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                // Top Row: Back + Title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 20.dp, top = 8.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back),
                            tint = DarkBlue
                        )
                    }
                    Text(
                        text = stringResource(R.string.offer_detail_title),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkBlue,
                            fontSize = 20.sp
                        )
                    )
                }

                // Small Header with Offer Info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BackgroundLight)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(offer.logoColor),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(offer.logoInitial, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(offer.title, color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(offer.company, color = LightBlue, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }

                LinkStageTabRow(
                    tabs = listOf(stringResource(R.string.common_details), stringResource(R.string.offer_detail_tab_applications), stringResource(R.string.common_manage)),
                    selectedIndex = selectedTab,
                    onTabSelected = { selectedTab = it },
                )
            }
        }
    ) { paddingValues ->
        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> DetailsTab(offer = offer)
                1 -> ApplicationsTab(navController = navController, applications = applications)
                2 -> ManageTab(
                    onEdit = { navController.navigate(InstituicaoRoutes.offerFormRoute(offer.id)) },
                    onClose = { showCloseDialog = true },
                    onDelete = { showDeleteDialog = true },
                )
            }
        }
    }
}

// region Tab 0 — Details

@Composable
private fun DetailsTab(offer: OfferDetail) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        OfferMetaChips(offer = offer)

        Spacer(modifier = Modifier.height(16.dp))

        ContentSection(title = stringResource(R.string.offer_detail_about)) {
            Text(
                text = offer.aboutCompany,
                fontSize = 14.sp, color = DarkGrey, lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        ContentSection(title = stringResource(R.string.offer_detail_responsibilities)) {
            offer.responsibilities.forEach { item -> ResponsibilityItem(text = item) }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        ContentSectionColored(title = stringResource(R.string.offer_detail_requirements)) {
            offer.requirements.forEach { item -> CheckItem(text = item) }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun OfferMetaChips(offer: OfferDetail) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        MetaChipSmall(icon = Icons.Outlined.LocationOn, label = stringResource(R.string.offer_detail_location), value = offer.location, modifier = Modifier.weight(1f))
        MetaChipSmall(icon = Icons.Outlined.Schedule, label = stringResource(R.string.offer_detail_duration), value = offer.duration, modifier = Modifier.weight(1f))
        MetaChipSmall(icon = Icons.Outlined.Work, label = stringResource(R.string.offer_detail_type), value = offer.type, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun MetaChipSmall(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(10.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LightBlue,
                modifier = Modifier.size(20.dp),
            )
            Text(label, fontSize = 11.sp, color = DarkGrey, textAlign = TextAlign.Center)
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkBlue, textAlign = TextAlign.Center)
        }
    }
}

// endregion

// region Tab 1 — Applications

@Composable
private fun ApplicationsTab(navController: NavController, applications: List<InstitutionApplication>) {
    val filterAll = stringResource(R.string.applications_filter_all)
    val filterPending = stringResource(R.string.applications_filter_pending)
    val filterAccepted = stringResource(R.string.applications_filter_accepted)
    val filterRejected = stringResource(R.string.applications_filter_rejected)
    val applicationStatusFilters = listOf(filterAll, filterPending, filterAccepted, filterRejected)

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf(filterAll) }

    val filtered = applications.filter { app ->
        val matchesSearch = searchQuery.isEmpty() ||
            app.studentName.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            filterPending -> app.status == ApplicationStatus.PENDING
            filterAccepted -> app.status == ApplicationStatus.ACCEPTED
            filterRejected -> app.status == ApplicationStatus.REJECTED
            else -> true
        }

        matchesSearch && matchesFilter
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            placeholder = { Text(stringResource(R.string.common_search_placeholder), color = DarkGrey) },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = DarkGrey) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = DarkBlue,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
            ),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            applicationStatusFilters.forEach { filter ->
                val isSelected = filter == selectedFilter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = filter },
                    label = {
                        Text(
                            text = filter,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            ),
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DarkBlue,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = DarkGrey,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true, selected = isSelected,
                        borderColor = BorderGrey, selectedBorderColor = Color.Transparent,
                    ),
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (filtered.isEmpty()) {
            EmptyStateCard(stringResource(R.string.app_detail_no_applications))
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                items(filtered, key = { it.id }) { application ->
                    ApplicationListItem(
                        application = application,
                        onClick = { navController.navigate(InstituicaoRoutes.applicationDetailRoute(application.id)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ApplicationListItem(
    application: InstitutionApplication,
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
                    .clip(CircleShape)
                    .background(avatarColors[application.studentAvatarColorIndex % avatarColors.size]),
                contentAlignment = Alignment.Center,
            ) {
                Text(application.studentAvatarInitials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(application.studentName, color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(stringResource(R.string.app_detail_institution_label, application.institution), color = DarkGrey, fontSize = 12.sp)
            }
            ApplicationStatusBadge(application.status)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = DarkGrey, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun ApplicationStatusBadge(status: ApplicationStatus) {
    val (label, color) = when (status) {
        ApplicationStatus.ACCEPTED -> stringResource(R.string.app_detail_status_accepted) to Color(0xFF4CAF50)
        ApplicationStatus.REJECTED -> stringResource(R.string.app_detail_status_rejected) to Red
        ApplicationStatus.PENDING -> stringResource(R.string.app_detail_status_pending) to Color(0xFFF5C518)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(label, color = color, fontWeight = FontWeight.Bold, fontSize = 10.sp)
    }
}

@Composable
private fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Text(
            text = message, color = DarkGrey, fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(20.dp),
        )
    }
}

// endregion

// region Tab 2 — Manage

@Composable
private fun ManageTab(
    onEdit: () -> Unit,
    onClose: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        LinkStageButton(
            text = stringResource(R.string.offer_detail_edit),
            onClick = onEdit,
            height = 48.dp
        )

        LinkStageOutlinedButton(
            text = stringResource(R.string.offer_detail_close),
            onClick = onClose,
            height = 48.dp
        )

        LinkStageButton(
            text = stringResource(R.string.offer_detail_remove),
            onClick = onDelete,
            height = 48.dp,
            brush = SolidColor(Red)
        )
    }
}

// endregion

// region Previews

@Preview(showSystemUi = true)
@Composable
private fun OfferDetailInstituicaoScreenPreview() {
    MaterialTheme {
        OfferDetailInstituicaoScreen(offerId = "1", navController = rememberNavController())
    }
}

@Preview(showSystemUi = true, device = "spec:width=411dp,height=891dp,orientation=landscape")
@Composable
private fun OfferDetailInstituicaoScreenLandscapePreview() {
    MaterialTheme {
        OfferDetailInstituicaoScreen(offerId = "1", navController = rememberNavController())
    }
}

// endregion
