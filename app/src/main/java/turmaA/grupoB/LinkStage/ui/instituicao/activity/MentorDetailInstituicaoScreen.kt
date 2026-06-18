package turmaA.grupoB.LinkStage.ui.instituicao.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.admin.AdminMentor
import turmaA.grupoB.LinkStage.ui.admin.sampleMentors
import turmaA.grupoB.LinkStage.ui.common.ConfirmationDialog
import turmaA.grupoB.LinkStage.ui.common.LinkStageTabRow
import turmaA.grupoB.LinkStage.ui.common.SectionLabel
import turmaA.grupoB.LinkStage.ui.instituicao.InstituicaoRoutes
import androidx.compose.ui.platform.LocalContext
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.BorderGrey
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.Fade1
import turmaA.grupoB.LinkStage.ui.theme.Fade2
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.viewmodel.instituicao.activity.InstitutionActivityUiState
import turmaA.grupoB.LinkStage.viewmodel.instituicao.activity.InstitutionActivityViewModel
import turmaA.grupoB.LinkStage.viewmodel.instituicao.activity.InstitutionActivityViewModelFactory

@Composable
fun MentorDetailInstituicaoScreen(
    mentorId: String,
    navController: NavController,
    modifier: Modifier = Modifier,
    activityViewModel: InstitutionActivityViewModel = viewModel(factory = InstitutionActivityViewModelFactory()),
) {
    val context = LocalContext.current
    val activityUiState by activityViewModel.uiState.collectAsState()
    val mentor = (activityUiState as? InstitutionActivityUiState.Success)?.data?.adminMentors?.find { it.id == mentorId }
        ?: sampleMentors(context).find { it.id == mentorId } ?: sampleMentors(context).first()
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        activityViewModel.loadActivityForCurrentInstitution()
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
                        text = stringResource(R.string.admin_mentors_title),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkBlue,
                            fontSize = 20.sp
                        )
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BackgroundLight)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Fade1),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(mentor.avatarInitials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(mentor.name, color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(mentor.institution, color = LightBlue, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }
                }

                LinkStageTabRow(
                    tabs = listOf(stringResource(R.string.mentor_detail_tab_details), stringResource(R.string.mentor_detail_tab_skills), stringResource(R.string.mentor_detail_tab_manage)),
                    selectedIndex = selectedTab,
                    onTabSelected = { selectedTab = it },
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> DetailsTab(mentor = mentor)
                1 -> SkillsTab(mentor = mentor)
                2 -> ManageTab(mentor = mentor, navController = navController)
            }
        }
    }
}

// region Tab 0 — Detalhes

