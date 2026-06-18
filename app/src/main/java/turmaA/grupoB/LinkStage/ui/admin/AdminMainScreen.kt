package turmaA.grupoB.LinkStage.ui.admin

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import turmaA.grupoB.LinkStage.ui.admin.home.HomeAdminScreen
import turmaA.grupoB.LinkStage.ui.admin.institutions.InstitutionDetailAdminScreen
import turmaA.grupoB.LinkStage.ui.admin.institutions.InstitutionsAdminScreen
import turmaA.grupoB.LinkStage.ui.admin.settings.SettingsAdminScreen
import turmaA.grupoB.LinkStage.ui.admin.students.InternshipDetailAdminScreen
import turmaA.grupoB.LinkStage.ui.admin.students.MentorDetailAdminScreen
import turmaA.grupoB.LinkStage.ui.admin.students.StudentDetailAdminScreen
import turmaA.grupoB.LinkStage.ui.admin.students.StudentsAdminScreen
import turmaA.grupoB.LinkStage.ui.common.PrivacyPolicyScreen
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.LightBlue

object AdminRoutes {
    const val HOME = "admin_home"
    const val STUDENTS = "admin_students"
    const val STUDENT_DETAIL = "admin_student/{id}"
    const val MENTOR_DETAIL = "admin_mentor/{id}"
    const val INSTITUTIONS = "admin_institutions"
    const val INSTITUTION_DETAIL = "admin_institution/{id}"
    const val INTERNSHIP_DETAIL = "admin_internship/{id}"
    const val SETTINGS = "admin_settings"
    const val PRIVACY_POLICY = "admin_privacy_policy"

    fun studentDetail(id: String) = "admin_student/$id"
    fun mentorDetail(id: String) = "admin_mentor/$id"
    fun institutionDetail(id: String) = "admin_institution/$id"
    fun internshipDetail(id: String) = "admin_internship/$id"
}

private data class AdminTab(
    @StringRes val titleResId: Int,
    val icon: ImageVector,
    val route: String,
)

private val adminTabs = listOf(
    AdminTab(R.string.tab_home, Icons.Outlined.Home, AdminRoutes.HOME),
    AdminTab(R.string.tab_users, Icons.Outlined.People, AdminRoutes.STUDENTS),
    AdminTab(R.string.tab_institutions, Icons.Outlined.AccountBalance, AdminRoutes.INSTITUTIONS),
    AdminTab(R.string.tab_settings, Icons.Outlined.Settings, AdminRoutes.SETTINGS),
)

@Composable
fun AdminMainScreen(onLogout: () -> Unit = {}) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = adminTabs.any { tab ->
        currentDestination?.hierarchy?.any { it.route == tab.route } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = DarkBlue) {
                    adminTabs.forEach { tab ->
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
            startDestination = AdminRoutes.HOME,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(AdminRoutes.HOME) {
                HomeAdminScreen(navController = navController)
            }
            composable(AdminRoutes.STUDENTS) {
                StudentsAdminScreen(navController = navController)
            }
            composable(
                route = AdminRoutes.STUDENT_DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: return@composable
                StudentDetailAdminScreen(
                    studentId = id,
                    onBack = { navController.popBackStack() },
                    onViewInternship = { internshipId ->
                        navController.navigate(AdminRoutes.internshipDetail(internshipId))
                    }
                )
            }
            composable(
                route = AdminRoutes.MENTOR_DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: return@composable
                MentorDetailAdminScreen(
                    mentorId = id,
                    onBack = { navController.popBackStack() },
                )
            }
            composable(AdminRoutes.INSTITUTIONS) {
                InstitutionsAdminScreen(navController = navController)
            }
            composable(
                route = AdminRoutes.INSTITUTION_DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: return@composable
                InstitutionDetailAdminScreen(
                    institutionId = id,
                    onBack = { navController.popBackStack() },
                )
            }
            composable(
                route = AdminRoutes.INTERNSHIP_DETAIL,
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: return@composable
                InternshipDetailAdminScreen(
                    internshipId = id,
                    onBack = { navController.popBackStack() },
                )
            }
            composable(AdminRoutes.SETTINGS) {
                SettingsAdminScreen(
                    onLogout = onLogout,
                    onPrivacyPolicyClick = {
                        navController.navigate(AdminRoutes.PRIVACY_POLICY)
                    }
                )
            }
            composable(AdminRoutes.PRIVACY_POLICY) {
                PrivacyPolicyScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
