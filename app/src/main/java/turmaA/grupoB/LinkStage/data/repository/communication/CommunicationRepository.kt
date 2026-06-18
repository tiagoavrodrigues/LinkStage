package turmaA.grupoB.LinkStage.data.repository.communication

import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import turmaA.grupoB.LinkStage.data.remote.model.communication.MessageModel
import turmaA.grupoB.LinkStage.data.remote.model.communication.CreateMessageThreadInput
import turmaA.grupoB.LinkStage.data.remote.model.communication.CreateMessageThreadParticipantInput
import turmaA.grupoB.LinkStage.data.remote.model.communication.MessageThreadModel
import turmaA.grupoB.LinkStage.data.remote.model.communication.MessageThreadParticipantModel
import turmaA.grupoB.LinkStage.data.remote.model.communication.NotificationModel
import turmaA.grupoB.LinkStage.data.remote.model.communication.SendMessageInput
import turmaA.grupoB.LinkStage.data.remote.supabase.SupabaseClientProvider

class CommunicationRepository : CommunicationRepositoryInterface {

    private val supabase = SupabaseClientProvider.client

    override suspend fun getNotificationsByUser(userId: String): List<NotificationModel> {
        return supabase
            .from("notifications")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<NotificationModel>()
    }

    override suspend fun getUnreadNotificationsByUser(userId: String): List<NotificationModel> {
        return supabase
            .from("notifications")
            .select {
                filter {
                    eq("user_id", userId)
                    eq("is_read", false)
                }
            }
            .decodeList<NotificationModel>()
    }

    override suspend fun markNotificationAsRead(notificationId: String): NotificationModel? {
        val updateData = mapOf(
            "is_read" to true
        )

        return supabase
            .from("notifications")
            .update(updateData) {
                select()
                filter {
                    eq("id", notificationId)
                }
            }.decodeSingle<NotificationModel>()

    }

    override suspend fun getThreadById(threadId: String): MessageThreadModel? {
        return supabase
            .from("message_threads")
            .select {
                filter {
                    eq("id", threadId)
                }
            }
            .decodeList<MessageThreadModel>()
            .firstOrNull()
    }

    override suspend fun getThreadsByUser(userId: String): List<MessageThreadModel> {
        val participants = supabase
            .from("message_thread_participants")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<MessageThreadParticipantModel>()

        return participants
            .mapNotNull { participant -> getThreadById(participant.threadId) }
            .distinctBy { thread -> thread.id }
    }

    override suspend fun getThreadsByInternship(internshipId: String): List<MessageThreadModel> {
        return supabase
            .from("message_threads")
            .select {
                filter {
                    eq("internship_id", internshipId)
                }
            }
            .decodeList<MessageThreadModel>()
    }

    override suspend fun getThreadByApplication(applicationId: String): List<MessageThreadModel> {
        return supabase
            .from("message_threads")
            .select {
                filter {
                    eq("application_id", applicationId)
                }
            }
            .decodeList<MessageThreadModel>()
    }

    override suspend fun getThreadsWithParticipants(userIds: List<String>): List<MessageThreadModel> {
        if (userIds.isEmpty()) return emptyList()

        return supabase
            .from("message_threads")
            .select()
            .decodeList<MessageThreadModel>()
            .filter { thread ->
                val participantUserIds = getParticipantsByThread(thread.id)
                    .map { it.userId }
                    .toSet()
                userIds.all { it in participantUserIds }
            }
    }

    override suspend fun getParticipantsByThread(threadId: String): List<MessageThreadParticipantModel> {
        return supabase
            .from("message_thread_participants")
            .select {
                filter {
                    eq("thread_id", threadId)
                }
            }
            .decodeList<MessageThreadParticipantModel>()
    }

    override suspend fun getMessagesByThread(threadId: String): List<MessageModel> {
        return supabase
            .from("messages")
            .select {
                filter {
                    eq("thread_id", threadId)
                }
            }
            .decodeList<MessageModel>()
    }

    override suspend fun createThread(
        input: CreateMessageThreadInput,
        participantUserIds: List<String>,
    ): MessageThreadModel {
        val thread = supabase
            .from("message_threads")
            .insert(input) {
                select()
            }
            .decodeSingle<MessageThreadModel>()

        val participants = participantUserIds
            .distinct()
            .map { userId ->
                CreateMessageThreadParticipantInput(
                    threadId = thread.id,
                    userId = userId,
                )
            }

        supabase
            .from("message_thread_participants")
            .insert(participants)

        return thread
    }

    override suspend fun sendMessage(input: SendMessageInput): MessageModel? {
        return supabase
        .from("messages")
        .insert(input) {
            select()
        }
        .decodeSingle<MessageModel>()
    }

    override suspend fun markMessageAsRead(messageId: String): MessageModel? {
        val updateData = mapOf(
            "is_read" to true
        )

        return supabase
            .from("messages")
            .update(updateData) {
                select()
                filter {

                    eq("id", messageId)
                }
            }
            .decodeSingle<MessageModel>()
    }

    override suspend fun createThread(internshipId: String?, applicationId: String?): MessageThreadModel? {
        val input = buildJsonObject {
            internshipId?.let { put("internship_id", JsonPrimitive(it)) }
            applicationId?.let { put("application_id", JsonPrimitive(it)) }
        }
        return supabase
            .from("message_threads")
            .insert(input) {
                select()
            }
            .decodeSingle<MessageThreadModel>()
    }

    override suspend fun createThreadParticipant(threadId: String, userId: String): MessageThreadParticipantModel? {
        val input = mapOf<String, String>(
            "thread_id" to threadId,
            "user_id" to userId,
        )
        return supabase
            .from("message_thread_participants")
            .insert(input) {
                select()
            }
            .decodeSingle<MessageThreadParticipantModel>()
    }
}
