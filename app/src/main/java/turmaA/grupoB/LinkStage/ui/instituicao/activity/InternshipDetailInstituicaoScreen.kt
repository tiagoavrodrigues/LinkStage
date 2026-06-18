package turmaA.grupoB.LinkStage.ui.instituicao.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.aluno.activity.ActivityLogCard
import turmaA.grupoB.LinkStage.ui.aluno.chat.avatarColors
import turmaA.grupoB.LinkStage.ui.common.ConfirmationDialog
import turmaA.grupoB.LinkStage.ui.common.CreateCheckpointDialog
import turmaA.grupoB.LinkStage.ui.common.EvaluationReadOnlyCard
import turmaA.grupoB.LinkStage.ui.common.LinkStageButton
import turmaA.grupoB.LinkStage.ui.common.SectionLabel
import turmaA.grupoB.LinkStage.ui.common.formatGrade
import turmaA.grupoB.LinkStage.ui.common.validateGrade
import turmaA.grupoB.LinkStage.ui.instituicao.InstituicaoRoutes
import turmaA.grupoB.LinkStage.ui.instituicao.InstitutionInternship
import turmaA.grupoB.LinkStage.ui.instituicao.InternshipOrigin
import turmaA.grupoB.LinkStage.ui.instituicao.InternshipStatus
import turmaA.grupoB.LinkStage.ui.instituicao.internshipStatusColor
import turmaA.grupoB.LinkStage.ui.instituicao.internshipStatusLabel
import turmaA.grupoB.LinkStage.ui.instituicao.sampleInstitutionInternships
import turmaA.grupoB.LinkStage.ui.orientador.EvaluationState
import turmaA.grupoB.LinkStage.ui.orientador.InternshipEvaluation
import turmaA.grupoB.LinkStage.ui.orientador.InternshipType
import turmaA.grupoB.LinkStage.ui.orientador.sampleMentorActivityLogs
import androidx.compose.ui.platform.LocalContext
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.BorderGrey
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.Fade2
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.ui.theme.Red
import turmaA.grupoB.LinkStage.viewmodel.institutionhome.InstitutionHomeViewModel
import turmaA.grupoB.LinkStage.viewmodel.institutionhome.InstitutionHomeViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.instituicao.activity.InstitutionActivityUiState
import turmaA.grupoB.LinkStage.viewmodel.instituicao.activity.InstitutionActivityViewModel
import turmaA.grupoB.LinkStage.viewmodel.instituicao.activity.InstitutionActivityViewModelFactory

