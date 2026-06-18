package turmaA.grupoB.LinkStage.ui.instituicao.activity

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.aluno.chat.avatarColors
import turmaA.grupoB.LinkStage.ui.common.CommonTopBar
import turmaA.grupoB.LinkStage.ui.common.ConfirmationDialog
import turmaA.grupoB.LinkStage.ui.common.LinkStageButton
import turmaA.grupoB.LinkStage.ui.common.LinkStageDialog
import turmaA.grupoB.LinkStage.ui.common.LinkStageOutlinedButton
import turmaA.grupoB.LinkStage.ui.common.LinkStageTabRow
import turmaA.grupoB.LinkStage.ui.common.PasswordField
import turmaA.grupoB.LinkStage.ui.common.SectionLabel
import turmaA.grupoB.LinkStage.ui.instituicao.InstituicaoRoutes
import turmaA.grupoB.LinkStage.ui.instituicao.InstitutionInternship
import turmaA.grupoB.LinkStage.ui.instituicao.InstitutionMentorItem
import turmaA.grupoB.LinkStage.ui.instituicao.InternshipStatus
import turmaA.grupoB.LinkStage.ui.instituicao.MentorStatus
import turmaA.grupoB.LinkStage.ui.instituicao.internshipStatusColor
import turmaA.grupoB.LinkStage.ui.instituicao.internshipStatusLabel
import turmaA.grupoB.LinkStage.ui.instituicao.mentorStatusColor
import turmaA.grupoB.LinkStage.ui.instituicao.mentorStatusLabel
import turmaA.grupoB.LinkStage.viewmodel.instituicao.activity.InstitutionActivityUiState
import turmaA.grupoB.LinkStage.viewmodel.instituicao.activity.InstitutionActivityViewModel
import turmaA.grupoB.LinkStage.viewmodel.instituicao.activity.InstitutionActivityViewModelFactory
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.BorderGrey
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.ui.theme.Red

