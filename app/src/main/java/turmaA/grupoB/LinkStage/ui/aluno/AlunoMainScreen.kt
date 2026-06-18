package turmaA.grupoB.LinkStage.ui.aluno

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.History
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import turmaA.grupoB.LinkStage.ui.auth.updatepassword.UpdatePasswordScreen
import turmaA.grupoB.LinkStage.ui.common.PrivacyPolicyScreen
import turmaA.grupoB.LinkStage.ui.aluno.activity.InternshipResultScreen
import turmaA.grupoB.LinkStage.ui.aluno.activity.RecentActivityAlunoScreen
import turmaA.grupoB.LinkStage.ui.aluno.chat.ChatAlunoScreen
import turmaA.grupoB.LinkStage.ui.aluno.chat.StudentChatScreen
import turmaA.grupoB.LinkStage.ui.aluno.home.HomeAlunoScreen
import turmaA.grupoB.LinkStage.ui.aluno.notifications.NotificationsAlunoScreen
import turmaA.grupoB.LinkStage.ui.aluno.activity.ActivityDetailAlunoScreen
import turmaA.grupoB.LinkStage.ui.aluno.activity.ReportSuccessScreen
import turmaA.grupoB.LinkStage.ui.aluno.apply.ApplyScreen
import turmaA.grupoB.LinkStage.ui.aluno.apply.ApplySuccessScreen
import turmaA.grupoB.LinkStage.ui.aluno.apply.EditSkillsScreen
import turmaA.grupoB.LinkStage.ui.aluno.offers.OfferDetailAlunoScreen
import turmaA.grupoB.LinkStage.ui.aluno.offers.OffersAlunoScreen
import turmaA.grupoB.LinkStage.ui.aluno.settings.SettingsAlunoScreen
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.viewmodel.apply.ApplyViewModel
import turmaA.grupoB.LinkStage.viewmodel.home.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import turmaA.grupoB.LinkStage.ui.aluno.offers.OfferDetail

object AlunoRoutes {
    const val HOME = "home"
    const val DISCOVER = "discover"
    const val ACTIVITY = "activity"
    const val MESSAGES = "messages"
    const val SETTINGS = "settings"
    const val NOTIFICATIONS = "notifications"
    const val CHAT = "chat/{threadId}"
    const val OFFER_DETAIL = "offer_detail/{offerId}"
    const val APPLY = "apply/{offerId}"
    const val EDIT_SKILLS = "edit_skills"
    const val APPLY_SUCCESS = "apply_success/{offerId}"
    const val ACTIVITY_DETAIL = "activity_detail/{checkpointId}"
    const val UPDATE_PASSWORD = "update_password"
    const val INTERNSHIP_RESULT = "internship_result/{internshipId}"
    const val REPORT_SUCCESS = "report_success"
    const val PRIVACY_POLICY = "privacy_policy"

    fun chatRoute(threadId: String) = "chat/$threadId"
    fun internshipResultRoute(internshipId: String) = "internship_result/$internshipId"
    fun offerDetailRoute(offerId: String) = "offer_detail/$offerId"
    fun applyRoute(offerId: String) = "apply/$offerId"
    fun applySuccessRoute(offerId: String) = "apply_success/$offerId"
    fun activityDetailRoute(checkpointId: String) = "activity_detail/$checkpointId"
}

private data class AlunoTab(
    @StringRes val titleResId: Int,
    val icon: ImageVector,
    val route: String,
)

private val alunoTabs = listOf(
    AlunoTab(R.string.tab_home, Icons.Outlined.Home, AlunoRoutes.HOME),
    AlunoTab(R.string.tab_internships, Icons.Outlined.Work, AlunoRoutes.DISCOVER),
    AlunoTab(R.string.tab_activity, Icons.Outlined.History, AlunoRoutes.ACTIVITY),
    AlunoTab(R.string.tab_messages, Icons.AutoMirrored.Outlined.Chat, AlunoRoutes.MESSAGES),
    AlunoTab(R.string.tab_settings, Icons.Outlined.Settings, AlunoRoutes.SETTINGS),
)

