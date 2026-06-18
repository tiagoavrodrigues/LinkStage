package turmaA.grupoB.LinkStage.ui.instituicao.activity

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.instituicao.InstituicaoRoutes
import turmaA.grupoB.LinkStage.ui.instituicao.sampleInstitutionInternships
import androidx.compose.ui.platform.LocalContext
import turmaA.grupoB.LinkStage.ui.theme.BorderGrey
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.Fade1
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.viewmodel.instituicao.activity.InstitutionActivityUiState
import turmaA.grupoB.LinkStage.viewmodel.instituicao.activity.InstitutionActivityViewModel
import turmaA.grupoB.LinkStage.viewmodel.instituicao.activity.InstitutionActivityViewModelFactory

@Composable
fun MentorAssignedSuccessScreen(
    internshipId: String,
    navController: NavController,
    modifier: Modifier = Modifier,
    activityViewModel: InstitutionActivityViewModel = viewModel(factory = InstitutionActivityViewModelFactory()),
) {
    val context = LocalContext.current
    val activityUiState by activityViewModel.uiState.collectAsState()
    val internship = (activityUiState as? InstitutionActivityUiState.Success)?.data?.internships?.find { it.id == internshipId }
        ?: sampleInstitutionInternships(context).find { it.id == internshipId }
        ?: sampleInstitutionInternships(context).first()

    LaunchedEffect(Unit) {
        activityViewModel.loadActivityForCurrentInstitution()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Success banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF4CAF50).copy(alpha = 0.4f)),
            ),
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.mentor_assigned_success),
                    color = Color(0xFF4CAF50), fontWeight = FontWeight.SemiBold, fontSize = 14.sp,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Result card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Fade1),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            internship.studentAvatarInitials,
                            color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp,
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(internship.studentName, color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(internship.offerTitle, color = DarkGrey, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = BorderGrey)
                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    Text(stringResource(R.string.mentor_success_advisor_label), color = DarkGrey, fontSize = 13.sp, modifier = Modifier.weight(1f))
                    Text(internship.mentorName, color = DarkBlue, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.mentor_success_new_status), color = DarkGrey, fontSize = 13.sp, modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(LightBlue.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(stringResource(R.string.mentor_success_monitoring), color = LightBlue, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Next step card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = LightBlue.copy(alpha = 0.08f)),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(LightBlue.copy(alpha = 0.3f)),
            ),
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(Icons.Outlined.Info, contentDescription = null, tint = LightBlue, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(stringResource(R.string.mentor_success_next_step), color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(
                        stringResource(R.string.mentor_success_next_step_message),
                        color = DarkGrey, fontSize = 12.sp,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                navController.navigate(InstituicaoRoutes.internshipDetailRoute(internship.id)) {
                    popUpTo(InstituicaoRoutes.ACTIVITY) { inclusive = false }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
        ) {
            Text(stringResource(R.string.mentor_success_view_internship), fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                navController.navigate(InstituicaoRoutes.ACTIVITY) {
                    popUpTo(InstituicaoRoutes.ACTIVITY) { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                brush = androidx.compose.ui.graphics.SolidColor(DarkBlue),
            ),
        ) {
            Text(stringResource(R.string.mentor_success_back_advisors), color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showSystemUi = true)
@Composable
private fun MentorAssignedSuccessScreenPreview() {
    MaterialTheme {
        MentorAssignedSuccessScreen(internshipId = "int1", navController = rememberNavController())
    }
}

@Preview(showSystemUi = true, device = "spec:width=411dp,height=891dp,orientation=landscape")
@Composable
private fun MentorAssignedSuccessScreenLandscapePreview() {
    MaterialTheme {
        MentorAssignedSuccessScreen(internshipId = "int1", navController = rememberNavController())
    }
}
