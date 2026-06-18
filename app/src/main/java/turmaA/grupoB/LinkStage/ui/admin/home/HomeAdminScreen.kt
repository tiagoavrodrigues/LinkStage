package turmaA.grupoB.LinkStage.ui.admin.home

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.admin.AdminInstitution
import turmaA.grupoB.LinkStage.ui.admin.AdminMentor
import turmaA.grupoB.LinkStage.ui.admin.AdminRoutes
import turmaA.grupoB.LinkStage.ui.admin.AdminStudent
import turmaA.grupoB.LinkStage.ui.admin.InstitutionStatus
import turmaA.grupoB.LinkStage.ui.admin.avatarColors
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminDashboardUiState
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminDashboardViewModel
import turmaA.grupoB.LinkStage.viewmodel.admin.AdminDashboardViewModelFactory
import turmaA.grupoB.LinkStage.ui.common.CommonTopBar
import turmaA.grupoB.LinkStage.ui.common.EvaluationNotificationModal
import turmaA.grupoB.LinkStage.ui.common.EvaluationPendingCard
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.viewmodel.settings.SettingsViewModel

@Composable
fun HomeAdminScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel = viewModel(),
) {
    val dashboardViewModel: AdminDashboardViewModel = viewModel(factory = AdminDashboardViewModelFactory())
    val dashboardUiState by dashboardViewModel.uiState.collectAsState()
    val user by settingsViewModel.user.collectAsState()
    val adminUserName = user.name.ifBlank { "Admin" }
    val pendingInstitutions = when (val state = dashboardUiState) {
        is AdminDashboardUiState.Success -> state.data.pendingInstitutions
        else -> emptyList()
    }
    var showPendingModal by remember { mutableStateOf(false) }

    LaunchedEffect(dashboardViewModel) {
        dashboardViewModel.loadDashboard()
    }

    if (showPendingModal) {
        EvaluationNotificationModal(
            title = stringResource(R.string.admin_home_pending_title),
            message = stringResource(R.string.admin_home_pending_message, pendingInstitutions.size),
            actionLabel = stringResource(R.string.admin_home_review_requests),
            onAction = {
                navController.navigate(AdminRoutes.INSTITUTIONS) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            onDismiss = { showPendingModal = false },
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
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.home_greeting, adminUserName),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue,
                ),
            )
            Text(
                text = stringResource(R.string.admin_home_welcome),
                style = MaterialTheme.typography.bodyMedium,
                color = DarkGrey
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            if (pendingInstitutions.isNotEmpty()) {
                EvaluationPendingCard(
                    title = stringResource(R.string.admin_home_pending_approvals),
                    message = stringResource(R.string.admin_home_pending_approvals_message, pendingInstitutions.size),
                    actionLabel = stringResource(R.string.admin_home_review_requests),
                    isDanger = false,
                    onClick = {
                        navController.navigate(AdminRoutes.INSTITUTIONS) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Novos alunos
            SectionHeader(
                title = stringResource(R.string.admin_new_students),
                onViewAll = {
                    navController.navigate(AdminRoutes.STUDENTS) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
            Spacer(modifier = Modifier.height(8.dp))

            val recentStudents = when (val state = dashboardUiState) {
                is AdminDashboardUiState.Success -> state.data.recentStudents
                else -> emptyList()
            }
            recentStudents.take(2).forEach { student ->
                AdminUserRow(
                    student = student,
                    onClick = { navController.navigate(AdminRoutes.studentDetail(student.id)) },
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Novos orientadores
            SectionHeader(
                title = stringResource(R.string.admin_new_mentors),
                onViewAll = {
                    navController.navigate(AdminRoutes.STUDENTS) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
            Spacer(modifier = Modifier.height(8.dp))

            val recentMentors = when (val state = dashboardUiState) {
                is AdminDashboardUiState.Success -> state.data.recentMentors
                else -> emptyList()
            }
            recentMentors.take(2).forEach { mentor ->
                AdminMentorRow(
                    mentor = mentor,
                    onClick = { navController.navigate(AdminRoutes.mentorDetail(mentor.id)) },
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Novas instituições
            SectionHeader(
                title = stringResource(R.string.admin_new_institutions),
                onViewAll = {
                    navController.navigate(AdminRoutes.INSTITUTIONS) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
            Spacer(modifier = Modifier.height(8.dp))

            val recentInstitutions = when (val state = dashboardUiState) {
                is AdminDashboardUiState.Success -> state.data.recentInstitutions
                else -> emptyList()
            }
            recentInstitutions.take(2).forEach { institution ->
                AdminInstitutionRow(
                    institution = institution,
                    onClick = { navController.navigate(AdminRoutes.institutionDetail(institution.id)) },
                )
                Spacer(modifier = Modifier.height(8.dp))
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
        modifier = Modifier.fillMaxWidth(),
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
private fun AdminUserRow(
    student: AdminStudent,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(avatarColors[student.avatarColorIndex % avatarColors.size]),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = student.avatarInitials,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.name,
                    color = DarkBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
                Text(
                    text = stringResource(R.string.admin_home_registered_ago, student.registeredAgo),
                    color = DarkGrey,
                    fontSize = 12.sp,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(LightBlue.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = student.institutionCode,
                    color = DarkBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                )
            }
        }
    }
}

@Composable
private fun AdminMentorRow(
    mentor: AdminMentor,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(avatarColors[mentor.avatarColorIndex % avatarColors.size]),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = mentor.avatarInitials,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
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
                    text = stringResource(R.string.admin_home_registered_ago, mentor.registeredAgo),
                    color = DarkGrey,
                    fontSize = 12.sp,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(LightBlue.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = mentor.institution,
                    color = DarkBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                )
            }
        }
    }
}

@Composable
private fun AdminInstitutionRow(
    institution: AdminInstitution,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(institution.logoColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = institution.logoInitial,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = institution.name,
                    color = DarkBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
                Text(
                    text = stringResource(R.string.admin_home_registered_ago, institution.registeredAgo),
                    color = DarkGrey,
                    fontSize = 12.sp,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
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

@Preview(showSystemUi = true)
@Composable
private fun HomeAdminScreenPreview() {
    MaterialTheme {
        HomeAdminScreen(navController = rememberNavController())
    }
}
