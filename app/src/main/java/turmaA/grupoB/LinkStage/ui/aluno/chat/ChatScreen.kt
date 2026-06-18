package turmaA.grupoB.LinkStage.ui.aluno.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import turmaA.grupoB.LinkStage.ui.common.LinkStageDialog
import androidx.compose.ui.res.stringResource
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepository
import turmaA.grupoB.LinkStage.data.repository.communication.CommunicationRepository
import turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepository
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.BorderGrey
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.viewmodel.auth.AuthUiState
import turmaA.grupoB.LinkStage.viewmodel.auth.AuthViewModel
import turmaA.grupoB.LinkStage.viewmodel.auth.AuthViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.communication.CommunicationUiState
import turmaA.grupoB.LinkStage.viewmodel.communication.CommunicationViewModel
import turmaA.grupoB.LinkStage.viewmodel.communication.CommunicationViewModelFactory

// region Data models

data class ChatMessage(
    val id: String,
    val text: String,
    val isSentByMe: Boolean,
    val time: String,
)

// endregion

@Preview(showBackground = true)
@Composable
private fun ChatScreenPreview() {
    MaterialTheme {
        ChatScreen(
            conversation = Conversation(
                id = "1",
                name = "Viana S.T.Arts",
                lastMessage = "Olá! Tens alguma dúvida?",
                time = "22:40",
                unreadCount = 2,
                initials = "V",
                avatarColorIndex = 0
            ),
            onBack = {}
        )
    }
}

@Composable
fun getSampleMessages(): Map<String, List<ChatMessage>> {
    val yesterdayLabel = stringResource(R.string.time_yesterday)
    val daysAgoLabel = stringResource(R.string.time_days_ago, "2")

    return mapOf(
        "1" to listOf(
            ChatMessage("m1", "Olá! Tens alguma dúvida?", false, "22:40"),
            ChatMessage("m2", "Sim, tenho uma pergunta sobre o estágio.", true, "22:41"),
            ChatMessage("m3", "Boa pergunta.", false, "22:42"),
        ),
        "2" to listOf(
            ChatMessage("m1", "Viste o email que enviei?", false, "$yesterdayLabel 18:00"),
            ChatMessage("m2", "Como assim?", true, "$yesterdayLabel 18:05"),
        ),
        "3" to listOf(
            ChatMessage("m1", "O relatório foi submetido.", true, daysAgoLabel),
            ChatMessage("m2", "Nota-se.", false, daysAgoLabel),
        ),
        "4" to listOf(
            ChatMessage("m1", "Precisamos de actualizar a dashboard.", false, "22:40"),
            ChatMessage("m2", "Altera a dashboard", true, "22:42"),
        ),
    )
}

// endregion

// region Chat Screen

@Composable
fun StudentChatScreen(
    threadId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(AuthRepository())
    ),
    communicationViewModel: CommunicationViewModel = viewModel(
        factory = CommunicationViewModelFactory(
            CommunicationRepository(),
            ProfileRepository(),
        )
    ),
) {
    val authUiState by authViewModel.uiState.collectAsState()
    val chatUiState by communicationViewModel.chatUiState.collectAsState()
    val profile = (authUiState as? AuthUiState.Success)?.profile

    LaunchedEffect(Unit) {
        authViewModel.loadCurrentUserProfile()
    }

    LaunchedEffect(threadId, profile?.id) {
        profile?.id?.let { userId ->
            communicationViewModel.loadConversation(threadId, userId)
        }
    }

    when (val state = chatUiState) {
        is CommunicationUiState.SuccessConversation -> {
            val details = state.conversation
            val participantName = details.participant?.name ?: "Conversa"
            val conversation = Conversation(
                id = details.thread.id,
                name = participantName,
                initials = participantName.toChatInitials(),
                lastMessage = details.messages.lastOrNull()?.content.orEmpty(),
                time = details.messages.lastOrNull()?.createdAt.toChatTimeLabel(),
                avatarColorIndex = participantName.hashCode() and Int.MAX_VALUE,
            )
            val messages = details.messages.map { message ->
                ChatMessage(
                    id = message.id,
                    text = message.content,
                    isSentByMe = message.senderId == profile?.id,
                    time = message.createdAt.toChatTimeLabel(),
                )
            }

            ChatScreen(
                conversation = conversation,
                onBack = onBack,
                modifier = modifier,
                realMessages = messages,
                onSendMessage = { content ->
                    profile?.id?.let { senderId ->
                        communicationViewModel.sendStudentMessage(threadId, senderId, content)
                    }
                },
            )
        }

        is CommunicationUiState.Error -> ChatStatusScreen(state.message, onBack, modifier)
        CommunicationUiState.Empty -> ChatStatusScreen("Conversa não encontrada.", onBack, modifier)
        else -> ChatStatusScreen("A carregar conversa...", onBack, modifier)
    }
}

