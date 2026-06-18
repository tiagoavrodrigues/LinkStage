package turmaA.grupoB.LinkStage.ui.orientador

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
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
import androidx.lifecycle.viewmodel.compose.viewModel
import turmaA.grupoB.LinkStage.ui.common.FinalGradeSubmittedScreen
import turmaA.grupoB.LinkStage.ui.chat.ChatDetailScreen
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepository
import turmaA.grupoB.LinkStage.data.repository.communication.CommunicationRepository
import turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepository
import turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepository
import turmaA.grupoB.LinkStage.data.repository.student.StudentRepository
import turmaA.grupoB.LinkStage.data.repository.supervisor.SupervisorRepository
import turmaA.grupoB.LinkStage.viewmodel.chat.ChatDataSource
import turmaA.grupoB.LinkStage.viewmodel.chat.ChatUiState
import turmaA.grupoB.LinkStage.viewmodel.chat.ChatViewModel
import turmaA.grupoB.LinkStage.viewmodel.chat.ChatViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.chat.OrientadorChatDataSource
import turmaA.grupoB.LinkStage.ui.aluno.chat.ChatScreen
import turmaA.grupoB.LinkStage.ui.aluno.chat.Conversation
import turmaA.grupoB.LinkStage.ui.aluno.chat.getSampleContacts
import turmaA.grupoB.LinkStage.ui.common.PrivacyPolicyScreen
import turmaA.grupoB.LinkStage.ui.orientador.students.MentorCheckpointDetailScreen
import turmaA.grupoB.LinkStage.ui.orientador.students.MentorStudentDetailScreen
import turmaA.grupoB.LinkStage.ui.aluno.chat.sampleConversations
import turmaA.grupoB.LinkStage.ui.orientador.chat.ChatOrientadorScreen
import turmaA.grupoB.LinkStage.ui.orientador.internships.MentorInternshipDetailScreen
import turmaA.grupoB.LinkStage.ui.orientador.home.HomeOrientadorScreen
import turmaA.grupoB.LinkStage.ui.orientador.internships.InternshipsOrientadorScreen
import turmaA.grupoB.LinkStage.ui.orientador.notifications.NotificationsOrientadorScreen
import turmaA.grupoB.LinkStage.ui.orientador.settings.SettingsOrientadorScreen
import turmaA.grupoB.LinkStage.ui.orientador.students.StudentsOrientadorScreen
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.viewmodel.advisorhome.AdvisorHomeViewModel
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorDashboardViewModel
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorInternshipDetailViewModel
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorStudentDetailViewModel
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorDashboardViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorInternshipDetailViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.orientador.OrientadorStudentDetailViewModelFactory

object OrientadorRoutes {
    const val HOME = "orientador_home"
    const val STUDENTS = "orientador_students"
    const val INTERNSHIPS = "orientador_internships"
    const val MESSAGES = "orientador_messages"
    const val SETTINGS = "orientador_settings"
    const val NOTIFICATIONS = "orientador_notifications"
    const val CHAT = "orientador_chat/{conversationId}"
    const val MENTOR_STUDENT_DETAIL = "mentor_student/{studentId}"
    const val MENTOR_CHECKPOINT_DETAIL = "mentor_checkpoint/{checkpointId}"
    const val MENTOR_INTERNSHIP_DETAIL = "mentor_internship/{internshipId}"
    const val FINAL_GRADE_SUBMITTED = "final_grade_submitted/{internshipId}"
    const val PRIVACY_POLICY = "orientador_privacy_policy"

    fun chatRoute(conversationId: String) = "orientador_chat/$conversationId"
    fun mentorStudentDetail(studentId: String) = "mentor_student/$studentId"
    fun mentorCheckpointDetail(checkpointId: String) = "mentor_checkpoint/$checkpointId"
    fun mentorInternshipDetail(internshipId: String) = "mentor_internship/$internshipId"
    fun finalGradeSubmittedRoute(internshipId: String) = "final_grade_submitted/$internshipId"
}

private data class OrientadorTab(
    @StringRes val titleResId: Int,
    val icon: ImageVector,
    val route: String,
)

private val orientadorTabs = listOf(
    OrientadorTab(R.string.tab_home, Icons.Outlined.Home, OrientadorRoutes.HOME),
    OrientadorTab(R.string.tab_students, Icons.Outlined.People, OrientadorRoutes.STUDENTS),
    OrientadorTab(R.string.tab_internships, Icons.Outlined.Work, OrientadorRoutes.INTERNSHIPS),
    OrientadorTab(R.string.tab_messages, Icons.AutoMirrored.Outlined.Chat, OrientadorRoutes.MESSAGES),
    OrientadorTab(R.string.tab_settings, Icons.Outlined.Settings, OrientadorRoutes.SETTINGS),
)

