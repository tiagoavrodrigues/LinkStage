package turmaA.grupoB.LinkStage.viewmodel.chat

import turmaA.grupoB.LinkStage.data.remote.model.communication.MessageModel
import turmaA.grupoB.LinkStage.data.remote.model.communication.MessageThreadModel
import turmaA.grupoB.LinkStage.data.remote.model.communication.MessageThreadParticipantModel
import turmaA.grupoB.LinkStage.data.remote.model.user.ProfileModel
import turmaA.grupoB.LinkStage.ui.aluno.chat.ChatMessage
import turmaA.grupoB.LinkStage.ui.aluno.chat.Contact
import turmaA.grupoB.LinkStage.ui.aluno.chat.Conversation
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object ChatMappers {
    fun toConversation(
        thread: MessageThreadModel,
        participants: List<MessageThreadParticipantModel>,
        messages: List<MessageModel>,
        profilesByUserId: Map<String, ProfileModel>,
        currentUserId: String,
        fallbackName: String? = null,
    ): Conversation {
        val otherProfiles = participants
            .mapNotNull { participant -> profilesByUserId[participant.userId] }
            .filter { it.id != currentUserId }
            .distinctBy { it.id }

        val name = otherProfiles.firstOrNull()?.name
            ?: fallbackName
            ?: "Conversa ${thread.id.takeLast(4)}"
        val initials = otherProfiles.firstOrNull()?.initials()
            ?: thread.id.takeLast(2).uppercase()
        val latestMessage = messages.latestMessage()
        val lastMessage = latestMessage?.content ?: "Sem mensagens ainda"
        val time = latestMessage?.createdAt?.toDisplayTime() ?: ""
        val unreadCount = messages.count { message ->
            message.senderId != currentUserId && !message.isRead
        }

        return Conversation(
            id = thread.id,
            name = name,
            initials = initials,
            lastMessage = lastMessage,
            time = time,
            unreadCount = unreadCount,
            avatarColorIndex = deterministicColorIndex(thread.id),
        )
    }

    fun toContact(profile: ProfileModel, fallbackRole: String? = null): Contact {
        return Contact(
            id = profile.id,
            name = profile.name,
            role = fallbackRole ?: profile.role.name,
            initials = profile.initials(),
            avatarColorIndex = deterministicColorIndex(profile.id),
        )
    }

    fun toContacts(
        participants: List<MessageThreadParticipantModel>,
        profilesByUserId: Map<String, ProfileModel>,
        currentUserId: String,
        fallbackRole: String,
    ): List<Contact> {
        return participants
            .mapNotNull { participant ->
                val profile = profilesByUserId[participant.userId] ?: return@mapNotNull null
                if (profile.id == currentUserId) return@mapNotNull null
                toContact(profile)
            }
            .distinctBy { it.id }
            .ifEmpty {
                listOf(
                    Contact(
                        id = "fallback-${fallbackRole.hashCode().absoluteValue}",
                        name = fallbackRole,
                        role = fallbackRole,
                        initials = fallbackRole.take(2).uppercase(),
                        avatarColorIndex = 0,
                    )
                )
            }
    }

    fun toChatMessage(message: MessageModel, currentUserId: String): ChatMessage {
        return ChatMessage(
            id = message.id,
            text = message.content,
            isSentByMe = message.senderId == currentUserId,
            time = message.createdAt.toDisplayTime(),
        )
    }

    private fun List<MessageModel>.latestMessage(): MessageModel? {
        if (isEmpty()) return null
        val latestParsedMessage = mapNotNull { message ->
            message.createdAt.toDisplayInstant()?.let { message to it }
        }.maxByOrNull { it.second }?.first
        if (latestParsedMessage != null) return latestParsedMessage
        return maxByOrNull { it.createdAt }
    }

    fun String.toDisplayTime(): String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return try {
            // Timestamps without timezone info (e.g. "2026-04-02T09:15:00") are
            // displayed as-is, without converting between timezones.
            LocalDateTime.parse(this).format(formatter)
        } catch (_: DateTimeParseException) {
            try {
                OffsetDateTime.parse(this).atZoneSameInstant(ZoneId.systemDefault()).format(formatter)
            } catch (_: DateTimeParseException) {
                try {
                    Instant.parse(this).atZone(ZoneId.systemDefault()).format(formatter)
                } catch (_: DateTimeParseException) {
                    this
                }
            }
        }
    }

    private fun String.toDisplayInstant(): Instant? {
        return try {
            LocalDateTime.parse(this).toInstant(ZoneOffset.UTC)
        } catch (_: DateTimeParseException) {
            try {
                OffsetDateTime.parse(this).toInstant()
            } catch (_: DateTimeParseException) {
                try {
                    Instant.parse(this)
                } catch (_: DateTimeParseException) {
                    null
                }
            }
        }
    }

    private fun ProfileModel.initials(): String {
        val words = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        return when {
            words.isEmpty() -> id.take(2).uppercase()
            words.size == 1 -> words.first().take(2).uppercase()
            else -> words.take(2).joinToString("") { it.first().toString() }.uppercase()
        }
    }

    private fun deterministicColorIndex(value: String): Int {
        return if (value.isBlank()) 0 else kotlin.math.abs(value.hashCode()) % 3
    }
}

private val Int.absoluteValue: Int
    get() = if (this == Int.MIN_VALUE) Int.MAX_VALUE else kotlin.math.abs(this)
