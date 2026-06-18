package turmaA.grupoB.LinkStage.ui.orientador.internships

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import turmaA.grupoB.LinkStage.ui.common.CommonTopBar
import turmaA.grupoB.LinkStage.ui.orientador.MentorInternship
import turmaA.grupoB.LinkStage.ui.orientador.OrientadorRoutes
import turmaA.grupoB.LinkStage.ui.orientador.formatInternshipDate
import turmaA.grupoB.LinkStage.ui.orientador.sampleMentorInternships
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.BorderGrey
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.ui.theme.MediumBlue
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorDashboardData
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorDashboardUiState
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorDashboardViewModel
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorDashboardViewModelFactory

@Composable
fun InternshipsOrientadorScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    orientadorDashboardViewModel: OrientadorDashboardViewModel = viewModel(factory = OrientadorDashboardViewModelFactory()),
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val dashboardUiState by orientadorDashboardViewModel.uiState.collectAsState()
    val dashboardData = dashboardUiState.dashboardData(context)

    val filtered = if (searchQuery.isEmpty()) dashboardData.internships
    else dashboardData.internships.filter {
        it.offerTitle.contains(searchQuery, ignoreCase = true) ||
            it.businessInstitutionName.contains(searchQuery, ignoreCase = true) ||
            it.schoolInstitutionName.contains(searchQuery, ignoreCase = true)
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
            text = stringResource(R.string.advisor_internships_title),
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
                    imageVector = Icons.Outlined.Work,
                    contentDescription = null,
                    tint = BorderGrey,
                    modifier = Modifier.size(64.dp),
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.advisor_no_internships),
                    color = DarkGrey,
                    fontSize = 16.sp,
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filtered, key = { it.id }) { internship ->
                    InternshipCard(
                        internship = internship,
                        onClick = {
                            navController.navigate(OrientadorRoutes.mentorInternshipDetail(internship.id))
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun InternshipCard(
    internship: MentorInternship,
    onClick: () -> Unit = {},
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.padding(14.dp),
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
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = internship.offerTitle,
                        color = DarkBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    )
                    
                    Text(
                        text = if (internship.isBusinessInternship) internship.businessInstitutionName else internship.schoolInstitutionName,
                        color = if (internship.isBusinessInternship) MediumBlue else DarkGrey,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (internship.isBusinessInternship) LightBlue.copy(alpha = 0.15f) else DarkGrey.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = stringResource(if (internship.isBusinessInternship) R.string.advisor_type_business else R.string.advisor_type_school),
                            color = if (internship.isBusinessInternship) DarkBlue else DarkGrey,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                        )
                    }
                }
            }

            HorizontalDivider(
                color = BorderGrey,
                modifier = Modifier.padding(horizontal = 14.dp),
            )

            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = DarkGrey,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.advisor_ends_at, formatInternshipDate(internship.endDate, context)),
                    color = DarkGrey,
                    fontSize = 12.sp,
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
            internships = sampleMentorInternships(context),
            students = emptyList(),
            activityLogs = emptyList(),
            evaluation = null,
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun InternshipsOrientadorScreenPreview() {
    MaterialTheme {
        InternshipsOrientadorScreen(navController = rememberNavController())
    }
}
