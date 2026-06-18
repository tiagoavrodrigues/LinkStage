package turmaA.grupoB.LinkStage.viewmodel.auth

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import turmaA.grupoB.LinkStage.data.remote.model.auth.SignInInput
import turmaA.grupoB.LinkStage.data.remote.model.auth.SignUpInput
import turmaA.grupoB.LinkStage.data.remote.model.enums.UserRole
import turmaA.grupoB.LinkStage.data.remote.model.user.ProfileModel
import turmaA.grupoB.LinkStage.data.remote.model.user.UpdateProfileInput
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepositoryInterface

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private lateinit var fakeRepository: FakeAuthRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        fakeRepository = FakeAuthRepository()
        viewModel = AuthViewModel(fakeRepository)
    }

    @Test
    fun initialState_isIdle() {
        assertEquals(
            AuthUiState.Idle, // expected
            viewModel.uiState.value // actual
        )
    }

    @Test
    fun signIn_withValidCredentials_setsSuccessState() = runTest {
        fakeRepository.currentProfile = testProfile

        viewModel.signIn(
            SignInInput(
                email = "test@linkstage.test",
                password = "password123!"
            )
        )

        advanceUntilIdle()

        assertEquals(
            AuthUiState.Success(testProfile),
            viewModel.uiState.value
        )
    }

    @Test
    fun signIn_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnSignIn = true

        viewModel.signIn(
            SignInInput(
                email="test@linkstage.test",
                password="wrong-password"
            )
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertTrue(state is AuthUiState.Error)
        assertEquals(
            "Credenciais inválidas.",
            (state as AuthUiState.Error).message
        )
    }

    @Test
    fun signUp_withValidData_setsSuccessState() = runTest {
        fakeRepository.currentProfile = testProfile

        viewModel.signUp(
            SignUpInput(
                name = "Test User",
                email = "test@linkstage.test",
                password = "Password123!",
                phone = "900000000",
                role = UserRole.STUDENT,
                rgpdConsent = true
            )
        )

        advanceUntilIdle()

        assertEquals(
            AuthUiState.Success(testProfile),
            viewModel.uiState.value
        )
    }

    @Test
    fun signOut_setsUnauthenticatedState() = runTest {
        fakeRepository.currentProfile = testProfile

        viewModel.signOut()

        advanceUntilIdle()

        assertEquals(
            AuthUiState.Unauthenticated,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadCurrentUserProfile_whenProfileExists_setsSuccessState() = runTest {
        fakeRepository.currentProfile = testProfile

        viewModel.loadCurrentUserProfile()

        advanceUntilIdle()

        assertEquals(
            AuthUiState.Success(testProfile),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadCurrentUserProfile_whenProfileDoesNotExist_setsUnauthenticatedState() = runTest {
        fakeRepository.currentProfile = null

        viewModel.loadCurrentUserProfile()

        advanceUntilIdle()

        assertEquals(
            AuthUiState.Unauthenticated,
            viewModel.uiState.value
        )
    }

    @Test
    fun resetState_setsIdleState() = runTest {
        fakeRepository.shouldThrowOnSignIn = true

        viewModel.signIn(
            SignInInput(
                email="test@linkstage.test",
                password="wrong-password"
            )
        )

        advanceUntilIdle()

        viewModel.resetState()

        assertEquals(
            AuthUiState.Idle,
            viewModel.uiState.value
        )
    }
    private companion object{
        val testProfile = ProfileModel(
            id = "00000000-0000-0000-0000-000000000001",
            name = "Test User",
            email = "test@linkstage.test",
            phone = "900000000",
            photoUrl = null,
            role = UserRole.STUDENT,
            active = true,
            rgpdConsent = true,
            rgpdConsentAt = "2026-01-01T00:00:00Z",
            createdAt = "2026-01-01T00:00:00Z",
            updatedAt = "2026-01-01T00:00:00Z"
        )
    }

}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }

}

private class FakeAuthRepository : AuthRepositoryInterface {
    var currentProfile: ProfileModel? = null
    var shouldThrowOnSignIn: Boolean = false
    var shouldThrowOnSignUp: Boolean = false
    var shouldThrowOnSignOut: Boolean = false

    override suspend fun signUp(input: SignUpInput): ProfileModel {
        if (shouldThrowOnSignUp) {
            throw IllegalStateException("Erro ao criar conta.")
        }

        return currentProfile ?: throw IllegalStateException("Profile não encontrado.")
    }

    override suspend fun signIn(input: SignInInput) {
        if (shouldThrowOnSignIn) {
            throw IllegalStateException("Credenciais inválidas.")
        }
    }

    override suspend fun signOut() {
        if (shouldThrowOnSignOut) {
            throw IllegalStateException("Erro ao terminar sessão.")
        }

        currentProfile = null
    }

    override fun getCurrentUserId(): String? {
        return currentProfile?.id
    }

    override fun isUserLoggedIn(): Boolean {
        return currentProfile != null
    }

    override suspend fun getCurrentUserProfile(): ProfileModel? {
        return currentProfile
    }

    override suspend fun updateProfile(
        userId: String,
        input: UpdateProfileInput
    ): ProfileModel {
        return currentProfile ?: throw IllegalStateException("Profile não encontrado.")
    }

    override suspend fun updatePassword(newPassword: String) {
    }

    override suspend fun createManagedAccount(input: SignUpInput): ProfileModel {
        return currentProfile ?: throw IllegalStateException("Profile não encontrado.")
    }

    override suspend fun setProfileActive(userId: String, active: Boolean) {
    }
}