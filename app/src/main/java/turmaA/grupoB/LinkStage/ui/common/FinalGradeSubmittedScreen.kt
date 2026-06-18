package turmaA.grupoB.LinkStage.ui.common

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.orientador.EvaluationState
import turmaA.grupoB.LinkStage.ui.orientador.InternshipType
import turmaA.grupoB.LinkStage.ui.orientador.OrientadorRoutes
import turmaA.grupoB.LinkStage.ui.orientador.sampleEvaluation
import turmaA.grupoB.LinkStage.ui.orientador.sampleMentorStudents
import turmaA.grupoB.LinkStage.ui.orientador.sampleStudentInternship
import androidx.compose.ui.platform.LocalContext
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.Fade2
import turmaA.grupoB.LinkStage.ui.theme.Fade3

@Composable
fun FinalGradeSubmittedScreen(
    internshipId: String,
    navController: NavController,
) {
    val context = LocalContext.current
    val student = sampleMentorStudents(context).first()
    val internship = sampleStudentInternship(context)
    val evaluation = sampleEvaluation(context).copy(
        state = EvaluationState.COMPLETED,
        schoolMentorGrade = 16f,
        schoolMentorObservation = "Bom desempenho global ao longo do estágio.",
    )
    val finalGrade = evaluation.schoolMentorGrade!!

    var animStarted by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (animStarted) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale",
    )
    LaunchedEffect(Unit) { animStarted = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
    ) {
        // Fixed header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(Color(0xFF4CAF50).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(44.dp),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.final_grade_title),
                color = DarkBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.final_grade_message),
                color = DarkGrey,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
            )
        }

        // Scrollable content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Student + final grade card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Fade3)
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = student.avatarInitials,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = student.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                )
                                Text(
                                    text = internship.title,
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontSize = 13.sp,
                                )
                            }
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.2f))

                        Text(
                            text = stringResource(R.string.final_grade_label),
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 13.sp,
                        )
                        Text(
                            text = stringResource(R.string.final_grade_values, formatGrade(finalGrade)),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 42.sp,
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.final_grade_completed),
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
            }

            // Title
            item {
                Text(
                    text = stringResource(R.string.final_grade_summary),
                    color = DarkBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 4.dp),
                )
            }

            // Evaluation cards based on type
            when (evaluation.internshipType) {
                InternshipType.COMPANY_SCHOOL -> {
                    if (evaluation.companyResponsibleGrade != null) {
                        item {
                            EvaluationReadOnlyCard(
                                role = stringResource(R.string.eval_role_company_responsible),
                                name = evaluation.companyResponsibleName,
                                grade = evaluation.companyResponsibleGrade,
                                observation = evaluation.companyResponsibleObservation,
                            )
                        }
                    }
                    if (evaluation.companyMentorGrade != null) {
                        item {
                            EvaluationReadOnlyCard(
                                role = stringResource(R.string.eval_role_company_mentor),
                                name = evaluation.companyMentorName,
                                grade = evaluation.companyMentorGrade,
                                observation = evaluation.companyMentorObservation,
                            )
                        }
                    }
                }
                InternshipType.SCHOOL_ONLY -> {
                    if (evaluation.institutionGrade != null) {
                        item {
                            EvaluationReadOnlyCard(
                                role = stringResource(R.string.eval_role_institution),
                                name = evaluation.institutionName,
                                grade = evaluation.institutionGrade,
                                observation = evaluation.institutionObservation,
                            )
                        }
                    }
                }
            }

            // Final grade card (highlighted)
            item {
                EvaluationReadOnlyCard(
                    role = stringResource(R.string.eval_role_school_mentor),
                    name = evaluation.schoolMentorName,
                    grade = evaluation.schoolMentorGrade!!,
                    observation = evaluation.schoolMentorObservation,
                    highlight = true,
                )
            }

            // Buttons
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    Button(
                        onClick = {
                            navController.navigate(OrientadorRoutes.mentorStudentDetail(student.id)) {
                                popUpTo("final_grade_submitted/$internshipId") { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(Fade2, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    ) {
                        Text(
                            text = stringResource(R.string.final_grade_view_student),
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            navController.navigate(OrientadorRoutes.HOME) {
                                popUpTo(0) { inclusive = false }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Fade2),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkBlue),
                    ) {
                        Text(
                            text = stringResource(R.string.eval_back_home),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun FinalGradeSubmittedScreenPreview() {
    MaterialTheme {
        FinalGradeSubmittedScreen(
            internshipId = "int1",
            navController = rememberNavController(),
        )
    }
}
