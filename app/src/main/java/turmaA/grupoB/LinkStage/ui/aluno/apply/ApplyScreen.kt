package turmaA.grupoB.LinkStage.ui.aluno.apply

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Grade
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.data.remote.model.application.CreateApplicationInput
import turmaA.grupoB.LinkStage.data.repository.application.ApplicationRepository
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepository
import turmaA.grupoB.LinkStage.data.repository.student.StudentRepository
import turmaA.grupoB.LinkStage.data.repository.storage.StorageRepository
import turmaA.grupoB.LinkStage.ui.common.LinkStageButton
import turmaA.grupoB.LinkStage.ui.common.SecondaryTopBar
import turmaA.grupoB.LinkStage.ui.common.LinkStageDialog
import turmaA.grupoB.LinkStage.ui.common.SectionLabel
import turmaA.grupoB.LinkStage.ui.common.readUploadFile
import turmaA.grupoB.LinkStage.ui.common.safeUploadFileName
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.BorderGrey
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.ui.theme.Red
import turmaA.grupoB.LinkStage.viewmodel.apply.ApplyViewModel
import turmaA.grupoB.LinkStage.viewmodel.application.ApplicationUiState
import turmaA.grupoB.LinkStage.viewmodel.application.ApplicationViewModel
import turmaA.grupoB.LinkStage.viewmodel.application.ApplicationViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.auth.AuthUiState
import turmaA.grupoB.LinkStage.viewmodel.auth.AuthViewModel
import turmaA.grupoB.LinkStage.viewmodel.auth.AuthViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.student.StudentUiState
import turmaA.grupoB.LinkStage.viewmodel.student.StudentViewModel
import turmaA.grupoB.LinkStage.viewmodel.student.StudentViewModelFactory

// region Main Screen

