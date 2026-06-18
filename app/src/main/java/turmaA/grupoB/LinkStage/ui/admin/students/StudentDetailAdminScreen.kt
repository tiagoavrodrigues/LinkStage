package turmaA.grupoB.LinkStage.ui.admin.students

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import turmaA.grupoB.LinkStage.data.remote.model.enums.ApplicationStatus
import turmaA.grupoB.LinkStage.data.remote.model.enums.InternshipStatus
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.admin.sampleStudentsList
import turmaA.grupoB.LinkStage.ui.common.ContentSection
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminApplicationSummary
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminStudentDetailUiState
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminStudentDetailViewModel
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminStudentDetailViewModelFactory
import turmaA.grupoB.LinkStage.ui.common.LinkStageButton
import turmaA.grupoB.LinkStage.ui.common.LinkStageDialog
import turmaA.grupoB.LinkStage.ui.common.SecondaryTopBar
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.Fade1
import turmaA.grupoB.LinkStage.ui.theme.Fade3
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.ui.theme.MediumBlue
import turmaA.grupoB.LinkStage.ui.theme.Red

@Composable
fun StudentDetailAdminScreen(
    studentId: String,
    onBack: () -> Unit,
    onViewInternship: (String) -> Unit = {},
) {
    val studentDetailViewModel: AdminStudentDetailViewModel = viewModel(factory = AdminStudentDetailViewModelFactory())
    val studentDetailUiState by studentDetailViewModel.uiState.collectAsState()
    val student = when (val state = studentDetailUiState) {
        is AdminStudentDetailUiState.Success -> state.data.student
        else -> sampleStudentsList.first()
    }
    val activeInternshipId = when (val state = studentDetailUiState) {
        is AdminStudentDetailUiState.Success -> state.data.internships
            .firstOrNull { it.status == InternshipStatus.IN_PROGRESS }
            ?.id
        else -> null
    }
    val applicationSummaries = when (val state = studentDetailUiState) {
        is AdminStudentDetailUiState.Success -> state.data.applicationSummaries
        else -> null
    }

    LaunchedEffect(studentId) {
        studentDetailViewModel.loadStudent(studentId)
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        LinkStageDialog(
            title = stringResource(R.string.admin_student_remove),
            onConfirm = {
                showDeleteDialog = false
                studentDetailViewModel.removeAccount(student.userId, onBack)
            },
            onDismiss = { showDeleteDialog = false },
            confirmText = stringResource(R.string.common_remove),
            dismissText = stringResource(R.string.dialog_cancel),
            content = {
                Text(
                    text = stringResource(R.string.admin_student_remove_confirm, student.name),
                    color = DarkGrey,
                    lineHeight = 22.sp,
                )
            }
        )
    }

    Scaffold(
        topBar = { SecondaryTopBar(title = stringResource(R.string.admin_student_detail_title), onBack = onBack) },
        containerColor = BackgroundLight,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundLight)
                    .padding(16.dp)
            ) {
                LinkStageButton(
                    text = stringResource(R.string.admin_student_remove),
                    onClick = { showDeleteDialog = true },
                    height = 50.dp,
                    brush = SolidColor(Red)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
        ) {
            // Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(brush = Fade1),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = student.avatarInitials,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = student.name,
                            color = DarkBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        )
                        Text(
                            text = student.email,
                            color = DarkGrey,
                            fontSize = 13.sp,
                        )
                        Text(
                            text = student.institution,
                            color = MediumBlue,
                            fontSize = 12.sp,
                        )
                    }
                }
            }

            // Personal Information
            ContentSection(title = stringResource(R.string.admin_detail_personal_info)) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    DetailRow(stringResource(R.string.admin_detail_course), student.course)
                    DetailRow(stringResource(R.string.admin_detail_phone), student.phone)
                    DetailRow(stringResource(R.string.admin_detail_gpa), stringResource(R.string.admin_detail_gpa_values, student.gpa))
                    DetailRow(stringResource(R.string.admin_detail_registered), student.registeredAgo)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Internship Status
            if (student.hasActiveInternship) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(brush = Fade3)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = stringResource(R.string.admin_student_active_internship),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.admin_student_company, student.internshipCompany),
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            enabled = !activeInternshipId.isNullOrBlank(),
                            onClick = { activeInternshipId?.let { onViewInternship(it) } },
                            modifier = Modifier.fillMaxWidth().height(44.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = DarkBlue
                            )
                        ) {
                            Text(stringResource(R.string.admin_detail_view_internship), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(R.string.admin_student_no_internship),
                            color = DarkGrey,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.admin_detail_applications_submitted, student.applicationCount),
                            color = DarkGrey,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Recent Applications
            ContentSection(title = stringResource(R.string.admin_detail_recent_applications)) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    when {
                        applicationSummaries == null -> {
                            ApplicationPlaceholderItem(
                                title = stringResource(R.string.mock_application_designer),
                                company = stringResource(R.string.mock_company_viana),
                                status = stringResource(R.string.application_status_pending),
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            ApplicationPlaceholderItem(
                                title = stringResource(R.string.mock_application_frontend),
                                company = stringResource(R.string.mock_company_pingodoce),
                                status = stringResource(R.string.application_status_reviewing),
                            )
                        }
                        applicationSummaries.isEmpty() -> {
                            Text(
                                text = stringResource(R.string.app_detail_no_applications),
                                color = DarkGrey,
                                fontSize = 13.sp,
                            )
                        }
                        else -> {
                            applicationSummaries.forEachIndexed { index, application ->
                                if (index > 0) Spacer(modifier = Modifier.height(8.dp))
                                ApplicationSummaryItem(
                                    application = application,
                                    onClick = { application.internshipId?.let(onViewInternship) },
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, color = DarkGrey, fontSize = 13.sp)
        Text(text = value, color = DarkBlue, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

@Composable
private fun ApplicationPlaceholderItem(title: String, company: String, status: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundLight),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = DarkBlue, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text(text = company, color = DarkGrey, fontSize = 12.sp)
            }
            Text(text = status, color = MediumBlue, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ApplicationSummaryItem(application: AdminApplicationSummary, onClick: () -> Unit) {
    val (statusLabel, statusColor) = when (application.status) {
        ApplicationStatus.ACCEPTED -> stringResource(R.string.app_detail_status_accepted) to Color(0xFF4CAF50)
        ApplicationStatus.REJECTED -> stringResource(R.string.app_detail_status_rejected) to Red
        ApplicationStatus.PENDING -> stringResource(R.string.app_detail_status_pending) to MediumBlue
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = application.internshipId != null, onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundLight),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = application.offerTitle, color = DarkBlue, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text(text = application.companyName, color = DarkGrey, fontSize = 12.sp)
            }
            Text(text = statusLabel, color = statusColor, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun StudentDetailPreview() {
    MaterialTheme {
        StudentDetailAdminScreen(studentId = "s1", onBack = {})
    }
}

@Preview(showSystemUi = true)
@Composable
private fun StudentDetailNoInternshipPreview() {
    MaterialTheme {
        StudentDetailAdminScreen(studentId = "s2", onBack = {})
    }
}
