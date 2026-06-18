package turmaA.grupoB.LinkStage.viewmodel.communication

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
import turmaA.grupoB.LinkStage.data.remote.model.communication.CreateMessageThreadInput
import turmaA.grupoB.LinkStage.data.remote.model.communication.MessageModel
import turmaA.grupoB.LinkStage.data.remote.model.communication.MessageThreadModel
import turmaA.grupoB.LinkStage.data.remote.model.communication.MessageThreadParticipantModel
import turmaA.grupoB.LinkStage.data.remote.model.communication.NotificationModel
import turmaA.grupoB.LinkStage.data.remote.model.communication.SendMessageInput
import turmaA.grupoB.LinkStage.data.repository.communication.CommunicationRepositoryInterface

@OptIn(ExperimentalCoroutinesApi::class)
class CommunicationViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRepository: FakeCommunicationRepository
    private lateinit var viewModel: CommunicationViewModel

    @Before
    fun setup() {
        fakeRepository = FakeCommunicationRepository()
        viewModel = CommunicationViewModel(fakeRepository)
    }

    @Test
    fun initialState_isIdle() {
        assertEquals(
            CommunicationUiState.Idle,
            viewModel.uiState.value
        )
    }

    // --- getNotificationsByUser ---

    @Test
    fun getNotificationsByUser_whenNotificationsExist_setsSuccessNotificationList() = runTest {
        fakeRepository.notifications = listOf(testNotification)

        viewModel.getNotificationsByUser("user-1")

        advanceUntilIdle()

        assertEquals(
            CommunicationUiState.SuccessNotificationList(listOf(testNotification)),
            viewModel.uiState.value
        )
    }

    @Test
    fun getNotificationsByUser_whenEmpty_setsEmptyState() = runTest {
        fakeRepository.notifications = emptyList()

        viewModel.getNotificationsByUser("user-1")

        advanceUntilIdle()

        assertEquals(
            CommunicationUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun getNotificationsByUser_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetNotifications = true

        viewModel.getNotificationsByUser("user-1")

        advanceUntilIdle()

        assertEquals(
            CommunicationUiState.Error("Erro ao carregar notificações."),
            viewModel.uiState.value
        )
    }

    // --- getUnreadNotificationsByUser ---

    @Test
    fun getUnreadNotificationsByUser_whenNotificationsExist_setsSuccessNotificationList() = runTest {
        fakeRepository.notifications = listOf(testNotification)

        viewModel.getUnreadNotificationsByUser("user-1")

        advanceUntilIdle()

        assertEquals(
            CommunicationUiState.SuccessNotificationList(listOf(testNotification)),
            viewModel.uiState.value
        )
    }

    @Test
    fun getUnreadNotificationsByUser_whenEmpty_setsEmptyState() = runTest {
        fakeRepository.notifications = emptyList()

        viewModel.getUnreadNotificationsByUser("user-1")

        advanceUntilIdle()

        assertEquals(
            CommunicationUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun getUnreadNotificationsByUser_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetUnreadNotifications = true

        viewModel.getUnreadNotificationsByUser("user-1")

        advanceUntilIdle()

        assertEquals(
            CommunicationUiState.Error("Erro ao carregar notificações."),
            viewModel.uiState.value
        )
    }

    // --- markNotificationAsRead ---

    @Test
    fun markNotificationAsRead_whenNotificationExists_setsSuccessNotification() = runTest {
        viewModel.markNotificationAsRead("notif-1")

        advanceUntilIdle()

        val actualState = viewModel.uiState.value
        assert(actualState is CommunicationUiState.SuccessNotification)
        val notification = (actualState as CommunicationUiState.SuccessNotification).message
        assertEquals("notif-1", notification.id)
        assertEquals(true, notification.isRead)
        assertEquals("Test Notification", notification.title)
        assertEquals("Test notification body", notification.message)
    }

    @Test
    fun markNotificationAsRead_whenRepositoryReturnsNull_setsEmptyState() = runTest {
        fakeRepository.returnNullNotification = true

        viewModel.markNotificationAsRead("notif-1")

        advanceUntilIdle()

        assertEquals(
            CommunicationUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun markNotificationAsRead_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnMarkNotificationAsRead = true

        viewModel.markNotificationAsRead("notif-1")

        advanceUntilIdle()

        assertEquals(
            CommunicationUiState.Error("Erro ao marcar notificação como lida."),
            viewModel.uiState.value
        )
    }

    // --- getThreadById ---

    @Test
    fun getThreadById_whenThreadExists_setsSuccessThread() = runTest {
        viewModel.getThreadById("thread-1")

        advanceUntilIdle()

        assertEquals(
            CommunicationUiState.SuccessThread(testThread),
            viewModel.uiState.value
        )
    }

    @Test
    fun getThreadById_whenThreadDoesNotExist_setsEmptyState() = runTest {
        fakeRepository.returnNullThread = true

        viewModel.getThreadById("thread-1")

        advanceUntilIdle()

        assertEquals(
            CommunicationUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun getThreadById_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetThreadById = true

        viewModel.getThreadById("thread-1")

        advanceUntilIdle()

        assertEquals(
            CommunicationUiState.Error("Erro ao carregar thread."),
            viewModel.uiState.value
        )
    }

    // --- getThreadsByInternship ---

    @Test
    fun getThreadsByInternship_whenThreadsExist_setsSuccessThreadList() = runTest {
        fakeRepository.threadsList = listOf(testThread)

        viewModel.getThreadsByInternship("internship-1")

        advanceUntilIdle()

        assertEquals(
            CommunicationUiState.SuccessThreadList(listOf(testThread)),
            viewModel.uiState.value
        )
    }

    @Test
    fun getThreadsByInternship_whenEmpty_setsEmptyState() = runTest {
        fakeRepository.threadsList = emptyList()

        viewModel.getThreadsByInternship("internship-1")

        advanceUntilIdle()

        assertEquals(
            CommunicationUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun getThreadsByInternship_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetThreadsByInternship = true

        viewModel.getThreadsByInternship("internship-1")

        advanceUntilIdle()

        assertEquals(
            CommunicationUiState.Error("Erro ao carregar threads."),
            viewModel.uiState.value
        )
    }

    // --- getThreadByApplication ---

    @Test
    fun getThreadByApplication_whenThreadsExist_setsSuccessThreadList() = runTest {
        fakeRepository.threadsList = listOf(testThread)

        viewModel.getThreadByApplication("application-1")

        advanceUntilIdle()

        assertEquals(
            CommunicationUiState.SuccessThreadList(listOf(testThread)),
            viewModel.uiState.value
        )
    }

    @Test
    fun getThreadByApplication_whenEmpty_setsEmptyState() = runTest {
        fakeRepository.threadsList = emptyList()

        viewModel.getThreadByApplication("application-1")

        advanceUntilIdle()

        assertEquals(
            CommunicationUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun getThreadByApplication_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetThreadByApplication = true

        viewModel.getThreadByApplication("application-1")

        advanceUntilIdle()

        assertEquals(
            CommunicationUiState.Error("Erro ao carregar threads."),
            viewModel.uiState.value
        )
    }

    // --- getMessagesByThread ---

    @Test
    fun getMessagesByThread_whenMessagesExist_setsSuccessList() = runTest {
        fakeRepository.messages = listOf(testMessage)

        viewModel.getMessagesByThread("thread-1")

        advanceUntilIdle()

        assertEquals(
            CommunicationUiState.SuccessList(listOf(testMessage)),
            viewModel.uiState.value
        )
    }

    @Test
    fun getMessagesByThread_whenEmpty_setsEmptyState() = runTest {
        fakeRepository.messages = emptyList()

        viewModel.getMessagesByThread("thread-1")

        advanceUntilIdle()

        assertEquals(
            CommunicationUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun getMessagesByThread_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnGetMessages = true

        viewModel.getMessagesByThread("thread-1")

        advanceUntilIdle()

        assertEquals(
            CommunicationUiState.Error("Erro ao carregar mensagens."),
            viewModel.uiState.value
        )
    }

    // --- sendMessage ---

    @Test
    fun sendMessage_whenSuccessful_setsSuccessState() = runTest {
        val input = SendMessageInput(
            threadId = "thread-1",
            senderId = "sender-1",
            content = "Hello test"
        )

        viewModel.sendMessage(input)

        advanceUntilIdle()

        val actualState = viewModel.uiState.value
        assert(actualState is CommunicationUiState.Success)
        val message = (actualState as CommunicationUiState.Success).message
        assertEquals("thread-1", message.threadId)
        assertEquals("sender-1", message.senderId)
        assertEquals("Hello test", message.content)
    }

    @Test
    fun sendMessage_whenRepositoryReturnsNull_setsEmptyState() = runTest {
        fakeRepository.returnNullMessage = true
        val input = SendMessageInput(
            threadId = "thread-1",
            senderId = "sender-1",
            content = "Hello test"
        )

        viewModel.sendMessage(input)

        advanceUntilIdle()

        assertEquals(
            CommunicationUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun sendMessage_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnSendMessage = true
        val input = SendMessageInput(
            threadId = "thread-1",
            senderId = "sender-1",
            content = "Hello test"
        )

        viewModel.sendMessage(input)

        advanceUntilIdle()

        assertEquals(
            CommunicationUiState.Error("Erro ao enviar mensagem."),
            viewModel.uiState.value
        )
    }

    // --- markMessageAsRead ---

    @Test
    fun markMessageAsRead_whenSuccessful_setsSuccessState() = runTest {
        viewModel.markMessageAsRead("msg-1")

        advanceUntilIdle()

        val actualState = viewModel.uiState.value
        assert(actualState is CommunicationUiState.Success)
        val message = (actualState as CommunicationUiState.Success).message
        assertEquals("msg-1", message.id)
        assertEquals(true, message.isRead)
    }

    @Test
    fun markMessageAsRead_whenRepositoryReturnsNull_setsEmptyState() = runTest {
        fakeRepository.returnNullMessageOnMarkRead = true

        viewModel.markMessageAsRead("msg-1")

        advanceUntilIdle()

        assertEquals(
            CommunicationUiState.Empty,
            viewModel.uiState.value
        )
    }

    @Test
    fun markMessageAsRead_whenRepositoryThrows_setsErrorState() = runTest {
        fakeRepository.shouldThrowOnMarkMessageAsRead = true

        viewModel.markMessageAsRead("msg-1")

        advanceUntilIdle()

        assertEquals(
            CommunicationUiState.Error("Erro ao marcar mensagem como lida."),
            viewModel.uiState.value
        )
    }

    // --- companion test data ---

    private companion object {
        val testMessage = MessageModel(
            id = "msg-001",
            threadId = "thread-001",
            senderId = "sender-001",
            content = "Test message content",
            isRead = false,
            createdAt = "2026-01-01T00:00:00Z"
        )

        val testNotification = NotificationModel(
            id = "notif-001",
            userId = "user-001",
            isRead = false,
            title = "Test Notification",
            message = "Test notification body",
            createdAt = "2026-01-01T00:00:00Z"
        )

        val testThread = MessageThreadModel(
            id = "thread-001",
            applicationId = "app-001",
            internshipId = "intern-001",
            createdAt = "2026-01-01T00:00:00Z"
        )

        val testParticipant = MessageThreadParticipantModel(
            id = "part-001",
            threadId = "thread-001",
            userId = "user-001",
            createdAt = "2026-01-01T00:00:00Z"
        )
    }
}

// --- Fake Repository ---

private class FakeCommunicationRepository : CommunicationRepositoryInterface {

    // Flags for granular error control
    var shouldThrowOnGetNotifications = false
    var shouldThrowOnGetUnreadNotifications = false
    var shouldThrowOnMarkNotificationAsRead = false
    var shouldThrowOnGetThreadById = false
    var shouldThrowOnGetThreadsByInternship = false
    var shouldThrowOnGetThreadByApplication = false
    var shouldThrowOnGetParticipants = false
    var shouldThrowOnGetMessages = false
    var shouldThrowOnSendMessage = false
    var shouldThrowOnMarkMessageAsRead = false

    // Null return overrides
    var returnNullNotification = false
    var returnNullThread = false
    var returnNullMessage = false
    var returnNullMessageOnMarkRead = false

    // Data stores
    var notifications: List<NotificationModel> = emptyList()
    var threadsList: List<MessageThreadModel> = emptyList()
    var messages: List<MessageModel> = emptyList()
    var participants: List<MessageThreadParticipantModel> = emptyList()

    private val defaultNotification = NotificationModel(
        id = "notif-001",
        userId = "user-001",
        isRead = false,
        title = "Test Notification",
        message = "Test notification body",
        createdAt = "2026-01-01T00:00:00Z"
    )

    private val defaultThread = MessageThreadModel(
        id = "thread-001",
        applicationId = "app-001",
        internshipId = "intern-001",
        createdAt = "2026-01-01T00:00:00Z"
    )

    private val defaultMessage = MessageModel(
        id = "msg-001",
        threadId = "thread-001",
        senderId = "sender-001",
        content = "Test message content",
        isRead = false,
        createdAt = "2026-01-01T00:00:00Z"
    )

    override suspend fun getNotificationsByUser(userId: String): List<NotificationModel> {
        if (shouldThrowOnGetNotifications) {
            throw IllegalStateException("Erro ao carregar notificações.")
        }
        return notifications
    }

    override suspend fun getUnreadNotificationsByUser(userId: String): List<NotificationModel> {
        if (shouldThrowOnGetUnreadNotifications) {
            throw IllegalStateException("Erro ao carregar notificações.")
        }
        return notifications.filter { !it.isRead }
    }

    override suspend fun markNotificationAsRead(notificationId: String): NotificationModel? {
        if (shouldThrowOnMarkNotificationAsRead) {
            throw IllegalStateException("Erro ao marcar notificação como lida.")
        }
        if (returnNullNotification) return null
        return defaultNotification.copy(id = notificationId, isRead = true)
    }

    override suspend fun getThreadById(threadId: String): MessageThreadModel? {
        if (shouldThrowOnGetThreadById) {
            throw IllegalStateException("Erro ao carregar thread.")
        }
        if (returnNullThread) return null
        return threadsList.firstOrNull { it.id == threadId } ?: defaultThread
    }

    override suspend fun getThreadsByInternship(internshipId: String): List<MessageThreadModel> {
        if (shouldThrowOnGetThreadsByInternship) {
            throw IllegalStateException("Erro ao carregar threads.")
        }
        return threadsList
    }

    override suspend fun getThreadByApplication(applicationId: String): List<MessageThreadModel> {
        if (shouldThrowOnGetThreadByApplication) {
            throw IllegalStateException("Erro ao carregar threads.")
        }
        return threadsList
    }

    override suspend fun getThreadsWithParticipants(userIds: List<String>): List<MessageThreadModel> = threadsList

    override suspend fun getParticipantsByThread(threadId: String): List<MessageThreadParticipantModel> {
        if (shouldThrowOnGetParticipants) {
            throw IllegalStateException("Erro ao carregar participantes.")
        }
        return participants
    }

    override suspend fun getMessagesByThread(threadId: String): List<MessageModel> {
        if (shouldThrowOnGetMessages) {
            throw IllegalStateException("Erro ao carregar mensagens.")
        }
        return messages
    }

    override suspend fun sendMessage(input: SendMessageInput): MessageModel? {
        if (shouldThrowOnSendMessage) {
            throw IllegalStateException("Erro ao enviar mensagem.")
        }
        if (returnNullMessage) return null
        return defaultMessage.copy(
            threadId = input.threadId,
            senderId = input.senderId,
            content = input.content
        )
    }

    override suspend fun markMessageAsRead(messageId: String): MessageModel? {
        if (shouldThrowOnMarkMessageAsRead) {
            throw IllegalStateException("Erro ao marcar mensagem como lida.")
        }
        if (returnNullMessageOnMarkRead) return null
        return defaultMessage.copy(id = messageId, isRead = true)
    }

    override suspend fun createThread(
        input: CreateMessageThreadInput,
        participantUserIds: List<String>,
    ): MessageThreadModel = MessageThreadModel(
        id = "new-thread",
        internshipId = input.internshipId,
        applicationId = input.applicationId,
        createdAt = "2026-01-01T00:00:00Z",
    )

    override suspend fun createThread(
        internshipId: String?,
        applicationId: String?
    ): MessageThreadModel? = MessageThreadModel(
        id = "new-thread",
        internshipId = internshipId,
        applicationId = applicationId,
        createdAt = "2026-01-01T00:00:00Z",
    )

    override suspend fun createThreadParticipant(
        threadId: String,
        userId: String
    ): MessageThreadParticipantModel = MessageThreadParticipantModel(
        id = "$threadId-$userId",
        threadId = threadId,
        userId = userId,
        createdAt = "2026-01-01T00:00:00Z",
    )
}

// --- MainDispatcherRule (kept local) ---

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