@Composable
fun ApplyScreen(
    offerId: String,
    offerTitle: String = "UI/UX Designer",
    offerCompany: String = "Viana S.T.Arts",
    offerLogoInitial: String = "V",
    offerLogoColor: Color = Color(0xFF212121),
    viewModel: ApplyViewModel,
    onBack: () -> Unit,
    onNavigateToEditSkills: () -> Unit,
    onSubmitSuccess: () -> Unit,
    authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(AuthRepository())
    ),
    studentViewModel: StudentViewModel = viewModel(
        factory = StudentViewModelFactory(StudentRepository())
    ),
    applicationViewModel: ApplicationViewModel = viewModel(
        factory = ApplicationViewModelFactory(
            ApplicationRepository(),
            StorageRepository(),
        )
    ),
) {
    val currentStep = viewModel.currentStep
    var showCvIncompleteDialog by remember { mutableStateOf(false) }
    val authUiState by authViewModel.uiState.collectAsState()
    val studentUiState by studentViewModel.uiState.collectAsState()
    val applicationUiState by applicationViewModel.uiState.collectAsState()
    val isSubmittingApplication = applicationUiState is ApplicationUiState.Loading

    // Step 0 validation
    var nameError by rememberSaveable { mutableStateOf(false) }
    var emailError by rememberSaveable { mutableStateOf(false) }
    var phoneError by rememberSaveable { mutableStateOf(false) }
    var courseError by rememberSaveable { mutableStateOf(false) }
    var institutionError by rememberSaveable { mutableStateOf(false) }
    var gpaError by rememberSaveable { mutableStateOf(false) }

    var currentStudentId by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        authViewModel.loadCurrentUserProfile()
    }

    LaunchedEffect(authUiState) {
        val state = authUiState

        if (state is AuthUiState.Success) {
            viewModel.applyProfileData(state.profile)
            studentViewModel.loadStudentByUserId(state.profile.id)
        }
    }

    LaunchedEffect(studentUiState) {
        val state = studentUiState

        if (state is StudentUiState.Success) {
            currentStudentId = state.student.id
            viewModel.applyStudentData(state.student)
        }
    }

    LaunchedEffect(applicationUiState) {
        val state = applicationUiState

        if (state is ApplicationUiState.Success) {
            applicationViewModel.resetState()
            onSubmitSuccess()
        }
    }

    if (showCvIncompleteDialog) {
        LinkStageDialog(
            title = stringResource(R.string.apply_cv_incomplete_title),
            onConfirm = { showCvIncompleteDialog = false },
            onDismiss = { showCvIncompleteDialog = false },
            confirmText = stringResource(R.string.common_back),
            content = {
                Text(
                    stringResource(R.string.apply_cv_incomplete_message),
                    color = DarkGrey,
                    lineHeight = 22.sp,
                )
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
    ) {
        // Header
        ApplyHeader(
            offerTitle = offerTitle,
            offerCompany = offerCompany,
            offerLogoInitial = offerLogoInitial,
            offerLogoColor = offerLogoColor,
            onBack = onBack,
        )

        // Stepper
        ApplyStepper(currentStep = currentStep)

        Spacer(modifier = Modifier.height(4.dp))

        // Step title + subtitle
        val (stepTitle, stepSubtitle) = when (currentStep) {
            0 -> stringResource(R.string.apply_step0_title) to stringResource(R.string.apply_step0_subtitle)
            1 -> stringResource(R.string.apply_step1_title) to stringResource(R.string.apply_step1_subtitle)
            else -> stringResource(R.string.apply_step2_title) to stringResource(R.string.apply_step2_subtitle)
        }
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(text = stepTitle, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
            Text(text = stepSubtitle, fontSize = 13.sp, color = DarkGrey)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Step content
        AnimatedContent(
            targetState = currentStep,
            modifier = Modifier.weight(1f),
            transitionSpec = {
                (slideInHorizontally { if (targetState > initialState) it else -it } + fadeIn())
                    .togetherWith(slideOutHorizontally { if (targetState > initialState) -it else it } + fadeOut())
            },
            label = "step",
        ) { step ->
            when (step) {
                0 -> StepPersonalInfo(
                    viewModel = viewModel,
                    nameError = nameError,
                    emailError = emailError,
                    phoneError = phoneError,
                    courseError = courseError,
                    institutionError = institutionError,
                    gpaError = gpaError,
                )
                1 -> StepEssentialInfo(
                    viewModel = viewModel,
                    onEditSkills = onNavigateToEditSkills,
                )
                2 -> StepReview(viewModel = viewModel)
            }
        }

        // Bottom bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LinkStageButton(
                text = when {
                    currentStep == 2 && isSubmittingApplication -> stringResource(R.string.apply_submitting)
                    currentStep == 2 -> stringResource(R.string.apply_submit)
                    else -> stringResource(R.string.apply_continue)
                },
                onClick = {
                    when (currentStep) {
                        0 -> {
                            nameError = viewModel.fullName.isBlank()
                            emailError = viewModel.email.isBlank()
                            phoneError = viewModel.phone.isBlank()
                            courseError = viewModel.course.isBlank()
                            institutionError = viewModel.institution.isBlank()
                            gpaError = viewModel.gpa.isBlank()
                            val valid = !nameError && !emailError && !phoneError &&
                                !courseError && !institutionError && !gpaError
                            if (valid) viewModel.currentStep = 1
                        }
                        1 -> {
                            if (!viewModel.isCvComplete) {
                                showCvIncompleteDialog = true
                            } else {
                                viewModel.currentStep = 2
                            }
                        }
                        2 -> {
                            val studentId = currentStudentId

                            if (studentId != null) {
                                val input = CreateApplicationInput(
                                    offerId = offerId,
                                    studentId = studentId,
                                )
                                val motivationFileBytes = viewModel.motivationFileBytes

                                if (motivationFileBytes != null) {
                                    val path = "students/$studentId/applications/$offerId/" +
                                        "motivation-letter/${System.currentTimeMillis()}_" +
                                        safeUploadFileName(viewModel.motivationFileName)
                                    applicationViewModel.createApplication(
                                        input = input,
                                        motivationLetterPath = path,
                                        motivationLetterBytes = motivationFileBytes,
                                    )
                                } else {
                                    applicationViewModel.createApplication(input)
                                }
                            }
                        }
                    }
                },
                enabled = !isSubmittingApplication
            )

            TextButton(
                onClick = {
                    viewModel.saveAsDraft()
                    onBack()
                },
            ) {
                Text(stringResource(R.string.apply_save_draft), color = DarkGrey, fontSize = 13.sp)
            }
        }
    }
}

