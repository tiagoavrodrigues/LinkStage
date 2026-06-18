package turmaA.grupoB.LinkStage.ui.orientador.home

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
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.admin.AdminStudent
import turmaA.grupoB.LinkStage.ui.aluno.chat.Conversation
import turmaA.grupoB.LinkStage.ui.aluno.chat.avatarColors
import turmaA.grupoB.LinkStage.ui.aluno.chat.sampleConversations
import turmaA.grupoB.LinkStage.ui.common.CommonTopBar
import turmaA.grupoB.LinkStage.ui.common.EvaluationNotificationModal
import turmaA.grupoB.LinkStage.ui.common.EvaluationPendingCard
import turmaA.grupoB.LinkStage.ui.orientador.EvaluationState
import turmaA.grupoB.LinkStage.ui.orientador.InternshipEvaluation
import turmaA.grupoB.LinkStage.ui.orientador.InternshipType
import turmaA.grupoB.LinkStage.ui.orientador.MentorInternship
import turmaA.grupoB.LinkStage.ui.orientador.OrientadorRoutes
import turmaA.grupoB.LinkStage.ui.orientador.sampleEvaluation
import turmaA.grupoB.LinkStage.ui.orientador.sampleMentorInternships
import turmaA.grupoB.LinkStage.ui.orientador.sampleMentorStudents
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.BorderGrey
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.Fade3
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.ui.theme.MediumBlue
import turmaA.grupoB.LinkStage.viewmodel.advisorhome.AdvisorHomeViewModel
import turmaA.grupoB.LinkStage.viewmodel.settings.SettingsViewModel
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorDashboardData
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorDashboardUiState
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorDashboardViewModel
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorDashboardViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.chat.ChatUiState
import turmaA.grupoB.LinkStage.viewmodel.chat.ChatViewModel

