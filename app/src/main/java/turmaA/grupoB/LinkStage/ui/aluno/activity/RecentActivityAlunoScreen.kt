package turmaA.grupoB.LinkStage.ui.aluno.activity

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.data.remote.model.internship.ActivityLogModel
import turmaA.grupoB.LinkStage.data.remote.model.internship.CreateActivityLogInput
import turmaA.grupoB.LinkStage.data.remote.model.enums.InternshipStatus
import turmaA.grupoB.LinkStage.data.remote.model.enums.ReportStatus
import turmaA.grupoB.LinkStage.data.remote.model.report.FinalReportModel
import turmaA.grupoB.LinkStage.data.repository.application.ApplicationRepository
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepository
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepository
import turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepository
import turmaA.grupoB.LinkStage.data.repository.internship.LocalActivityRepository
import turmaA.grupoB.LinkStage.data.room.AtDatabase
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepository
import turmaA.grupoB.LinkStage.data.repository.report.ReportRepository
import turmaA.grupoB.LinkStage.data.repository.storage.StorageRepository
import turmaA.grupoB.LinkStage.data.repository.student.StudentRepository
import turmaA.grupoB.LinkStage.ui.common.CommonTopBar
import turmaA.grupoB.LinkStage.ui.common.LinkStageDialog
import turmaA.grupoB.LinkStage.ui.common.SectionLabel
import turmaA.grupoB.LinkStage.ui.common.SelectedUploadFile
import turmaA.grupoB.LinkStage.ui.common.readUploadFile
import turmaA.grupoB.LinkStage.ui.common.safeUploadFileName
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.BorderGrey
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.ui.theme.Red
import turmaA.grupoB.LinkStage.ui.aluno.home.ApplicationStatus
import turmaA.grupoB.LinkStage.viewmodel.application.StudentApplicationDetails
import turmaA.grupoB.LinkStage.viewmodel.application.StudentApplicationsUiState
import turmaA.grupoB.LinkStage.viewmodel.application.StudentApplicationsViewModel
import turmaA.grupoB.LinkStage.viewmodel.application.StudentApplicationsViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.auth.AuthUiState
import turmaA.grupoB.LinkStage.viewmodel.auth.AuthViewModel
import turmaA.grupoB.LinkStage.viewmodel.auth.AuthViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.internships.InternshipUiState
import turmaA.grupoB.LinkStage.viewmodel.internships.ActivityCreationUiState
import turmaA.grupoB.LinkStage.viewmodel.internships.InternshipViewModel
import turmaA.grupoB.LinkStage.viewmodel.internships.InternshipViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.report.ReportUiState
import turmaA.grupoB.LinkStage.viewmodel.report.ReportViewModel
import turmaA.grupoB.LinkStage.viewmodel.report.ReportViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.student.StudentUiState
import turmaA.grupoB.LinkStage.viewmodel.student.StudentViewModel
import turmaA.grupoB.LinkStage.viewmodel.student.StudentViewModelFactory
import turmaA.grupoB.LinkStage.data.remote.model.enums.ApplicationStatus as RemoteApplicationStatus
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// region Data models

data class ApplicationItem(
    val id: String,
    val offerTitle: String,
    val company: String,
    val appliedAgo: String,
    val status: ApplicationStatus,
)

data class CheckpointFile(
    val id: String,
    val name: String,
)

enum class ActivityLogStatus { COMPLETED, PENDING }

data class ActivityLog(
    val id: String,
    val title: String,
    val description: String,
    val date: LocalDate,
    val status: ActivityLogStatus,
    val company: String = "",
    val companyLogoInitial: String = "",
    val companyLogoColor: Color = Color(0xFF0E1572),
    val requirements: List<String> = emptyList(),
    val hasSubmitted: Boolean = false,
    val submittedAt: LocalDate? = null,
    val submittedFiles: List<CheckpointFile> = emptyList(),
    val createdBy: String = "STUDENT",
    val createdByName: String = "",
    val attachmentUrl: String? = null,
    val viewers: List<turmaA.grupoB.LinkStage.ui.orientador.CheckpointViewer> = emptyList(),
)

