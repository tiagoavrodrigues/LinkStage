package turmaA.grupoB.LinkStage.ui.admin.students

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.admin.sampleMentorsList
import turmaA.grupoB.LinkStage.ui.admin.sampleStudentsList
import turmaA.grupoB.LinkStage.ui.common.ContentSection
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminMentorDetailUiState
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminMentorDetailViewModel
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminMentorDetailViewModelFactory
import turmaA.grupoB.LinkStage.ui.common.LinkStageButton
import turmaA.grupoB.LinkStage.ui.common.LinkStageDialog
import turmaA.grupoB.LinkStage.ui.common.SecondaryTopBar
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.Fade1
import turmaA.grupoB.LinkStage.ui.theme.MediumBlue
import turmaA.grupoB.LinkStage.ui.theme.Red

@Composable
fun MentorDetailAdminScreen(
    mentorId: String,
    onBack: () -> Unit,
) {
    val mentorDetailViewModel: AdminMentorDetailViewModel = viewModel(factory = AdminMentorDetailViewModelFactory())
    val mentorDetailUiState by mentorDetailViewModel.uiState.collectAsState()
    val mentor = when (val state = mentorDetailUiState) {
        is AdminMentorDetailUiState.Success -> state.data.mentor
        else -> sampleMentorsList.first()
    }
    val supervisedStudents = when (val state = mentorDetailUiState) {
        is AdminMentorDetailUiState.Success -> state.data.supervisedStudents
        else -> sampleStudentsList.filter { it.institutionCode == mentor.institution }
    }

    LaunchedEffect(mentorId) {
        mentorDetailViewModel.loadMentor(mentorId)
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        LinkStageDialog(
            title = stringResource(R.string.admin_mentor_remove),
            onConfirm = {
                showDeleteDialog = false
                mentorDetailViewModel.removeAccount(mentor.userId, onBack)
            },
            onDismiss = { showDeleteDialog = false },
            confirmText = stringResource(R.string.common_remove),
            dismissText = stringResource(R.string.dialog_cancel),
            content = {
                Text(
                    text = stringResource(R.string.admin_mentor_remove_confirm, mentor.name),
                    color = DarkGrey,
                    lineHeight = 22.sp,
                )
            }
        )
    }

    Scaffold(
        topBar = { SecondaryTopBar(title = stringResource(R.string.admin_mentor_detail_title), onBack = onBack) },
        containerColor = BackgroundLight,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundLight)
                    .padding(16.dp)
            ) {
                LinkStageButton(
                    text = stringResource(R.string.admin_mentor_remove),
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
                            text = mentor.avatarInitials,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = mentor.name,
                            color = DarkBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        )
                        Text(
                            text = mentor.email,
                            color = DarkGrey,
                            fontSize = 13.sp,
                        )
                        Text(
                            text = mentor.institution,
                            color = MediumBlue,
                            fontSize = 12.sp,
                        )
                    }
                }
            }

            // Information
            ContentSection(title = stringResource(R.string.admin_mentor_information)) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    DetailRow(stringResource(R.string.admin_detail_department), mentor.department)
                    DetailRow(stringResource(R.string.admin_detail_phone), mentor.phone)
                    DetailRow(stringResource(R.string.admin_detail_active_students), "${mentor.activeStudentsCount}")
                    DetailRow(stringResource(R.string.admin_detail_registered), mentor.registeredAgo)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Supervised Students
            ContentSection(title = stringResource(R.string.admin_detail_supervised_students)) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    if (supervisedStudents.isEmpty()) {
                        Text(
                            text = stringResource(R.string.admin_mentor_no_students),
                            color = DarkGrey,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        )
                    } else {
                        supervisedStudents.forEach { student ->
                            StudentListItem(
                                student = student,
                                onClick = { },
                                showStatus = false,
                            )
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

@Preview(showSystemUi = true)
@Composable
private fun MentorDetailPreview() {
    MaterialTheme {
        MentorDetailAdminScreen(mentorId = "m1", onBack = {})
    }
}
