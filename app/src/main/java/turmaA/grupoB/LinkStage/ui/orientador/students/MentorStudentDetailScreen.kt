package turmaA.grupoB.LinkStage.ui.orientador.students

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
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
import turmaA.grupoB.LinkStage.ui.aluno.activity.ActivityLogStatus
import turmaA.grupoB.LinkStage.ui.aluno.activity.InternshipHeader
import turmaA.grupoB.LinkStage.ui.aluno.activity.calculateInternshipProgress
import turmaA.grupoB.LinkStage.ui.common.LinkStageButton
import turmaA.grupoB.LinkStage.ui.common.LinkStageTabRow
import turmaA.grupoB.LinkStage.ui.common.SecondaryTopBar
import turmaA.grupoB.LinkStage.ui.common.SectionLabel
import turmaA.grupoB.LinkStage.ui.common.ConfirmationDialog
import turmaA.grupoB.LinkStage.ui.common.CreateCheckpointDialog
import turmaA.grupoB.LinkStage.ui.common.EvaluationReadOnlyCard
import turmaA.grupoB.LinkStage.ui.common.formatGrade
import turmaA.grupoB.LinkStage.ui.common.validateGrade
import turmaA.grupoB.LinkStage.ui.orientador.EvaluationState
import turmaA.grupoB.LinkStage.ui.orientador.InternshipEvaluation
import turmaA.grupoB.LinkStage.ui.orientador.InternshipType
import turmaA.grupoB.LinkStage.ui.orientador.OrientadorRoutes
import turmaA.grupoB.LinkStage.ui.orientador.formatCheckpointDate
import turmaA.grupoB.LinkStage.ui.orientador.sampleEvaluation
import turmaA.grupoB.LinkStage.ui.orientador.sampleMentorActivityLogs
import turmaA.grupoB.LinkStage.ui.orientador.sampleMentorStudents
import turmaA.grupoB.LinkStage.ui.orientador.sampleStudentInternship
import androidx.compose.ui.platform.LocalContext
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.BorderGrey
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.Fade1
import turmaA.grupoB.LinkStage.ui.theme.Fade2
import turmaA.grupoB.LinkStage.ui.theme.Green
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.ui.theme.MediumBlue
import turmaA.grupoB.LinkStage.ui.theme.Red
import turmaA.grupoB.LinkStage.viewmodel.advisorhome.AdvisorHomeViewModel
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorStudentDetailUiState
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorStudentDetailViewModel
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorStudentDetailViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.orientador.SubmitFinalGradeUiState
import turmaA.grupoB.LinkStage.viewmodel.chat.ChatViewModel
import turmaA.grupoB.LinkStage.viewmodel.chat.EnsureThreadResult
import android.widget.Toast

