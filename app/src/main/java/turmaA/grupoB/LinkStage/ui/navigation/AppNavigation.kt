package turmaA.grupoB.LinkStage.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import turmaA.grupoB.LinkStage.data.remote.model.auth.SignInInput
import turmaA.grupoB.LinkStage.ui.admin.AdminMainScreen
import turmaA.grupoB.LinkStage.ui.aluno.AlunoMainScreen
import turmaA.grupoB.LinkStage.ui.auth.ForceChangePasswordScreen
import turmaA.grupoB.LinkStage.ui.auth.login.LoginScreen
import turmaA.grupoB.LinkStage.ui.auth.register.RegisterScreen
import turmaA.grupoB.LinkStage.ui.auth.register.RegisterDataScreen
import turmaA.grupoB.LinkStage.ui.auth.register.RegisterSkillsScreen
import turmaA.grupoB.LinkStage.ui.auth.forgotpassword.ForgotPasswordScreen
import turmaA.grupoB.LinkStage.ui.auth.updatepassword.UpdatePasswordScreen
import turmaA.grupoB.LinkStage.ui.introSliders.IntroSlidersScreen
import turmaA.grupoB.LinkStage.ui.splash.SplashScreen
import turmaA.grupoB.LinkStage.ui.instituicao.InstituicaoMainScreen
import turmaA.grupoB.LinkStage.ui.instituicao.InstitutionPendingScreen
import turmaA.grupoB.LinkStage.ui.orientador.OrientadorMainScreen
import turmaA.grupoB.LinkStage.data.remote.model.enums.UserRole
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepository
import turmaA.grupoB.LinkStage.data.repository.student.StudentRepository
import turmaA.grupoB.LinkStage.viewmodel.auth.AuthUiState
import turmaA.grupoB.LinkStage.viewmodel.auth.AuthViewModel
import turmaA.grupoB.LinkStage.viewmodel.auth.AuthViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.auth.RegisterStudentInput
import turmaA.grupoB.LinkStage.viewmodel.auth.RegisterStudentUiState
import turmaA.grupoB.LinkStage.viewmodel.auth.RegisterStudentViewModel
import turmaA.grupoB.LinkStage.viewmodel.auth.RegisterStudentViewModelFactory

object Routes {
    const val SPLASH = "splash"
    const val INTRO = "intro"
    const val LOGIN = "auth/login"
    const val REGISTER = "auth/register"
    const val REGISTER_DATA = "auth/register-data/{profile}"
    const val REGISTER_SKILLS = "auth/register-skills"
    const val FORGOT_PASSWORD = "auth/forgot-password"
    const val UPDATE_PASSWORD = "auth/update-password"

    const val FORCE_CHANGE_PASSWORD = "force_change_password"
    const val INSTITUTION_PENDING = "institution_pending"

    const val ADMIN_MAIN = "admin"
    const val ALUNO_MAIN = "aluno"
    const val ORIENTADOR_MAIN = "orientador"
    const val INSTITUICAO_MAIN = "instituicao"
}

