package turmaA.grupoB.LinkStage.ui.aluno.home

import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.data.remote.model.internship.ActivityLogModel
import turmaA.grupoB.LinkStage.data.remote.model.internship.InternshipModel
import turmaA.grupoB.LinkStage.data.repository.application.ApplicationRepository
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepository
import turmaA.grupoB.LinkStage.data.repository.communication.CommunicationRepository
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepository
import turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepository
import turmaA.grupoB.LinkStage.data.repository.internship.LocalActivityRepository
import turmaA.grupoB.LinkStage.data.room.AtDatabase
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepository
import turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepository
import turmaA.grupoB.LinkStage.data.repository.student.StudentRepository
import turmaA.grupoB.LinkStage.ui.aluno.AlunoRoutes
import turmaA.grupoB.LinkStage.ui.aluno.activity.ActiveInternship
import turmaA.grupoB.LinkStage.ui.aluno.activity.ApplicationCard
import turmaA.grupoB.LinkStage.ui.aluno.activity.ApplicationItem
import turmaA.grupoB.LinkStage.ui.aluno.activity.InternshipHeader
import turmaA.grupoB.LinkStage.ui.aluno.activity.calculateInternshipProgress
import turmaA.grupoB.LinkStage.ui.aluno.chat.ConversationItem
import turmaA.grupoB.LinkStage.ui.aluno.chat.Conversation
import turmaA.grupoB.LinkStage.ui.common.CommonTopBar
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.BorderGrey
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.Fade3
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.viewmodel.application.StudentApplicationDetails
import turmaA.grupoB.LinkStage.viewmodel.application.StudentApplicationsUiState
import turmaA.grupoB.LinkStage.viewmodel.application.StudentApplicationsViewModel
import turmaA.grupoB.LinkStage.viewmodel.application.StudentApplicationsViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.auth.AuthUiState
import turmaA.grupoB.LinkStage.viewmodel.auth.AuthViewModel
import turmaA.grupoB.LinkStage.viewmodel.auth.AuthViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.communication.CommunicationUiState
import turmaA.grupoB.LinkStage.viewmodel.communication.CommunicationViewModel
import turmaA.grupoB.LinkStage.viewmodel.communication.CommunicationViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.communication.StudentConversationDetails
import turmaA.grupoB.LinkStage.viewmodel.internships.InternshipUiState
import turmaA.grupoB.LinkStage.viewmodel.internships.InternshipViewModel
import turmaA.grupoB.LinkStage.viewmodel.internships.InternshipViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.student.StudentUiState
import turmaA.grupoB.LinkStage.viewmodel.student.StudentViewModel
import turmaA.grupoB.LinkStage.viewmodel.student.StudentViewModelFactory
import turmaA.grupoB.LinkStage.data.remote.model.enums.ApplicationStatus as RemoteApplicationStatus
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

// region Data models

enum class ApplicationStatus(@StringRes val labelRes: Int) {
    ACCEPTED(R.string.applications_filter_accepted),
    REJECTED(R.string.applications_filter_rejected),
    PENDING(R.string.applications_filter_pending),
}

data class Entrega(
    val deadline: String,
    val title: String,
    val company: String,
)

// endregion

