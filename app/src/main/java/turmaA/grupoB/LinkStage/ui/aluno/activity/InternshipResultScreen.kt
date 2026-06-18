package turmaA.grupoB.LinkStage.ui.aluno.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.navigation.compose.rememberNavController
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.data.remote.model.enums.EvaluationType
import turmaA.grupoB.LinkStage.data.remote.model.evaluation.EvaluationModel
import turmaA.grupoB.LinkStage.data.repository.evaluation.EvaluationRepository
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepository
import turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepository
import turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepository
import turmaA.grupoB.LinkStage.ui.common.SecondaryTopBar
import turmaA.grupoB.LinkStage.ui.common.formatGrade
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.BorderGrey
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.Fade2
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.viewmodel.evaluation.EvaluationUiState
import turmaA.grupoB.LinkStage.viewmodel.evaluation.EvaluationViewModel
import turmaA.grupoB.LinkStage.viewmodel.evaluation.EvaluationViewModelFactory

@Composable
fun InternshipResultScreen(
    internshipId: String,
    navController: NavController,
    evaluationViewModel: EvaluationViewModel = viewModel(
        factory = EvaluationViewModelFactory(
            EvaluationRepository(),
            InternshipRepository(),
            ProfileRepository(),
            InstitutionRepository(),
        )
    ),
) {
    val evaluationUiState by evaluationViewModel.uiState.collectAsState()

    LaunchedEffect(internshipId) {
        evaluationViewModel.loadInternshipResult(internshipId)
    }

    Scaffold(
        topBar = {
            SecondaryTopBar(
                title = stringResource(R.string.result_internship_title),
                onBack = { navController.popBackStack() }
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Fade2)
                        .clickable { navController.popBackStack() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.result_back_home),
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        },
        containerColor = BackgroundLight,
    ) { paddingValues ->
        when (val state = evaluationUiState) {
            is EvaluationUiState.InternshipResultSuccess -> {
                InternshipResultContent(
                    state = state,
                    modifier = Modifier.padding(paddingValues),
                )
            }

            is EvaluationUiState.Error -> ResultStateMessage(
                message = state.message,
                modifier = Modifier.padding(paddingValues),
            )

            EvaluationUiState.Empty -> ResultStateMessage(
                message = stringResource(R.string.result_not_available),
                modifier = Modifier.padding(paddingValues),
            )

            else -> ResultStateMessage(
                message = stringResource(R.string.result_loading),
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Composable
private fun InternshipResultContent(
    state: EvaluationUiState.InternshipResultSuccess,
    modifier: Modifier = Modifier,
) {
    val institutionName = state.institution?.name.orEmpty()
    val institutionInitial = institutionName.firstOrNull()?.uppercase() ?: ""

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
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
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF212121)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        institutionInitial,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        state.internship.title,
                        color = DarkBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                    )
                    if (institutionName.isNotBlank()) {
                        Text(institutionName, color = LightBlue, fontSize = 14.sp)
                    }
                    Text(
                        stringResource(R.string.activity_status_completed),
                        color = LightBlue,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Fade2)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.report_final_grade),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = formatGrade(state.finalGrade.grade.toFloat()),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.sp,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.report_out_of_20),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        state.evaluations.forEach { evaluation ->
            val evaluatorName = state.evaluatorProfiles[evaluation.evaluatorUserId]?.name
                ?: evaluation.fallbackEvaluatorName(state.institution?.name)
            EvaluationCard(
                mentorName = evaluatorName,
                mentorRole = evaluation.roleLabel(),
                observation = evaluation.comment.orEmpty(),
                grade = formatGrade(evaluation.grade.toFloat()),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun EvaluationModel.roleLabel(): String {
    return when (evaluatorType) {
        EvaluationType.SUPERVISOR -> stringResource(R.string.eval_role_school_mentor_short)
        EvaluationType.INSTITUTION -> stringResource(R.string.eval_role_institution)
    }
}

@Composable
private fun EvaluationModel.fallbackEvaluatorName(institutionName: String?): String {
    return when (evaluatorType) {
        EvaluationType.SUPERVISOR -> stringResource(R.string.eval_role_school_mentor_short)
        EvaluationType.INSTITUTION -> institutionName
            ?.takeIf { it.isNotBlank() }
            ?: stringResource(R.string.eval_role_institution)
    }
}

@Composable
private fun ResultStateMessage(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            color = DarkGrey,
            modifier = Modifier.padding(24.dp),
        )
    }
}

@Composable
private fun EvaluationCard(
    mentorName: String,
    mentorRole: String,
    observation: String,
    grade: String,
) {
    val initials = mentorName.split(" ")
        .filter { it.isNotEmpty() }
        .map { it.first() }
        .take(2)
        .joinToString("")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(LightBlue),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(mentorName, color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(mentorRole, color = DarkGrey, fontSize = 12.sp)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(LightBlue.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        stringResource(R.string.result_grade_values, grade),
                        color = DarkBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = BorderGrey)
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                stringResource(R.string.result_observations),
                color = DarkGrey,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(observation, color = DarkBlue, fontSize = 14.sp, lineHeight = 20.sp)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun InternshipResultScreenPreview() {
    MaterialTheme {
        InternshipResultScreen(
            internshipId = "00000000-0000-0000-0000-000000000000",
            navController = rememberNavController(),
        )
    }
}
