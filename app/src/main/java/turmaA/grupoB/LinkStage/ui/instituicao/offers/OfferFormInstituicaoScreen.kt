package turmaA.grupoB.LinkStage.ui.instituicao.offers

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepository
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepository
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepository
import turmaA.grupoB.LinkStage.ui.admin.sampleMentors
import androidx.compose.ui.platform.LocalContext
import turmaA.grupoB.LinkStage.ui.aluno.apply.getSkillCategories
import turmaA.grupoB.LinkStage.ui.common.CommonTopBar
import turmaA.grupoB.LinkStage.ui.common.ConfirmationDialog
import turmaA.grupoB.LinkStage.ui.common.LinkStageButton
import turmaA.grupoB.LinkStage.ui.common.LinkStageOutlinedButton
import turmaA.grupoB.LinkStage.ui.common.LinkStageTabRow
import turmaA.grupoB.LinkStage.ui.common.SectionLabel
import turmaA.grupoB.LinkStage.ui.instituicao.InstituicaoRoutes
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.BorderGrey
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.ui.theme.Red
import turmaA.grupoB.LinkStage.viewmodel.offerform.OfferFormUiState
import turmaA.grupoB.LinkStage.viewmodel.offerform.OfferFormViewModel
import turmaA.grupoB.LinkStage.viewmodel.offerform.OfferFormViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferFormInstituicaoScreen(
    offerId: String,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: OfferFormViewModel = viewModel(
        factory = OfferFormViewModelFactory(
            offerRepository = OfferRepository(),
            institutionRepository = InstitutionRepository(),
            authRepository = AuthRepository(),
        )
    ),
) {
    val context = LocalContext.current
    val currentStep = viewModel.currentStep
    val formUiState by viewModel.uiState.collectAsState()

    // Validation errors
    var titleError by rememberSaveable { mutableStateOf(false) }
    var categoryError by rememberSaveable { mutableStateOf(false) }
    var descriptionError by rememberSaveable { mutableStateOf(false) }
    var locationError by rememberSaveable { mutableStateOf(false) }
    var deadlineError by rememberSaveable { mutableStateOf(false) }

    var showPublishDialog by remember { mutableStateOf(false) }

    LaunchedEffect(offerId) {
        viewModel.loadOfferForEdit(offerId)
    }

    LaunchedEffect(formUiState) {
        val state = formUiState
        if (state is OfferFormUiState.Success) {
            viewModel.resetState()
            navController.navigate(InstituicaoRoutes.offerSuccessRoute(state.offerId, isNew = offerId == "new")) {
                popUpTo(InstituicaoRoutes.offerFormRoute(offerId)) { inclusive = true }
            }
        }
    }

    if (showPublishDialog) {
        ConfirmationDialog(
            title = if (offerId == "new") stringResource(R.string.offer_form_publish_title) else stringResource(R.string.offer_form_save_title),
            body = if (offerId == "new")
                stringResource(R.string.offer_form_publish_body)
            else stringResource(R.string.offer_form_edit_body),
            confirmLabel = if (offerId == "new") stringResource(R.string.offer_form_publish_button) else stringResource(R.string.common_save),
            onConfirm = {
                showPublishDialog = false
                viewModel.submitOffer(offerId)
            },
            onDismiss = { showPublishDialog = false },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BackgroundLight,
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                // Top Row: Back + Title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 20.dp, top = 8.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.common_back_content_desc),
                            tint = DarkBlue
                        )
                    }
                    Text(
                        text = if (offerId == "new") stringResource(R.string.offer_form_create_title) else stringResource(R.string.offer_form_edit_title),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkBlue,
                            fontSize = 20.sp
                        )
                    )
                }

                // Stepper
                OfferFormStepper(currentStep = currentStep)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Step title + subtitle
            val (stepTitle, stepSubtitle) = when (currentStep) {
                0 -> stringResource(R.string.offer_form_step0_title) to stringResource(R.string.offer_form_step0_subtitle)
                1 -> stringResource(R.string.offer_form_step1_title) to stringResource(R.string.offer_form_step1_subtitle)
                else -> stringResource(R.string.offer_form_step2_title) to stringResource(R.string.offer_form_step2_subtitle)
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
                label = "offer_step",
            ) { step ->
                when (step) {
                    0 -> StepDetails(
                        viewModel = viewModel,
                        titleError = titleError,
                        categoryError = categoryError,
                        descriptionError = descriptionError,
                    )
                    1 -> StepRequirements(
                        viewModel = viewModel,
                        locationError = locationError,
                        deadlineError = deadlineError,
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
                if (formUiState is OfferFormUiState.Error) {
                    Text(
                        text = (formUiState as OfferFormUiState.Error).message,
                        color = Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }

                LinkStageButton(
                    text = if (currentStep == 2) stringResource(R.string.offer_form_publish_offer) else stringResource(R.string.offer_form_next),
                    enabled = formUiState !is OfferFormUiState.Loading,
                    onClick = {
                        when (currentStep) {
                            0 -> {
                                titleError = viewModel.title.isBlank()
                                categoryError = viewModel.category.isBlank()
                                descriptionError = viewModel.description.isBlank()
                                if (!titleError && !categoryError && !descriptionError) {
                                    viewModel.currentStep = 1
                                }
                            }
                            1 -> {
                                locationError = viewModel.location.isBlank()
                                deadlineError = viewModel.deadline.isBlank() || !viewModel.isValidDate(viewModel.deadline)
                                if (!locationError && !deadlineError) {
                                    viewModel.currentStep = 2
                                }
                            }
                            2 -> {
                                showPublishDialog = true
                            }
                        }
                    },
                    height = 50.dp,
                )

                Spacer(modifier = Modifier.height(12.dp))

                LinkStageOutlinedButton(
                    text = stringResource(R.string.common_back),
                    onClick = {
                        if (currentStep == 0) {
                            navController.popBackStack()
                        } else {
                            viewModel.currentStep = currentStep - 1
                        }
                    },
                    height = 50.dp,
                )
            }
        }
    }
}