@Composable
fun MentorStudentDetailScreen(
    studentId: String,
    navController: NavController,
    advisorHomeViewModel: AdvisorHomeViewModel = viewModel(),
    orientadorStudentDetailViewModel: OrientadorStudentDetailViewModel = viewModel(factory = OrientadorStudentDetailViewModelFactory()),
    chatViewModel: ChatViewModel? = null,
) {
    val context = LocalContext.current
    val detailUiState by orientadorStudentDetailViewModel.uiState.collectAsState()
    val detailData = (detailUiState as? OrientadorStudentDetailUiState.Success)?.data
    val fallbackStudents = sampleMentorStudents(context)
    val student = detailData?.student ?: fallbackStudents.find { it.id == studentId } ?: fallbackStudents.first()
    val fallbackInternship = sampleStudentInternship(context)
    val internship = detailData?.activeInternship ?: fallbackInternship
    val activityLogs = detailData?.activityLogs?.takeIf { it.isNotEmpty() } ?: sampleMentorActivityLogs(context)
    val evaluation = detailData?.evaluation ?: sampleEvaluation(context)
    var selectedTab by remember { mutableIntStateOf(0) }
    var showCreateCheckpointDialog by remember { mutableStateOf(false) }
    LaunchedEffect(orientadorStudentDetailViewModel, studentId) {
        orientadorStudentDetailViewModel.loadStudent(studentId)
    }

    if (chatViewModel != null) {
        val ensureResult by chatViewModel.ensureThreadResult.collectAsState()
        LaunchedEffect(ensureResult) {
            if (ensureResult != null) {
                navController.navigate(OrientadorRoutes.chatRoute(ensureResult!!.threadId))
                chatViewModel.clearEnsureThreadResult()
            }
        }
    }

    LaunchedEffect(selectedTab) {
        if (selectedTab == 2 && evaluation.state == EvaluationState.READY_FOR_FINAL) {
            advisorHomeViewModel.setHasSeenEvaluations(true)
        }
    }

    if (showCreateCheckpointDialog) {
        CreateCheckpointDialog(
            onSave = { title, description, date ->
                orientadorStudentDetailViewModel.createCheckpoint(
                    internshipId = internship.id,
                    studentId = student.id,
                    title = title,
                    description = description,
                    date = date,
                )
                showCreateCheckpointDialog = false
            },
            onDismiss = { showCreateCheckpointDialog = false },
        )
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                SecondaryTopBar(title = stringResource(R.string.student_detail_title), onBack = { navController.popBackStack() })

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Fade1),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(student.avatarInitials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(student.name, color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(student.institutionCode, color = LightBlue, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinkStageTabRow(
                    tabs = listOf(stringResource(R.string.common_details), stringResource(R.string.student_detail_tab_work), stringResource(R.string.student_detail_tab_evaluate)),
                    selectedIndex = selectedTab,
                    onTabSelected = { selectedTab = it },
                )
            }
        },
        bottomBar = {
            if (selectedTab == 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundLight)
                        .padding(16.dp)
                ) {
                    LinkStageButton(
                        text = stringResource(R.string.common_send_message),
                        onClick = {
                            chatViewModel?.ensureThreadForStudent(student.id)
                        },
                        height = 50.dp,
                        brush = Fade2
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == 1) {
                FloatingActionButton(
                    onClick = { showCreateCheckpointDialog = true },
                    containerColor = DarkBlue,
                    contentColor = Color.White,
                    shape = CircleShape,
                ) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.institution_create_checkpoint))
                }
            }
        },
        containerColor = BackgroundLight,
    ) { paddingValues ->
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "tab_content",
            modifier = Modifier.padding(paddingValues)
        ) { tab ->
            when (tab) {
                0 -> StudentDetailsTab(student, navController)
                1 -> StudentWorkTab(internship, activityLogs, navController)
                2 -> StudentEvaluateTab(student, evaluation, internship, navController, orientadorStudentDetailViewModel)
            }
        }
    }
}

// region Tab 0 — Detalhes

@Composable
private fun StudentDetailsTab(
    student: AdminStudent,
    navController: NavController,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        InfoField(label = stringResource(R.string.advisor_institution), value = student.institution, trailingBadge = "ipvc")
        InfoFieldWithIcon(label = stringResource(R.string.admin_detail_course), value = student.course, icon = Icons.AutoMirrored.Outlined.MenuBook)
        InfoFieldWithIcon(label = stringResource(R.string.app_detail_gpa), value = "${student.gpa}", icon = Icons.Outlined.Grade)
        InfoFieldWithIcon(label = stringResource(R.string.admin_detail_email), value = student.email, icon = Icons.Outlined.Email)
        InfoFieldWithIcon(label = stringResource(R.string.admin_detail_phone), value = student.phone, icon = Icons.Outlined.Phone)

        Spacer(modifier = Modifier.height(12.dp))

        if (student.skills.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionLabel(stringResource(R.string.student_detail_skills))
                    Spacer(modifier = Modifier.height(8.dp))
                    student.skills.forEach { skill ->
                        Text("• $skill", color = DarkGrey, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun InfoField(label: String, value: String, trailingBadge: String? = null) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            SectionLabel(label)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(value, color = DarkBlue, fontSize = 14.sp, modifier = Modifier.weight(1f))
                if (trailingBadge != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MediumBlue)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Text(trailingBadge, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoFieldWithIcon(label: String, value: String, icon: ImageVector) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            SectionLabel(label)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = DarkGrey, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(value, color = DarkBlue, fontSize = 14.sp)
            }
        }
    }
}

// endregion

// region Tab 1 — Trabalho Desenvolvido

@Composable
private fun StudentWorkTab(
    internship: ActiveInternship,
    activityLogs: List<ActivityLog>,
    navController: NavController,
) {
    val context = LocalContext.current
    val progress = calculateInternshipProgress(internship.startDate, internship.endDate)

    var animationStarted by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationStarted) progress else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "progress",
    )
    LaunchedEffect(Unit) { animationStarted = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        InternshipHeader(
            internship = internship,
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
                showViewers = true,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// endregion

// region Tab 2 — Avaliar

@Composable
private fun StudentEvaluateTab(
    student: AdminStudent,
    evaluation: InternshipEvaluation,
    internship: ActiveInternship,
    navController: NavController,
    orientadorStudentDetailViewModel: OrientadorStudentDetailViewModel,
) {
    val context = LocalContext.current
    val progress = calculateInternshipProgress(internship.startDate, internship.endDate)

    var animationStarted by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationStarted) progress else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "progress_eval",
    )
    LaunchedEffect(Unit) { animationStarted = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        InternshipHeader(
            internship = internship,
            animatedProgress = animatedProgress,
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (evaluation.state) {
            EvaluationState.PENDING -> PendingStateCard()
            EvaluationState.PARTIAL -> PartialStateContent(evaluation)
            EvaluationState.READY_FOR_FINAL -> ReadyForFinalContent(student, evaluation, navController, orientadorStudentDetailViewModel)
            EvaluationState.COMPLETED -> CompletedStateContent(evaluation)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PendingStateCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LightBlue.copy(alpha = 0.08f)),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                Icons.Outlined.Info,
                contentDescription = null,
                tint = LightBlue,
                modifier = Modifier.size(32.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.advisor_eval_pending_title),
                color = DarkBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.advisor_eval_pending_message),
                color = DarkGrey,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
            )
        }
    }
}