// endregion

// region Header & Stepper

@Composable
private fun ApplyHeader(
    offerTitle: String,
    offerCompany: String,
    offerLogoInitial: String,
    offerLogoColor: Color,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(bottom = 12.dp),
    ) {
        SecondaryTopBar(
            title = stringResource(R.string.apply_title),
            onBack = onBack
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(BackgroundLight)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(offerLogoColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(offerLogoInitial, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(offerTitle, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
                Text(offerCompany, fontSize = 13.sp, color = LightBlue)
            }
        }
    }
}

@Composable
private fun ApplyStepper(currentStep: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 40.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        for (i in 0..2) {
            val isCompleted = i < currentStep
            val isActive = i == currentStep

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> LightBlue
                            isActive -> DarkBlue
                            else -> BorderGrey
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (isCompleted) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                } else {
                    Text(
                        "${i + 1}",
                        color = if (isActive) Color.White else DarkGrey,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            if (i < 2) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .background(if (isCompleted) LightBlue else BorderGrey),
                )
            }
        }
    }
}

// endregion

// region Step 0 — Personal Info

@Composable
private fun StepPersonalInfo(
    viewModel: ApplyViewModel,
    nameError: Boolean,
    emailError: Boolean,
    phoneError: Boolean,
    courseError: Boolean,
    institutionError: Boolean,
    gpaError: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        ApplyTextField(
            label = stringResource(R.string.apply_name),
            value = viewModel.fullName,
            onValueChange = { viewModel.fullName = it },
            icon = Icons.Outlined.Person,
            isError = nameError,
        )
        ApplyTextField(
            label = stringResource(R.string.apply_email),
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            icon = Icons.Outlined.Email,
            keyboardType = KeyboardType.Email,
            isError = emailError,
        )
        ApplyTextField(
            label = stringResource(R.string.apply_phone),
            value = viewModel.phone,
            onValueChange = { viewModel.phone = it },
            icon = Icons.Outlined.Phone,
            keyboardType = KeyboardType.Phone,
            isError = phoneError,
        )
        ApplyTextField(
            label = stringResource(R.string.apply_course),
            value = viewModel.course,
            onValueChange = { viewModel.course = it },
            icon = Icons.AutoMirrored.Outlined.MenuBook,
            isError = courseError,
        )
        ApplyTextField(
            label = stringResource(R.string.apply_institution),
            value = viewModel.institution,
            onValueChange = { viewModel.institution = it },
            icon = Icons.Outlined.School,
            isError = institutionError,
        )
        ApplyTextField(
            label = stringResource(R.string.apply_gpa),
            value = viewModel.gpa,
            onValueChange = { viewModel.gpa = it },
            icon = Icons.Outlined.Grade,
            keyboardType = KeyboardType.Decimal,
            isError = gpaError,
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun ApplyTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        SectionLabel(label)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(icon, contentDescription = null, tint = DarkGrey) },
            placeholder = { Text(stringResource(R.string.common_write_here), color = DarkGrey, fontSize = 14.sp) },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isError) Red else LightBlue,
                unfocusedBorderColor = if (isError) Red else BorderGrey,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            isError = isError,
        )
        if (isError) {
            Text(stringResource(R.string.common_required_field), color = Red, fontSize = 12.sp)
        }
    }
}

// endregion

// region Step 1 — Essential Info

