package turmaA.grupoB.LinkStage.viewmodel.communication

import turmaA.grupoB.LinkStage.data.remote.model.communication.MessageModel
import turmaA.grupoB.LinkStage.data.remote.model.communication.MessageThreadModel
import turmaA.grupoB.LinkStage.data.remote.model.communication.MessageThreadParticipantModel
import turmaA.grupoB.LinkStage.data.remote.model.communication.NotificationModel
import turmaA.grupoB.LinkStage.data.remote.model.user.ProfileModel

data class StudentConversationDetails(
    val thread: MessageThreadModel,
    val participant: ProfileModel?,
    val messages: List<MessageModel>,
)

sealed class CommunicationUiState {
    data object Idle: CommunicationUiState()
    data object Loading :  CommunicationUiState()
    data class Success(val message: MessageModel) : CommunicationUiState()
    data class SuccessList(val messages: List<MessageModel>) : CommunicationUiState()
    data class SuccessNotification(val message: NotificationModel) : CommunicationUiState()
    data class SuccessNotificationList(val messages: List<NotificationModel>) : CommunicationUiState()
    data class SuccessThread(val message: MessageThreadModel) : CommunicationUiState()
    data class SuccessThreadList(val messages: List<MessageThreadModel>) : CommunicationUiState()
    data class SuccessParticipantsThread(val message: MessageThreadParticipantModel) : CommunicationUiState()
    data class SuccessParticipantsThreadList(val messages: List<MessageThreadParticipantModel>) : CommunicationUiState()
    data class SuccessConversationList(val conversations: List<StudentConversationDetails>) : CommunicationUiState()
    data class SuccessConversation(val conversation: StudentConversationDetails) : CommunicationUiState()
    data class SuccessContactList(val contacts: List<ProfileModel>) : CommunicationUiState()
    data class ConversationCreated(val threadId: String) : CommunicationUiState()
    data object Empty: CommunicationUiState()
    data class Error(val message : String): CommunicationUiState()
}