data class ActiveInternship(
    val id: String,
    val title: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val activityLogs: List<ActivityLog>,
)

// endregion

// region Utils

fun calculateInternshipProgress(startDate: LocalDate, endDate: LocalDate): Float {
    val today = LocalDate.now()
    return when {
        today <= startDate -> 0f
        today >= endDate -> 1f
        else -> {
            val total = ChronoUnit.DAYS.between(startDate, endDate).toFloat()
            val elapsed = ChronoUnit.DAYS.between(startDate, today).toFloat()
            (elapsed / total).coerceIn(0f, 1f)
        }
    }
}

private fun formatDate(date: LocalDate): String {
    return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}

private fun formatTimestamp(timestamp: String): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    return runCatching { LocalDateTime.parse(timestamp).format(formatter) }
        .recoverCatching {
            OffsetDateTime.parse(timestamp)
                .atZoneSameInstant(ZoneId.systemDefault())
                .format(formatter)
        }
        .recoverCatching {
            Instant.parse(timestamp)
                .atZone(ZoneId.systemDefault())
                .format(formatter)
        }
        .getOrDefault("")
}

// endregion

// region Main Screen

@Composable
fun RecentActivityAlunoScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(AuthRepository())
    ),
    studentViewModel: StudentViewModel = viewModel(
        factory = StudentViewModelFactory(StudentRepository())
    ),
    studentApplicationsViewModel: StudentApplicationsViewModel = viewModel(
        factory = StudentApplicationsViewModelFactory(
            ApplicationRepository(),
            OfferRepository(),
            InstitutionRepository(),
        )
    ),
    internshipViewModel: InternshipViewModel = viewModel(
        factory = InternshipViewModelFactory(
            InternshipRepository(),
            LocalActivityRepository(
                AtDatabase.getDatabase(LocalContext.current).atividadeDAO()
            ),
            StorageRepository(),
        )
    ),
    reportViewModel: ReportViewModel = viewModel(
        factory = ReportViewModelFactory(
            ReportRepository(),
            StorageRepository(),
        )
    ),
    onBack: (() -> Unit)? = null,
    onSubmitReport: () -> Unit = {},
    onActivityClick: (String) -> Unit = {},
    onViewResult: (String) -> Unit = {},
) {
    val authUiState by authViewModel.uiState.collectAsState()
    val studentUiState by studentViewModel.uiState.collectAsState()
    val studentApplicationsUiState by studentApplicationsViewModel.uiState.collectAsState()
    val internshipUiState by internshipViewModel.uiState.collectAsState()
    val activityCreationUiState by internshipViewModel.activityCreationUiState.collectAsState()
    val reportUiState by reportViewModel.uiState.collectAsState()

    var showFilterModal by remember { mutableStateOf(false) }
    var reportSubmissionRequested by remember { mutableStateOf(false) }
    var currentFilter by remember { mutableStateOf<ApplicationStatus?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddActivityModal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        authViewModel.loadCurrentUserProfile()
    }

    LaunchedEffect(authUiState) {
        val state = authUiState

        if (state is AuthUiState.Success) {
            studentViewModel.loadStudentByUserId(state.profile.id)
        }
    }

    LaunchedEffect(studentUiState) {
        val state = studentUiState

        if (state is StudentUiState.Success) {
            studentApplicationsViewModel.loadApplicationsByStudent(state.student.id)
            internshipViewModel.loadActiveInternshipByStudent(state.student.id)
        }
    }

    val activeInternshipState = internshipUiState as? InternshipUiState.ActiveInternshipSuccess
    val activeInternshipModel = activeInternshipState?.internship
    val activeInternship = activeInternshipState?.toActiveInternship()
    val currentStudent = (studentUiState as? StudentUiState.Success)?.student

    if (
        showAddActivityModal &&
        activeInternshipModel?.status == InternshipStatus.IN_PROGRESS &&
        currentStudent != null
    ) {
        AddActivityModal(
            onSave = { title, description, selectedFile ->
                val input = CreateActivityLogInput(
                    internshipId = activeInternshipModel.id,
                    studentId = currentStudent.id,
                    description = description.ifBlank { title },
                    activityDate = LocalDate.now().toString(),
                    type = title,
                )
                if (selectedFile == null) {
                    internshipViewModel.createActivityLog(input)
                    showAddActivityModal = false
                } else {
                    val path = "students/${currentStudent.id}/activities/" +
                        "${System.currentTimeMillis()}_${safeUploadFileName(selectedFile.name)}"
                    internshipViewModel.createActivityLog(
                        input = input,
                        attachmentPath = path,
                        attachmentBytes = selectedFile.bytes,
                    )
                    showAddActivityModal = false
                }
            },
            onDismiss = { showAddActivityModal = false },
        )
    }

    LaunchedEffect(activityCreationUiState) {
        if (activityCreationUiState is ActivityCreationUiState.Success) {
            showAddActivityModal = false
            internshipViewModel.resetActivityCreationState()
        }
    }

    LaunchedEffect(activeInternshipModel?.id) {
        activeInternshipModel?.let {
            reportViewModel.loadReportByInternship(it.id)
        }
    }

    LaunchedEffect(reportUiState, reportSubmissionRequested) {
        val state = reportUiState

        if (
            reportSubmissionRequested &&
            state is ReportUiState.Success &&
            state.report.status == ReportStatus.SUBMITTED
        ) {
            reportSubmissionRequested = false
            onSubmitReport()
        }
    }

    val realApplications: List<ApplicationItem> = when (val state = studentApplicationsUiState) {
        is StudentApplicationsUiState.SuccessList -> state.applications.map { application ->
            application.toApplicationItem()
        }

        else -> emptyList()
    }

    val pastApplications = realApplications.filter {
        it.status == ApplicationStatus.ACCEPTED || it.status == ApplicationStatus.REJECTED
    }

    val activeApplications = realApplications.filter {
        it.status == ApplicationStatus.PENDING
    }

    if (showFilterModal) {
        ActivityFilterModal(
            currentFilter = currentFilter,
            onDismiss = { showFilterModal = false },
            onApply = {
                currentFilter = it
                showFilterModal = false
            }
        )
    }

    if (activeInternship != null && currentStudent != null) {
        Scaffold(
            modifier = modifier,
            containerColor = BackgroundLight,
        ) { innerPadding ->
            Column(modifier = Modifier.fillMaxSize()) {
                CommonTopBar()
                
                ActiveInternshipContent(
                    internship = activeInternship,
                    studentId = currentStudent.id,
                    reportUiState = reportUiState,
                    activityCreationUiState = activityCreationUiState,
                    canAddActivity = activeInternshipModel?.status == InternshipStatus.IN_PROGRESS,
                    onAddActivity = { showAddActivityModal = true },
                    onSubmitReport = { reportId, filePath, fileBytes ->
                        reportSubmissionRequested = true
                        reportViewModel.uploadAndSubmitReport(
                            reportId = reportId,
                            filePath = filePath,
                            fileBytes = fileBytes,
                        )
                    },
                    onActivityClick = onActivityClick,
                    onViewResult = onViewResult,
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = innerPadding.calculateBottomPadding()),
                )
            }
        }
    } else if (activeInternshipModel != null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(BackgroundLight)
        ) {
            CommonTopBar()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = activeInternshipModel.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkBlue,
                        ),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.home_internship_dates_unavailable),
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkGrey,
                    )
                }
            }
        }
    } else {
        val applicationsErrorMessage =
            (studentApplicationsUiState as? StudentApplicationsUiState.Error)?.message
        val isLoadingApplications = studentApplicationsUiState is StudentApplicationsUiState.Loading
        val filteredActive = activeApplications.filter {
            (currentFilter == null || it.status == currentFilter) &&
                    (searchQuery.isBlank() || it.offerTitle.contains(searchQuery, ignoreCase = true) || it.company.contains(searchQuery, ignoreCase = true))
        }

        val filteredPast = pastApplications.filter {
            (currentFilter == null || it.status == currentFilter) &&
                    (searchQuery.isBlank() || it.offerTitle.contains(searchQuery, ignoreCase = true) || it.company.contains(searchQuery, ignoreCase = true))
        }

        ApplicationsContent(
            activeApplications = filteredActive,
            pastApplications = filteredPast,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onBack = onBack,
            modifier = modifier,
            onFilterClick = { showFilterModal = true },
            isLoading = isLoadingApplications,
            errorMessage = applicationsErrorMessage
        )
    }
}

