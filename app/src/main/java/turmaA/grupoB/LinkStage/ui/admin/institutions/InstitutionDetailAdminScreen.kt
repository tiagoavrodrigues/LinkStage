package turmaA.grupoB.LinkStage.ui.admin.institutions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.admin.InstitutionStatus
import turmaA.grupoB.LinkStage.ui.admin.sampleInstitutionsList
import turmaA.grupoB.LinkStage.ui.admin.sampleMentorsList
import turmaA.grupoB.LinkStage.ui.admin.sampleStudentsList
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminInstitutionDetailUiState
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminInstitutionDetailViewModel
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminInstitutionDetailViewModelFactory
import turmaA.grupoB.LinkStage.ui.admin.students.MentorListItem
import turmaA.grupoB.LinkStage.ui.admin.students.StudentListItem
import turmaA.grupoB.LinkStage.ui.common.ConfirmationDialog
import turmaA.grupoB.LinkStage.ui.common.ContentSection
import turmaA.grupoB.LinkStage.ui.common.LinkStageButton
import turmaA.grupoB.LinkStage.ui.common.LinkStageDialog
import turmaA.grupoB.LinkStage.ui.common.SecondaryTopBar
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.Green
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.ui.theme.Red

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstitutionDetailAdminScreen(
    institutionId: String,
    onBack: () -> Unit,
) {
    val institutionDetailViewModel: AdminInstitutionDetailViewModel = viewModel(factory = AdminInstitutionDetailViewModelFactory())
    val institutionDetailUiState by institutionDetailViewModel.uiState.collectAsState()
    val institution = when (val state = institutionDetailUiState) {
        is AdminInstitutionDetailUiState.Success -> state.data.institution
        else -> sampleInstitutionsList.first()
    }
    val relatedStudents = when (val state = institutionDetailUiState) {
        is AdminInstitutionDetailUiState.Success -> state.data.relatedStudents.take(3)
        else -> sampleStudentsList.filter { it.institutionCode == institution.code }.take(3)
    }
    val relatedMentors = when (val state = institutionDetailUiState) {
        is AdminInstitutionDetailUiState.Success -> state.data.relatedMentors
        else -> sampleMentorsList.filter { it.institution == institution.code }
    }

    LaunchedEffect(institutionId) {
        institutionDetailViewModel.loadInstitution(institutionId)
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showApproveDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        LinkStageDialog(
            title = stringResource(R.string.admin_institution_remove),
            onConfirm = {
                showDeleteDialog = false
                institutionDetailViewModel.removeAccount(institution.userId, onBack)
            },
            onDismiss = { showDeleteDialog = false },
            confirmText = stringResource(R.string.common_remove),
            dismissText = stringResource(R.string.dialog_cancel),
            content = {
                Text(
                    text = stringResource(R.string.admin_institution_remove_confirm, institution.name),
                    color = DarkGrey,
                    lineHeight = 22.sp,
                )
            }
        )
    }

    if (showApproveDialog) {
        ConfirmationDialog(
            title = stringResource(R.string.admin_approve_title),
            body = stringResource(R.string.admin_approve_body, institution.name),
            confirmLabel = stringResource(R.string.admin_approve_button),
            confirmBrush = SolidColor(Green),
            isDanger = false,
            onConfirm = {
                showApproveDialog = false
                onBack()
            },
            onDismiss = { showApproveDialog = false },
        )
    }

    if (showRejectDialog) {
        ConfirmationDialog(
            title = stringResource(R.string.admin_reject_title),
            body = stringResource(R.string.admin_reject_body, institution.name),
            confirmLabel = stringResource(R.string.admin_reject_button),
            isDanger = true,
            onConfirm = {
                showRejectDialog = false
                onBack()
            },
            onDismiss = { showRejectDialog = false },
        )
    }

    Scaffold(
        topBar = { SecondaryTopBar(title = stringResource(R.string.admin_institution_detail_title), onBack = onBack) },
        containerColor = BackgroundLight,
        bottomBar = {
            if (institution.status == InstitutionStatus.APPROVED) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundLight)
                        .padding(16.dp)
                ) {
                    LinkStageButton(
                        text = stringResource(R.string.admin_institution_remove),
                        onClick = { showDeleteDialog = true },
                        height = 50.dp,
                        brush = SolidColor(Red)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(bottom = 12.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BackgroundLight)
                        .padding(12.dp),
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = institution.name,
                                color = DarkBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(LightBlue.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    text = institution.type,
                                    color = DarkBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                )
                            }
                        }
                        Text(
                            text = institution.code,
                            color = LightBlue,
                            fontSize = 13.sp,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StatCard(
                    value = "${institution.studentsCount}",
                    label = stringResource(R.string.admin_detail_students_label),
                    valueColor = DarkBlue,
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    value = "${institution.mentorsCount}",
                    label = stringResource(R.string.admin_detail_mentors_label),
                    valueColor = DarkBlue,
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    value = "${institution.activeInternshipsCount}",
                    label = stringResource(R.string.admin_detail_active_internships),
                    valueColor = LightBlue,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Contact Information
            ContentSection(title = stringResource(R.string.admin_detail_contact_info)) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    DetailRow(stringResource(R.string.admin_detail_email), institution.email)
                    DetailRow(stringResource(R.string.admin_detail_website), institution.website)
                    DetailRow(stringResource(R.string.admin_detail_location), institution.location)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Recent Students
            ContentSection(title = stringResource(R.string.admin_detail_recent_students)) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    if (relatedStudents.isEmpty()) {
                        Text(
                            text = stringResource(R.string.admin_institution_no_students),
                            color = DarkGrey,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    } else {
                        relatedStudents.forEach { student ->
                            StudentListItem(
                                student = student,
                                onClick = { },
                                showStatus = false,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mentors
            ContentSection(title = stringResource(R.string.admin_detail_mentors_label)) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    if (relatedMentors.isEmpty()) {
                        Text(
                            text = stringResource(R.string.admin_institution_no_mentors),
                            color = DarkGrey,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    } else {
                        relatedMentors.forEach { mentor ->
                            MentorListItem(
                                mentor = mentor,
                                onClick = { },
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pending approval section
            if (institution.status == InstitutionStatus.PENDING_APPROVAL) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5C518).copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, Color(0xFFF5C518).copy(alpha = 0.4f)),
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Outlined.HourglassEmpty,
                            contentDescription = null,
                            tint = Color(0xFFF5C518),
                            modifier = Modifier.size(22.dp),
                        )
                        Column(modifier = Modifier.padding(start = 12.dp)) {
                            Text(
                                text = stringResource(R.string.admin_institution_pending_title),
                                color = DarkBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                            )
                            Text(
                                text = stringResource(R.string.admin_institution_pending_message),
                                color = DarkGrey,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { showApproveDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Green),
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.admin_institution_approve),
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { showRejectDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Red),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Red),
                ) {
                    Icon(
                        Icons.Outlined.Cancel,
                        contentDescription = null,
                        tint = Red,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.admin_institution_reject),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    valueColor: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                color = valueColor,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = DarkGrey,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
            )
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

@Preview(showSystemUi = true)
@Composable
private fun InstitutionDetailPreview() {
    MaterialTheme {
        InstitutionDetailAdminScreen(institutionId = "i1", onBack = {})
    }
}

@Preview(showSystemUi = true)
@Composable
private fun InstitutionDetailPendingPreview() {
    MaterialTheme {
        InstitutionDetailAdminScreen(institutionId = "i3", onBack = {})
    }
}