@Composable
fun OrientadorMainScreen(onLogout: () -> Unit = {}) {
    val navController = rememberNavController()
    val advisorHomeViewModel: AdvisorHomeViewModel = viewModel()
    val orientadorDashboardViewModel: OrientadorDashboardViewModel = viewModel(factory = OrientadorDashboardViewModelFactory())
    val orientadorStudentDetailViewModel: OrientadorStudentDetailViewModel = viewModel(factory = OrientadorStudentDetailViewModelFactory())
    val orientadorInternshipDetailViewModel: OrientadorInternshipDetailViewModel = viewModel(factory = OrientadorInternshipDetailViewModelFactory())
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = orientadorTabs.any { tab ->
        currentDestination?.hierarchy?.any { it.route == tab.route } == true
    }

    val chatDataSource: ChatDataSource = remember {
        OrientadorChatDataSource(
            authRepository = AuthRepository(),
            supervisorRepository = SupervisorRepository(),
            internshipRepository = InternshipRepository(),
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
                    orientadorTabs.forEach { tab ->
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
            startDestination = OrientadorRoutes.HOME,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(OrientadorRoutes.HOME) {
                HomeOrientadorScreen(
                    navController = navController,
                    advisorHomeViewModel = advisorHomeViewModel,
                    orientadorDashboardViewModel = orientadorDashboardViewModel,
                    chatViewModel = chatViewModel,
                )
            }
            composable(OrientadorRoutes.STUDENTS) {
                StudentsOrientadorScreen(
                    navController = navController,
                    orientadorDashboardViewModel = orientadorDashboardViewModel,
                )
            }
            composable(OrientadorRoutes.INTERNSHIPS) {
                InternshipsOrientadorScreen(
                    navController = navController,
                    orientadorDashboardViewModel = orientadorDashboardViewModel,
                )
            }
            composable(OrientadorRoutes.MESSAGES) {
                ChatOrientadorScreen(
                    chatViewModel = chatViewModel,
                    onOpenChat = { conversationId ->
                        navController.navigate(OrientadorRoutes.chatRoute(conversationId))
                    },
                )
            }
            composable(OrientadorRoutes.SETTINGS) {
                SettingsOrientadorScreen(
                    onLogout = onLogout,
                    onNotificationsClick = {
                        navController.navigate(OrientadorRoutes.NOTIFICATIONS)
                    },
                    onPrivacyPolicyClick = {
                        navController.navigate(OrientadorRoutes.PRIVACY_POLICY)
                    }
                )
            }
            composable(OrientadorRoutes.PRIVACY_POLICY) {
                PrivacyPolicyScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(OrientadorRoutes.NOTIFICATIONS) {
                NotificationsOrientadorScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = OrientadorRoutes.CHAT,
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
                route = OrientadorRoutes.MENTOR_STUDENT_DETAIL,
                arguments = listOf(navArgument("studentId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val studentId = backStackEntry.arguments?.getString("studentId") ?: return@composable
                MentorStudentDetailScreen(
                    studentId = studentId,
                    navController = navController,
                    advisorHomeViewModel = advisorHomeViewModel,
                    orientadorStudentDetailViewModel = orientadorStudentDetailViewModel,
                    chatViewModel = chatViewModel,
                )
            }
            composable(
                route = OrientadorRoutes.MENTOR_CHECKPOINT_DETAIL,
                arguments = listOf(navArgument("checkpointId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val checkpointId = backStackEntry.arguments?.getString("checkpointId") ?: return@composable
                MentorCheckpointDetailScreen(
                    checkpointId = checkpointId,
                    navController = navController,
                    orientadorDashboardViewModel = orientadorDashboardViewModel,
                )
            }
            composable(
                route = OrientadorRoutes.MENTOR_INTERNSHIP_DETAIL,
                arguments = listOf(navArgument("internshipId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val internshipId = backStackEntry.arguments?.getString("internshipId") ?: return@composable
                MentorInternshipDetailScreen(
                    internshipId = internshipId,
                    navController = navController,
                    orientadorInternshipDetailViewModel = orientadorInternshipDetailViewModel,
                )
            }
            composable(
                route = OrientadorRoutes.FINAL_GRADE_SUBMITTED,
                arguments = listOf(navArgument("internshipId") { type = NavType.StringType }),
            ) { backStackEntry ->
                val internshipId = backStackEntry.arguments?.getString("internshipId") ?: return@composable
                FinalGradeSubmittedScreen(
                    internshipId = internshipId,
                    navController = navController,
                )
            }
        }
    }
}
