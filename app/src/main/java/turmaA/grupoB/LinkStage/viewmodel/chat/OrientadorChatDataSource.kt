package turmaA.grupoB.LinkStage.viewmodel.chat

import turmaA.grupoB.LinkStage.data.remote.model.communication.MessageThreadModel
import turmaA.grupoB.LinkStage.data.remote.model.communication.SendMessageInput
import turmaA.grupoB.LinkStage.data.remote.model.enums.InternshipStatus
import turmaA.grupoB.LinkStage.data.remote.model.internship.InternshipModel
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.communication.CommunicationRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.student.StudentRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.supervisor.SupervisorRepositoryInterface
import turmaA.grupoB.LinkStage.ui.aluno.chat.ChatMessage
import turmaA.grupoB.LinkStage.ui.aluno.chat.Contact
import turmaA.grupoB.LinkStage.ui.aluno.chat.Conversation

class OrientadorChatDataSource(
    private val authRepository: AuthRepositoryInterface,
    private val supervisorRepository: SupervisorRepositoryInterface,
    private val internshipRepository: InternshipRepositoryInterface,
    private val studentRepository: StudentRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface,
    private val communicationRepository: CommunicationRepositoryInterface,
) : ChatDataSource {
    override suspend fun loadConversations(): ChatUiState.Success {
        val currentUserId = authRepository.getCurrentUserId()
            ?: throw IllegalStateException("Nenhum utilizador autenticado")
        if (currentUserId.isBlank()) throw IllegalStateException("Nenhum utilizador autenticado")

        val supervisor = supervisorRepository.getSupervisorByUserId(currentUserId)
            ?: throw IllegalStateException("Nenhum supervisor associado ao utilizador")
        val internships = internshipRepository.getInternships()
            .filter { it.supervisorId == supervisor.id }
        val threads = collectThreadsForSupervisor(internships)
        val internshipsById = internships.associateBy { it.id }
        val studentProfileNamesById = resolveStudentProfileNamesById()
        val conversations = threads.mapNotNull { thread ->
            val internshipStudentId = internshipsById[thread.internshipId]?.studentId
            thread.toConversationForOrientador(
                currentUserId = currentUserId,
                internshipsById = internshipsById,
                fallbackName = internshipStudentId?.let { studentProfileNamesById[it] },
            )
        }.distinctBy { it.id }
        val contacts = conversations.flatMap { conversation ->
            threadContactsForConversation(
                threadId = conversation.id,
                currentUserId = currentUserId,
                fallbackRole = "Estudante",
            )
        }.distinctBy { it.id }

        return ChatUiState.Success(conversations, contacts)
    }

    override suspend fun loadThreadMessages(threadId: String): List<ChatMessage> {
        val currentUserId = authRepository.getCurrentUserId()
            ?: throw IllegalStateException("Nenhum utilizador autenticado")
        val messages = communicationRepository.getMessagesByThread(threadId)
        return messages.map { ChatMappers.toChatMessage(it, currentUserId) }
    }

    override suspend fun markThreadMessagesAsRead(threadId: String) {
        val currentUserId = authRepository.getCurrentUserId()
            ?: throw IllegalStateException("Nenhum utilizador autenticado")
        communicationRepository.getMessagesByThread(threadId)
            .filter { message -> message.senderId != currentUserId && !message.isRead }
            .forEach { message -> communicationRepository.markMessageAsRead(message.id) }
    }

    override suspend fun sendMessage(threadId: String, text: String) {
        val senderId = authRepository.getCurrentUserId()
            ?: throw IllegalStateException("Nenhum utilizador autenticado")
        communicationRepository.sendMessage(
            SendMessageInput(
                threadId = threadId,
                senderId = senderId,
                content = text,
            )
        )
    }

    override suspend fun ensureThreadForStudent(studentId: String): EnsureThreadResult? {
        val currentUserId = authRepository.getCurrentUserId()
            ?: throw IllegalStateException("Nenhum utilizador autenticado")
        val supervisor = supervisorRepository.getSupervisorByUserId(currentUserId)
            ?: throw IllegalStateException("Nenhum supervisor associado ao utilizador")

        // Resolve student — parameter may be student.id or user/profile id
        var resolvedStudent = studentRepository.getStudentById(studentId)
        if (resolvedStudent == null) {
            resolvedStudent = studentRepository.getStudentByUserId(studentId)
        }
        if (resolvedStudent == null) return null
        val resolvedStudentId = resolvedStudent.id

        val internships = internshipRepository.getInternshipsByStudent(resolvedStudentId)
            .filter { it.supervisorId == supervisor.id }

        val activeInternship = internships.firstOrNull {
            it.status == InternshipStatus.IN_PROGRESS
        } ?: internships.maxByOrNull { it.createdAt }

        if (activeInternship != null) {
            val existingThreads = communicationRepository.getThreadsByInternship(activeInternship.id)
            if (existingThreads.isNotEmpty()) {
                return EnsureThreadResult(existingThreads.first().id, created = false)
            }
        } else {
            return null
        }

        // No existing thread for this exact supervisor/student internship, create one
        val thread = communicationRepository.createThread(
            internshipId = activeInternship.id,
            applicationId = null,
        ) ?: return null

        // Add participants: supervisor + student
        communicationRepository.createThreadParticipant(thread.id, currentUserId)
        communicationRepository.createThreadParticipant(thread.id, resolvedStudent.userId)

        return EnsureThreadResult(thread.id, created = true)
    }

    override fun resolveThreadIdForStudent(studentId: String): String? = null

    private suspend fun collectThreadsForSupervisor(
        internships: List<InternshipModel>,
    ): List<MessageThreadModel> {
        val threads = internships.flatMap { internship ->
            communicationRepository.getThreadsByInternship(internship.id)
        }
        return threads
            .distinctBy { it.id }
            .sortedByDescending { it.createdAt }
    }

    private suspend fun MessageThreadModel.toConversationForOrientador(
        currentUserId: String,
        internshipsById: Map<String, InternshipModel>,
        fallbackName: String? = null,
    ): Conversation? {
        val participants = communicationRepository.getParticipantsByThread(id)
        val messages = communicationRepository.getMessagesByThread(id)
        val profiles = participants.mapNotNull { participant ->
            profileRepository.getProfileById(participant.userId)?.let { participant.userId to it }
        }.toMap()
        return ChatMappers.toConversation(
            thread = this,
            participants = participants,
            messages = messages,
            profilesByUserId = profiles,
            currentUserId = currentUserId,
            fallbackName = fallbackName,
        )
    }

    private suspend fun resolveStudentProfileNamesById(): Map<String, String> {
        return studentRepository.getStudents()
            .mapNotNull { student ->
                profileRepository.getProfileById(student.userId)?.let { profile -> student.id to profile.name }
            }
            .toMap()
    }

    private suspend fun threadContactsForConversation(
        threadId: String,
        currentUserId: String,
        fallbackRole: String,
    ): List<Contact> {
        val participants = communicationRepository.getParticipantsByThread(threadId)
        val profiles = participants.mapNotNull { participant ->
            profileRepository.getProfileById(participant.userId)?.let { participant.userId to it }
        }.toMap()
        return ChatMappers.toContacts(participants, profiles, currentUserId, fallbackRole)
    }
}
