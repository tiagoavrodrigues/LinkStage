package turmaA.grupoB.LinkStage.ui.instituicao.home

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.aluno.chat.Conversation
import turmaA.grupoB.LinkStage.ui.aluno.chat.avatarColors
import turmaA.grupoB.LinkStage.ui.aluno.chat.sampleConversations
import turmaA.grupoB.LinkStage.ui.aluno.offers.OfferItem
import turmaA.grupoB.LinkStage.ui.common.CommonTopBar
import turmaA.grupoB.LinkStage.ui.common.EvaluationNotificationModal
import turmaA.grupoB.LinkStage.ui.common.EvaluationPendingCard
import turmaA.grupoB.LinkStage.ui.instituicao.InstituicaoRoutes
import turmaA.grupoB.LinkStage.ui.orientador.EvaluationState
import turmaA.grupoB.LinkStage.ui.orientador.InternshipEvaluation
import turmaA.grupoB.LinkStage.ui.orientador.InternshipType
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.BorderGrey
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.ui.theme.Red
import turmaA.grupoB.LinkStage.viewmodel.institutionhome.InstitutionDashboardUiState
import turmaA.grupoB.LinkStage.viewmodel.institutionhome.InstitutionHomeViewModel
import turmaA.grupoB.LinkStage.viewmodel.chat.ChatUiState
import turmaA.grupoB.LinkStage.viewmodel.chat.ChatViewModel
import turmaA.grupoB.LinkStage.viewmodel.institutionhome.InstitutionHomeViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.settings.SettingsViewModel

private val sampleInstitutionOffers = listOf(
    OfferItem("1", "Designer de Produto", "ESTG-IPVC", "Tempo Inteiro", "5h atras", Color(0xFF1565C0), "E", duration = "6 Meses", area = "Design", location = "Porto"),
    OfferItem("2", "Programador Full-Stack", "ESTG-IPVC", "Remoto", "2d atras", Color(0xFF1565C0), "E", duration = "9 Meses", area = "Tecnologia", location = "Remoto"),
)

