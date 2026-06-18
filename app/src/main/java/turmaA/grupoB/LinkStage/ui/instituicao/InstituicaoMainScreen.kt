package turmaA.grupoB.LinkStage.ui.instituicao

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import turmaA.grupoB.LinkStage.R
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import turmaA.grupoB.LinkStage.ui.common.PrivacyPolicyScreen
import turmaA.grupoB.LinkStage.ui.chat.ChatDetailScreen
import turmaA.grupoB.LinkStage.viewmodel.chat.ChatDataSource
import turmaA.grupoB.LinkStage.viewmodel.chat.ChatUiState
import turmaA.grupoB.LinkStage.viewmodel.chat.ChatViewModel
import turmaA.grupoB.LinkStage.viewmodel.chat.ChatViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.chat.InstituicaoChatDataSource
import turmaA.grupoB.LinkStage.data.repository.application.ApplicationRepository
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepository
import turmaA.grupoB.LinkStage.data.repository.communication.CommunicationRepository
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepository
import turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepository
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepository
import turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepository
import turmaA.grupoB.LinkStage.data.repository.student.StudentRepository
import turmaA.grupoB.LinkStage.ui.aluno.chat.ChatScreen
import turmaA.grupoB.LinkStage.ui.aluno.chat.Conversation
import turmaA.grupoB.LinkStage.ui.aluno.chat.getSampleContacts
import turmaA.grupoB.LinkStage.ui.aluno.chat.sampleConversations
import turmaA.grupoB.LinkStage.ui.instituicao.activity.ActivityInstituicaoScreen
import turmaA.grupoB.LinkStage.ui.instituicao.activity.AssignMentorInstituicaoScreen
import turmaA.grupoB.LinkStage.ui.instituicao.activity.InternshipDetailInstituicaoScreen
import turmaA.grupoB.LinkStage.ui.instituicao.activity.MentorAssignedSuccessScreen
import turmaA.grupoB.LinkStage.ui.instituicao.activity.MentorDetailInstituicaoScreen
import turmaA.grupoB.LinkStage.ui.instituicao.chat.ChatInstituicaoScreen
import turmaA.grupoB.LinkStage.ui.instituicao.home.HomeInstituicaoScreen
import turmaA.grupoB.LinkStage.ui.instituicao.offers.ApplicationDetailInstituicaoScreen
import turmaA.grupoB.LinkStage.ui.instituicao.offers.OfferDetailInstituicaoScreen
import turmaA.grupoB.LinkStage.ui.instituicao.offers.OfferFormInstituicaoScreen
import turmaA.grupoB.LinkStage.ui.instituicao.offers.OfferSuccessInstituicaoScreen
import turmaA.grupoB.LinkStage.ui.instituicao.offers.OffersInstituicaoScreen
import turmaA.grupoB.LinkStage.ui.common.EvaluationSubmittedScreen
import turmaA.grupoB.LinkStage.ui.instituicao.settings.NotificationsInstituicaoScreen
import turmaA.grupoB.LinkStage.ui.instituicao.settings.SettingsInstituicaoScreen
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.LightBlue

object InstituicaoRoutes {
    const val HOME = "instituicao_home"
    const val OFFERS = "instituicao_offers"
    const val ACTIVITY = "instituicao_activity"
    const val MESSAGES = "instituicao_messages"
    const val SETTINGS = "instituicao_settings"
    const val NOTIFICATIONS = "instituicao_notifications"
    const val CHAT = "instituicao_chat/{conversationId}"
    const val OFFER_FORM = "instituicao_offer_form/{offerId}"
    const val OFFER_DETAIL = "instituicao_offer_detail/{offerId}"
    const val APPLICATION_DETAIL = "instituicao_application/{applicationId}"
    const val OFFER_SUCCESS = "instituicao_offer_success/{offerId}?isNew={isNew}"
    const val MENTOR_DETAIL = "instituicao_mentor/{mentorId}"
    const val ASSIGN_MENTOR = "assign_mentor/{internshipId}"
    const val MENTOR_ASSIGNED_SUCCESS = "mentor_assigned_success/{internshipId}"
    const val INTERNSHIP_DETAIL = "instituicao_internship_detail/{internshipId}"
    const val EVALUATION_SUBMITTED = "evaluation_submitted/{internshipId}"
    const val PRIVACY_POLICY = "instituicao_privacy_policy"

