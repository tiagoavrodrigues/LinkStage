package turmaA.grupoB.LinkStage.viewmodel.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import turmaA.grupoB.LinkStage.ui.aluno.chat.ChatMessage

class ChatViewModel(
    private val dataSource: ChatDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    private val _chatUiState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
    val chatUiState: StateFlow<ChatUiState> = _chatUiState.asStateFlow()

    private val _threadMessagesUiState = MutableStateFlow<ThreadMessagesUiState>(ThreadMessagesUiState.Idle)
    val threadMessagesUiState: StateFlow<ThreadMessagesUiState> = _threadMessagesUiState.asStateFlow()

    private val _ensureThreadResult = MutableStateFlow<EnsureThreadResult?>(null)
    val ensureThreadResult: StateFlow<EnsureThreadResult?> = _ensureThreadResult.asStateFlow()

    fun loadConversations() {
        viewModelScope.launch(dispatcher) {
            _chatUiState.value = ChatUiState.Loading
            try {
                val result = dataSource.loadConversations()
                _chatUiState.value = if (result.conversations.isEmpty()) {
                    ChatUiState.Empty
                } else {
                    ChatUiState.Success(result.conversations, result.contacts)
                }
            } catch (error: Exception) {
                _chatUiState.value = ChatUiState.Error(error.message ?: "Erro ao carregar mensagens")
            }
        }
    }

    fun loadThreadMessages(threadId: String) {
        viewModelScope.launch(dispatcher) {
            val previousThreadState = _threadMessagesUiState.value
            val hadMessages = (previousThreadState as? ThreadMessagesUiState.Success)
                ?.messages
                ?.isNotEmpty() == true
            _threadMessagesUiState.value = ThreadMessagesUiState.Loading
            try {
                val messages = dataSource.loadThreadMessages(threadId)
                runCatching { dataSource.markThreadMessagesAsRead(threadId) }
                _threadMessagesUiState.value = when {
                    messages.isNotEmpty() -> ThreadMessagesUiState.Success(messages, threadId)
                    !hadMessages -> ThreadMessagesUiState.Empty
                    else -> previousThreadState
                }
            } catch (error: Exception) {
                _threadMessagesUiState.value = ThreadMessagesUiState.Error(error.message ?: "Erro ao carregar mensagens")
            }
        }
    }

    fun ensureThreadForStudent(studentId: String) {
        viewModelScope.launch(dispatcher) {
            try {
                val result = dataSource.ensureThreadForStudent(studentId)
                _ensureThreadResult.value = result
            } catch (error: Exception) {
                _ensureThreadResult.value = null
            }
        }
    }

    fun clearEnsureThreadResult() {
        _ensureThreadResult.value = null
    }

    fun resolveThreadIdForStudent(studentId: String): String? {
        return dataSource.resolveThreadIdForStudent(studentId)
    }

    fun sendMessage(threadId: String, text: String) {
        viewModelScope.launch(dispatcher) {
            val currentMessages = (_threadMessagesUiState.value as? ThreadMessagesUiState.Success)?.messages.orEmpty()
            val previousThreadState = _threadMessagesUiState.value
            val optimisticMessage = ChatMessage(
                id = "local_${currentMessages.size}_${System.currentTimeMillis()}",
                text = text,
                isSentByMe = true,
                time = "Agora",
            )
            _threadMessagesUiState.value = ThreadMessagesUiState.Success(
                messages = currentMessages + optimisticMessage,
                threadId = threadId,
            )
            try {
                dataSource.sendMessage(threadId, text)
                loadConversations()
                loadThreadMessages(threadId)
            } catch (error: Exception) {
                _threadMessagesUiState.value = previousThreadState
            }
        }
    }
}
