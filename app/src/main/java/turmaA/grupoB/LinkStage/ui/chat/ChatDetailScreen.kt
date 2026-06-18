package turmaA.grupoB.LinkStage.ui.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import turmaA.grupoB.LinkStage.ui.aluno.chat.ChatScreen
import turmaA.grupoB.LinkStage.ui.aluno.chat.Conversation
import turmaA.grupoB.LinkStage.viewmodel.chat.ChatDataSource
import turmaA.grupoB.LinkStage.viewmodel.chat.ChatViewModel
import turmaA.grupoB.LinkStage.viewmodel.chat.ChatViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.chat.ThreadMessagesUiState

@Composable
fun ChatDetailScreen(
    threadId: String,
    onBack: () -> Unit,
    dataSource: ChatDataSource,
    chatViewModel: ChatViewModel? = null,
    conversation: Conversation? = null,
) {
    val viewModel: ChatViewModel = chatViewModel ?: viewModel(
        factory = ChatViewModelFactory(dataSource)
    )
    val messagesState by viewModel.threadMessagesUiState.collectAsState()
    val messages = when (val state = messagesState) {
        is ThreadMessagesUiState.Success -> state.messages
        else -> emptyList()
    }

    LaunchedEffect(threadId) {
        viewModel.loadThreadMessages(threadId)
    }

    val conversationMetadata = remember(threadId, conversation, messages) {
        conversation ?: Conversation(
            id = threadId,
            name = "Conversa ${threadId.takeLast(4)}",
            initials = threadId.takeLast(2).uppercase(),
            lastMessage = messages.lastOrNull()?.text ?: "Inicia uma nova conversa.",
            time = messages.lastOrNull()?.time ?: "",
            avatarColorIndex = 0,
        )
    }

    ChatScreen(
        conversation = conversationMetadata,
        realMessages = messages,
        onSendMessage = { text ->
            viewModel.sendMessage(threadId, text)
        },
        onBack = onBack,
    )
}