    fun chatRoute(conversationId: String) = "instituicao_chat/$conversationId"
    fun offerFormRoute(offerId: String) = "instituicao_offer_form/$offerId"
    fun offerDetailRoute(offerId: String) = "instituicao_offer_detail/$offerId"
    fun offerSuccessRoute(offerId: String, isNew: Boolean = false) = "instituicao_offer_success/$offerId?isNew=$isNew"
    fun applicationDetailRoute(applicationId: String) = "instituicao_application/$applicationId"
    fun mentorDetailRoute(mentorId: String) = "instituicao_mentor/$mentorId"
    fun assignMentorRoute(internshipId: String) = "assign_mentor/$internshipId"
    fun mentorAssignedSuccessRoute(internshipId: String) = "mentor_assigned_success/$internshipId"
    fun internshipDetailRoute(internshipId: String) = "instituicao_internship_detail/$internshipId"
    fun evaluationSubmittedRoute(internshipId: String) = "evaluation_submitted/$internshipId"
}

private data class InstituicaoTab(
    @StringRes val titleResId: Int,
    val icon: ImageVector,
    val route: String,
)

private val instituicaoTabs = listOf(
    InstituicaoTab(R.string.tab_home, Icons.Outlined.Home, InstituicaoRoutes.HOME),
    InstituicaoTab(R.string.tab_offers, Icons.Outlined.Work, InstituicaoRoutes.OFFERS),
    InstituicaoTab(R.string.tab_activity, Icons.Outlined.CalendarMonth, InstituicaoRoutes.ACTIVITY),
    InstituicaoTab(R.string.tab_messages, Icons.AutoMirrored.Outlined.Chat, InstituicaoRoutes.MESSAGES),
    InstituicaoTab(R.string.tab_settings, Icons.Outlined.Settings, InstituicaoRoutes.SETTINGS),
)