private const val USE_MOCK_LOGIN_FALLBACK = false // Only for local UI testing.
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.SPLASH,
) {

    var pendingStudentRegisterData by remember {
        mutableStateOf<Map<String, String>?>(null)
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            fadeIn(animationSpec = tween(400)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(400)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(400)) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(400)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(400)) + slideIntoContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(400)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(400)) + slideOutOfContainer(
                AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(400)
            )
        }
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Routes.INTRO) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.INTRO) {
            IntroSlidersScreen(
                onFinish = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.INTRO) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.LOGIN) {
            val authViewModel: AuthViewModel = viewModel(
                factory = AuthViewModelFactory(AuthRepository())
            )

            val authUiState by authViewModel.uiState.collectAsState()

            LaunchedEffect(authUiState) {
                val state = authUiState

                if (state is AuthUiState.Success) {
                    val destination = when (state.profile.role) {
                        UserRole.ADMIN -> Routes.ADMIN_MAIN
                        UserRole.STUDENT -> Routes.ALUNO_MAIN
                        UserRole.SUPERVISOR -> Routes.ORIENTADOR_MAIN
                        UserRole.INSTITUTION -> Routes.INSTITUICAO_MAIN
                    }

                    authViewModel.resetState()

                    navController.navigate(destination) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            }

            LoginScreen(
                onLoginClick = { email, password ->
                    if (!USE_MOCK_LOGIN_FALLBACK) {
                        authViewModel.signIn(
                            SignInInput(
                                email = email.trim(),
                                password = password
                            )
                        )
                    } else {
                        // Hardcoded: simulate a mentor account that must change password.
                        val mustChangePassword = false
                        if (mustChangePassword) {
                            navController.navigate(Routes.FORCE_CHANGE_PASSWORD) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Routes.ORIENTADOR_MAIN) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Routes.REGISTER)
                },
                onForgotPasswordClick = {
                    navController.navigate(Routes.FORGOT_PASSWORD)
                },
                isLoading = authUiState is AuthUiState.Loading,
                errorMessage = (authUiState as? AuthUiState.Error)?.message
            )
        }
        composable(Routes.FORCE_CHANGE_PASSWORD) {
            ForceChangePasswordScreen(
                navController = navController,
                userDestination = Routes.ORIENTADOR_MAIN,
            )
        }
        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onBackToLogin = {
                    navController.popBackStack()
                },
                onSendResetLinkClick = {
                    // Placeholder for logic handled by colleagues
                    // For UI flow:
                    navController.navigate(Routes.UPDATE_PASSWORD)
                }
            )
        }
        composable(Routes.UPDATE_PASSWORD) {
            UpdatePasswordScreen(
                onBackToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onUpdatePasswordClick = {
                    // Placeholder for logic handled by colleagues
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                onBackToLogin = {
                    navController.popBackStack()
                },
                onContinueClick = { profile ->
                    navController.navigate("auth/register-data/$profile")
                }
            )
        }
        composable(
            route = Routes.REGISTER_DATA,
            arguments = listOf(
                androidx.navigation.navArgument("profile") {
                    type = androidx.navigation.NavType.StringType
                }
            )
        ) { backStackEntry ->
            val profileStr = backStackEntry.arguments?.getString("profile") ?: ""
            val profile = try {
                UserRole.valueOf(profileStr)
            } catch (_: IllegalArgumentException) {
                UserRole.STUDENT
            }
            RegisterDataScreen(
                selectedProfile = profile,
                onBackClick = {
                    navController.popBackStack()
                },
                onContinueClick = { data ->
                    if (profile == UserRole.STUDENT) {
                        pendingStudentRegisterData = data
                        navController.navigate(Routes.REGISTER_SKILLS)
                    } else {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable(Routes.REGISTER_SKILLS) {
            val registerStudentViewModel: RegisterStudentViewModel = viewModel(
                factory = RegisterStudentViewModelFactory(
                    authRepository = AuthRepository(),
                    studentRepository = StudentRepository()
                )
            )

            val registerStudentUiState by registerStudentViewModel.uiState.collectAsState()

            RegisterSkillsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onRegisterClick = { skills ->
                    val registerData = pendingStudentRegisterData

                    if (registerData == null) {
                        navController.navigate(Routes.REGISTER) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                        }
                        return@RegisterSkillsScreen
                    }

                    registerStudentViewModel.registerStudent(
                        RegisterStudentInput(
                            name = registerData["name"].orEmpty(),
                            email = registerData["email"].orEmpty(),
                            password = registerData["password"].orEmpty(),
                            phone = null,
                            studentNumber = registerData["email"].orEmpty(),
                            course = registerData["institute"].orEmpty(),
                            academicYear = null,
                            rgpdConsent = true
                        )
                    )
                },
                onSuccessConfirm = {
                    pendingStudentRegisterData = null
                    registerStudentViewModel.resetState()

                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                isLoading = registerStudentUiState is RegisterStudentUiState.Loading,
                isSuccess = registerStudentUiState is RegisterStudentUiState.Success,
                errorMessage = (registerStudentUiState as? RegisterStudentUiState.Error)?.message
            )
        }
        composable(Routes.ADMIN_MAIN) {
            AdminMainScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.ALUNO_MAIN) {
            AlunoMainScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.ORIENTADOR_MAIN) {
            OrientadorMainScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
        composable(Routes.INSTITUTION_PENDING) {
            InstitutionPendingScreen(navController = navController)
        }
        composable(Routes.INSTITUICAO_MAIN) { InstituicaoMainScreen() }
    }
}