@Composable
private fun PartialStateContent(evaluation: InternshipEvaluation) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5C518).copy(alpha = 0.08f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF5C518).copy(alpha = 0.5f)),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Outlined.Info,
                contentDescription = null,
                tint = Color(0xFFF5C518),
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = stringResource(R.string.advisor_eval_partial_title),
                    color = DarkBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
                Text(
                    text = stringResource(R.string.advisor_eval_partial_message),
                    color = DarkGrey,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    if (evaluation.companyResponsibleGrade != null) {
        EvaluationReadOnlyCard(
            role = stringResource(R.string.eval_role_company_responsible),
            name = evaluation.companyResponsibleName,
            grade = evaluation.companyResponsibleGrade,
            observation = evaluation.companyResponsibleObservation,
        )
    }
    if (evaluation.companyMentorGrade != null) {
        EvaluationReadOnlyCard(
            role = stringResource(R.string.eval_role_company_mentor),
            name = evaluation.companyMentorName,
            grade = evaluation.companyMentorGrade,
            observation = evaluation.companyMentorObservation,
        )
    }
}

@Composable
private fun ReadyForFinalContent(
    student: AdminStudent,
    evaluation: InternshipEvaluation,
    navController: NavController,
    orientadorStudentDetailViewModel: OrientadorStudentDetailViewModel,
) {
    var observation by remember { mutableStateOf("") }
    var gradeText by remember { mutableStateOf("") }
    var gradeError by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val parsedGrade = validateGrade(gradeText)
    val submitGradeState by orientadorStudentDetailViewModel.submitGradeState.collectAsState()

    LaunchedEffect(submitGradeState) {
        if (submitGradeState is SubmitFinalGradeUiState.Success) {
            orientadorStudentDetailViewModel.resetSubmitGradeState()
            navController.navigate(OrientadorRoutes.finalGradeSubmittedRoute(evaluation.internshipId))
        }
    }

    if (showConfirmDialog && parsedGrade != null) {
        ConfirmationDialog(
            title = stringResource(R.string.advisor_eval_assign_title),
            body = stringResource(R.string.advisor_eval_assign_body, formatGrade(parsedGrade)),
            confirmLabel = stringResource(R.string.advisor_eval_assign_button),
            isDanger = false,
            onConfirm = {
                showConfirmDialog = false
                orientadorStudentDetailViewModel.submitFinalGrade(
                    internshipId = evaluation.internshipId,
                    grade = parsedGrade.toDouble(),
                    comment = observation,
                )
            },
            onDismiss = { showConfirmDialog = false },
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Green.copy(alpha = 0.08f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Green.copy(alpha = 0.5f)),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = Green,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.advisor_eval_ready_message),
                color = DarkBlue,
                fontSize = 13.sp,
                lineHeight = 19.sp,
            )
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    when (evaluation.internshipType) {
        InternshipType.COMPANY_SCHOOL -> {
            EvaluationReadOnlyCard(
                role = stringResource(R.string.eval_role_company_responsible),
                name = evaluation.companyResponsibleName,
                grade = evaluation.companyResponsibleGrade!!,
                observation = evaluation.companyResponsibleObservation,
            )
            EvaluationReadOnlyCard(
                role = stringResource(R.string.eval_role_company_mentor),
                name = evaluation.companyMentorName,
                grade = evaluation.companyMentorGrade!!,
                observation = evaluation.companyMentorObservation,
            )
        }
        InternshipType.SCHOOL_ONLY -> {
            EvaluationReadOnlyCard(
                role = stringResource(R.string.eval_role_institution),
                name = evaluation.institutionName,
                grade = evaluation.institutionGrade!!,
                observation = evaluation.institutionObservation,
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionLabel(stringResource(R.string.advisor_eval_final_observation))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = observation,
            onValueChange = { observation = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.common_write_here), color = DarkGrey) },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LightBlue,
                unfocusedBorderColor = BorderGrey,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
            ),
            minLines = 4,
            maxLines = 8,
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionLabel(stringResource(R.string.advisor_eval_final_grade_label))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = gradeText,
            onValueChange = {
                gradeText = it
                gradeError = false
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.advisor_eval_grade_placeholder), color = DarkGrey) },
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
                text = stringResource(R.string.advisor_eval_grade_error),
                color = Red,
                fontSize = 12.sp,
            )
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    LinkStageButton(
        text = stringResource(R.string.advisor_eval_assign_grade),
        onClick = {
            if (validateGrade(gradeText) == null) {
                gradeError = true
            } else {
                showConfirmDialog = true
            }
        },
        modifier = Modifier.padding(horizontal = 16.dp),
        height = 48.dp,
        brush = Fade1,
    )

    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun CompletedStateContent(evaluation: InternshipEvaluation) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Green.copy(alpha = 0.08f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Green.copy(alpha = 0.5f)),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = Green,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.advisor_eval_completed, formatGrade(evaluation.schoolMentorGrade!!)),
                color = DarkBlue,
                fontSize = 13.sp,
                lineHeight = 19.sp,
            )
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    when (evaluation.internshipType) {
        InternshipType.COMPANY_SCHOOL -> {
            if (evaluation.companyResponsibleGrade != null) {
                EvaluationReadOnlyCard(
                    role = stringResource(R.string.eval_role_company_responsible),
                    name = evaluation.companyResponsibleName,
                    grade = evaluation.companyResponsibleGrade,
                    observation = evaluation.companyResponsibleObservation,
                )
            }
            if (evaluation.companyMentorGrade != null) {
                EvaluationReadOnlyCard(
                    role = stringResource(R.string.eval_role_company_mentor),
                    name = evaluation.companyMentorName,
                    grade = evaluation.companyMentorGrade,
                    observation = evaluation.companyMentorObservation,
                )
            }
        }
        InternshipType.SCHOOL_ONLY -> {
            if (evaluation.institutionGrade != null) {
                EvaluationReadOnlyCard(
                    role = stringResource(R.string.eval_role_institution),
                    name = evaluation.institutionName,
                    grade = evaluation.institutionGrade,
                    observation = evaluation.institutionObservation,
                )
            }
        }
    }

    EvaluationReadOnlyCard(
        role = stringResource(R.string.eval_role_school_mentor_short),
        name = evaluation.schoolMentorName,
        grade = evaluation.schoolMentorGrade!!,
        observation = evaluation.schoolMentorObservation,
    )
}

// endregion

// region Shared — ExpandableFileRow

@Composable
fun ExpandableFileRow(file: turmaA.grupoB.LinkStage.ui.aluno.activity.CheckpointFile) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Outlined.Description,
                contentDescription = null,
                tint = DarkBlue,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = file.name,
                color = DarkBlue,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f),
            )
            Icon(
                if (expanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                tint = DarkGrey,
                modifier = Modifier.size(18.dp),
            )
        }
        AnimatedVisibility(visible = expanded) {
            Box(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .height(36.dp)
                    .width(140.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Fade2)
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.common_download), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
        HorizontalDivider(color = BorderGrey)
    }
}

// endregion

@Preview(showSystemUi = true)
@Composable
private fun MentorStudentDetailScreenPreview() {
    MaterialTheme {
        MentorStudentDetailScreen(studentId = "s1", navController = rememberNavController())
    }
}

@Preview(showSystemUi = true, device = "spec:width=411dp,height=891dp,orientation=landscape")
@Composable
private fun MentorStudentDetailScreenLandscapePreview() {
    MaterialTheme {
        MentorStudentDetailScreen(studentId = "s1", navController = rememberNavController())
    }
}