@Composable
private fun ChatStatusScreen(
    message: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.common_back_content_desc),
                    )
                }
            }
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center,
        ) {
            Text(message, color = DarkGrey)
        }
    }
}

private fun String.toChatInitials(): String = trim()
    .split(Regex("\\s+"))
    .filter { it.isNotBlank() }
    .take(2)
    .mapNotNull { it.firstOrNull()?.uppercase() }
    .joinToString("")
    .ifBlank { "?" }

private fun String?.toChatTimeLabel(): String {
    if (this == null) return ""
    return substringAfter('T', this).take(5)
}

@Composable
fun ChatScreen(
    conversation: Conversation,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    realMessages: List<ChatMessage>? = null,
    onSendMessage: ((String) -> Unit)? = null,
) {
    val sampleMessages = getSampleMessages()
    val initialMessages = sampleMessages[conversation.id] ?: emptyList()
    val localMessages = remember { mutableStateListOf(*initialMessages.toTypedArray()) }
    val messages = realMessages ?: localMessages
    var inputText by remember { mutableStateOf("") }
    var searchQueries by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }
    var showMuteDialog by remember { mutableStateOf(false) }

    val filteredMessages = remember(messages, searchQueries, isSearchActive) {
        if (isSearchActive && searchQueries.isNotBlank()) {
            messages.filter { it.text.contains(searchQueries, ignoreCase = true) }
        } else {
            messages
        }
    }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val nowLabel = stringResource(R.string.time_now)

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    fun sendMessage() {
        val text = inputText.trim()
        if (text.isEmpty()) return
        if (onSendMessage != null) {
            onSendMessage(text)
        } else {
            localMessages.add(
                ChatMessage(
                    id = "new_${messages.size}",
                    text = text,
                    isSentByMe = true,
                    time = nowLabel,
                )
            )
        }
        inputText = ""
        if (onSendMessage == null) {
            coroutineScope.launch {
                listState.animateScrollToItem(localMessages.size - 1)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            ChatTopBar(
                conversation = conversation,
                onBack = {
                    if (isSearchActive) {
                        isSearchActive = false
                        searchQueries = ""
                    } else {
                        onBack()
                    }
                },
                onClearHistory = {
                    if (realMessages == null) localMessages.clear()
                },
                isSearchActive = isSearchActive,
                searchQuery = searchQueries,
                onSearchQueryChange = { searchQueries = it },
                onToggleSearch = { isSearchActive = !isSearchActive },
                onMute = {
                    if (isMuted) isMuted = false
                    else showMuteDialog = true
                },
                isMuted = isMuted
            )
        },
        bottomBar = {
            if (!isSearchActive) {
                ChatInputBar(
                    text = inputText,
                    onTextChange = { inputText = it },
                    onSend = { sendMessage() },
                )
            }
        },
        containerColor = BackgroundLight,
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
        ) {
            items(filteredMessages, key = { it.id }) { message ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                ) {
                    ChatBubble(message = message)
                }
            }
        }
    }

    if (showMuteDialog) {
        MuteNotificationsDialog(
            onDismiss = { showMuteDialog = false },
            onConfirm = {
                isMuted = true
                showMuteDialog = false
            }
        )
    }
}

