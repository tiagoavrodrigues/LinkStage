package turmaA.grupoB.LinkStage.viewmodel.communication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import turmaA.grupoB.LinkStage.data.remote.model.communication.SendMessageInput
import turmaA.grupoB.LinkStage.data.remote.model.communication.CreateMessageThreadInput
import turmaA.grupoB.LinkStage.data.repository.communication.CommunicationRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepositoryInterface

class CommunicationViewModel(
    private val messageRepository: CommunicationRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface? = null,
) : ViewModel() {
    private val _uiState = MutableStateFlow<CommunicationUiState>(CommunicationUiState.Idle)
    val uiState: StateFlow<CommunicationUiState> = _uiState.asStateFlow()

    private val _conversationsUiState = MutableStateFlow<CommunicationUiState>(CommunicationUiState.Idle)
    val conversationsUiState: StateFlow<CommunicationUiState> = _conversationsUiState.asStateFlow()

    private val _chatUiState = MutableStateFlow<CommunicationUiState>(CommunicationUiState.Idle)
    val chatUiState: StateFlow<CommunicationUiState> = _chatUiState.asStateFlow()

    private val _contactsUiState = MutableStateFlow<CommunicationUiState>(CommunicationUiState.Idle)
    val contactsUiState: StateFlow<CommunicationUiState> = _contactsUiState.asStateFlow()

    private val _creationUiState = MutableStateFlow<CommunicationUiState>(CommunicationUiState.Idle)
    val creationUiState: StateFlow<CommunicationUiState> = _creationUiState.asStateFlow()

    fun loadAvailableContacts(userId: String) {
        viewModelScope.launch {
            _contactsUiState.value = CommunicationUiState.Loading

            try {
                val repository = profileRepository
                    ?: error("Repositório de perfis indisponível.")
                val contacts = repository.getProfiles()
                    .filter { profile -> profile.active && profile.id != userId }
                    .sortedBy { profile -> profile.name }

                _contactsUiState.value = if (contacts.isEmpty()) {
                    CommunicationUiState.Empty
                } else {
                    CommunicationUiState.SuccessContactList(contacts)
                }
            } catch (e: Exception) {
                _contactsUiState.value = CommunicationUiState.Error(
                    e.message ?: "Erro ao carregar contactos."
                )
            }
        }
    }

    fun createConversation(userId: String, contactUserId: String) {
        viewModelScope.launch {
            _creationUiState.value = CommunicationUiState.Loading

            try {
                val expectedParticipantIds = setOf(userId, contactUserId)
                val existingThread = messageRepository.getThreadsByUser(userId)
                    .firstOrNull { thread ->
                        val participantIds = messageRepository
                            .getParticipantsByThread(thread.id)
                            .map { participant -> participant.userId }
                            .toSet()

                        participantIds == expectedParticipantIds
                    }

                val thread = existingThread ?: messageRepository.createThread(
                    input = CreateMessageThreadInput(),
                    participantUserIds = listOf(userId, contactUserId),
                )

                _creationUiState.value = CommunicationUiState.ConversationCreated(thread.id)
                loadConversationsByUser(userId)
            } catch (e: Exception) {
                _creationUiState.value = CommunicationUiState.Error(
                    e.message ?: "Erro ao criar conversa."
                )
            }
        }
    }

    fun resetCreationState() {
        _creationUiState.value = CommunicationUiState.Idle
    }

    fun loadConversationsByUser(userId: String) {
        viewModelScope.launch {
            _conversationsUiState.value = CommunicationUiState.Loading

            try {
                val conversations = messageRepository.getThreadsByUser(userId)
                    .map { thread -> buildConversation(thread, userId) }
                    .sortedByDescending { conversation ->
                        conversation.messages.maxOfOrNull { it.createdAt } ?: conversation.thread.createdAt
                    }

                _conversationsUiState.value = if (conversations.isEmpty()) {
                    CommunicationUiState.Empty
                } else {
                    CommunicationUiState.SuccessConversationList(conversations)
                }
            } catch (e: Exception) {
                _conversationsUiState.value = CommunicationUiState.Error(
                    e.message ?: "Erro ao carregar conversas."
                )
            }
        }
    }

    fun loadConversation(threadId: String, userId: String) {
        viewModelScope.launch {
            _chatUiState.value = CommunicationUiState.Loading

            try {
                loadConversationState(threadId, userId)
            } catch (e: Exception) {
                _chatUiState.value = CommunicationUiState.Error(
                    e.message ?: "Erro ao carregar conversa."
                )
            }
        }
    }

    fun sendStudentMessage(threadId: String, senderId: String, content: String) {
        val messageContent = content.trim()
        if (messageContent.isEmpty()) return

        viewModelScope.launch {
            try {
                messageRepository.sendMessage(
                    SendMessageInput(
                        threadId = threadId,
                        senderId = senderId,
                        content = messageContent,
                    )
                ) ?: error("Não foi possível enviar a mensagem.")

                loadConversationState(threadId, senderId)
            } catch (e: Exception) {
                _chatUiState.value = CommunicationUiState.Error(
                    e.message ?: "Erro ao enviar mensagem."
                )
            }
        }
    }

    private suspend fun loadConversationState(threadId: String, userId: String) {
        val thread = messageRepository.getThreadById(threadId)

        if (thread == null) {
            _chatUiState.value = CommunicationUiState.Empty
            return
        }

        val conversation = buildConversation(thread, userId)
        conversation.messages
            .filter { message -> message.senderId != userId && !message.isRead }
            .forEach { message ->
                runCatching { messageRepository.markMessageAsRead(message.id) }
            }

        _chatUiState.value = CommunicationUiState.SuccessConversation(conversation)
    }

    private suspend fun buildConversation(
        thread: turmaA.grupoB.LinkStage.data.remote.model.communication.MessageThreadModel,
        userId: String,
    ): StudentConversationDetails {
        val participant = messageRepository.getParticipantsByThread(thread.id)
            .firstOrNull { it.userId != userId }
            ?.let { profileRepository?.getProfileById(it.userId) }
        val messages = messageRepository.getMessagesByThread(thread.id)
            .sortedBy { it.createdAt }

        return StudentConversationDetails(
            thread = thread,
            participant = participant,
            messages = messages,
        )
    }
    
    fun getNotificationsByUser(userId: String){
        viewModelScope.launch { 
            _uiState.value = CommunicationUiState.Loading
            try {
                val notifications = messageRepository.getNotificationsByUser(userId)

                _uiState.value = if (notifications.isEmpty()) {
                    CommunicationUiState.Empty
                } else {
                    CommunicationUiState.SuccessNotificationList(notifications)
                }
            } catch (e: Exception) {
                _uiState.value = CommunicationUiState.Error(
                    e.message ?: "Erro ao carregar notificações."
                )
            }
        }
    }
    fun getUnreadNotificationsByUser(userId: String){
        viewModelScope.launch {
            _uiState.value = CommunicationUiState.Loading
            try {
                val unreadNotifications = messageRepository.getUnreadNotificationsByUser(userId)

                _uiState.value = if (unreadNotifications.isEmpty()) {
                    CommunicationUiState.Empty
                } else {
                    CommunicationUiState.SuccessNotificationList(unreadNotifications)
                }
            } catch (e: Exception) {
                _uiState.value = CommunicationUiState.Error(
                    e.message ?: "Erro ao carregar notificações."
                )
            }
        }
    }
    fun markNotificationAsRead(notificationId: String){
        viewModelScope.launch {
            _uiState.value = CommunicationUiState.Loading
            try {
                val unreadNotification = messageRepository.markNotificationAsRead(notificationId)

                _uiState.value = if (unreadNotification  == null) {
                    CommunicationUiState.Empty
                } else {
                    CommunicationUiState.SuccessNotification(unreadNotification)
                }
            } catch (e: Exception) {
                _uiState.value = CommunicationUiState.Error(
                    e.message ?: "Erro ao marcar notificação como lida."
                )
            }
        }
    }
    fun getThreadById(threadId: String){
        viewModelScope.launch {
            _uiState.value = CommunicationUiState.Loading
            try {
                val thread = messageRepository.getThreadById(threadId)

                _uiState.value = if (thread == null) {
                    CommunicationUiState.Empty
                } else {
                    CommunicationUiState.SuccessThread(thread)
                }
            } catch (e: Exception) {
                _uiState.value = CommunicationUiState.Error(
                    e.message ?: "Erro ao carregar thread."
                )
            }
        }
    }
    fun getThreadsByInternship(internshipId: String){
        viewModelScope.launch {
            _uiState.value = CommunicationUiState.Loading
            try {
                val thread = messageRepository.getThreadsByInternship(internshipId)

                _uiState.value = if (thread.isEmpty()) {
                    CommunicationUiState.Empty
                } else {
                    CommunicationUiState.SuccessThreadList(thread)
                }
            } catch (e: Exception) {
                _uiState.value = CommunicationUiState.Error(
                    e.message ?: "Erro ao carregar threads."
                )
            }
        }
    }
    fun getThreadByApplication(applicationId: String){
        viewModelScope.launch {
            _uiState.value = CommunicationUiState.Loading
            try {
                val thread = messageRepository.getThreadByApplication(applicationId)

                _uiState.value = if (thread.isEmpty()) {
                    CommunicationUiState.Empty
                } else {
                    CommunicationUiState.SuccessThreadList(thread)
                }
            } catch (e: Exception) {
                _uiState.value = CommunicationUiState.Error(
                    e.message ?: "Erro ao carregar threads."
                )
            }
        }
    }
    fun getMessagesByThread(threadId: String){
        viewModelScope.launch {
            _uiState.value = CommunicationUiState.Loading
            try {
                val messages = messageRepository.getMessagesByThread(threadId)

                _uiState.value = if (messages.isEmpty()) {
                    CommunicationUiState.Empty
                } else {
                    CommunicationUiState.SuccessList(messages)
                }
            } catch (e: Exception) {
                _uiState.value = CommunicationUiState.Error(
                    e.message ?: "Erro ao carregar mensagens."
                )
            }
        }
    }
    fun sendMessage(input: SendMessageInput){
        viewModelScope.launch {
            _uiState.value = CommunicationUiState.Loading
            try {
                val message = messageRepository.sendMessage(input)

                _uiState.value = if (message == null) {
                    CommunicationUiState.Empty
                } else {
                    CommunicationUiState.Success(message)
                }
            } catch (e: Exception) {
                _uiState.value = CommunicationUiState.Error(
                    e.message ?: "Erro ao enviar mensagem."
                )
            }
        }
    }
    fun markMessageAsRead(messageId: String){
        viewModelScope.launch {
            _uiState.value = CommunicationUiState.Loading
            try {
                val message = messageRepository.markMessageAsRead(messageId)

                _uiState.value = if (message == null) {
                    CommunicationUiState.Empty
                } else {
                    CommunicationUiState.Success(message)
                }
            } catch (e: Exception) {
                _uiState.value = CommunicationUiState.Error(
                    e.message ?: "Erro ao marcar mensagem como lida."
                )
            }
        }
    }
}