@Composable
fun HomeOrientadorScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    advisorHomeViewModel: AdvisorHomeViewModel = viewModel(),
    orientadorDashboardViewModel: OrientadorDashboardViewModel = viewModel(factory = OrientadorDashboardViewModelFactory()),
    settingsViewModel: SettingsViewModel = viewModel(),
    chatViewModel: ChatViewModel? = null,
) {
    val context = LocalContext.current
    val user by settingsViewModel.user.collectAsState()
    val orientadorUserName = user.name.ifBlank { "JJ" }
    val dashboardUiState by orientadorDashboardViewModel.uiState.collectAsState()
    val dashboardData = dashboardUiState.dashboardData(context)
    val evaluation = dashboardData.evaluation ?: sampleEvaluation(context)
    val hasSeenResult by advisorHomeViewModel.hasSeenEvaluations.collectAsState()
    val hasDismissedModal by advisorHomeViewModel.hasDismissedEvaluationModal.collectAsState()

    LaunchedEffect(Unit) {
        chatViewModel?.loadConversations()
    }

    val chatState = chatViewModel?.chatUiState?.collectAsState()
    val recentMessages = chatState?.value?.let { state ->
        when (state) {
            is ChatUiState.Success -> state.conversations.take(3)
            else -> sampleConversations.take(3)
        }
    } ?: sampleConversations.take(3)

    LaunchedEffect(orientadorDashboardViewModel) {
        orientadorDashboardViewModel.loadDashboard()
    }

    val showEvaluationModal = evaluation.state == EvaluationState.READY_FOR_FINAL && 
            !evaluation.hasSeenNotification && 
            !hasSeenResult && 
            !hasDismissedModal

    if (showEvaluationModal) {
        val (modalTitle, modalMessage) = when (evaluation.internshipType) {
            InternshipType.COMPANY_SCHOOL -> Pair(
                stringResource(R.string.advisor_home_evals_submitted_title),
                stringResource(R.string.advisor_home_evals_submitted_company)
            )
            InternshipType.SCHOOL_ONLY -> Pair(
                stringResource(R.string.institution_eval_submitted),
                stringResource(R.string.advisor_home_eval_submitted_school)
            )
        }
        EvaluationNotificationModal(
            title = modalTitle,
            message = modalMessage,
            actionLabel = stringResource(R.string.advisor_home_view_evaluations),
            onAction = {
                advisorHomeViewModel.setHasSeenEvaluations(true)
                navController.navigate(OrientadorRoutes.mentorStudentDetail("s1"))
            },
            onDismiss = { advisorHomeViewModel.setHasDismissedEvaluationModal(true) },
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight),
    ) {
        CommonTopBar()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                stringResource(R.string.home_greeting, orientadorUserName),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue,
                ),
            )
            Text(
                stringResource(R.string.home_welcome),
                style = MaterialTheme.typography.bodyMedium,
                color = DarkGrey
            )
        }

        if (evaluation.state == EvaluationState.READY_FOR_FINAL && !hasSeenResult) {
            EvaluationPendingCard(
                title = stringResource(R.string.advisor_home_final_grade_pending),
                message = stringResource(R.string.advisor_eval_ready_message),
                actionLabel = stringResource(R.string.advisor_eval_assign_grade),
                isDanger = true,
                onClick = {
                    advisorHomeViewModel.setHasSeenEvaluations(true)
                    navController.navigate(OrientadorRoutes.mentorStudentDetail("s1"))
                },
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Section 1 — Os seus estágios
            SectionHeader(
                title = stringResource(R.string.advisor_home_your_internships),
                onViewAll = {
                    navController.navigate(OrientadorRoutes.INTERNSHIPS) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (dashboardData.internships.isNotEmpty()) {
                dashboardData.internships.take(2).forEach { internship ->
                    MentorInternshipCard(internship = internship)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {
                EmptyStateCard(stringResource(R.string.advisor_no_internships))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 2 — Alunos orientados
            SectionHeader(
                title = stringResource(R.string.advisor_home_supervised_students),
                onViewAll = {
                    navController.navigate(OrientadorRoutes.STUDENTS) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (dashboardData.students.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Fade3, RoundedCornerShape(12.dp)),
                    ) {
                        dashboardData.students.take(2).forEachIndexed { index, student ->
                            MentorStudentRow(student = student)
                            if (index < dashboardData.students.take(2).size - 1) {
                                HorizontalDivider(
                                    color = Color.White.copy(alpha = 0.2f),
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                )
                            }
                        }
                    }
                }
            } else {
                EmptyStateCard(stringResource(R.string.advisor_no_students))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 3 — Mensagens Recentes
            SectionHeader(
                title = stringResource(R.string.institution_home_recent_messages),
                onViewAll = {
                    navController.navigate(OrientadorRoutes.MESSAGES) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (recentMessages.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                ) {
                    recentMessages.forEachIndexed { index, conversation ->
                        MessageRow(
                            conversation = conversation,
                            onClick = {
                                navController.navigate(OrientadorRoutes.chatRoute(conversation.id))
                            },
                        )
                        if (index < sampleConversations.take(3).size - 1) {
                            HorizontalDivider(
                                color = BorderGrey,
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                        }
                    }
                }
            } else {
                EmptyStateCard(stringResource(R.string.advisor_home_no_messages))
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onViewAll: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = DarkBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
        )
        TextButton(onClick = onViewAll) {
            Text(
                text = stringResource(R.string.common_view_all),
                color = LightBlue,
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
private fun MentorInternshipCard(internship: MentorInternship) {
    var isFav by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(internship.logoColor),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = internship.logoInitial,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = internship.offerTitle,
                        color = DarkBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    )
                    Text(
                        text = internship.businessInstitutionName,
                        color = MediumBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = internship.schoolInstitutionName,
                        color = DarkGrey,
                        fontSize = 12.sp,
                    )
                }
                IconButton(onClick = { isFav = !isFav }) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = stringResource(R.string.institution_home_favorite),
                        tint = if (isFav) LightBlue else DarkGrey,
                    )
                }
            }

            HorizontalDivider(
                color = BorderGrey,
                modifier = Modifier.padding(horizontal = 12.dp),
            )

            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
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
                    text = stringResource(R.string.advisor_published_ago),
                    color = DarkGrey,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
private fun MentorStudentRow(student: AdminStudent) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(22.dp),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
        ) {
            Text(
                text = student.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
            Text(
                text = student.institutionCode,
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun MessageRow(
    conversation: Conversation,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(avatarColors[conversation.avatarColorIndex % avatarColors.size]),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = conversation.initials,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
        ) {
            Text(
                text = conversation.name,
                color = DarkBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
            Text(
                text = conversation.lastMessage,
                color = DarkGrey,
                fontSize = 13.sp,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
            contentDescription = null,
            tint = DarkGrey,
            modifier = Modifier.size(18.dp),
        )
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
            text = message,
            color = DarkGrey,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        )
    }
}

private fun OrientadorDashboardUiState.dashboardData(context: android.content.Context): OrientadorDashboardData {
    return when (this) {
        is OrientadorDashboardUiState.Success -> data
        OrientadorDashboardUiState.Idle,
        OrientadorDashboardUiState.Loading,
        OrientadorDashboardUiState.Empty,
        is OrientadorDashboardUiState.Error -> OrientadorDashboardData(
            internships = sampleMentorInternships(context),
            students = sampleMentorStudents(context),
            activityLogs = emptyList(),
            evaluation = sampleEvaluation(context),
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeOrientadorScreenPreview() {
    MaterialTheme {
        HomeOrientadorScreen(navController = rememberNavController())
    }
}

@Preview(showSystemUi = true, device = "spec:width=411dp,height=891dp,orientation=landscape")
@Composable
private fun HomeOrientadorScreenLandscapePreview() {
    MaterialTheme {
        HomeOrientadorScreen(navController = rememberNavController())
    }
}