// endregion

// region State 1: No internship — applications list

@Composable
private fun ApplicationsContent(
    activeApplications: List<ApplicationItem>,
    pastApplications: List<ApplicationItem>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    onFilterClick: () -> Unit = {},
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        item { CommonTopBar() }

        item {
            Text(
                text = stringResource(R.string.activity_recent),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue,
                ),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )
        }

        item {
            ApplicationSearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onFilterClick = onFilterClick
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        item {
            if (isLoading) {
                Text(
                    text = stringResource(R.string.applications_loading),
                    color = DarkGrey,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = Red,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
        }

        item {
            if (activeApplications.isEmpty() && pastApplications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.applications_empty), color = DarkGrey)
                }
            }
        }

        if (activeApplications.isNotEmpty()) {
            item {
                ApplicationSection(title = stringResource(R.string.applications_active)) {
                    activeApplications.forEach { app ->
                        ApplicationCard(application = app)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (pastApplications.isNotEmpty()) {
            item {
                ApplicationSection(title = stringResource(R.string.applications_past)) {
                    pastApplications.forEach { app ->
                        ApplicationCard(application = app)
                    }
                }
            }
        }
    }
}

@Composable
private fun ApplicationSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = LightBlue,
                ),
                modifier = Modifier.padding(bottom = 8.dp),
            )
            content()
        }
    }
}

