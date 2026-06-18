package turmaA.grupoB.LinkStage.viewmodel.chat

import turmaA.grupoB.LinkStage.ui.aluno.chat.ChatMessage
import turmaA.grupoB.LinkStage.ui.aluno.chat.Contact
import turmaA.grupoB.LinkStage.ui.aluno.chat.Conversation

sealed class ChatUiState {
    data object Idle : ChatUiState()
    data object Loading : ChatUiState()

    data class Success(
        val conversations: List<Conversation>,
        val contacts: List<Contact>,
    ) : ChatUiState()

    data object Empty : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}

sealed class ThreadMessagesUiState {
    data object Idle : ThreadMessagesUiState()
    data object Loading : ThreadMessagesUiState()

    data class Success(
        val messages: List<ChatMessage>,
        val threadId: String,
    ) : ThreadMessagesUiState()

    data object Empty : ThreadMessagesUiState()
    data class Error(val message: String) : ThreadMessagesUiState()
}