@Composable
fun InternshipDetailInstituicaoScreen(
    internshipId: String,
    navController: NavController,
    modifier: Modifier = Modifier,
    institutionHomeViewModel: InstitutionHomeViewModel = viewModel(factory = InstitutionHomeViewModelFactory()),
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

    val evaluation: InternshipEvaluation? = remember {
        InternshipEvaluation(
            internshipId = internshipId,
            internshipType = InternshipType.SCHOOL_ONLY,
            state = EvaluationState.PENDING,
            institutionName = "ESTG-IPVC",
            schoolMentorName = "Prof. Tiago Alex.",
            hasSeenNotification = false,
        )
    }

    // Evaluation Form State
    var observation by remember { mutableStateOf("") }
    var gradeText by remember { mutableStateOf("") }
    var gradeError by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val isFormValid = observation.isNotBlank() && validateGrade(gradeText) != null

    if (showConfirmDialog) {
        ConfirmationDialog(
            title = stringResource(R.string.institution_eval_submit_title),
            body = stringResource(R.string.institution_eval_submit_body),
            confirmLabel = stringResource(R.string.institution_eval_submit_button),
            isDanger = false,
            onConfirm = {
                showConfirmDialog = false
                navController.navigate(InstituicaoRoutes.evaluationSubmittedRoute(internshipId))
            },
            onDismiss = { showConfirmDialog = false },
        )
    }

    LaunchedEffect(Unit) {
        if (evaluation?.state == EvaluationState.PENDING) {
            institutionHomeViewModel.setHasSeenEvaluations(true)
        }
    }

    var showCreateCheckpointDialog by remember { mutableStateOf(false) }

    if (showCreateCheckpointDialog) {
        CreateCheckpointDialog(
            onSave = { _, _, _ ->
                showCreateCheckpointDialog = false
            },
            onDismiss = { showCreateCheckpointDialog = false },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundLight,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateCheckpointDialog = true },
                containerColor = DarkBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = if (evaluation?.state == EvaluationState.PENDING) 80.dp else 0.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.institution_create_checkpoint))
            }
        },
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 20.dp, top = 8.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back_content_desc),
                            tint = DarkBlue
                        )
                    }
                    Text(
                        text = stringResource(R.string.institution_internship_detail),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkBlue,
                            fontSize = 20.sp
                        )
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (!internship.hasMentor) {
                        LinkStageButton(
                            text = stringResource(R.string.assign_mentor_title),
                            onClick = { navController.navigate(InstituicaoRoutes.assignMentorRoute(internship.id)) },
                            height = 48.dp,
                            brush = Fade2,
                        )
                    }

                    if (evaluation?.state == EvaluationState.PENDING) {
                        LinkStageButton(
                            text = stringResource(R.string.institution_eval_submit_grade),
                            onClick = {
                                if (validateGrade(gradeText) == null) {
                                    gradeError = true
                                } else {
                                    showConfirmDialog = true
                                }
                            },
                            enabled = isFormValid,
                            height = 48.dp,
                            brush = Fade2
                        )
                    }

                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(
                                brush = if (evaluation?.state == EvaluationState.PENDING) SolidColor(Color.Transparent) else Fade2,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .border(
                                width = if (evaluation?.state == EvaluationState.PENDING) 1.dp else 0.dp,
                                brush = if (evaluation?.state == EvaluationState.PENDING) Fade2 else SolidColor(Color.Transparent),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = if (evaluation?.state == EvaluationState.PENDING) DarkBlue else Color.White
                        ),
                    ) {
                        Text(stringResource(R.string.institution_view_activities), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            InternshipInfoCard(internship = internship)

            Spacer(modifier = Modifier.height(16.dp))

            // Activity section
            Text(
                text = stringResource(R.string.institution_activity_recent),
                color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))

            sampleMentorActivityLogs(context).forEach { activityLog ->
                ActivityLogCard(
                    activityLog = activityLog,
                    showViewers = true,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (evaluation != null) {
                InstitutionEvaluationSection(
                    evaluation = evaluation,
                    observation = observation,
                    onObservationChange = { observation = it },
                    gradeText = gradeText,
                    onGradeTextChange = { 
                        gradeText = it
                        gradeError = false
                    },
                    gradeError = gradeError
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun InternshipInfoCard(internship: InstitutionInternship) {
    val statusColor = internshipStatusColor(internship.status)
    val statusLabel = internshipStatusLabel(internship.status)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(avatarColors[internship.studentAvatarColorIndex % avatarColors.size]),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        internship.studentAvatarInitials,
                        color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp,
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(internship.studentName, color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(internship.offerTitle, color = DarkGrey, fontSize = 13.sp)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(statusLabel, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }

            HorizontalDivider(color = BorderGrey, modifier = Modifier.padding(horizontal = 14.dp))

            // Metadata grid
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.institution_origin), color = DarkGrey, fontSize = 11.sp)
                    Text(
                        if (internship.origin == InternshipOrigin.SCHOOL) stringResource(R.string.institution_origin_school) else stringResource(R.string.institution_origin_company),
                        color = DarkBlue, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.institution_school_mentor), color = DarkGrey, fontSize = 11.sp)
                    Text(
                        internship.schoolMentorName.ifEmpty { stringResource(R.string.institution_to_be_defined) },
                        color = DarkBlue, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                    )
                }
            }
            if (internship.origin == InternshipOrigin.COMPANY) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.institution_company_mentor), color = DarkGrey, fontSize = 11.sp)
                        Text(
                            internship.companyMentorName.ifEmpty { stringResource(R.string.institution_to_be_defined) },
                            color = DarkBlue, fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.institution_start_date_label), color = DarkGrey, fontSize = 11.sp)
                    Text("01 Set 2026", color = DarkBlue, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.institution_progress), color = DarkGrey, fontSize = 11.sp)
                    Text("${internship.progressPercent}%", color = DarkBlue, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { internship.progressPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .padding(horizontal = 14.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = LightBlue,
                trackColor = BorderGrey,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.institution_final_report_label), color = DarkGrey, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF4CAF50).copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(stringResource(R.string.institution_status_pending), color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun ActivityPlaceholderCard(
    title: String,
    status: String,
    statusColor: Color,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = DarkBlue, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(statusColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
            ) {
                Text(status, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            }
        }
    }
}

// region Institution Evaluation Section

@Composable
private fun InstitutionEvaluationSection(
    evaluation: InternshipEvaluation,
    observation: String,
    onObservationChange: (String) -> Unit,
    gradeText: String,
    onGradeTextChange: (String) -> Unit,
    gradeError: Boolean
) {
    when (evaluation.state) {
        EvaluationState.PENDING -> {
            InstitutionEvaluationForm(
                observation = observation,
                onObservationChange = onObservationChange,
                gradeText = gradeText,
                onGradeTextChange = onGradeTextChange,
                gradeError = gradeError
            )
        }
        EvaluationState.PARTIAL, EvaluationState.READY_FOR_FINAL -> {
            Text(
                text = stringResource(R.string.institution_eval_submitted),
                color = DarkBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (evaluation.institutionGrade != null) {
                EvaluationReadOnlyCard(
                    role = stringResource(R.string.eval_role_institution),
                    name = evaluation.institutionName,
                    grade = evaluation.institutionGrade,
                    observation = evaluation.institutionObservation,
                )
            }
        }
        EvaluationState.COMPLETED -> {
            Text(
                text = stringResource(R.string.institution_eval_completed),
                color = DarkBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (evaluation.institutionGrade != null) {
                EvaluationReadOnlyCard(
                    role = stringResource(R.string.eval_role_institution),
                    name = evaluation.institutionName,
                    grade = evaluation.institutionGrade,
                    observation = evaluation.institutionObservation,
                )
            }
            if (evaluation.schoolMentorGrade != null) {
                EvaluationReadOnlyCard(
                    role = stringResource(R.string.eval_role_school_mentor_short),
                    name = evaluation.schoolMentorName,
                    grade = evaluation.schoolMentorGrade,
                    observation = evaluation.schoolMentorObservation,
                )
            }
        }
    }
}

@Composable
private fun InstitutionEvaluationForm(
    observation: String,
    onObservationChange: (String) -> Unit,
    gradeText: String,
    onGradeTextChange: (String) -> Unit,
    gradeError: Boolean
) {
    Text(
        text = stringResource(R.string.institution_eval_title),
        color = DarkBlue,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        modifier = Modifier.padding(horizontal = 16.dp),
    )
    Spacer(modifier = Modifier.height(8.dp))

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionLabel(stringResource(R.string.institution_eval_observation))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = observation,
            onValueChange = onObservationChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.common_write_here), color = DarkGrey) },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LightBlue,
                unfocusedBorderColor = BorderGrey,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
            ),
            minLines = 3,
            maxLines = 6,
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionLabel(stringResource(R.string.institution_eval_grade_label))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = gradeText,
            onValueChange = onGradeTextChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.institution_eval_grade_placeholder), color = DarkGrey) },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (gradeError) Red else LightBlue,
                unfocusedBorderColor = if (gradeError) Red else BorderGrey,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            isError = gradeError,
        )
        if (gradeError) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.institution_eval_grade_error),
                color = Red,
                fontSize = 12.sp,
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}

// endregion

// region Previews

@Preview(showSystemUi = true)
@Composable
private fun InternshipDetailInstituicaoScreenPreview() {
    MaterialTheme {
        InternshipDetailInstituicaoScreen(internshipId = "int1", navController = rememberNavController())
    }
}

@Preview(showSystemUi = true, device = "spec:width=411dp,height=891dp,orientation=landscape")
@Composable
private fun InternshipDetailInstituicaoScreenLandscapePreview() {
    MaterialTheme {
        InternshipDetailInstituicaoScreen(internshipId = "int1", navController = rememberNavController())
    }
}

// endregion