@Composable
fun ApplicationCard(application: ApplicationItem) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                imageVector = Icons.Outlined.Work,
                contentDescription = null,
                tint = LightBlue,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = application.offerTitle,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = application.company,
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkGrey,
                )
                Text(
                    text = stringResource(R.string.applications_applied_ago, application.appliedAgo),
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkGrey,
                )
            }
            StatusBadge(status = application.status)
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            color = BorderGrey,
        )
    }
}

@Composable
fun StatusBadge(status: ApplicationStatus) {
    val (labelRes, color) = when (status) {
        ApplicationStatus.PENDING -> R.string.applications_filter_pending to DarkGrey
        ApplicationStatus.ACCEPTED -> R.string.applications_filter_accepted to LightBlue
        ApplicationStatus.REJECTED -> R.string.applications_filter_rejected to Red
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = color,
        )
    }
}

// endregion

// region State 2: Active internship — progress + activity logs

@Composable
private fun ActiveInternshipContent(
    internship: ActiveInternship,
    studentId: String,
    reportUiState: ReportUiState,
    activityCreationUiState: ActivityCreationUiState,
    canAddActivity: Boolean,
    onAddActivity: () -> Unit,
    onSubmitReport: (reportId: String, filePath: String, fileBytes: ByteArray) -> Unit,
    onActivityClick: (String) -> Unit = {},
    onViewResult: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var showSubmitConfirmation by remember { mutableStateOf(false) }
    var selectedReportFile by remember { mutableStateOf<SelectedUploadFile?>(null) }
    val report = (reportUiState as? ReportUiState.Success)?.report
    val canSubmitReport = report?.status == ReportStatus.DRAFT
    val reportFileLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        val selectedFile = uri?.let { context.contentResolver.readUploadFile(it) }
        if (selectedFile != null) {
            selectedReportFile = selectedFile
            showSubmitConfirmation = true
        }
    }

    if (showSubmitConfirmation) {
        LinkStageDialog(
            title = stringResource(R.string.report_submit_title),
            onConfirm = {
                showSubmitConfirmation = false
                val selectedFile = selectedReportFile
                if (report != null && selectedFile != null) {
                    val path = "students/$studentId/internships/${internship.id}/final-report/" +
                        "${System.currentTimeMillis()}_${safeUploadFileName(selectedFile.name)}"
                    onSubmitReport(report.id, path, selectedFile.bytes)
                }
            },
            onDismiss = { showSubmitConfirmation = false },
            confirmText = stringResource(R.string.activity_submit),
            dismissText = stringResource(R.string.common_cancel),
            content = {
                Text(
                    text = stringResource(R.string.report_submit_message),
                    color = DarkGrey,
                    lineHeight = 22.sp,
                )
            }
        )
    }

    val progress = calculateInternshipProgress(internship.startDate, internship.endDate)

    var animationStarted by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationStarted) progress else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "progress_animation",
    )
    LaunchedEffect(Unit) { animationStarted = true }

    val daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), internship.endDate)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight)
    ) {
        InternshipHeader(
            internship = internship,
            animatedProgress = animatedProgress,
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp),
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(R.string.activity_recent),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkBlue,
                        ),
                    )
                    if (canAddActivity) {
                        IconButton(
                            onClick = onAddActivity,
                            enabled = activityCreationUiState !is ActivityCreationUiState.Loading,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(R.string.activity_add),
                                tint = LightBlue,
                            )
                        }
                    }
                }

                if (activityCreationUiState is ActivityCreationUiState.Error) {
                    Text(
                        text = activityCreationUiState.message,
                        color = Red,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    )
                }
            }

            items(internship.activityLogs, key = { it.id }) { activityLog ->
                ActivityLogCard(
                    activityLog = activityLog,
                    onClick = { onActivityClick(activityLog.id) },
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))

                ReportSubmissionCard(
                    daysRemaining = daysRemaining,
                    report = report,
                    isLoading = reportUiState is ReportUiState.Loading,
                    errorMessage = (reportUiState as? ReportUiState.Error)?.message,
                    onSubmit = { reportFileLauncher.launch("application/*") },
                    enabled = canSubmitReport,
                )
            }
        }
    }
}