@Composable
fun AlunoMainScreen(onLogout: () -> Unit = {}) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    var pendingApplyOffer by remember {
        mutableStateOf<OfferDetail?>(null)
    }

    val showBottomBar = alunoTabs.any { tab ->
        currentDestination?.hierarchy?.any { it.route == tab.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = DarkBlue) {
                    alunoTabs.forEach { tab ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == tab.route
                        } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(tab.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = false
                                        }
                                        launchSingleTop = true
                                        restoreState = false
                                    }
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
            startDestination = AlunoRoutes.HOME,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(AlunoRoutes.HOME) {
                HomeAlunoScreen(navController = navController)
            }
            composable(AlunoRoutes.DISCOVER) {
                OffersAlunoScreen(
                    onOfferClick = { offerId ->
                        navController.navigate(AlunoRoutes.offerDetailRoute(offerId))
                    }
                )
            }
            composable(
                route = AlunoRoutes.OFFER_DETAIL,
                arguments = listOf(navArgument("offerId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val offerId = backStackEntry.arguments?.getString("offerId") ?: return@composable
                OfferDetailAlunoScreen(
                    offerId = offerId,
                    onBack = { navController.popBackStack() },
                    onApply = { offer ->
                        pendingApplyOffer = offer
                        navController.navigate(AlunoRoutes.applyRoute(offer.id))
                    },
                )
            }
            composable(
                route = AlunoRoutes.APPLY,
                arguments = listOf(navArgument("offerId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val offerId = backStackEntry.arguments?.getString("offerId") ?: return@composable
                val applyViewModel: ApplyViewModel = viewModel(backStackEntry)
                val selectedOffer = pendingApplyOffer
                ApplyScreen(
                    offerId = offerId,
                    offerTitle = selectedOffer?.title ?: "",
                    offerCompany = selectedOffer?.company ?: "",
                    offerLogoInitial = selectedOffer?.logoInitial ?: "?",
                    offerLogoColor = selectedOffer?.logoColor ?: Color(0xFF212121),
                    viewModel = applyViewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToEditSkills = { navController.navigate(AlunoRoutes.EDIT_SKILLS) },
                    onSubmitSuccess = {
                        navController.navigate(AlunoRoutes.applySuccessRoute(offerId)) {
                            popUpTo(AlunoRoutes.applyRoute(offerId)) { inclusive = true }
                        }
                    },
                )
            }
            composable(AlunoRoutes.EDIT_SKILLS) {
                val applyEntry = remember(it) {
                    navController.getBackStackEntry(AlunoRoutes.APPLY)
                }
                val applyViewModel: ApplyViewModel = viewModel(applyEntry)
                EditSkillsScreen(
                    viewModel = applyViewModel,
                    onBack = { navController.popBackStack() },
                )
            }
            composable(
                route = AlunoRoutes.APPLY_SUCCESS,
                arguments = listOf(navArgument("offerId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val offerId = backStackEntry.arguments?.getString("offerId") ?: return@composable
                val selectedOffer = pendingApplyOffer
                ApplySuccessScreen(
                    offerId = offerId,
                    offerTitle = selectedOffer?.title ?: "",
                    offerCompany = selectedOffer?.company ?: "",
                    offerLogoInitial = selectedOffer?.logoInitial ?: "?",
                    offerLogoColor = selectedOffer?.logoColor ?: Color(0xFF212121),
                    onNavigateBack = {
                        pendingApplyOffer = null

                        navController.navigate(AlunoRoutes.DISCOVER) {
                            popUpTo(AlunoRoutes.HOME) { inclusive = false }
                        }
                    },
                )
            }
            composable(AlunoRoutes.ACTIVITY) {
                RecentActivityAlunoScreen(
                    onBack = { navController.popBackStack() },
                    onActivityClick = { checkpointId ->
                        navController.navigate(AlunoRoutes.activityDetailRoute(checkpointId))
                    },
                    onViewResult = { internshipId ->
                        navController.navigate(AlunoRoutes.internshipResultRoute(internshipId))
                    },
                    onSubmitReport = {
                        navController.navigate(AlunoRoutes.REPORT_SUCCESS)
                    }
                )
            }
            composable(AlunoRoutes.REPORT_SUCCESS) {
                ReportSuccessScreen(
                    onNavigateBack = {
                        navController.navigate(AlunoRoutes.HOME) {
                            popUpTo(AlunoRoutes.HOME) { inclusive = true }
                        }
                    }
                )
            }
            composable(
                route = AlunoRoutes.ACTIVITY_DETAIL,
                arguments = listOf(navArgument("checkpointId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val checkpointId = backStackEntry.arguments?.getString("checkpointId") ?: return@composable
                ActivityDetailAlunoScreen(
                    checkpointId = checkpointId,
                    onBack = { navController.popBackStack() },
                )
            }
            composable(AlunoRoutes.MESSAGES) {
                ChatAlunoScreen(
                    onOpenThread = { threadId ->
                        navController.navigate(AlunoRoutes.chatRoute(threadId))
                    },
                )
            }
            composable(AlunoRoutes.SETTINGS) {
                SettingsAlunoScreen(
                    onLogout = onLogout,
                    onNotificationsClick = {
                        navController.navigate(AlunoRoutes.NOTIFICATIONS)
                    },
                    onPrivacyPolicyClick = {
                        navController.navigate(AlunoRoutes.PRIVACY_POLICY)
                    }
                )
            }
            composable(AlunoRoutes.PRIVACY_POLICY) {
                PrivacyPolicyScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(AlunoRoutes.NOTIFICATIONS) {
                NotificationsAlunoScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = AlunoRoutes.INTERNSHIP_RESULT,
                arguments = listOf(navArgument("internshipId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val internshipId = backStackEntry.arguments?.getString("internshipId") ?: return@composable
                InternshipResultScreen(
                    internshipId = internshipId,
                    navController = navController,
                )
            }
            composable(AlunoRoutes.UPDATE_PASSWORD) {
                UpdatePasswordScreen(
                    onUpdatePasswordClick = { /* Lógica de update */ },
                    onBackToLogin = { navController.popBackStack() }
                )
            }
            composable(
                route = AlunoRoutes.CHAT,
                arguments = listOf(navArgument("threadId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val threadId = backStackEntry.arguments?.getString("threadId") ?: return@composable
                
                StudentChatScreen(
                    threadId = threadId,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