@Composable
fun ActivityInstituicaoScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    activityViewModel: InstitutionActivityViewModel = viewModel(factory = InstitutionActivityViewModelFactory()),
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showCreateMentorDialog by remember { mutableStateOf(false) }

    // Internship filters
    var filterInternshipStatus by rememberSaveable { mutableStateOf("") }
    var filterInternshipMentor by rememberSaveable { mutableStateOf("") }

    // Mentor filters
    var filterMentorStatus by rememberSaveable { mutableStateOf("") }
    var filterMentorInstitution by rememberSaveable { mutableStateOf("") }

    val activityUiState by activityViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        activityViewModel.loadActivityForCurrentInstitution()
    }

    val internships = (activityUiState as? InstitutionActivityUiState.Success)?.data?.internships.orEmpty()
    val mentors = (activityUiState as? InstitutionActivityUiState.Success)?.data?.mentors.orEmpty()
    val isLoading = activityUiState is InstitutionActivityUiState.Loading

    if (showCreateMentorDialog) {
        CreateMentorDialog(
            onSave = { showCreateMentorDialog = false },
            onDismiss = { showCreateMentorDialog = false },
        )
    }

    if (showFilterDialog) {
        when (selectedTab) {
            0 -> InternshipFilterDialog(
                currentStatus = filterInternshipStatus,
                currentMentor = filterInternshipMentor,
                onApply = { status, mentor ->
                    filterInternshipStatus = status
                    filterInternshipMentor = mentor
                    showFilterDialog = false
                },
                onDismiss = { showFilterDialog = false },
            )
            1 -> MentorFilterDialog(
                currentStatus = filterMentorStatus,
                currentInstitution = filterMentorInstitution,
                onApply = { status, institution ->
                    filterMentorStatus = status
                    filterMentorInstitution = institution
                    showFilterDialog = false
                },
                onDismiss = { showFilterDialog = false },
            )
        }
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 1) {
                        showCreateMentorDialog = true
                    }
                },
                containerColor = LightBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = if (selectedTab == 0) stringResource(R.string.activity_fab_associate) else stringResource(R.string.activity_fab_add_advisor))
            }
        },
        containerColor = BackgroundLight,
        topBar = { CommonTopBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = stringResource(R.string.activity_recent_title),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue,
                    ),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            SearchBarWithFilter(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onFilterClick = { showFilterDialog = true },
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinkStageTabRow(
                tabs = listOf(stringResource(R.string.activity_tab_internships), stringResource(R.string.activity_tab_advisors)),
                selectedIndex = selectedTab,
                onTabSelected = { selectedTab = it },
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = LightBlue)
                }
            } else {
                when (selectedTab) {
                    0 -> InternshipsTab(
                        internships = internships,
                        searchQuery = searchQuery,
                        filterStatus = filterInternshipStatus,
                        filterMentor = filterInternshipMentor,
                        navController = navController,
                    )
                    1 -> MentorsTab(
                        mentors = mentors,
                        searchQuery = searchQuery,
                        filterStatus = filterMentorStatus,
                        filterInstitution = filterMentorInstitution,
                        navController = navController,
                    )
                }
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

// region Filter Dialogs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InternshipFilterDialog(
    currentStatus: String,
    currentMentor: String,
    onApply: (status: String, mentor: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var status by remember { mutableStateOf(currentStatus) }
    var mentor by remember { mutableStateOf(currentMentor) }
    var statusExpanded by remember { mutableStateOf(false) }

    val statusOptions = listOf(
        "" to stringResource(R.string.filter_all),
        "IN_PROGRESS" to stringResource(R.string.status_in_progress),
        "PENDING_REVIEW" to stringResource(R.string.status_pending_review),
        "COMPLETED" to stringResource(R.string.status_completed),
        "NO_MENTOR" to stringResource(R.string.status_no_mentor),
    )

    LinkStageDialog(
        title = stringResource(R.string.activity_filter_internships_title),
        onConfirm = {
            onApply(
                status,
                mentor,
            )
        },
        onDismiss = onDismiss,
        confirmText = stringResource(R.string.filter_apply),
        dismissText = stringResource(R.string.common_cancel),
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionLabel(stringResource(R.string.admin_users_filter_status))
                    ExposedDropdownMenuBox(
                        expanded = statusExpanded,
                        onExpandedChange = { statusExpanded = it },
                    ) {
                        OutlinedTextField(
                            value = statusOptions.find { it.first == status }?.second ?: statusOptions.first().second,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
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
                            expanded = statusExpanded,
                            onDismissRequest = { statusExpanded = false },
                        ) {
                            statusOptions.forEach { (key, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        status = key
                                        statusExpanded = false
                                    },
                                )
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionLabel(stringResource(R.string.activity_filter_advisor_label))
                    OutlinedTextField(
                        value = mentor,
                        onValueChange = { mentor = it },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MentorFilterDialog(
    currentStatus: String,
    currentInstitution: String,
    onApply: (status: String, institution: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var status by remember { mutableStateOf(currentStatus) }
    var institution by remember { mutableStateOf(currentInstitution) }
    var statusExpanded by remember { mutableStateOf(false) }

    val statusOptions = listOf(
        "" to stringResource(R.string.filter_all),
        "ACTIVE" to stringResource(R.string.mentor_status_active),
        "INACTIVE" to stringResource(R.string.mentor_status_inactive),
        "NO_STUDENTS" to stringResource(R.string.mentor_status_no_students),
    )

    LinkStageDialog(
        title = stringResource(R.string.activity_filter_advisors_title),
        onConfirm = {
            onApply(
                status,
                institution,
            )
        },
        onDismiss = onDismiss,
        confirmText = stringResource(R.string.filter_apply),
        dismissText = stringResource(R.string.common_cancel),
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionLabel(stringResource(R.string.admin_users_filter_status))
                    ExposedDropdownMenuBox(
                        expanded = statusExpanded,
                        onExpandedChange = { statusExpanded = it },
                    ) {
                        OutlinedTextField(
                            value = statusOptions.find { it.first == status }?.second ?: statusOptions.first().second,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
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
                            expanded = statusExpanded,
                            onDismissRequest = { statusExpanded = false },
                        ) {
                            statusOptions.forEach { (key, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        status = key
                                        statusExpanded = false
                                    },
                                )
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionLabel(stringResource(R.string.advisor_institution))
                    OutlinedTextField(
                        value = institution,
                        onValueChange = { institution = it },
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

// endregion

// region Tabs

@Composable
private fun InternshipsTab(
    internships: List<InstitutionInternship>,
    searchQuery: String,
    filterStatus: String,
    filterMentor: String,
    navController: NavController,
) {
    val filtered = internships.filter { internship ->
        val matchesSearch = searchQuery.isEmpty() ||
            internship.studentName.contains(searchQuery, ignoreCase = true) ||
            internship.offerTitle.contains(searchQuery, ignoreCase = true)
        val matchesStatus = filterStatus.isEmpty() || when (filterStatus) {
            "IN_PROGRESS" -> internship.status == InternshipStatus.IN_PROGRESS
            "PENDING_REVIEW" -> internship.status == InternshipStatus.PENDING_REVIEW
            "COMPLETED" -> internship.status == InternshipStatus.COMPLETED
            "NO_MENTOR" -> internship.status == InternshipStatus.NO_MENTOR
            else -> true
        }
        val matchesMentor = filterMentor.isEmpty() ||
            internship.mentorName.contains(filterMentor, ignoreCase = true)
        matchesSearch && matchesStatus && matchesMentor
    }

    LazyColumn(
        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
    ) {
        items(filtered, key = { it.id }) { internship ->
            InternshipCard(
                internship = internship,
                onClick = { navController.navigate(InstituicaoRoutes.internshipDetailRoute(internship.id)) },
            )
        }
    }
}

@Composable
private fun InternshipCard(internship: InstitutionInternship, onClick: () -> Unit = {}) {
    val statusColor = internshipStatusColor(internship.status)
    val statusLabel = internshipStatusLabel(internship.status)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
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
                        .clip(CircleShape)
                        .background(avatarColors[internship.studentAvatarColorIndex % avatarColors.size]),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = internship.studentAvatarInitials,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = internship.studentName,
                        color = DarkBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                    Text(
                        text = internship.offerTitle,
                        color = DarkGrey,
                        fontSize = 12.sp,
                    )
                    Text(
                        text = if (internship.mentorName.isNotEmpty()) stringResource(R.string.activity_advisor_with_name, internship.mentorName)
                               else stringResource(R.string.activity_advisor_undefined),
                        color = DarkGrey,
                        fontSize = 11.sp,
                    )
                }
                StatusBadge(label = statusLabel, color = statusColor)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = DarkGrey,
                    modifier = Modifier.size(18.dp),
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { internship.progressPercent / 100f },
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = statusColor,
                    trackColor = BorderGrey,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.activity_progress_completed, internship.progressPercent),
                    color = DarkGrey,
                    fontSize = 10.sp,
                )
            }
        }
    }
}

@Composable
private fun MentorsTab(
    mentors: List<InstitutionMentorItem>,
    searchQuery: String,
    filterStatus: String,
    filterInstitution: String,
    navController: NavController,
) {
    val filtered = mentors.filter { mentor ->
        val matchesSearch = searchQuery.isEmpty() ||
            mentor.name.contains(searchQuery, ignoreCase = true) ||
            mentor.institution.contains(searchQuery, ignoreCase = true)
        val matchesStatus = filterStatus.isEmpty() || when (filterStatus) {
            "ACTIVE" -> mentor.status == MentorStatus.ACTIVE
            "INACTIVE" -> mentor.status == MentorStatus.INACTIVE
            "NO_STUDENTS" -> mentor.status == MentorStatus.NO_STUDENTS
            else -> true
        }
        val matchesInstitution = filterInstitution.isEmpty() ||
            mentor.institution.contains(filterInstitution, ignoreCase = true)
        matchesSearch && matchesStatus && matchesInstitution
    }

    LazyColumn(
        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
    ) {
        items(filtered, key = { it.id }) { mentor ->
            MentorCard(
                mentor = mentor,
                onClick = { navController.navigate(InstituicaoRoutes.mentorDetailRoute(mentor.id)) },
            )
        }
    }
}

@Composable
private fun MentorCard(mentor: InstitutionMentorItem, onClick: () -> Unit = {}) {
    val statusColor = mentorStatusColor(mentor.status)
    val statusLabel = mentorStatusLabel(mentor.status)

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
                    .background(avatarColors[mentor.avatarColorIndex % avatarColors.size]),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = mentor.avatarInitials,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = mentor.name,
                    color = DarkBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
                Text(
                    text = stringResource(R.string.activity_institution_label, mentor.institution),
                    color = DarkGrey,
                    fontSize = 12.sp,
                )
            }
            StatusBadge(label = statusLabel, color = statusColor)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = DarkGrey,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

// endregion

@Composable
private fun StatusBadge(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = label,
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
        )
    }
}

// region Create Mentor Dialog

@Composable
private fun CreateMentorDialog(
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var initialPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var initialPasswordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    var showConfirmDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    if (showConfirmDialog) {
        ConfirmationDialog(
            title = stringResource(R.string.activity_create_mentor_title),
            body = stringResource(R.string.activity_create_mentor_body, name),
            confirmLabel = stringResource(R.string.activity_create_mentor_confirm),
            isDanger = false,
            onConfirm = {
                showConfirmDialog = false
                onSave()
            },
            onDismiss = { showConfirmDialog = false },
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.activity_add_advisor_title),
                        color = DarkBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.common_close_label),
                            tint = DarkBlue,
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SectionLabel(stringResource(R.string.activity_full_name))
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        placeholder = { Text(stringResource(R.string.activity_name_placeholder), color = DarkGrey, fontSize = 14.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (nameError != null) Red else LightBlue,
                            unfocusedBorderColor = if (nameError != null) Red else BorderGrey,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                        ),
                        isError = nameError != null,
                    )
                    if (nameError != null) {
                        Text(nameError!!, color = Red, fontSize = 12.sp)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SectionLabel(stringResource(R.string.activity_institutional_email))
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        placeholder = { Text(stringResource(R.string.activity_email_placeholder), color = DarkGrey, fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(Icons.Outlined.Email, contentDescription = null, tint = DarkGrey)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (emailError != null) Red else LightBlue,
                            unfocusedBorderColor = if (emailError != null) Red else BorderGrey,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                        ),
                        isError = emailError != null,
                    )
                    if (emailError != null) {
                        Text(emailError!!, color = Red, fontSize = 12.sp)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SectionLabel(stringResource(R.string.activity_department_area))
                    OutlinedTextField(
                        value = department,
                        onValueChange = { department = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        placeholder = { Text(stringResource(R.string.activity_department_placeholder), color = DarkGrey, fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(Icons.AutoMirrored.Outlined.MenuBook, contentDescription = null, tint = DarkGrey)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LightBlue,
                            unfocusedBorderColor = BorderGrey,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                        ),
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SectionLabel(stringResource(R.string.activity_initial_password))
                    PasswordField(
                        label = "",
                        value = initialPassword,
                        onValueChange = {
                            initialPassword = it
                            initialPasswordError = null
                        },
                        error = initialPasswordError,
                    )
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = LightBlue.copy(alpha = 0.08f)),
                        border = BorderStroke(1.dp, LightBlue.copy(alpha = 0.3f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = LightBlue,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.activity_password_change_info),
                                color = DarkGrey,
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SectionLabel(stringResource(R.string.activity_confirm_password))
                    PasswordField(
                        label = "",
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            confirmPasswordError = null
                        },
                        error = confirmPasswordError,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    LinkStageOutlinedButton(
                        text = stringResource(R.string.common_cancel),
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        height = 48.dp
                    )
                    LinkStageButton(
                        text = stringResource(R.string.activity_create_account),
                        onClick = {
                            var isValid = true
                            if (name.isBlank()) {
                                nameError = context.getString(R.string.activity_error_required)
                                isValid = false
                            }
                            if (!email.contains("@")) {
                                emailError = context.getString(R.string.activity_error_email)
                                isValid = false
                            }
                            if (initialPassword.length < 8) {
                                initialPasswordError = context.getString(R.string.activity_error_password_length)
                                isValid = false
                            }
                            if (initialPassword != confirmPassword) {
                                confirmPasswordError = context.getString(R.string.activity_error_password_mismatch)
                                isValid = false
                            }
                            if (isValid) {
                                showConfirmDialog = true
                            }
                        },
                        modifier = Modifier.weight(1f),
                        height = 48.dp
                    )
                }
            }
        }
    }
}

// endregion

@Preview(showSystemUi = true)
@Composable
private fun ActivityInstituicaoScreenPreview() {
    MaterialTheme {
        ActivityInstituicaoScreen(navController = rememberNavController())
    }
}

@Preview(showSystemUi = true, device = "spec:width=411dp,height=891dp,orientation=landscape")
@Composable
private fun ActivityInstituicaoScreenLandscapePreview() {
    MaterialTheme {
        ActivityInstituicaoScreen(navController = rememberNavController())
    }
}