@Composable
fun InternshipHeader(
    internship: ActiveInternship,
    animatedProgress: Float,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        Text(
            text = internship.title,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = DarkBlue,
            ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = LightBlue,
            trackColor = BorderGrey,
            strokeCap = StrokeCap.Round,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "${stringResource(R.string.activity_start)} ${formatDate(internship.startDate)}",
                style = MaterialTheme.typography.labelSmall,
                color = DarkGrey,
            )
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = LightBlue,
            )
            Text(
                text = "${stringResource(R.string.activity_end)} ${formatDate(internship.endDate)}",
                style = MaterialTheme.typography.labelSmall,
                color = DarkGrey,
            )
        }
    }
}

@Composable
fun ActivityLogCard(
    activityLog: ActivityLog,
    onClick: () -> Unit = {},
    showViewers: Boolean = false,
) {
    val isCompleted = activityLog.status == ActivityLogStatus.COMPLETED

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (isCompleted) LightBlue else BorderGrey),
                contentAlignment = Alignment.Center,
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(R.string.activity_status_completed),
                        tint = Color.White,
                        modifier = Modifier.size(16.dp),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = stringResource(R.string.activity_status_pending),
                        tint = DarkGrey,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activityLog.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                )

                if (activityLog.createdBy == "MENTOR") {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.School,
                            contentDescription = null,
                            tint = LightBlue,
                            modifier = Modifier.size(12.dp),
                        )
                        Text(
                            text = stringResource(R.string.activity_defined_by_advisor),
                            fontSize = 11.sp,
                            color = LightBlue,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 4.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = activityLog.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkGrey,
                    lineHeight = 18.sp,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = DarkGrey,
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatDate(activityLog.date),
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkGrey,
                    )
                }

                if (showViewers && activityLog.viewers.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Visibility,
                            contentDescription = null,
                            tint = DarkGrey,
                            modifier = Modifier.size(13.dp),
                        )
                        Text(
                            text = stringResource(R.string.activity_reviewed_by, activityLog.viewers.size),
                            color = DarkGrey,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 4.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReportSubmissionCard(
    daysRemaining: Long,
    report: FinalReportModel?,
    isLoading: Boolean,
    errorMessage: String?,
    onSubmit: () -> Unit,
    enabled: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Text(
            text = stringResource(R.string.report_title),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = DarkBlue,
            ),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = when {
                isLoading -> stringResource(R.string.report_loading)
                errorMessage != null -> errorMessage
                report == null -> stringResource(R.string.report_not_found)
                report.status == ReportStatus.DRAFT ->
                    stringResource(R.string.report_message, daysRemaining.toInt())
                else -> stringResource(R.string.report_already_submitted)
            },
            style = MaterialTheme.typography.bodyMedium,
            color = DarkGrey,
            lineHeight = 20.sp,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSubmit,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LightBlue,
                contentColor = Color.White,
            ),
        ) {
            Icon(
                imageVector = Icons.Default.Upload,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = when (report?.status) {
                    ReportStatus.SUBMITTED,
                    ReportStatus.REVIEWED -> stringResource(R.string.activity_submitted)

                    else -> stringResource(R.string.activity_submit)
                },
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

// endregion

// region Add Activity Modal

@Composable
fun AddActivityModal(
    onSave: (title: String, description: String, file: SelectedUploadFile?) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedFile by remember { mutableStateOf<SelectedUploadFile?>(null) }
    var fileName by remember { mutableStateOf<String?>(null) }
    var titleError by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val file = uri?.let { context.contentResolver.readUploadFile(it) }
        selectedFile = file
        fileName = file?.name
    }

    LinkStageDialog(
        title = stringResource(R.string.activity_add_title),
        onConfirm = {
            if (title.isBlank()) {
                titleError = true
            } else {
                onSave(title, description, selectedFile)
            }
        },
        onDismiss = onDismiss,
        confirmText = stringResource(R.string.common_save),
        dismissText = stringResource(R.string.common_cancel),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                // Título
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionLabel(stringResource(R.string.activity_add_title_label))
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            titleError = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.common_write_here), color = DarkGrey, fontSize = 14.sp) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (titleError) Red else LightBlue,
                            unfocusedBorderColor = if (titleError) Red else BorderGrey,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                        ),
                        singleLine = true,
                        isError = titleError,
                    )
                    if (titleError) {
                        Text(stringResource(R.string.common_required_field), color = Red, fontSize = 12.sp)
                    }
                }

                // Descrição
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionLabel(stringResource(R.string.activity_add_desc_label))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.common_write_here), color = DarkGrey, fontSize = 14.sp) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LightBlue,
                            unfocusedBorderColor = BorderGrey,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                        ),
                        minLines = 3,
                        maxLines = 5,
                    )
                }

                // Anexos
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionLabel(stringResource(R.string.activity_attachments))
                    if (selectedFile != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, LightBlue, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f),
                            ) {
                                Icon(Icons.Outlined.Description, contentDescription = null, tint = LightBlue)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    fileName ?: stringResource(R.string.common_file),
                                    fontSize = 13.sp,
                                    color = DarkBlue,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            IconButton(onClick = { selectedFile = null; fileName = null }) {
                                Icon(Icons.Default.Close, contentDescription = stringResource(R.string.common_remove), tint = DarkGrey)
                            }
                        }
                    } else {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { launcher.launch("*/*") },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BorderGrey),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                Icon(
                                    Icons.Outlined.Description,
                                    contentDescription = null,
                                    tint = DarkGrey,
                                    modifier = Modifier.size(40.dp),
                                )
                                Text(stringResource(R.string.apply_select_file), fontWeight = FontWeight.Bold, color = DarkBlue, fontSize = 14.sp)
                                Text(stringResource(R.string.apply_file_format), color = DarkGrey, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    )
}


