package turmaA.grupoB.LinkStage.ui.orientador.students

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import turmaA.grupoB.LinkStage.ui.common.ContentSection
import turmaA.grupoB.LinkStage.ui.common.SecondaryTopBar
import turmaA.grupoB.LinkStage.ui.orientador.formatCheckpointDateLong
import turmaA.grupoB.LinkStage.ui.orientador.sampleMentorActivityLogs
import androidx.compose.ui.platform.LocalContext
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorDashboardUiState
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorDashboardViewModel
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorDashboardViewModelFactory

@Composable
fun MentorCheckpointDetailScreen(
    checkpointId: String,
    navController: NavController,
    orientadorDashboardViewModel: OrientadorDashboardViewModel = viewModel(factory = OrientadorDashboardViewModelFactory()),
) {
    val context = LocalContext.current
    val dashboardUiState by orientadorDashboardViewModel.uiState.collectAsState()
    val dashboardData = dashboardUiState.dashboardData(context)
    val activityLog = dashboardData.activityLogs.find { it.id == checkpointId }
        ?: sampleMentorActivityLogs(context).find { it.id == checkpointId }
        ?: sampleMentorActivityLogs(context).first()

    LaunchedEffect(orientadorDashboardViewModel) {
        orientadorDashboardViewModel.loadDashboard()
    }

    Scaffold(
        topBar = {
            SecondaryTopBar(
                title = stringResource(R.string.advisor_checkpoint_title),
                onBack = { navController.popBackStack() }
            )
        },
        containerColor = BackgroundLight,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = activityLog.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBlue,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            if (activityLog.createdBy == "MENTOR") {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.School,
                        contentDescription = null,
                        tint = LightBlue,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = stringResource(R.string.advisor_checkpoint_defined_by) +
                            if (activityLog.createdByName.isNotEmpty()) " — ${activityLog.createdByName}" else "",
                        fontSize = 12.sp,
                        color = LightBlue,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 6.dp),
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Delivery date
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = LightBlue,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.advisor_checkpoint_delivery_date),
                        color = DarkGrey,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = formatCheckpointDateLong(activityLog.date, context),
                        color = DarkBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            ContentSection(title = stringResource(R.string.advisor_checkpoint_description)) {
                Text(
                    text = activityLog.description,
                    fontSize = 14.sp,
                    color = DarkGrey,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Attachments
            if (activityLog.submittedFiles.isNotEmpty()) {
                ContentSection(title = stringResource(R.string.advisor_checkpoint_attachments)) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        activityLog.submittedFiles.forEach { file ->
                            ExpandableFileRow(file = file)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Viewers section
            if (activityLog.viewers.isNotEmpty()) {
                ContentSection(title = stringResource(R.string.advisor_checkpoint_reviewed_by)) {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        activityLog.viewers.forEach { viewer ->
                            val initials = viewer.viewerName.split(" ")
                                .filter { it.isNotEmpty() }
                                .map { it.first() }
                                .take(2)
                                .joinToString("")
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(LightBlue.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = initials,
                                        color = LightBlue,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .padding(start = 10.dp)
                                        .weight(1f),
                                ) {
                                    Text(
                                        text = viewer.viewerName,
                                        color = DarkBlue,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                    )
                                    Text(
                                        text = viewer.viewerRole,
                                        color = DarkGrey,
                                        fontSize = 11.sp,
                                    )
                                }
                                Text(
                                    text = viewer.viewedAt,
                                    color = DarkGrey,
                                    fontSize = 11.sp,
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

private fun OrientadorDashboardUiState.dashboardData(context: android.content.Context): turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorDashboardData {
    return when (this) {
        is OrientadorDashboardUiState.Success -> data
        OrientadorDashboardUiState.Idle,
        OrientadorDashboardUiState.Loading,
        OrientadorDashboardUiState.Empty,
        is OrientadorDashboardUiState.Error -> turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorDashboardData(
            internships = emptyList(),
            students = emptyList(),
            activityLogs = sampleMentorActivityLogs(context),
            evaluation = null,
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun MentorCheckpointDetailScreenPreview() {
    MaterialTheme {
        MentorCheckpointDetailScreen(checkpointId = "1", navController = rememberNavController())
    }
}

@Preview(showSystemUi = true, device = "spec:width=411dp,height=891dp,orientation=landscape")
@Composable
private fun MentorCheckpointDetailScreenLandscapePreview() {
    MaterialTheme {
        MentorCheckpointDetailScreen(checkpointId = "1", navController = rememberNavController())
    }
}
