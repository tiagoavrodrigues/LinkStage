package turmaA.grupoB.LinkStage.viewmodel.chat

import turmaA.grupoB.LinkStage.data.remote.model.application.ApplicationModel
import turmaA.grupoB.LinkStage.data.remote.model.communication.MessageThreadModel
import turmaA.grupoB.LinkStage.data.remote.model.enums.ApplicationStatus
import turmaA.grupoB.LinkStage.data.remote.model.enums.InternshipStatus
import turmaA.grupoB.LinkStage.data.remote.model.internship.InternshipModel
import turmaA.grupoB.LinkStage.data.remote.model.user.StudentModel
import turmaA.grupoB.LinkStage.data.repository.application.ApplicationRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.communication.CommunicationRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.student.StudentRepositoryInterface
import turmaA.grupoB.LinkStage.data.remote.model.communication.SendMessageInput
import turmaA.grupoB.LinkStage.ui.aluno.chat.ChatMessage
import turmaA.grupoB.LinkStage.ui.aluno.chat.Contact
import turmaA.grupoB.LinkStage.ui.aluno.chat.Conversation

class InstituicaoChatDataSource(
    private val authRepository: AuthRepositoryInterface,
    private val institutionRepository: InstitutionRepositoryInterface,
    private val offerRepository: OfferRepositoryInterface,
    private val internshipRepository: InternshipRepositoryInterface,
    private val applicationRepository: ApplicationRepositoryInterface,
    private val studentRepository: StudentRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface,
    private val communicationRepository: CommunicationRepositoryInterface,
) : ChatDataSource {
    override suspend fun loadConversations(): ChatUiState.Success {
        val currentUserId = authRepository.getCurrentUserId()
            ?: throw IllegalStateException("Nenhum utilizador autenticado")
        if (currentUserId.isBlank()) throw IllegalStateException("Nenhum utilizador autenticado")

        val institution = institutionRepository.getInstitutionByUserId(currentUserId)
            ?: throw IllegalStateException("Nenhuma instituição associada ao utilizador")

        val offers = offerRepository.getOffersByInstitution(institution.id)
        val applications = offers.flatMap { offer -> applicationRepository.getApplicationsByOffer(offer.id) }
        val internships = internshipRepository.getInternshipsByInstitution(institution.id)
        val threads = collectThreadsForInstitution(internships, applications, currentUserId)
        val internshipsById = internships.associateBy { it.id }
        val applicationsById = applications.associateBy { it.id }
        val studentProfileNamesById = resolveStudentProfileNamesById()

        val conversations = threads.mapNotNull { thread ->
            val internshipStudentId = internshipsById[thread.internshipId]?.studentId
            val applicationStudentId = applicationsById[thread.applicationId]?.studentId
            thread.toConversationForInstitution(
                currentUserId = currentUserId,
                internshipsById = internshipsById,
                applicationsById = applicationsById,
                fallbackName = internshipStudentId?.let { studentProfileNamesById[it] }
                    ?: applicationStudentId?.let { studentProfileNamesById[it] },
            )
        }.distinctBy { it.id }

        val threadContacts = conversations.flatMap { conversation ->
            threadContactsForConversation(
                threadId = conversation.id,
                currentUserId = currentUserId,
                fallbackRole = "Estudante",
            )
        }
        val studentContacts = resolveStudentContacts(internships, applications, currentUserId)
        val contacts = (studentContacts + threadContacts).distinctBy { it.id }

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
        val institution = institutionRepository.getInstitutionByUserId(currentUserId)
            ?: throw IllegalStateException("Nenhuma instituição associada ao utilizador")

        // Resolve student — parameter may be student.id or user/profile id
        var resolvedStudent = studentRepository.getStudentById(studentId)
        if (resolvedStudent == null) {
            resolvedStudent = studentRepository.getStudentByUserId(studentId)
        }
        if (resolvedStudent == null) return null
        val resolvedStudentId = resolvedStudent.id

        val offers = offerRepository.getOffersByInstitution(institution.id)
        val applications = offers.flatMap { offer ->
            applicationRepository.getApplicationsByOffer(offer.id)
                .filter { it.studentId == resolvedStudentId }
        }
        val internships = internshipRepository.getInternshipsByInstitution(institution.id)
            .filter { it.studentId == resolvedStudentId }

        // Check for existing internship thread (preferred)
        val activeInternship = internships.firstOrNull {
            it.status == InternshipStatus.IN_PROGRESS
        } ?: internships.maxByOrNull { it.createdAt }

        if (activeInternship != null) {
            val existingThreads = communicationRepository.getThreadsByInternship(activeInternship.id)
            if (existingThreads.isNotEmpty()) {
                return EnsureThreadResult(existingThreads.first().id, created = false)
            }
        }

        // Check for existing application thread
        val activeApplication = applications.filter { it.status == ApplicationStatus.ACCEPTED }
            .maxByOrNull { it.createdAt }
            ?: applications.maxByOrNull { it.createdAt }

        if (activeApplication != null) {
            val existingThreads = communicationRepository.getThreadByApplication(activeApplication.id)
            if (existingThreads.isNotEmpty()) {
                return EnsureThreadResult(existingThreads.first().id, created = false)
            }
        }

        val existingGeneralThread = communicationRepository
            .getThreadsWithParticipants(listOf(currentUserId, resolvedStudent.userId))
            .firstOrNull { thread ->
                thread.internshipId.isNullOrBlank() && thread.applicationId.isNullOrBlank()
            }
        if (existingGeneralThread != null) {
            return EnsureThreadResult(existingGeneralThread.id, created = false)
        }

        // Create new thread
        val thread = communicationRepository.createThread(null, null)
            ?: return null

        // Add participants: current institution user + student
        communicationRepository.createThreadParticipant(thread.id, currentUserId)
        communicationRepository.createThreadParticipant(thread.id, resolvedStudent.userId)

        return EnsureThreadResult(thread.id, created = true)
    }

    private suspend fun collectThreadsForInstitution(
        internships: List<InternshipModel>,
        applications: List<ApplicationModel>,
        currentUserId: String,
    ): List<MessageThreadModel> {
        val internshipThreads = internships.flatMap { internship ->
            communicationRepository.getThreadsByInternship(internship.id)
        }
        val applicationThreads = applications.flatMap { application ->
            communicationRepository.getThreadByApplication(application.id)
        }
        val generalThreads = communicationRepository
            .getThreadsWithParticipants(listOf(currentUserId))
            .filter { it.internshipId.isNullOrBlank() && it.applicationId.isNullOrBlank() }
        return (internshipThreads + applicationThreads + generalThreads)
            .distinctBy { it.id }
            .sortedByDescending { it.createdAt }
    }

    private suspend fun MessageThreadModel.toConversationForInstitution(
        currentUserId: String,
        internshipsById: Map<String, InternshipModel>,
        applicationsById: Map<String, ApplicationModel>,
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

    private suspend fun resolveStudentContacts(
        internships: List<InternshipModel>,
        applications: List<ApplicationModel>,
        currentUserId: String,
    ): List<Contact> {
        val studentIds = (internships.map { it.studentId } + applications.map { it.studentId }).distinct()
        return studentIds.mapNotNull { studentId ->
            val student = studentRepository.getStudentById(studentId) ?: return@mapNotNull null
            val profile = profileRepository.getProfileById(student.userId) ?: return@mapNotNull null
            if (profile.id == currentUserId) return@mapNotNull null
            ChatMappers.toContact(profile, fallbackRole = "Estudante")
        }.distinctBy { it.id }
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