// endregion

// region Shared components

@Composable
private fun ActivityFilterModal(
    currentFilter: ApplicationStatus?,
    onDismiss: () -> Unit,
    onApply: (ApplicationStatus?) -> Unit,
) {
    var selectedOption by remember { mutableStateOf(currentFilter) }
    val options = listOf(
        null to stringResource(R.string.applications_filter_all),
        ApplicationStatus.PENDING to stringResource(R.string.applications_filter_pending),
        ApplicationStatus.ACCEPTED to stringResource(R.string.applications_filter_accepted),
        ApplicationStatus.REJECTED to stringResource(R.string.applications_filter_rejected),
    )

    LinkStageDialog(
        title = stringResource(R.string.applications_filter_title),
        onConfirm = { onApply(selectedOption) },
        onDismiss = onDismiss,
        confirmText = stringResource(R.string.filter_apply),
        dismissText = stringResource(R.string.common_cancel),
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEach { (status, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedOption = status }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = (status == selectedOption),
                            onClick = { selectedOption = status },
                            colors = RadioButtonDefaults.colors(selectedColor = DarkBlue),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = label, color = DarkGrey)
                    }
                }
            }
        }
    )
}

@Composable
private fun ApplicationSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text(stringResource(R.string.common_search), color = DarkGrey) },
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
            Icon(Icons.Outlined.FilterList, contentDescription = stringResource(R.string.discover_filters), tint = Color.White)
        }
    }
}

