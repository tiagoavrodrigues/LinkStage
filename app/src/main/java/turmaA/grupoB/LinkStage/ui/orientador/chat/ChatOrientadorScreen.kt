package turmaA.grupoB.LinkStage.ui.orientador.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepository
import turmaA.grupoB.LinkStage.data.repository.communication.CommunicationRepository
import turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepository
import turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepository
import turmaA.grupoB.LinkStage.data.repository.student.StudentRepository
import turmaA.grupoB.LinkStage.data.repository.supervisor.SupervisorRepository
import turmaA.grupoB.LinkStage.ui.aluno.chat.Contact
import turmaA.grupoB.LinkStage.ui.aluno.chat.Conversation
import turmaA.grupoB.LinkStage.ui.aluno.chat.ConversationItem
import turmaA.grupoB.LinkStage.ui.aluno.chat.avatarColors
import turmaA.grupoB.LinkStage.ui.aluno.chat.getSampleContacts
import turmaA.grupoB.LinkStage.ui.aluno.chat.sampleConversations
import turmaA.grupoB.LinkStage.ui.common.CommonTopBar
import turmaA.grupoB.LinkStage.ui.common.LinkStageDialog
import turmaA.grupoB.LinkStage.viewmodel.chat.ChatDataSource
import turmaA.grupoB.LinkStage.viewmodel.chat.ChatUiState
import turmaA.grupoB.LinkStage.viewmodel.chat.ChatViewModel
import turmaA.grupoB.LinkStage.viewmodel.chat.ChatViewModelFactory
import turmaA.grupoB.LinkStage.viewmodel.chat.OrientadorChatDataSource
import turmaA.grupoB.LinkStage.ui.theme.BackgroundLight
import turmaA.grupoB.LinkStage.ui.theme.BorderGrey
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.DarkGrey
import turmaA.grupoB.LinkStage.ui.theme.LightBlue

import turmaA.grupoB.LinkStage.viewmodel.chat.EnsureThreadResult

@Composable
fun ChatOrientadorScreen(
    onOpenChat: (String) -> Unit,
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel? = null,
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showNewMessageModal by rememberSaveable { mutableStateOf(false) }
    var conversationToDelete by remember { mutableStateOf<Conversation?>(null) }
    var currentConversations by remember { mutableStateOf(sampleConversations) }

    val dataSource: ChatDataSource = remember {
        OrientadorChatDataSource(
            authRepository = AuthRepository(),
            supervisorRepository = SupervisorRepository(),
            internshipRepository = InternshipRepository(),
            studentRepository = StudentRepository(),
            profileRepository = ProfileRepository(),
            communicationRepository = CommunicationRepository(),
        )
    }
    val chatViewModelInstance = chatViewModel ?: viewModel(factory = ChatViewModelFactory(dataSource))
    val uiState by chatViewModelInstance.chatUiState.collectAsState()

    LaunchedEffect(Unit) {
        chatViewModelInstance.loadConversations()
    }

    val realConversations = when (val state = uiState) {
        is ChatUiState.Success -> state.conversations
        else -> emptyList()
    }
    LaunchedEffect(realConversations) {
        if (realConversations.isNotEmpty()) currentConversations = realConversations
    }

    val realContacts = when (val state = uiState) {
        is ChatUiState.Success -> state.contacts
        else -> emptyList()
    }
    val currentContacts = if (realContacts.isEmpty()) getSampleContacts() else realContacts

    val filtered = if (searchQuery.isEmpty()) currentConversations
    else currentConversations.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
            it.lastMessage.contains(searchQuery, ignoreCase = true)
    }

    if (showNewMessageModal) {
        NewMessageModal(
            contacts = currentContacts,
            onDismiss = { showNewMessageModal = false },
            onContactSelected = { contactId ->
                showNewMessageModal = false
                chatViewModelInstance.ensureThreadForStudent(contactId)
            }
        )
    }

    if (chatViewModel != null || chatViewModelInstance != null) {
        val vm = chatViewModel ?: chatViewModelInstance
        val ensureResult by vm!!.ensureThreadResult.collectAsState()
        LaunchedEffect(ensureResult) {
            if (ensureResult != null) {
                onOpenChat(ensureResult!!.threadId)
                vm.clearEnsureThreadResult()
            }
        }
    }

    if (conversationToDelete != null) {
        LinkStageDialog(
            title = stringResource(R.string.chat_delete_title),
            onConfirm = {
                currentConversations = currentConversations.filter { it.id != conversationToDelete!!.id }
                conversationToDelete = null
            },
            onDismiss = { conversationToDelete = null },
            confirmText = stringResource(R.string.chat_delete_button),
            dismissText = stringResource(R.string.dialog_cancel),
            content = {
                Text(
                    text = stringResource(R.string.chat_delete_confirm, conversationToDelete!!.name),
                    color = DarkGrey,
                    lineHeight = 22.sp,
                )
            }
        )
    }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewMessageModal = true },
                containerColor = LightBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.chat_new_message))
            }
        },
        topBar = {
            Column(modifier = Modifier.background(BackgroundLight)) {
                CommonTopBar()
                Text(
                    text = stringResource(R.string.tab_messages),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue,
                    ),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                placeholder = { Text(stringResource(R.string.common_search), color = DarkGrey) },
                leadingIcon = {
                    Icon(Icons.Outlined.Search, contentDescription = stringResource(R.string.common_search), tint = DarkGrey)
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = DarkBlue,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                ),
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filtered, key = { it.id }) { conversation ->
                    ConversationItem(
                        conversation = conversation,
                        onClick = { onOpenChat(conversation.id) },
                        onLongClick = { conversationToDelete = conversation }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color = BorderGrey,
                    )
                }
            }
        }
    }
}

@Composable
private fun NewMessageModal(
    contacts: List<Contact>,
    onDismiss: () -> Unit,
    onContactSelected: (String) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val filteredContacts = contacts.filter {
        it.name.contains(query, ignoreCase = true) || it.role.contains(query, ignoreCase = true)
    }

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(500.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.chat_new_message),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = DarkBlue
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.common_close), tint = DarkGrey)
                    }
                }

                // Search
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    placeholder = { Text(stringResource(R.string.chat_search_contacts), color = DarkGrey, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Outlined.Search, null, tint = DarkGrey) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LightBlue,
                        unfocusedBorderColor = BorderGrey,
                        focusedContainerColor = BackgroundLight.copy(alpha = 0.5f),
                        unfocusedContainerColor = BackgroundLight.copy(alpha = 0.5f),
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Contacts List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(filteredContacts) { contact ->
                        ContactItem(
                            contact = contact,
                            onClick = { onContactSelected(contact.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactItem(
    contact: Contact,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(avatarColors[contact.avatarColorIndex % avatarColors.size]),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = contact.initials,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = DarkBlue
            )
            Text(
                text = contact.role,
                style = MaterialTheme.typography.labelSmall,
                color = DarkGrey
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun ChatOrientadorScreenPreview() {
    MaterialTheme {
        ChatOrientadorScreen(onOpenChat = {})
    }
}
