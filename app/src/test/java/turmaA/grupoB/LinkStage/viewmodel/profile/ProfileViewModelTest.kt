package turmaA.grupoB.LinkStage.viewmodel.profile

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.runner.Description
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import turmaA.grupoB.LinkStage.data.remote.model.enums.UserRole
import turmaA.grupoB.LinkStage.data.remote.model.user.ProfileModel
import turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepositoryInterface

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepository: FakeProfileRepository
    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setup() {
        fakeRepository = FakeProfileRepository()
        viewModel = ProfileViewModel(fakeRepository)
    }

    @Test
    fun initialState_isIdle() {
        assertEquals(
            ProfileUiState.Idle,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadProfiles_whenProfilesExist_setsSuccessListState() = runTest {
        fakeRepository.profiles = listOf(testProfile)

        viewModel.loadProfiles()

        advanceUntilIdle()

        assertEquals(
            ProfileUiState.SuccessList(listOf(testProfile)),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadProfiles_whenProfilesDoNotExist_setsEmptyState() = runTest {
        fakeRepository.profiles = emptyList()

        viewModel.loadProfiles()

        advanceUntilIdle()

        assertEquals(
            ProfileUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadProfiles_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetProfiles = true

        viewModel.loadProfiles()

        advanceUntilIdle()

        assertEquals(
            ProfileUiState.Error("Erro ao carregar perfis."),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadProfileById_WhenProfileExists_setsSuccessState() = runTest {
        fakeRepository.profiles = listOf(testProfile)

        viewModel.loadProfileById(testProfile.id)

        advanceUntilIdle()

        assertEquals(
            ProfileUiState.Success(testProfile),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadProfileById_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetProfileById = true

        viewModel.loadProfileById(testProfile.id)

        advanceUntilIdle()

        assertEquals(
            ProfileUiState.Error("Erro ao carregar perfil."),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadProfilesByRole_whenProfilesExist_setsSuccessListState() = runTest {
        fakeRepository.profiles = listOf(testProfile)

        viewModel.loadProfilesByRole("STUDENT")

        advanceUntilIdle()

        assertEquals(
            ProfileUiState.SuccessList(listOf(testProfile)),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadProfilesByRole_whenProfilesDoNotExist_setsEmptyState() = runTest{
        fakeRepository.profiles = emptyList()

        viewModel.loadProfilesByRole("STUDENT")

        advanceUntilIdle()

        assertEquals(
            ProfileUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun resetState_setsIdleState() = runTest {
        fakeRepository.profiles = listOf(testProfile)

        viewModel.loadProfiles()

        advanceUntilIdle()

        viewModel.resetState()

        assertEquals(
            ProfileUiState.Idle,
            viewModel.uiState.value
        )
    }

    private companion object {
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

private class FakeProfileRepository : ProfileRepositoryInterface {

    var profiles : List<ProfileModel> = emptyList()

    var shouldThrowOnGetProfiles: Boolean = false
    var shouldThrowOnGetProfileById: Boolean = false
    var shouldThrowOnGetProfilesByRole: Boolean = false

    override suspend fun getProfiles(): List<ProfileModel> {
        if (shouldThrowOnGetProfiles) {
            throw IllegalStateException("Erro ao carregar perfis.")
        }

        return profiles
    }

    override suspend fun getProfileById(userId: String): ProfileModel? {
        if (shouldThrowOnGetProfileById) {
            throw IllegalStateException("Erro ao carregar perfil.")
        }

        return profiles.firstOrNull { it.id == userId}
    }

    override suspend fun getProfilesByRole(role: String): List<ProfileModel> {
        if (shouldThrowOnGetProfilesByRole) {
            throw IllegalStateException("Erro ao carregar perfis por role.")
        }

        return profiles.filter { it.role.name == role }
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