@Composable
fun HomeAlunoScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(AuthRepository())
    ),
    studentViewModel: StudentViewModel = viewModel(
        factory = StudentViewModelFactory(StudentRepository())
    ),
    studentApplicationsViewModel: StudentApplicationsViewModel = viewModel(
        factory = StudentApplicationsViewModelFactory(
            ApplicationRepository(),
            OfferRepository(),
            InstitutionRepository(),
        )
    ),
    internshipViewModel: InternshipViewModel = viewModel(
        factory = InternshipViewModelFactory(
            InternshipRepository(),
            LocalActivityRepository(
                AtDatabase.getDatabase(LocalContext.current).atividadeDAO()
            ),
        )
    ),
    communicationViewModel: CommunicationViewModel = viewModel(
        factory = CommunicationViewModelFactory(
            CommunicationRepository(),
            ProfileRepository(),
        )
    ),
) {
    val authUiState by authViewModel.uiState.collectAsState()
    val studentUiState by studentViewModel.uiState.collectAsState()
    val studentApplicationsUiState by studentApplicationsViewModel.uiState.collectAsState()
    val internshipUiState by internshipViewModel.uiState.collectAsState()
    val conversationsUiState by communicationViewModel.conversationsUiState.collectAsState()

    val profile = (authUiState as? AuthUiState.Success)?.profile
    
    LaunchedEffect(Unit) {
        authViewModel.loadCurrentUserProfile()
    }

    LaunchedEffect(authUiState) {
        val state = authUiState

        if (state is AuthUiState.Success) {
            studentViewModel.loadStudentByUserId(state.profile.id)
            communicationViewModel.loadConversationsByUser(state.profile.id)
        }
    }

    LaunchedEffect(studentUiState) {
        val state = studentUiState

        if (state is StudentUiState.Success) {
            studentApplicationsViewModel.loadApplicationsByStudent(state.student.id)
            internshipViewModel.loadActiveInternshipByStudent(state.student.id)
        }
    }

    val recentApplications = when (val state = studentApplicationsUiState) {
        is StudentApplicationsUiState.SuccessList -> state.applications.map {
            it.toApplicationItem()
        }

        else -> emptyList()
    }

    val activeInternshipState = internshipUiState as? InternshipUiState.ActiveInternshipSuccess
    val activeInternshipModel = activeInternshipState?.internship
    val activeInternship = activeInternshipModel?.toActiveInternship()
    val upcomingDeliveries = activeInternshipState
        ?.activityLogs
        ?.mapNotNull { it.toEntrega() }
        ?.sortedBy { it.date }
        ?.take(2)
        ?.map { it.entrega }
        .orEmpty()
    val recentConversations = when (val state = conversationsUiState) {
        is CommunicationUiState.SuccessConversationList -> state.conversations.map {
            it.toHomeConversation()
        }
        else -> emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
    ) {
        CommonTopBar()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            profile?.let {
                Text(
                    stringResource(R.string.home_greeting, it.name),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue,
                    ),
                )
            }
            Text(
                stringResource(R.string.home_welcome),
                style = MaterialTheme.typography.bodyMedium,
                color = DarkGrey
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (activeInternship != null) {
                // State B: Active internship
                val internship = activeInternship
                val progress = calculateInternshipProgress(internship.startDate, internship.endDate)

                var animationStarted by remember { mutableStateOf(false) }
                val animatedProgress by animateFloatAsState(
                    targetValue = if (animationStarted) progress else 0f,
                    animationSpec = tween(durationMillis = 1000),
                    label = "home_progress",
                )
                LaunchedEffect(Unit) { animationStarted = true }

                HomeSectionCard(
                    title = stringResource(R.string.home_active_internship),
                    actionText = stringResource(R.string.home_view_details),
                    onAction = { navController.navigate(AlunoRoutes.ACTIVITY) }
                ) {
                    InternshipHeader(
                        internship = internship,
                        animatedProgress = animatedProgress,
                    )
                }

                if (upcomingDeliveries.isNotEmpty()) {
                    EntregasCard(upcomingDeliveries)
                }
            } else if (activeInternshipModel != null) {
                HomeSectionCard(
                    title = stringResource(R.string.home_active_internship),
                    actionText = stringResource(R.string.home_view_details),
                    onAction = { navController.navigate(AlunoRoutes.ACTIVITY) }
                ) {
                    Text(
                        text = stringResource(R.string.home_internship_dates_unavailable),
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkGrey,
                    )
                }
            } else {
                // State A: No internship — show recent applications
                HomeSectionCard(
                    title = stringResource(R.string.home_applications_title),
                    actionText = stringResource(R.string.common_view_all),
                    onAction = { navController.navigate(AlunoRoutes.ACTIVITY) }
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        when (val state = studentApplicationsUiState) {
                            StudentApplicationsUiState.Idle,
                            StudentApplicationsUiState.Loading -> Text(
                                text = stringResource(R.string.applications_loading),
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkGrey,
                            )

                            StudentApplicationsUiState.Empty -> Text(
                                text = stringResource(R.string.applications_empty),
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkGrey,
                            )

                            is StudentApplicationsUiState.Error -> Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = DarkGrey,
                            )

                            is StudentApplicationsUiState.SuccessList -> {
                                recentApplications.take(2).forEach { application ->
                                    ApplicationCard(application = application)
                                }
                            }
                        }
                    }
                }

                HomeSectionCard(
                    title = stringResource(R.string.home_discover_title),
                    actionText = stringResource(R.string.home_discover_action),
                    onAction = { navController.navigate(AlunoRoutes.DISCOVER) }
                ) {
                    Text(
                        stringResource(R.string.home_discover_message),
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkGrey,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            // Recent messages — both states
            HomeSectionCard(
                title = stringResource(R.string.home_recent_messages),
                actionText = stringResource(R.string.common_view_all),
                onAction = { navController.navigate(AlunoRoutes.MESSAGES) }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    when (val state = conversationsUiState) {
                        CommunicationUiState.Idle,
                        CommunicationUiState.Loading -> Text(
                            text = "A carregar conversas...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkGrey,
                        )

                        CommunicationUiState.Empty -> Text(
                            text = "Ainda não existem conversas.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkGrey,
                        )

                        is CommunicationUiState.Error -> Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = DarkGrey,
                        )

                        is CommunicationUiState.SuccessConversationList -> {
                            recentConversations.take(3).forEach { conversation ->
                                ConversationItem(
                                    conversation = conversation,
                                    onClick = {
                                        navController.navigate(AlunoRoutes.chatRoute(conversation.id))
                                    },
                                )
                            }
                        }

                        else -> Unit
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

private fun StudentConversationDetails.toHomeConversation(): Conversation {
    val participantName = participant?.name ?: "Conversa"
    val lastMessage = messages.lastOrNull()

    return Conversation(
        id = thread.id,
        name = participantName,
        initials = participantName.toHomeInitials(),
        lastMessage = lastMessage?.content ?: "Sem mensagens.",
        time = lastMessage?.createdAt.toHomeTimeLabel(),
        unreadCount = messages.count { !it.isRead && it.senderId == participant?.id },
        avatarColorIndex = participantName.hashCode() and Int.MAX_VALUE,
    )
}

private fun String.toHomeInitials(): String = trim()
    .split(Regex("\\s+"))
    .filter { it.isNotBlank() }
    .take(2)
    .mapNotNull { it.firstOrNull()?.uppercase() }
    .joinToString("")
    .ifBlank { "?" }

private fun String?.toHomeTimeLabel(): String {
    if (this == null) return ""
    return substringAfter('T', this).take(5)
}

private data class DatedEntrega(
    val date: LocalDate,
    val entrega: Entrega,
)

private fun ActivityLogModel.toEntrega(): DatedEntrega? {
    val date = runCatching { LocalDate.parse(activityDate) }.getOrNull()
        ?: return null
    if (date.isBefore(LocalDate.now())) {
        return null
    }

    return DatedEntrega(
        date = date,
        entrega = Entrega(
            deadline = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            title = type?.takeIf { it.isNotBlank() } ?: description,
            company = location.orEmpty(),
        ),
    )
}

private fun StudentApplicationDetails.toApplicationItem(): ApplicationItem {
    return ApplicationItem(
        id = application.id,
        offerTitle = offerTitle,
        company = institutionName,
        appliedAgo = application.createdAt.toHomeTimestamp(),
        status = application.status.toUiApplicationStatus(),
    )
}

private fun RemoteApplicationStatus.toUiApplicationStatus(): ApplicationStatus {
    return when (this) {
        RemoteApplicationStatus.PENDING -> ApplicationStatus.PENDING
        RemoteApplicationStatus.ACCEPTED -> ApplicationStatus.ACCEPTED
        RemoteApplicationStatus.REJECTED -> ApplicationStatus.REJECTED
    }
}

private fun String.toHomeTimestamp(): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    return runCatching { LocalDateTime.parse(this).format(formatter) }
        .recoverCatching {
            OffsetDateTime.parse(this)
                .atZoneSameInstant(ZoneId.systemDefault())
                .format(formatter)
        }
        .recoverCatching {
            Instant.parse(this)
                .atZone(ZoneId.systemDefault())
                .format(formatter)
        }
        .getOrDefault("")
}

private fun InternshipModel.toActiveInternship(): ActiveInternship? {
    val parsedStartDate = startDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        ?: return null
    val parsedEndDate = endDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        ?: return null

    return ActiveInternship(
        id = id,
        title = title,
        startDate = parsedStartDate,
        endDate = parsedEndDate,
        activityLogs = emptyList(),
    )
}

@Composable
private fun HomeSectionCard(
    title: String,
    actionText: String,
    onAction: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        border = BorderStroke(1.dp, BorderGrey.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader(
                title = title,
                actionText = actionText,
                onAction = onAction
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

// region Components

@Composable
private fun EntregasCard(entregas: List<Entrega>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Fade3, RoundedCornerShape(16.dp))
            .padding(20.dp),
    ) {
        Column {
            Text(
                stringResource(R.string.home_deliveries),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            entregas.forEachIndexed { index, entrega ->
                Text(
                    entrega.deadline,
                    style = MaterialTheme.typography.labelMedium.copy(color = LightBlue),
                )
                Text(
                    entrega.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                    ),
                )
                Text(
                    entrega.company,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.7f),
                    ),
                )
                if (index < entregas.lastIndex) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionText: String,
    onAction: () -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = DarkBlue,
            ),
        )
        Row(
            modifier = Modifier.clickable { onAction() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                actionText,
                color = LightBlue,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            )
            Spacer(modifier = Modifier.width(2.dp))
            Icon(
                Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = LightBlue,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

// endregion
