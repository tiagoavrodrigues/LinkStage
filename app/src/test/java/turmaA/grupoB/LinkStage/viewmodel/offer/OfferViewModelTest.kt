package turmaA.grupoB.LinkStage.viewmodel.offer

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
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import turmaA.grupoB.LinkStage.data.remote.model.enums.OfferStatus
import turmaA.grupoB.LinkStage.data.remote.model.offer.CreateOfferInput
import turmaA.grupoB.LinkStage.data.remote.model.offer.InternshipOfferModel
import turmaA.grupoB.LinkStage.data.remote.model.offer.UpdateOfferInput
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepositoryInterface
import turmaA.grupoB.LinkStage.viewmodel.auth.MainDispatcherRule

@OptIn(ExperimentalCoroutinesApi::class)
class OfferViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepository: FakeOfferRepository
    private lateinit var viewModel: OfferViewModel

    @Before
    fun setup() {
        fakeRepository = FakeOfferRepository()
        viewModel = OfferViewModel(fakeRepository)
    }

    @Test
    fun initialState_isIdle() {
        assertEquals(
            OfferUiState.Idle,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadPublishedOffers_whenOffersExist_setsSuccessListState() = runTest {
        fakeRepository.offers = listOf(testOffer)

        viewModel.loadPublishedOffers()

        advanceUntilIdle()

        assertEquals(
            OfferUiState.SuccessList(listOf(testOffer)),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadPublishedOffers_whenOffersDoNotExist_setsEmptyState() = runTest {
        fakeRepository.offers = emptyList()

        viewModel.loadPublishedOffers()

        advanceUntilIdle()

        assertEquals(
            OfferUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadPublishedOffers_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetPublishedOffers = true

        viewModel.loadPublishedOffers()

        advanceUntilIdle()

        assertEquals(
            OfferUiState.Error("Erro ao carregar ofertas publicadas."),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadOffersByInstitution_whenOffersExist_setsSuccessListState() = runTest {
        fakeRepository.offers = listOf(testOffer)

        viewModel.loadOffersByInstitution(testInstitutionId)

        advanceUntilIdle()

        assertEquals(
            OfferUiState.SuccessList(listOf(testOffer)),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadOffersByInstitution_whenOffersDoNotExist_setsEmptyState() = runTest {
        fakeRepository.offers = emptyList()

        viewModel.loadOffersByInstitution(testInstitutionId)

        advanceUntilIdle()

        assertEquals(
            OfferUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun loadOffersById_whenOfferExists_setsSuccessState() = runTest {
        fakeRepository.offers = listOf(testOffer)

        viewModel.loadOfferById(testOffer.id)

        advanceUntilIdle()

        assertEquals(
            OfferUiState.Success(testOffer),
            viewModel.uiState.value
        )
    }

    @Test
    fun loadOffersId_whenOfferDoesNotExist_setsEmptyState() = runTest {
        fakeRepository.offers = emptyList()

        viewModel.loadOfferById("unknow-offer-id")

        advanceUntilIdle()

        assertEquals(
            OfferUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun createOffer_withValidData_setsSuccessState() = runTest {
        fakeRepository.offerToReturn = testOffer

        viewModel.createOffer(testCreateOfferInput)

        advanceUntilIdle()

        assertEquals(
            OfferUiState.Success(testOffer),
            viewModel.uiState.value
        )
    }

    @Test
    fun updateOffer_withValidData_setsSuccessState() = runTest {
        fakeRepository.offerToReturn = updatedOffer

        viewModel.updateOffer(
            offerId = testOffer.id,
            input = testUpdateOfferInput
        )

        advanceUntilIdle()

        assertEquals(
            OfferUiState.Success(updatedOffer),
            viewModel.uiState.value
        )
    }

    @Test
    fun closeOffer_setsSuccessState() = runTest {
        fakeRepository.offerToReturn = closedOffer

        viewModel.closeOffer(testOffer.id)

        advanceUntilIdle()

        assertEquals(
            OfferUiState.Success(closedOffer),
            viewModel.uiState.value
        )
    }

    @Test
    fun marOfferAsRemoved_setsSuccessState() = runTest {
        fakeRepository.offerToReturn = removedOffer

        viewModel.markOfferAsRemoved(testOffer.id)

        advanceUntilIdle()

        assertEquals(
            OfferUiState.Success(removedOffer),
            viewModel.uiState.value
        )
    }

    @Test
    fun resetState_setsIdleState() = runTest{
        fakeRepository.offers = listOf(testOffer)

        viewModel.loadPublishedOffers()

        advanceUntilIdle()

        viewModel.resetState()

        assertEquals(
            OfferUiState.Idle,
            viewModel.uiState.value
        )
    }

    private companion object {
        const val testInstitutionId = "00000000-0000-0000-0000-000000000010"

        val testOffer = InternshipOfferModel(
            id = "00000000-0000-0000-0000-000000000001",
            institutionId = testInstitutionId,
            title = "Android Developer Internship",
            description = "Estágio em desenvolvimento Android.",
            area = "Mobile Development",
            location = "Lisboa",
            modality = "Híbrido",
            salary = 750.0,
            vacancies = 1,
            requirements = "Kotlin e Android",
            publishDate = "2026-02-01",
            deadline = "2026-02-01",
            status = OfferStatus.PUBLISHED,
            createdAt = "2026-01-01T00:00:00Z",
            updatedAt = "2026-01-01T00:00:00Z"
        )

        val updatedOffer = testOffer.copy(
            title = "Updated Android Developer Internship"
        )

        val closedOffer = testOffer.copy(
            status = OfferStatus.CLOSED
        )

        val removedOffer = testOffer.copy(
            status = OfferStatus.REMOVED
        )

        val testCreateOfferInput = CreateOfferInput(
            institutionId = testInstitutionId,
            title = testOffer.title,
            description = testOffer.description,
            area = testOffer.area,
            location = testOffer.location,
            salary = testOffer.salary,
            modality = testOffer.modality,
            vacancies = testOffer.vacancies,
            requirements = testOffer.requirements,
            publishDate = testOffer.publishDate,
            deadline = testOffer.deadline,
            status = testOffer.status
        )

        val testUpdateOfferInput = UpdateOfferInput(
            title = updatedOffer.title
        )
    }
}

private class FakeOfferRepository : OfferRepositoryInterface {
    var offers: List<InternshipOfferModel> = emptyList()
    var offerToReturn: InternshipOfferModel? = null

    var shouldThrowOnGetPublishedOffers: Boolean = false
    var shouldThrowOnGetOffersByInstitution: Boolean = false
    var shouldThrowOnGetOfferById: Boolean = false
    var shouldThrowOnCreateOffer: Boolean = false
    var shouldThrowOnUpdateOffer: Boolean = false
    var shouldThrowOnCloseOffer: Boolean = false
    var shouldThrowOnMarkOfferAsRemoved: Boolean = false

    override suspend fun getPublishedOffers(): List<InternshipOfferModel> {
        if (shouldThrowOnGetPublishedOffers) {
            throw IllegalStateException("Erro ao carregar ofertas publicadas.")
        }

        return offers.filter { it.status == OfferStatus.PUBLISHED}
    }

    override suspend fun getOffersByInstitution(
        institutionId: String
    ): List<InternshipOfferModel> {
        if (shouldThrowOnGetOffersByInstitution) {
            throw IllegalStateException("Erro ao carregar ofertas da instituição.")
        }

        return offers
    }

    override suspend fun getOfferById(
        offerId: String
    ): InternshipOfferModel? {
        if (shouldThrowOnGetOfferById) {
            throw IllegalStateException("Erro ao carregar oferta.")
        }

        return offers.firstOrNull { it.id == offerId }
    }

    override suspend fun createOffer(
        input: CreateOfferInput
    ): InternshipOfferModel {
        if (shouldThrowOnCreateOffer) {
            throw IllegalStateException("Erro ao criar oferta.")
        }

        return offerToReturn ?: throw IllegalStateException("Oferta não encontrada.")
    }

    override suspend fun updateOffer(
        offerId: String,
        input: UpdateOfferInput
    ): InternshipOfferModel {
        if (shouldThrowOnUpdateOffer) {
            throw IllegalStateException("Erro ao atualizar oferta.")
        }

        return offerToReturn ?: throw IllegalStateException("Oferta não encontrada.")
    }

    override suspend fun closeOffer(
        offerId: String
    ): InternshipOfferModel {
        if (shouldThrowOnCloseOffer) {
            throw IllegalStateException("Erro ao fechar a oferta.")
        }

        return offerToReturn ?: throw IllegalStateException("Oferta não encontrada.")
    }

    override suspend fun markOfferAsRemoved(
        offerId: String
    ): InternshipOfferModel {
        if (shouldThrowOnMarkOfferAsRemoved) {
            throw IllegalStateException("Erro ao remover oferta.")
        }

        return offerToReturn ?: throw IllegalStateException("Oferta não encontrada.")
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