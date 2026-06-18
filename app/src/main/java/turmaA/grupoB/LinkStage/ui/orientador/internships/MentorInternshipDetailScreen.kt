package turmaA.grupoB.LinkStage.ui.orientador.internships

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.admin.AdminStudent
import turmaA.grupoB.LinkStage.ui.admin.avatarColors
import turmaA.grupoB.LinkStage.ui.aluno.activity.ActiveInternship
import turmaA.grupoB.LinkStage.ui.aluno.activity.ActivityLog
import turmaA.grupoB.LinkStage.ui.aluno.activity.ActivityLogCard
import turmaA.grupoB.LinkStage.ui.aluno.activity.InternshipHeader
import turmaA.grupoB.LinkStage.ui.aluno.activity.calculateInternshipProgress
import turmaA.grupoB.LinkStage.ui.common.SecondaryTopBar
import turmaA.grupoB.LinkStage.ui.orientador.EvaluationState
import turmaA.grupoB.LinkStage.ui.orientador.MentorInternship
import turmaA.grupoB.LinkStage.ui.orientador.OrientadorRoutes
import turmaA.grupoB.LinkStage.ui.orientador.sampleEvaluation
import turmaA.grupoB.LinkStage.ui.orientador.sampleMentorActivityLogs
import turmaA.grupoB.LinkStage.ui.orientador.sampleMentorInternships
import turmaA.grupoB.LinkStage.ui.orientador.sampleMentorStudents
import turmaA.grupoB.LinkStage.ui.orientador.sampleStudentInternship
import androidx.compose.ui.platform.LocalContext
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.BorderGrey
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.Fade1
import turmaA.grupoB.LinkStage.ui.theme.Fade2
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorInternshipDetailUiState
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorInternshipDetailViewModel
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorInternshipDetailViewModelFactory

@Composable
fun MentorInternshipDetailScreen(
    internshipId: String = "i1",
    navController: NavController,
    orientadorInternshipDetailViewModel: OrientadorInternshipDetailViewModel = viewModel(factory = OrientadorInternshipDetailViewModelFactory()),
) {
    val context = LocalContext.current
    val detailUiState by orientadorInternshipDetailViewModel.uiState.collectAsState()
    val detailData = (detailUiState as? OrientadorInternshipDetailUiState.Success)?.data
    val fallbackInternships = sampleMentorInternships(context)
    val fallbackStudents = sampleMentorStudents(context)
    val internship = detailData?.internship ?: fallbackInternships.find { it.id == internshipId } ?: fallbackInternships.first()
    val student = detailData?.student ?: fallbackStudents.find { it.id == internship.studentId } ?: fallbackStudents.first()
    val activeInternship = detailData?.activeInternship ?: sampleStudentInternship(context)
    val activityLogs = detailData?.activityLogs?.takeIf { it.isNotEmpty() } ?: sampleMentorActivityLogs(context)
    val evaluation = detailData?.evaluation ?: sampleEvaluation(context)

    LaunchedEffect(orientadorInternshipDetailViewModel, internshipId) {
        orientadorInternshipDetailViewModel.loadInternship(internshipId)
    }

    val progress = calculateInternshipProgress(activeInternship.startDate, activeInternship.endDate)

    var animationStarted by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationStarted) progress else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "progress",
    )
    LaunchedEffect(Unit) { animationStarted = true }

    val showEvaluateButton = evaluation.state == EvaluationState.READY_FOR_FINAL

    Scaffold(
        topBar = { SecondaryTopBar(title = stringResource(R.string.mentor_internship_detail_title), onBack = { navController.popBackStack() }) },
        containerColor = BackgroundLight,
        bottomBar = {
            if (showEvaluateButton) {
                Surface(
                    color = Color.White,
                    shadowElevation = 8.dp,
                ) {
                    Button(
                        onClick = {
                            navController.navigate(OrientadorRoutes.mentorStudentDetail(student.id))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .height(50.dp)
                            .background(Fade2, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    ) {
                        Icon(
                            Icons.Outlined.Grade,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.mentor_internship_evaluate),
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                        )
                    }
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            InternshipHeader(
                internship = activeInternship,
                animatedProgress = animatedProgress,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.activity_recent),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue,
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )

            activityLogs.forEach { activityLog ->
                ActivityLogCard(
                    activityLog = activityLog,
                    onClick = {
                        navController.navigate(OrientadorRoutes.mentorCheckpointDetail(activityLog.id))
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            InternshipStudentSection(student = student, navController = navController)

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun InternshipStudentSection(
    student: AdminStudent,
    navController: NavController,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(bottom = 8.dp)) {
            Row(
                modifier = Modifier.padding(start = 16.dp, top = 14.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(18.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Fade1),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.mentor_internship_student_section),
                    color = LightBlue,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = BorderGrey)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        navController.navigate(OrientadorRoutes.mentorStudentDetail(student.id))
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(avatarColors[student.avatarColorIndex % avatarColors.size]),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = student.avatarInitials,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp),
                ) {
                    Text(
                        text = student.name,
                        color = DarkBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    )
                    Text(
                        text = student.institutionCode,
                        color = DarkGrey,
                        fontSize = 12.sp,
                    )
                }

                TextButton(
                    onClick = {
                        navController.navigate(OrientadorRoutes.mentorStudentDetail(student.id))
                    },
                ) {
                    Text(
                        text = stringResource(R.string.mentor_internship_more),
                        color = LightBlue,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                    )
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
                        tint = LightBlue,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(start = 2.dp),
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun MentorInternshipDetailPreview() {
    MaterialTheme {
        MentorInternshipDetailScreen(navController = rememberNavController())
    }
}

@Preview(
    showSystemUi = true,
    device = "spec:width=891dp,height=411dp,orientation=landscape",
)
@Composable
private fun MentorInternshipDetailLandscapePreview() {
    MaterialTheme {
        MentorInternshipDetailScreen(navController = rememberNavController())
    }
}