@Composable
fun HomeInstituicaoScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    institutionHomeViewModel: InstitutionHomeViewModel = viewModel(factory = InstitutionHomeViewModelFactory()),
    settingsViewModel: SettingsViewModel = viewModel(),
    chatViewModel: ChatViewModel? = null,
) {
    val user by settingsViewModel.user.collectAsState()
    val dashboardUiState by institutionHomeViewModel.dashboardUiState.collectAsState()
    val dashboardData = dashboardUiState as? InstitutionDashboardUiState.Success
    val institutionName = user.name.ifBlank { "Instituição" }
    val activeOffersCount = dashboardData?.activeOffersCount ?: 5
    val applicationsCount = dashboardData?.applicationsCount ?: 12
    val activeInternshipsCount = dashboardData?.activeInternshipsCount ?: 3
    val pendingEvaluationsCount = dashboardData?.pendingEvaluationsCount ?: 1
    val noMentorCount = dashboardData?.noMentorCount ?: 1

    LaunchedEffect(Unit) {
        institutionHomeViewModel.loadDashboardForCurrentUser()
        chatViewModel?.loadConversations()
    }

    val chatState = chatViewModel?.chatUiState?.collectAsState()
    val recentMessages = chatState?.value?.let { state ->
        when (state) {
            is ChatUiState.Success -> state.conversations.take(3)
            else -> sampleConversations.take(3)
        }
    } ?: sampleConversations.take(3)

    val evaluation: InternshipEvaluation? = remember {
        InternshipEvaluation(
            internshipId = "int3",
            internshipType = InternshipType.SCHOOL_ONLY,
            state = EvaluationState.PENDING,
            institutionName = "ESTG-IPVC",
            schoolMentorName = "Prof. Tiago Alex.",
            hasSeenNotification = false,
        )
    }

    val hasSeenResult by institutionHomeViewModel.hasSeenEvaluations.collectAsState()
    val hasDismissedModal by institutionHomeViewModel.hasDismissedEvaluationModal.collectAsState()

    val showEvaluationModal = evaluation?.state == EvaluationState.PENDING && 
            !hasSeenResult && 
            !hasDismissedModal

    if (showEvaluationModal) {
        EvaluationNotificationModal(
            title = stringResource(R.string.institution_home_completed_title),
            message = stringResource(R.string.institution_home_completed_message),
            actionLabel = stringResource(R.string.institution_eval_submit_grade),
            onAction = {
                institutionHomeViewModel.setHasSeenEvaluations(true)
                navController.navigate(
                    InstituicaoRoutes.internshipDetailRoute(evaluation.internshipId)
                )
            },
            onDismiss = { institutionHomeViewModel.setHasDismissedEvaluationModal(true) },
        )
    }

    Scaffold(
        modifier = modifier,
        containerColor = BackgroundLight,
        topBar = {
            Column(modifier = Modifier.background(BackgroundLight)) {
                CommonTopBar()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = stringResource(R.string.institution_home_greeting, institutionName),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkBlue,
                        ),
                    )
                    Text(
                        text = stringResource(R.string.institution_home_welcome),
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkGrey,
                    )
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
            if (evaluation?.state == EvaluationState.PENDING && !hasSeenResult) {
                EvaluationPendingCard(
                    title = stringResource(R.string.institution_home_eval_pending),
                    message = stringResource(R.string.institution_home_eval_pending_message),
                    actionLabel = stringResource(R.string.institution_eval_submit_grade),
                    isDanger = false,
                    onClick = {
                        institutionHomeViewModel.setHasSeenEvaluations(true)
                        navController.navigate(
                            InstituicaoRoutes.internshipDetailRoute(evaluation.internshipId)
                        )
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Section 1 — Resumo (stat cards 2x2)
            SectionTitle(stringResource(R.string.institution_home_summary))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StatCard(
                    icon = Icons.Outlined.Work,
                    label = stringResource(R.string.institution_home_active_offers),
                    count = activeOffersCount,
                    color = LightBlue,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate(InstituicaoRoutes.OFFERS) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true; restoreState = true
                        }
                    },
                )
                StatCard(
                    icon = Icons.Outlined.People,
                    label = stringResource(R.string.institution_home_applications),
                    count = applicationsCount,
                    color = DarkBlue,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate(InstituicaoRoutes.OFFERS) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true; restoreState = true
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StatCard(
                    icon = Icons.Outlined.CalendarMonth,
                    label = stringResource(R.string.institution_home_active_internships),
                    count = activeInternshipsCount,
                    color = LightBlue,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate(InstituicaoRoutes.ACTIVITY) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true; restoreState = true
                        }
                    },
                )
                StatCard(
                    icon = Icons.Outlined.RateReview,
                    label = stringResource(R.string.institution_home_pending_eval),
                    count = pendingEvaluationsCount,
                    color = Red,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate(InstituicaoRoutes.ACTIVITY) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true; restoreState = true
                        }
                    },
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 2 — Ações pendentes
            SectionTitle(stringResource(R.string.institution_home_pending_actions))
            Spacer(modifier = Modifier.height(8.dp))

            if (applicationsCount > 0 || noMentorCount > 0) {
                if (applicationsCount > 0) {
                    ActionCard(
                        icon = Icons.Outlined.People,
                        iconBg = LightBlue.copy(alpha = 0.15f),
                        iconTint = LightBlue,
                        title = stringResource(R.string.institution_home_applications_waiting, applicationsCount),
                        subtitle = stringResource(R.string.institution_home_applications_waiting_sub),
                        onClick = {
                            navController.navigate(InstituicaoRoutes.OFFERS) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                if (noMentorCount > 0) {
                    ActionCard(
                        icon = Icons.Outlined.PersonOff,
                        iconBg = Red.copy(alpha = 0.12f),
                        iconTint = Red,
                        title = stringResource(R.string.institution_home_no_mentor, noMentorCount),
                        subtitle = stringResource(R.string.institution_home_no_mentor_sub),
                        onClick = {
                            navController.navigate(InstituicaoRoutes.ACTIVITY) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            } else {
                EmptyStateCard(stringResource(R.string.institution_home_no_pending))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 3 — Ofertas Recentes
            SectionHeader(
                title = stringResource(R.string.institution_home_recent_offers),
                onViewAll = {
                    navController.navigate(InstituicaoRoutes.OFFERS) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (sampleInstitutionOffers.isNotEmpty()) {
                sampleInstitutionOffers.take(2).forEach { offer ->
                    SimpleOfferCard(offer = offer)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else {
                EmptyStateCard(stringResource(R.string.institution_home_no_offers))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 4 — Mensagens Recentes
            SectionHeader(
                title = stringResource(R.string.institution_home_recent_messages),
                onViewAll = {
                    navController.navigate(InstituicaoRoutes.MESSAGES) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (recentMessages.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                ) {
                    recentMessages.forEachIndexed { index, conversation ->
                        MessageRow(
                            conversation = conversation,
                            onClick = {
                                navController.navigate(InstituicaoRoutes.chatRoute(conversation.id))
                            },
                        )
                        if (index < sampleConversations.take(3).size - 1) {
                            HorizontalDivider(
                                color = BorderGrey,
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                        }
                    }
                }
            } else {
                EmptyStateCard(stringResource(R.string.institution_home_no_messages))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        color = DarkBlue,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        modifier = Modifier.padding(horizontal = 20.dp),
    )
}

@Composable
private fun SectionHeader(
    title: String,
    onViewAll: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = DarkBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
        )
        TextButton(onClick = onViewAll) {
            Text(
                text = stringResource(R.string.common_view_all),
                color = LightBlue,
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
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
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "$count",
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                )
                Text(
                    text = label,
                    color = DarkGrey,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                )
            }
        }
    }
}

@Composable
private fun ActionCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
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
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp),
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = DarkBlue,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                )
                Text(
                    text = subtitle,
                    color = DarkGrey,
                    fontSize = 12.sp,
                )
            }
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = DarkGrey,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun SimpleOfferCard(offer: OfferItem) {
    var isFav by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(offer.logoColor),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = offer.logoInitial,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = offer.title,
                        color = DarkBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    )
                    Text(
                        text = offer.company,
                        color = DarkGrey,
                        fontSize = 13.sp,
                    )
                }
                IconButton(onClick = { isFav = !isFav }) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = stringResource(R.string.institution_home_favorite),
                        tint = if (isFav) LightBlue else DarkGrey,
                    )
                }
            }

            HorizontalDivider(
                color = BorderGrey,
                modifier = Modifier.padding(horizontal = 12.dp),
            )

            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = DarkGrey,
                    modifier = Modifier.size(13.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.offers_published, offer.publishedAgo),
                    color = DarkGrey,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
private fun MessageRow(
    conversation: Conversation,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(avatarColors[conversation.avatarColorIndex % avatarColors.size]),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = conversation.initials,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
        ) {
            Text(
                text = conversation.name,
                color = DarkBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
            )
            Text(
                text = conversation.lastMessage,
                color = DarkGrey,
                fontSize = 13.sp,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
            contentDescription = null,
            tint = DarkGrey,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun EmptyStateCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Text(
            text = message,
            color = DarkGrey,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun HomeInstituicaoScreenPreview() {
    MaterialTheme {
        HomeInstituicaoScreen(navController = rememberNavController())
    }
}

@Preview(showSystemUi = true, device = "spec:width=411dp,height=891dp,orientation=landscape")
@Composable
private fun HomeInstituicaoScreenLandscapePreview() {
    MaterialTheme {
        HomeInstituicaoScreen(navController = rememberNavController())
    }
}