@Composable
private fun StepEssentialInfo(
    viewModel: ApplyViewModel,
    onEditSkills: () -> Unit,
) {
    val context = LocalContext.current
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        viewModel.motivationFile = null
        viewModel.motivationFileName = ""
        viewModel.motivationFileBytes = null
        if (uri != null) {
            context.contentResolver.readUploadFile(uri)?.let { selectedFile ->
                viewModel.motivationFile = uri
                viewModel.motivationFileName = selectedFile.name
                viewModel.motivationFileBytes = selectedFile.bytes
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Personal Statement
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SectionLabel(stringResource(R.string.apply_personal_statement))
            Text(
                stringResource(R.string.apply_personal_statement_desc),
                fontSize = 12.sp,
                color = DarkGrey,
            )
            OutlinedTextField(
                value = viewModel.personalStatement,
                onValueChange = { viewModel.personalStatement = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.common_write_here), color = DarkGrey, fontSize = 14.sp) },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LightBlue,
                    unfocusedBorderColor = BorderGrey,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                ),
                minLines = 5,
            )
        }

        // Edit Skills
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SectionLabel(stringResource(R.string.apply_skills_label))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGrey, RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .clickable { onEditSkills() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(stringResource(R.string.apply_skills_label), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkBlue)
                    Text(
                        "${viewModel.userSkills.size} ${stringResource(R.string.apply_skills_count)}",
                        fontSize = 12.sp,
                        color = if (viewModel.isCvComplete) LightBlue else DarkGrey,
                    )
                }
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = DarkGrey,
                )
            }
        }

        // Motivation Letter
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SectionLabel(stringResource(R.string.apply_motivation_letter))
            if (viewModel.motivationFile != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, LightBlue, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Description, contentDescription = null, tint = LightBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            viewModel.motivationFileName,
                            fontSize = 13.sp,
                            color = DarkBlue,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    IconButton(
                        onClick = {
                            viewModel.motivationFile = null
                            viewModel.motivationFileName = ""
                            viewModel.motivationFileBytes = null
                        },
                    ) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.common_remove), tint = DarkGrey)
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { filePickerLauncher.launch("application/*") },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderGrey),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            Icons.Outlined.Description,
                            contentDescription = null,
                            tint = DarkGrey,
                            modifier = Modifier.size(48.dp),
                        )
                        Text(stringResource(R.string.apply_select_file), fontWeight = FontWeight.Bold, color = DarkBlue, fontSize = 14.sp)
                        Text(stringResource(R.string.apply_file_format), color = DarkGrey, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

// endregion

// region Step 2 — Review

@Composable
private fun StepReview(viewModel: ApplyViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        ReviewRow(label = stringResource(R.string.apply_review_name), value = viewModel.fullName)
        ReviewRow(label = stringResource(R.string.apply_email), value = viewModel.email)
        ReviewRow(
            label = stringResource(R.string.apply_review_course),
            value = "${viewModel.course} - ${viewModel.institution}",
        )
        ReviewRow(label = stringResource(R.string.apply_review_gpa), value = "${viewModel.gpa} ${stringResource(R.string.common_values)}")
        ReviewRow(
            label = "CV",
            value = if (viewModel.isCvComplete) stringResource(R.string.apply_review_complete) else stringResource(R.string.apply_review_incomplete),
            valueColor = if (viewModel.isCvComplete) LightBlue else Red,
        )
        ReviewRow(
            label = stringResource(R.string.apply_motivation_letter),
            value = if (viewModel.isMotivationComplete) stringResource(R.string.apply_review_complete) else stringResource(R.string.apply_review_not_attached),
            valueColor = if (viewModel.isMotivationComplete) LightBlue else DarkGrey,
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ReviewRow(
    label: String,
    value: String,
    valueColor: Color = DarkBlue,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                color = DarkGrey,
                modifier = Modifier.weight(0.4f),
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = valueColor,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(0.6f),
            )
        }
        HorizontalDivider(color = BorderGrey)
    }
}

// endregion

// region Preview

@Preview(showSystemUi = true)
@Suppress("ViewModelConstructorInComposable")
@Composable
private fun ApplyScreenPreview() {
    MaterialTheme {
        ApplyScreen(
            offerId = "2",
            viewModel = ApplyViewModel(),
            onBack = {},
            onNavigateToEditSkills = {},
            onSubmitSuccess = {},
        )
    }
}

// endregion