@Composable
private fun DetailsTab(mentor: AdminMentor) {
    var isEditing by remember { mutableStateOf(false) }
    var noteText by rememberSaveable { mutableStateOf(mentor.internalNote) }
    var showSaveDialog by remember { mutableStateOf(false) }

    if (showSaveDialog) {
        ConfirmationDialog(
            title = stringResource(R.string.mentor_detail_save_note_title),
            body = stringResource(R.string.mentor_detail_save_note_body),
            confirmLabel = stringResource(R.string.common_save),
            onConfirm = {
                showSaveDialog = false
                isEditing = false
            },
            onDismiss = { showSaveDialog = false },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
    ) {
        InfoFieldWithIcon(label = stringResource(R.string.admin_detail_email), value = mentor.email, icon = Icons.Outlined.Email)
        InfoFieldWithIcon(label = stringResource(R.string.admin_detail_phone), value = mentor.phone, icon = Icons.Outlined.Phone)
        InfoFieldWithIcon(label = stringResource(R.string.mentor_detail_area), value = mentor.department, icon = Icons.AutoMirrored.Outlined.MenuBook)

        Spacer(modifier = Modifier.height(8.dp))

        // Internal note
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionLabel(stringResource(R.string.mentor_detail_internal_note))
                Spacer(modifier = Modifier.weight(1f))
                if (!isEditing) {
                    IconButton(onClick = { isEditing = true }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.Edit, contentDescription = stringResource(R.string.common_edit), tint = LightBlue, modifier = Modifier.size(20.dp))
                    }
                }
            }
            Text(
                text = stringResource(R.string.mentor_detail_note_info),
                color = DarkGrey, fontSize = 12.sp,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                if (isEditing) {
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringResource(R.string.mentor_detail_note_placeholder), color = DarkGrey, fontSize = 14.sp) },
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LightBlue,
                            unfocusedBorderColor = BorderGrey,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                        ),
                        minLines = 3,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { showSaveDialog = true },
                            modifier = Modifier.background(Fade2, RoundedCornerShape(10.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(10.dp),
                        ) {
                            Text(stringResource(R.string.common_save), fontWeight = FontWeight.SemiBold)
                        }
                        TextButton(onClick = {
                            noteText = mentor.internalNote
                            isEditing = false
                        }) {
                            Text(stringResource(R.string.common_cancel), color = DarkGrey, fontWeight = FontWeight.SemiBold)
                        }
                    }
                } else {
                    val noNoteText = stringResource(R.string.mentor_detail_no_note)
                    Text(
                        text = noteText.ifBlank { noNoteText },
                        color = DarkGrey, fontSize = 14.sp, lineHeight = 20.sp,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(50.dp)
                .background(Fade2, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        ) {
            Text(stringResource(R.string.mentor_detail_view_assigned), fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(16.dp))
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

// region Tab 1 — Competências

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SkillsTab(mentor: AdminMentor) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionLabel(stringResource(R.string.mentor_detail_technical_skills))
        }
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            mentor.skills.forEach { skill ->
                SkillChip(text = skill)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionLabel(stringResource(R.string.mentor_detail_supervision_areas))
        }
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            mentor.supervisionAreas.forEach { area ->
                SkillChip(text = area)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SkillChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(LightBlue.copy(alpha = 0.2f))
            .border(1.dp, LightBlue, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(text, color = DarkBlue, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

// endregion

// region Tab 2 — Gerir

@Composable
private fun ManageTab(mentor: AdminMentor, navController: NavController) {
    var showReassignDialog by remember { mutableStateOf(false) }

    if (showReassignDialog) {
        ConfirmationDialog(
            title = stringResource(R.string.mentor_detail_reassign_title),
            body = stringResource(R.string.mentor_detail_reassign_body),
            confirmLabel = stringResource(R.string.common_confirm),
            onConfirm = {
                showReassignDialog = false
                navController.navigate(InstituicaoRoutes.assignMentorRoute(mentor.id))
            },
            onDismiss = { showReassignDialog = false },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp),
    ) {
        ActionRow(
            icon = Icons.Outlined.Work,
            title = stringResource(R.string.mentor_detail_assign_internship),
            subtitle = stringResource(R.string.mentor_detail_assign_internship_sub),
            iconBg = LightBlue.copy(alpha = 0.15f),
            onClick = { navController.navigate(InstituicaoRoutes.assignMentorRoute(mentor.id)) },
        )
        ActionRow(
            icon = Icons.Outlined.SwapHoriz,
            title = stringResource(R.string.mentor_detail_reassign_title),
            subtitle = stringResource(R.string.mentor_detail_reassign_body),
            iconBg = LightBlue.copy(alpha = 0.15f),
            onClick = { showReassignDialog = true },
        )
        ActionRow(
            icon = Icons.Outlined.CalendarMonth,
            title = stringResource(R.string.mentor_detail_view_assigned),
            subtitle = stringResource(R.string.mentor_detail_view_assigned_sub),
            iconBg = LightBlue.copy(alpha = 0.15f),
            onClick = { },
        )
        ActionRow(
            icon = Icons.AutoMirrored.Outlined.MenuBook,
            title = stringResource(R.string.mentor_detail_check_availability),
            subtitle = stringResource(R.string.mentor_detail_check_availability_sub),
            iconBg = LightBlue.copy(alpha = 0.15f),
            onClick = { },
        )
        ActionRow(
            icon = Icons.AutoMirrored.Outlined.Chat,
            title = stringResource(R.string.common_send_message),
            subtitle = stringResource(R.string.mentor_detail_send_message_sub),
            iconBg = LightBlue.copy(alpha = 0.15f),
            onClick = { navController.navigate(InstituicaoRoutes.chatRoute(mentor.id)) },
        )
        ActionRow(
            icon = Icons.Outlined.PersonAdd,
            title = stringResource(R.string.mentor_detail_assign_student),
            subtitle = stringResource(R.string.mentor_detail_assign_student_sub),
            iconBg = LightBlue.copy(alpha = 0.15f),
            onClick = { },
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconBg: Color,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = null, tint = LightBlue, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtitle, color = DarkGrey, fontSize = 12.sp, lineHeight = 18.sp)
            }
            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = DarkGrey, modifier = Modifier.size(18.dp))
        }
    }
}

// endregion

// region Previews

@Preview(showSystemUi = true)
@Composable
private fun MentorDetailInstituicaoScreenPreview() {
    MaterialTheme {
        MentorDetailInstituicaoScreen(mentorId = "m1", navController = rememberNavController())
    }
}

@Preview(showSystemUi = true, device = "spec:width=411dp,height=891dp,orientation=landscape")
@Composable
private fun MentorDetailInstituicaoScreenLandscapePreview() {
    MaterialTheme {
        MentorDetailInstituicaoScreen(mentorId = "m1", navController = rememberNavController())
    }
}

// endregion