@Composable
fun InstituicaoMainScreen(onLogout: () -> Unit = {}) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = instituicaoTabs.any { tab ->
        currentDestination?.hierarchy?.any { it.route == tab.route } == true
    }

    val chatDataSource: ChatDataSource = remember {
        InstituicaoChatDataSource(
            authRepository = AuthRepository(),
            institutionRepository = InstitutionRepository(),
            offerRepository = OfferRepository(),
            internshipRepository = InternshipRepository(),
            applicationRepository = ApplicationRepository(),
            studentRepository = StudentRepository(),
            profileRepository = ProfileRepository(),
            communicationRepository = CommunicationRepository(),
        )
    }
    val chatViewModel = remember { ChatViewModel(chatDataSource) }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = DarkBlue) {
                    instituicaoTabs.forEach { tab ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == tab.route
                        } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, contentDescription = stringResource(tab.titleResId)) },
                            label = { Text(stringResource(tab.titleResId)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = LightBlue,
                                selectedTextColor = LightBlue,
                                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                                unselectedTextColor = Color.White.copy(alpha = 0.7f),
                                indicatorColor = DarkBlue,
                            ),
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = InstituicaoRoutes.HOME,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(InstituicaoRoutes.HOME) {
                HomeInstituicaoScreen(
                    navController = navController,
                    chatViewModel = chatViewModel,
                )
            }
            composable(InstituicaoRoutes.OFFERS) {
                OffersInstituicaoScreen(navController = navController)
            }
            composable(InstituicaoRoutes.ACTIVITY) {
                ActivityInstituicaoScreen(navController = navController)
            }
            composable(InstituicaoRoutes.MESSAGES) {
                ChatInstituicaoScreen(
                    chatViewModel = chatViewModel,
                    onOpenChat = { conversationId ->
                        navController.navigate(InstituicaoRoutes.chatRoute(conversationId))
                    },
                )
            }
            composable(InstituicaoRoutes.SETTINGS) {
                SettingsInstituicaoScreen(
                    onLogout = onLogout,
                    onNotificationsClick = {
                        navController.navigate(InstituicaoRoutes.NOTIFICATIONS)
                    },
                    onPrivacyPolicyClick = {
                        navController.navigate(InstituicaoRoutes.PRIVACY_POLICY)
                    }
                )
            }
            composable(InstituicaoRoutes.PRIVACY_POLICY) {
                PrivacyPolicyScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(InstituicaoRoutes.NOTIFICATIONS) {
                NotificationsInstituicaoScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = InstituicaoRoutes.CHAT,
                arguments = listOf(navArgument("conversationId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val conversationId = backStackEntry.arguments?.getString("conversationId") ?: return@composable
                val chatUiState by chatViewModel.chatUiState.collectAsState()
                val conversation = (chatUiState as? ChatUiState.Success)
                    ?.conversations
                    ?.firstOrNull { it.id == conversationId }

                ChatDetailScreen(
                    threadId = conversationId,
                    dataSource = chatDataSource,
                    chatViewModel = chatViewModel,
                    conversation = conversation,
                    onBack = { navController.popBackStack() },
                )
            }
            composable(
                route = InstituicaoRoutes.OFFER_FORM,
                arguments = listOf(navArgument("offerId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val offerId = backStackEntry.arguments?.getString("offerId") ?: "new"
                OfferFormInstituicaoScreen(
                    offerId = offerId,
                    navController = navController,
                )
            }
            composable(
                route = InstituicaoRoutes.OFFER_SUCCESS,
                arguments = listOf(
                    navArgument("offerId") { type = NavType.StringType },
                    navArgument("isNew") { type = NavType.BoolType; defaultValue = false },
                ),
            ) { backStackEntry ->
                val offerId = backStackEntry.arguments?.getString("offerId") ?: ""
                val isNew = backStackEntry.arguments?.getBoolean("isNew") ?: false
                OfferSuccessInstituicaoScreen(
                    offerId = offerId,
                    isNew = isNew,
                    navController = navController,
                )
            }
            composable(
                route = InstituicaoRoutes.OFFER_DETAIL,
                arguments = listOf(navArgument("offerId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val offerId = backStackEntry.arguments?.getString("offerId") ?: return@composable
                OfferDetailInstituicaoScreen(
                    offerId = offerId,
                    navController = navController,
                )
            }
            composable(
                route = InstituicaoRoutes.MENTOR_DETAIL,
                arguments = listOf(navArgument("mentorId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val mentorId = backStackEntry.arguments?.getString("mentorId") ?: return@composable
                MentorDetailInstituicaoScreen(
                    mentorId = mentorId,
                    navController = navController,
                )
            }
            composable(
                route = InstituicaoRoutes.ASSIGN_MENTOR,
                arguments = listOf(navArgument("internshipId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val internshipId = backStackEntry.arguments?.getString("internshipId") ?: return@composable
                AssignMentorInstituicaoScreen(
                    internshipId = internshipId,
                    navController = navController,
                )
            }
            composable(
                route = InstituicaoRoutes.MENTOR_ASSIGNED_SUCCESS,
                arguments = listOf(navArgument("internshipId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val internshipId = backStackEntry.arguments?.getString("internshipId") ?: return@composable
                MentorAssignedSuccessScreen(
                    internshipId = internshipId,
                    navController = navController,
                )
            }
            composable(
                route = InstituicaoRoutes.INTERNSHIP_DETAIL,
                arguments = listOf(navArgument("internshipId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val internshipId = backStackEntry.arguments?.getString("internshipId") ?: return@composable
                InternshipDetailInstituicaoScreen(
                    internshipId = internshipId,
                    navController = navController,
                )
            }
            composable(
                route = InstituicaoRoutes.APPLICATION_DETAIL,
                arguments = listOf(navArgument("applicationId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val applicationId = backStackEntry.arguments?.getString("applicationId") ?: return@composable
                ApplicationDetailInstituicaoScreen(
                    applicationId = applicationId,
                    navController = navController,
                )
            }
            composable(
                route = InstituicaoRoutes.EVALUATION_SUBMITTED,
                arguments = listOf(navArgument("internshipId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val internshipId = backStackEntry.arguments?.getString("internshipId") ?: return@composable
                EvaluationSubmittedScreen(
                    internshipId = internshipId,
                    navController = navController,
                )
            }
        }
    }
}
