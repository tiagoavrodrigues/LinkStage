package turmaA.grupoB.LinkStage.viewmodel.chat

import turmaA.grupoB.LinkStage.ui.aluno.chat.ChatMessage
import turmaA.grupoB.LinkStage.ui.aluno.chat.Contact

data class EnsureThreadResult(
    val threadId: String,
    val created: Boolean,
)

interface ChatDataSource {
    suspend fun loadConversations(): ChatUiState.Success
    suspend fun loadThreadMessages(threadId: String): List<ChatMessage>
    suspend fun sendMessage(threadId: String, text: String)
    suspend fun markThreadMessagesAsRead(threadId: String)
    suspend fun ensureThreadForStudent(studentId: String): EnsureThreadResult?
    fun resolveThreadIdForStudent(studentId: String): String? = null
}