// endregion

// region Components

@Composable
private fun ChatTopBar(
    conversation: Conversation,
    onBack: () -> Unit,
    onClearHistory: () -> Unit = {},
    isSearchActive: Boolean = false,
    searchQuery: String = "",
    onSearchQueryChange: (String) -> Unit = {},
    onToggleSearch: () -> Unit = {},
    onMute: () -> Unit = {},
    isMuted: Boolean = false,
) {
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        color = Color.White,
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 8.dp, end = 8.dp, top = 2.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.common_back_content_desc),
                    tint = DarkBlue,
                )
            }

            if (isSearchActive) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    placeholder = { Text(stringResource(R.string.common_search), color = DarkGrey) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = BackgroundLight,
                        unfocusedContainerColor = BackgroundLight,
                    ),
                    shape = RoundedCornerShape(20.dp),
                    trailingIcon = {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.common_clear), tint = DarkGrey)
                        }
                    }
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(avatarColors[conversation.avatarColorIndex % avatarColors.size]),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = conversation.initials,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = conversation.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = stringResource(R.string.chat_online),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF4CAF50),
                    )
                }
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.common_more_options),
                        tint = DarkGrey,
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier
                        .background(BackgroundLight)
                        .border(1.dp, BorderGrey, RoundedCornerShape(8.dp))
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.chat_menu_search)) },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        onClick = {
                            showMenu = false
                            onToggleSearch()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(if (isMuted) stringResource(R.string.chat_menu_unmute) else stringResource(R.string.chat_menu_mute)) },
                        onClick = {
                            showMenu = false
                            onMute()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.chat_menu_clear)) },
                        onClick = {
                            showMenu = false
                            onClearHistory()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MuteNotificationsDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val option8h = stringResource(R.string.chat_mute_8hours)
    val option1w = stringResource(R.string.chat_mute_1week)
    val optionAlways = stringResource(R.string.chat_mute_always)
    var selectedOption by remember { mutableStateOf(option8h) }
    val options = listOf(option8h, option1w, optionAlways)

    LinkStageDialog(
        onDismiss = onDismiss,
        title = stringResource(R.string.chat_mute_title),
        onConfirm = { onConfirm(selectedOption) },
        confirmText = stringResource(R.string.chat_mute_button),
        dismissText = stringResource(R.string.dialog_cancel),
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedOption = option }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (option == selectedOption),
                            onClick = { selectedOption = option },
                            colors = RadioButtonDefaults.colors(selectedColor = DarkBlue)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = option, color = DarkGrey)
                    }
                }
            }
        }
    )
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val alignment = if (message.isSentByMe) Alignment.End else Alignment.Start
    val bubbleColor = if (message.isSentByMe) DarkBlue else Color.White
    val textColor = if (message.isSentByMe) Color.White else DarkBlue
    val shape = if (message.isSentByMe) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment,
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(shape)
                .background(bubbleColor)
                .padding(horizontal = 14.dp, vertical = 10.dp),
        ) {
            Text(
                text = message.text,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp,
            )
        }
        Text(
            text = message.time,
            style = MaterialTheme.typography.labelSmall,
            color = DarkGrey,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
        )
    }
}

@Composable
private fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        stringResource(R.string.chat_input_placeholder),
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkGrey,
                    )
                },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = BorderGrey,
                    focusedBorderColor = DarkBlue,
                    unfocusedContainerColor = BackgroundLight,
                    focusedContainerColor = BackgroundLight,
                ),
                maxLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() }),
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (text.isNotBlank()) LightBlue else DarkGrey)
                    .clickable(enabled = text.isNotBlank()) { onSend() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.common_send),
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

// endregion
