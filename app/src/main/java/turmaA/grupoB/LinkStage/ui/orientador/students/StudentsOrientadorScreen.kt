package turmaA.grupoB.LinkStage.ui.orientador.students

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.admin.AdminStudent
import turmaA.grupoB.LinkStage.ui.orientador.OrientadorRoutes
import turmaA.grupoB.LinkStage.ui.orientador.sampleMentorStudents
import turmaA.grupoB.LinkStage.ui.common.CommonTopBar
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.BorderGrey
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorDashboardData
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorDashboardUiState
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorDashboardViewModel
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorDashboardViewModelFactory

@Composable
fun StudentsOrientadorScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    orientadorDashboardViewModel: OrientadorDashboardViewModel = viewModel(factory = OrientadorDashboardViewModelFactory()),
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val dashboardUiState by orientadorDashboardViewModel.uiState.collectAsState()
    val dashboardData = dashboardUiState.dashboardData(context)

    val filtered = if (searchQuery.isEmpty()) dashboardData.students
    else dashboardData.students.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
            it.institution.contains(searchQuery, ignoreCase = true)
    }

    LaunchedEffect(orientadorDashboardViewModel) {
        orientadorDashboardViewModel.loadDashboard()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundLight),
    ) {
        CommonTopBar()

        Text(
            text = stringResource(R.string.advisor_students_title),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = DarkBlue,
            ),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            placeholder = { Text(stringResource(R.string.common_search), color = DarkGrey) },
            leadingIcon = {
                Icon(Icons.Outlined.Search, contentDescription = stringResource(R.string.common_search), tint = DarkGrey)
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = DarkBlue,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
            ),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (filtered.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.People,
                    contentDescription = null,
                    tint = BorderGrey,
                    modifier = Modifier.size(64.dp),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.advisor_no_students),
                    color = DarkGrey,
                    fontSize = 16.sp,
                )
            }
        } else {
            val grouped = filtered.groupBy { it.institution }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                grouped.forEach { (institutionName, students) ->
                    item(key = "header_$institutionName") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BackgroundLight)
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = institutionName,
                                color = DarkBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = stringResource(R.string.advisor_student_count, students.size),
                                color = DarkGrey,
                                fontSize = 12.sp,
                            )
                        }
                    }

                    items(students, key = { it.id }) { student ->
                        StudentCard(
                            student = student,
                            onClick = {
                                navController.navigate(OrientadorRoutes.mentorStudentDetail(student.id))
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentCard(
    student: AdminStudent,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(DarkGrey),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp),
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
                    text = stringResource(R.string.advisor_registered_ago, student.registeredAgo),
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

private fun OrientadorDashboardUiState.dashboardData(context: android.content.Context): OrientadorDashboardData {
    return when (this) {
        is OrientadorDashboardUiState.Success -> data
        OrientadorDashboardUiState.Idle,
        OrientadorDashboardUiState.Loading,
        OrientadorDashboardUiState.Empty,
        is OrientadorDashboardUiState.Error -> OrientadorDashboardData(
            internships = emptyList(),
            students = sampleMentorStudents(context),
            activityLogs = emptyList(),
            evaluation = null,
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun StudentsOrientadorScreenPreview() {
    MaterialTheme {
        StudentsOrientadorScreen(navController = rememberNavController())
    }
}

@Preview(showSystemUi = true, device = "spec:width=411dp,height=891dp,orientation=landscape")
@Composable
private fun StudentsOrientadorScreenLandscapePreview() {
    MaterialTheme {
        StudentsOrientadorScreen(navController = rememberNavController())
    }
}