private fun StudentApplicationDetails.toApplicationItem(): ApplicationItem {
    return ApplicationItem(
        id = application.id,
        offerTitle = offerTitle,
        company = institutionName,
        appliedAgo = formatTimestamp(application.createdAt),
        status = application.status.toUiApplicationStatus()
    )
}

private fun InternshipUiState.ActiveInternshipSuccess.toActiveInternship(): ActiveInternship? {
    val parsedStartDate = internship.startDate
        ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        ?: return null
    val parsedEndDate = internship.endDate
        ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        ?: return null

    return ActiveInternship(
        id = internship.id,
        title = internship.title,
        startDate = parsedStartDate,
        endDate = parsedEndDate,
        activityLogs = activityLogs.mapNotNull { it.toActivityLog() },
    )
}

private fun ActivityLogModel.toActivityLog(): ActivityLog? {
    val parsedDate = runCatching { LocalDate.parse(activityDate) }.getOrNull()
        ?: return null

    return ActivityLog(
        id = id,
        title = type?.takeIf { it.isNotBlank() } ?: description,
        description = description,
        date = parsedDate,
        status = ActivityLogStatus.COMPLETED,
        attachmentUrl = attachmentUrl,
    )
}

private fun RemoteApplicationStatus.toUiApplicationStatus(): ApplicationStatus {
    return when (this) {
        RemoteApplicationStatus.PENDING -> ApplicationStatus.PENDING
        RemoteApplicationStatus.ACCEPTED -> ApplicationStatus.ACCEPTED
        RemoteApplicationStatus.REJECTED -> ApplicationStatus.REJECTED
    }
}

// endregion