// region Stepper

@Composable
private fun OfferFormStepper(currentStep: Int) {
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
                            else -> Color(0xFFE0E0E0)
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (isCompleted) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp),
                    )
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
                        .height(2.dp)
                        .background(if (isCompleted) LightBlue else Color(0xFFE0E0E0)),
                )
            }
        }
    }
}

// endregion

// region Step 0 — Detalhes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StepDetails(
    viewModel: OfferFormViewModel,
    titleError: Boolean,
    categoryError: Boolean,
    descriptionError: Boolean,
) {
    val context = LocalContext.current
    val categoryOptions = getSkillCategories().keys.sorted()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Tipo de estágio (escola vs empresa)
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SectionLabel(stringResource(R.string.offer_form_internship_type))
            LinkStageTabRow(
                tabs = listOf(stringResource(R.string.offer_form_type_school), stringResource(R.string.offer_form_type_company)),
                selectedIndex = if (viewModel.isCompanyOffer) 1 else 0,
                onTabSelected = { viewModel.isCompanyOffer = it == 1 }
            )
            Text(
                text = if (viewModel.isCompanyOffer)
                    stringResource(R.string.offer_form_company_hint)
                else
                    stringResource(R.string.offer_form_school_hint),
                color = DarkGrey, fontSize = 12.sp,
            )
        }

        // Orientador escolar
        OfferDropdown(
            label = stringResource(R.string.offer_form_school_mentor),
            value = viewModel.schoolMentorName,
            placeholder = stringResource(R.string.offer_form_school_mentor_placeholder),
            options = sampleMentors(context).map { it.name },
            onOptionSelected = { name ->
                viewModel.schoolMentorName = name
                viewModel.schoolMentorId = sampleMentors(context).find { it.name == name }?.id ?: ""
            },
        )

        // Orientador da empresa (só visível se for estágio empresa)
        if (viewModel.isCompanyOffer) {
            OfferTextField(
                label = stringResource(R.string.offer_form_company_mentor),
                value = viewModel.companyMentorName,
                onValueChange = { viewModel.companyMentorName = it },
                placeholder = stringResource(R.string.offer_form_company_mentor_placeholder),
            )
        }

        // Título
        OfferTextField(
            label = stringResource(R.string.offer_form_title_label),
            value = viewModel.title,
            onValueChange = { viewModel.title = it },
            placeholder = stringResource(R.string.offer_form_title_placeholder),
            isError = titleError,
        )

        // Categoria
        OfferDropdown(
            label = stringResource(R.string.offer_form_category),
            value = viewModel.category,
            placeholder = stringResource(R.string.offer_form_category_placeholder),
            options = categoryOptions,
            onOptionSelected = { viewModel.category = it },
            isError = categoryError,
        )

        // Descrição
        OfferTextField(
            label = stringResource(R.string.offer_form_description),
            value = viewModel.description,
            onValueChange = { viewModel.description = it },
            placeholder = stringResource(R.string.offer_form_description_placeholder),
            singleLine = false,
            minLines = 4,
            maxLines = 8,
            isError = descriptionError,
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

// endregion

// region Step 1 — Requisitos e Logística

@Composable
private fun StepRequirements(
    viewModel: OfferFormViewModel,
    locationError: Boolean,
    deadlineError: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        // Requisitos
        OfferTextField(
            label = stringResource(R.string.offer_form_requirements),
            value = viewModel.requirements,
            onValueChange = { viewModel.requirements = it },
            placeholder = stringResource(R.string.offer_form_requirements_placeholder),
            singleLine = false,
            minLines = 4,
            maxLines = 8,
        )

        // Local
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SectionLabel(stringResource(R.string.offer_form_location))
            OutlinedTextField(
                value = viewModel.location,
                onValueChange = { viewModel.location = it },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = DarkGrey,
                        modifier = Modifier.size(18.dp),
                    )
                },
                placeholder = { Text(stringResource(R.string.offer_form_location_placeholder), color = DarkGrey, fontSize = 14.sp) },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (locationError) Red else LightBlue,
                    unfocusedBorderColor = if (locationError) Red else BorderGrey,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = DarkBlue,
                    unfocusedTextColor = DarkBlue,
                ),
                singleLine = true,
                isError = locationError,
            )
            if (locationError) {
                Text(stringResource(R.string.common_required_field), color = Red, fontSize = 12.sp)
            }
        }

        // Data de fecho
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SectionLabel(stringResource(R.string.offer_form_deadline))
            OutlinedTextField(
                value = viewModel.deadline,
                onValueChange = { newValue ->
                    val digits = newValue.filter { it.isDigit() }
                    if (digits.length <= 8) {
                        val formatted = buildString {
                            digits.forEachIndexed { index, c ->
                                append(c)
                                if ((index == 1 || index == 3) && index < digits.lastIndex) {
                                    append('/')
                                }
                            }
                        }
                        viewModel.deadline = formatted
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = DarkGrey,
                        modifier = Modifier.size(18.dp),
                    )
                },
                placeholder = { Text(stringResource(R.string.checkpoint_deadline_placeholder), color = DarkGrey, fontSize = 14.sp) },
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (deadlineError) Red else LightBlue,
                    unfocusedBorderColor = if (deadlineError) Red else BorderGrey,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = DarkBlue,
                    unfocusedTextColor = DarkBlue,
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = deadlineError,
            )
            if (deadlineError) {
                Text(stringResource(R.string.offer_form_deadline_error), color = Red, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

// endregion

// region Step 2 — Rever

@Composable
private fun StepReview(viewModel: OfferFormViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        ReviewRow(label = stringResource(R.string.offer_form_title_label), value = viewModel.title)
        ReviewRow(
            label = stringResource(R.string.offer_form_review_type),
            value = if (viewModel.isCompanyOffer) stringResource(R.string.offer_form_type_company) else stringResource(R.string.offer_form_type_school),
        )
        ReviewRow(
            label = stringResource(R.string.offer_form_school_mentor),
            value = viewModel.schoolMentorName.ifBlank { stringResource(R.string.offer_form_not_assigned) },
            valueColor = if (viewModel.schoolMentorName.isBlank()) DarkGrey else DarkBlue,
        )
        if (viewModel.isCompanyOffer) {
            ReviewRow(
                label = stringResource(R.string.offer_form_company_mentor_short),
                value = viewModel.companyMentorName.ifBlank { stringResource(R.string.offer_form_not_assigned) },
                valueColor = if (viewModel.companyMentorName.isBlank()) DarkGrey else DarkBlue,
            )
        }
        ReviewRow(label = stringResource(R.string.offer_form_category), value = viewModel.category)
        ReviewRow(label = stringResource(R.string.offer_form_location), value = viewModel.location)
        ReviewRow(label = stringResource(R.string.offer_form_review_deadline), value = viewModel.deadline)
        ReviewRow(
            label = stringResource(R.string.offer_form_review_requirements),
            value = if (viewModel.requirements.isNotBlank()) stringResource(R.string.common_complete) else stringResource(R.string.offer_form_no_info),
            valueColor = if (viewModel.requirements.isNotBlank()) LightBlue else DarkGrey,
        )
        ReviewRow(
            label = stringResource(R.string.offer_form_description),
            value = stringResource(R.string.common_complete),
            valueColor = LightBlue,
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
        androidx.compose.material3.HorizontalDivider(color = BorderGrey)
    }
}

// endregion

// region Reusable form fields

@Composable
private fun OfferTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1,
    isError: Boolean = false,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        SectionLabel(label)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = DarkGrey, fontSize = 14.sp) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isError) Red else LightBlue,
                unfocusedBorderColor = if (isError) Red else BorderGrey,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = DarkBlue,
                unfocusedTextColor = DarkBlue,
            ),
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            isError = isError,
        )
        if (isError) {
            Text(stringResource(R.string.common_required_field), color = Red, fontSize = 12.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OfferDropdown(
    label: String,
    value: String,
    placeholder: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    isError: Boolean = false,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        SectionLabel(label)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(placeholder, color = DarkGrey, fontSize = 14.sp) },
                trailingIcon = {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = DarkGrey)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = if (isError) Red else LightBlue,
                    unfocusedBorderColor = if (isError) Red else BorderGrey,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = DarkBlue,
                    unfocusedTextColor = DarkBlue,
                ),
                singleLine = true,
                isError = isError,
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, color = DarkBlue) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        },
                    )
                }
            }
        }
        if (isError) {
            Text(stringResource(R.string.common_required_field), color = Red, fontSize = 12.sp)
        }
    }
}

// endregion

// region Previews

@Preview(showSystemUi = true)
@Composable
private fun OfferFormInstituicaoScreenPreview() {
    MaterialTheme {
        OfferFormInstituicaoScreen(offerId = "new", navController = rememberNavController())
    }
}

@Preview(showSystemUi = true, device = "spec:width=411dp,height=891dp,orientation=landscape")
@Composable
private fun OfferFormInstituicaoScreenLandscapePreview() {
    MaterialTheme {
        OfferFormInstituicaoScreen(offerId = "new", navController = rememberNavController())
    }
}

// endregion
