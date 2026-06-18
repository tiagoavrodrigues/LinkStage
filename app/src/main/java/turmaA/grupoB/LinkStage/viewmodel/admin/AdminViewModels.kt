package turmaA.grupoB.LinkStage.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import turmaA.grupoB.LinkStage.data.remote.model.auth.SignUpInput
import turmaA.grupoB.LinkStage.data.remote.model.enums.ApplicationStatus
import turmaA.grupoB.LinkStage.data.remote.model.enums.InternshipStatus
import turmaA.grupoB.LinkStage.data.remote.model.enums.UserRole
import turmaA.grupoB.LinkStage.data.remote.model.institution.CreateInstitutionInput
import turmaA.grupoB.LinkStage.data.remote.model.institution.InstitutionModel
import turmaA.grupoB.LinkStage.data.remote.model.user.CreateStudentInput
import turmaA.grupoB.LinkStage.data.remote.model.user.CreateSupervisorInput
import turmaA.grupoB.LinkStage.data.remote.model.user.StudentModel
import turmaA.grupoB.LinkStage.data.remote.model.user.SupervisorModel
import turmaA.grupoB.LinkStage.data.repository.application.ApplicationRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.institution.InstitutionRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.offer.OfferRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.student.StudentRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.supervisor.SupervisorRepositoryInterface
import turmaA.grupoB.LinkStage.data.util.generateTemporaryPassword
import turmaA.grupoB.LinkStage.ui.admin.AdminInstitution
import turmaA.grupoB.LinkStage.ui.admin.AdminMentor
import turmaA.grupoB.LinkStage.ui.admin.AdminStudent
import turmaA.grupoB.LinkStage.ui.admin.InstitutionStatus
import turmaA.grupoB.LinkStage.ui.admin.avatarColors
import turmaA.grupoB.LinkStage.ui.admin.sampleInstitutionsList
import turmaA.grupoB.LinkStage.ui.admin.sampleMentorsList
import turmaA.grupoB.LinkStage.ui.admin.sampleStudentsList
import turmaA.grupoB.LinkStage.viewmodel.admin.toAdminStudents

class AdminDashboardViewModel(
    private val studentRepository: StudentRepositoryInterface,
    private val supervisorRepository: SupervisorRepositoryInterface,
    private val institutionRepository: InstitutionRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface,
    private val applicationRepository: ApplicationRepositoryInterface,
    private val internshipRepository: InternshipRepositoryInterface,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminDashboardUiState>(AdminDashboardUiState.Idle)
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = AdminDashboardUiState.Loading
            _uiState.value = AdminDashboardUiState.Success(loadDashboardData())
        }
    }

    fun resetState() {
        _uiState.value = AdminDashboardUiState.Idle
    }

    private suspend fun loadDashboardData(): AdminDashboardData = try {
        val students: List<StudentModel> = runCatching { studentRepository.getStudents() }.getOrDefault(emptyList())
        val supervisors: List<SupervisorModel> = runCatching { supervisorRepository.getSupervisors() }.getOrDefault(emptyList())
        val institutions: List<InstitutionModel> = runCatching { institutionRepository.getInstitutions() }.getOrDefault(emptyList())
        val profiles = profileRepository.getProfiles()
        val profilesByUserId = profiles.associateBy { it.id }

        val mappedInstitutions = institutions.map { institution ->
            institution.toAdminInstitution(
                profile = profilesByUserId[institution.userId],
                studentsCount = 0,
                mentorsCount = 0,
                activeInternshipsCount = 0,
            )
        }
        val mappedStudents = students.toAdminStudents(
            profilesByUserId = profilesByUserId,
            applicationsByStudentId = emptyMap(),
            internshipsByStudentId = emptyMap(),
            offersById = emptyMap(),
        )
        val mappedMentors = supervisors.toAdminMentors(
            profilesByUserId = profilesByUserId,
            activeInternshipsBySupervisorId = emptyMap(),
            skillsBySupervisorId = emptyMap(),
        )

        val pendingInstitutions = mappedInstitutions
            .takeIf { it.isNotEmpty() }
            ?.filter { it.status == InstitutionStatus.PENDING_APPROVAL }
            ?: fallbackDashboardData().pendingInstitutions
        val recentStudents = mappedStudents.takeIf { it.isNotEmpty() }?.take(3) ?: fallbackDashboardData().recentStudents
        val mentors = mappedMentors.takeIf { it.isNotEmpty() } ?: fallbackDashboardData().recentMentors
        val recentInstitutions = mappedInstitutions.takeIf { it.isNotEmpty() } ?: fallbackDashboardData().recentInstitutions

        AdminDashboardData(
            pendingInstitutions = pendingInstitutions,
            recentStudents = recentStudents,
            recentMentors = mentors.take(3),
            recentInstitutions = recentInstitutions.take(3),
            totalStudents = students.size,
            totalMentors = supervisors.size,
            totalInstitutions = institutions.size,
            pendingApplications = applicationRepository.getApplicationsByStatus(ApplicationStatus.PENDING).size,
            activeInternships = internshipRepository.getInternshipsByStatus(InternshipStatus.IN_PROGRESS).size,
        )
    } catch (_: Exception) {
        fallbackDashboardData()
    }
}

class AdminUsersViewModel(
    private val studentRepository: StudentRepositoryInterface,
    private val supervisorRepository: SupervisorRepositoryInterface,
    private val institutionRepository: InstitutionRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface,
    private val internshipRepository: InternshipRepositoryInterface,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminUsersUiState>(AdminUsersUiState.Idle)
    val uiState: StateFlow<AdminUsersUiState> = _uiState.asStateFlow()

    private var students: List<AdminStudent> = emptyList()
    private var mentors: List<AdminMentor> = emptyList()

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = AdminUsersUiState.Loading
            students = loadStudents()
            mentors = loadMentors()
            _uiState.value = AdminUsersUiState.Success(students, mentors)
        }
    }

    fun filterStudents(
        query: String = "",
        institution: String = "Todas",
        course: String = "Todos",
        internshipStatus: String = "Todos",
    ) {
        _uiState.value = AdminUsersUiState.StudentsSuccess(
            students.filterStudents(
                query = query,
                institution = institution,
                course = course,
                internshipStatus = internshipStatus,
            ),
        )
    }

    fun filterMentors(
        query: String = "",
        institution: String = "Todas",
        department: String = "Todos",
    ) {
        _uiState.value = AdminUsersUiState.MentorsSuccess(
            mentors.filterMentors(
                query = query,
                institution = institution,
                department = department,
            ),
        )
    }

    fun resetState() {
        students = emptyList()
        mentors = emptyList()
        _uiState.value = AdminUsersUiState.Idle
    }

    private suspend fun loadStudents(): List<AdminStudent> = try {
        val profiles = profileRepository.getProfiles()
        val profilesByUserId = profiles.associateBy { it.id }
        val internships = internshipRepository.getInternships()
        val internshipsByStudentId = internships.groupBy { it.studentId }
        val offersById = emptyMap<String, turmaA.grupoB.LinkStage.data.remote.model.offer.InternshipOfferModel>()

        studentRepository.getStudents()
            .filter { profilesByUserId[it.userId]?.active != false }
            .toAdminStudents(
                profilesByUserId = profilesByUserId,
                applicationsByStudentId = emptyMap(),
                internshipsByStudentId = internshipsByStudentId,
                offersById = offersById,
            )
            .takeIf { it.isNotEmpty() }
            ?: sampleStudentsList
    } catch (_: Exception) {
        sampleStudentsList
    }

    private suspend fun loadMentors(): List<AdminMentor> = try {
        val profiles = profileRepository.getProfiles()
        val profilesByUserId = profiles.associateBy { it.id }
        val internships = internshipRepository.getInternships()
        val activeInternshipsBySupervisorId = internships
            .filter { it.status.name == "IN_PROGRESS" }
            .groupBy { it.supervisorId ?: "" }

        supervisorRepository.getSupervisors()
            .filter { profilesByUserId[it.userId]?.active != false }
            .toAdminMentors(
                profilesByUserId = profilesByUserId,
                activeInternshipsBySupervisorId = activeInternshipsBySupervisorId,
                skillsBySupervisorId = emptyMap(),
            )
            .takeIf { it.isNotEmpty() }
            ?: sampleMentorsList
    } catch (_: Exception) {
        sampleMentorsList
    }
}

class AdminStudentDetailViewModel(
    private val studentRepository: StudentRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface,
    private val internshipRepository: InternshipRepositoryInterface,
    private val applicationRepository: ApplicationRepositoryInterface,
    private val institutionRepository: InstitutionRepositoryInterface,
    private val offerRepository: OfferRepositoryInterface,
    private val authRepository: AuthRepositoryInterface,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminStudentDetailUiState>(AdminStudentDetailUiState.Idle)
    val uiState: StateFlow<AdminStudentDetailUiState> = _uiState.asStateFlow()

    fun loadStudent(studentId: String) {
        viewModelScope.launch {
            _uiState.value = AdminStudentDetailUiState.Loading
            _uiState.value = AdminStudentDetailUiState.Success(loadStudentData(studentId))
        }
    }

    fun resetState() {
        _uiState.value = AdminStudentDetailUiState.Idle
    }

    fun removeAccount(userId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.setProfileActive(userId, false)
            onComplete()
        }
    }

    private suspend fun loadStudentData(studentId: String): AdminStudentDetailData = try {
        val student = studentRepository.getStudentById(studentId) ?: return fallbackStudentDetail(studentId)
        val profile = profileRepository.getProfileById(student.userId)
        val applications = applicationRepository.getApplicationsByStudent(student.id)
        val internships = internshipRepository.getInternshipsByStudent(student.id)
        val offersById = runCatching {
            (internships.map { it.offerId } + applications.map { it.offerId })
                .distinct()
                .mapNotNull { offerRepository.getOfferById(it) }
                .associateBy { it.id }
        }.getOrDefault(emptyMap())
        val institutions = institutionRepository.getInstitutions().associateBy { it.id }

        val adminStudent = listOf(student).toAdminStudents(
            profilesByUserId = profile?.let { mapOf(student.userId to it) } ?: emptyMap(),
            applicationsByStudentId = mapOf(student.id to applications),
            internshipsByStudentId = mapOf(student.id to internships),
            offersById = offersById,
            institutionsById = institutions.mapValues { it.value.name },
        ).first().copy(
            institution = institutions[internships.firstOrNull()?.institutionId]?.name ?: student.institutionFallback(),
            institutionCode = institutions[internships.firstOrNull()?.institutionId]?.id ?: student.course.substringBefore(" ").uppercase(),
        )

        AdminStudentDetailData(
            student = adminStudent,
            applications = applications,
            internships = internships,
            applicationSummaries = applications.toApplicationSummaries(
                offersById = offersById,
                institutionsById = institutions,
                internships = internships,
            ),
        )
    } catch (_: Exception) {
        fallbackStudentDetail(studentId)
    }
}

class AdminMentorDetailViewModel(
    private val supervisorRepository: SupervisorRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface,
    private val internshipRepository: InternshipRepositoryInterface,
    private val studentRepository: StudentRepositoryInterface,
    private val authRepository: AuthRepositoryInterface,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminMentorDetailUiState>(AdminMentorDetailUiState.Idle)
    val uiState: StateFlow<AdminMentorDetailUiState> = _uiState.asStateFlow()

    fun loadMentor(mentorId: String) {
        viewModelScope.launch {
            _uiState.value = AdminMentorDetailUiState.Loading
            _uiState.value = AdminMentorDetailUiState.Success(loadMentorData(mentorId))
        }
    }

    fun resetState() {
        _uiState.value = AdminMentorDetailUiState.Idle
    }

    fun removeAccount(userId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.setProfileActive(userId, false)
            onComplete()
        }
    }

    private suspend fun loadMentorData(mentorId: String): AdminMentorDetailData = try {
        val supervisor = supervisorRepository.getSupervisorById(mentorId) ?: return fallbackMentorDetail(mentorId)
        val profile = profileRepository.getProfileById(supervisor.userId)
        val skills = supervisorRepository.getSupervisorSkills(mentorId)
        val activeInternships = internshipRepository.getInternshipsByStatus(turmaA.grupoB.LinkStage.data.remote.model.enums.InternshipStatus.IN_PROGRESS)
        val activeInternshipsBySupervisorId = activeInternships.groupBy { it.supervisorId ?: "" }
        val mentor = supervisor.toAdminMentor(
            profile = profile,
            activeInternshipsBySupervisorId = activeInternshipsBySupervisorId,
            skills = skills,
        )
        val supervisedStudents = activeInternshipsBySupervisorId[mentorId]
            .orEmpty()
            .mapNotNull { internshipRepository.getInternshipById(it.id)?.studentId }
            .mapNotNull { studentId -> studentRepository.getStudentById(studentId) }
            .toAdminStudents(
                profilesByUserId = profileRepository.getProfiles().associateBy { it.id },
                applicationsByStudentId = emptyMap(),
                internshipsByStudentId = emptyMap(),
                offersById = emptyMap(),
            )
            .takeIf { it.isNotEmpty() }
            ?: sampleStudentsList.filter { it.institution == mentor.institution }

        AdminMentorDetailData(
            mentor = mentor,
            supervisedStudents = supervisedStudents,
        )
    } catch (_: Exception) {
        fallbackMentorDetail(mentorId)
    }
}

class AdminInstitutionsViewModel(
    private val institutionRepository: InstitutionRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface,
    private val studentRepository: StudentRepositoryInterface,
    private val supervisorRepository: SupervisorRepositoryInterface,
    private val internshipRepository: InternshipRepositoryInterface,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminInstitutionsUiState>(AdminInstitutionsUiState.Idle)
    val uiState: StateFlow<AdminInstitutionsUiState> = _uiState.asStateFlow()

    private var approvedInstitutions: List<AdminInstitution> = emptyList()
    private var pendingInstitutions: List<AdminInstitution> = emptyList()

    fun loadInstitutions() {
        viewModelScope.launch {
            _uiState.value = AdminInstitutionsUiState.Loading
            val institutions = loadInstitutionList()
            approvedInstitutions = institutions.filter { it.status == InstitutionStatus.APPROVED }
            pendingInstitutions = institutions.filter { it.status == InstitutionStatus.PENDING_APPROVAL }
            _uiState.value = AdminInstitutionsUiState.Success(approvedInstitutions, pendingInstitutions)
        }
    }

    fun filterInstitutions(
        query: String = "",
        type: String = "Todas",
        location: String = "Todas",
    ) {
        val all = approvedInstitutions + pendingInstitutions
        _uiState.value = AdminInstitutionsUiState.Success(
            approvedInstitutions = all.filterInstitutions(query, type, location)
                .filter { it.status == InstitutionStatus.APPROVED },
            pendingInstitutions = all.filterInstitutions(query, type, location)
                .filter { it.status == InstitutionStatus.PENDING_APPROVAL },
        )
    }

    fun resetState() {
        approvedInstitutions = emptyList()
        pendingInstitutions = emptyList()
        _uiState.value = AdminInstitutionsUiState.Idle
    }

    private suspend fun loadInstitutionList(): List<AdminInstitution> = try {
        val profiles = profileRepository.getProfiles()
        val profilesByUserId = profiles.associateBy { it.id }
        val students = studentRepository.getStudents()
        val supervisors = supervisorRepository.getSupervisors()
        val internships = internshipRepository.getInternships()
        val activeInternshipsByInstitution = internships
            .filter { it.status.name == "IN_PROGRESS" }
            .groupBy { it.institutionId }

        institutionRepository.getInstitutions()
            .filter { profilesByUserId[it.userId]?.active != false }
            .map { institution ->
                institution.toAdminInstitution(
                    profile = profilesByUserId[institution.userId],
                    studentsCount = students.count { it.course.contains(institution.name, ignoreCase = true) },
                    mentorsCount = supervisors.count { it.department?.contains(institution.name, ignoreCase = true) == true },
                    activeInternshipsCount = activeInternshipsByInstitution[institution.id].orEmpty().size,
                )
            }
            .takeIf { it.isNotEmpty() }
            ?: sampleInstitutionsList
    } catch (_: Exception) {
        sampleInstitutionsList
    }
}

class AdminInstitutionDetailViewModel(
    private val institutionRepository: InstitutionRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface,
    private val studentRepository: StudentRepositoryInterface,
    private val supervisorRepository: SupervisorRepositoryInterface,
    private val internshipRepository: InternshipRepositoryInterface,
    private val authRepository: AuthRepositoryInterface,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminInstitutionDetailUiState>(AdminInstitutionDetailUiState.Idle)
    val uiState: StateFlow<AdminInstitutionDetailUiState> = _uiState.asStateFlow()

    fun loadInstitution(institutionId: String) {
        viewModelScope.launch {
            _uiState.value = AdminInstitutionDetailUiState.Loading
            _uiState.value = AdminInstitutionDetailUiState.Success(loadInstitutionData(institutionId))
        }
    }

    fun resetState() {
        _uiState.value = AdminInstitutionDetailUiState.Idle
    }

    fun removeAccount(userId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.setProfileActive(userId, false)
            onComplete()
        }
    }

    private suspend fun loadInstitutionData(institutionId: String): AdminInstitutionDetailData = try {
        val institution = institutionRepository.getInstitutionById(institutionId) ?: return fallbackInstitutionDetail(institutionId)
        val profile = profileRepository.getProfileById(institution.userId)
        val students = studentRepository.getStudents()
        val supervisors = supervisorRepository.getSupervisors()
        val internships = internshipRepository.getInternshipsByInstitution(institutionId)
        val adminInstitution = institution.toAdminInstitution(
            profile = profile,
            studentsCount = students.count { it.course.contains(institution.name, ignoreCase = true) },
            mentorsCount = supervisors.count { it.department?.contains(institution.name, ignoreCase = true) == true },
            activeInternshipsCount = internships.count { it.status.name == "IN_PROGRESS" },
        )
        val relatedStudents = students
            .filter { it.course.contains(institution.name, ignoreCase = true) }
            .toAdminStudents(
                profilesByUserId = profileRepository.getProfiles().associateBy { it.id },
                applicationsByStudentId = emptyMap(),
                internshipsByStudentId = emptyMap(),
                offersById = emptyMap(),
            )
            .ifEmpty { sampleStudentsList }
        val relatedMentors = supervisors
            .filter { it.department?.contains(institution.name, ignoreCase = true) == true }
            .toAdminMentors(
                profilesByUserId = profileRepository.getProfiles().associateBy { it.id },
                activeInternshipsBySupervisorId = emptyMap(),
                skillsBySupervisorId = emptyMap(),
            )
            .ifEmpty { sampleMentorsList }

        AdminInstitutionDetailData(
            institution = adminInstitution,
            relatedStudents = relatedStudents,
            relatedMentors = relatedMentors,
        )
    } catch (_: Exception) {
        fallbackInstitutionDetail(institutionId)
    }
}

class AdminInternshipDetailViewModel(
    private val internshipRepository: InternshipRepositoryInterface,
    private val offerRepository: turmaA.grupoB.LinkStage.data.repository.offer.OfferRepositoryInterface,
    private val institutionRepository: InstitutionRepositoryInterface,
    private val studentRepository: StudentRepositoryInterface,
    private val supervisorRepository: SupervisorRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface,
    private val applicationRepository: ApplicationRepositoryInterface,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminInternshipDetailUiState>(AdminInternshipDetailUiState.Idle)
    val uiState: StateFlow<AdminInternshipDetailUiState> = _uiState.asStateFlow()

    fun loadInternship(internshipId: String) {
        viewModelScope.launch {
            _uiState.value = AdminInternshipDetailUiState.Loading
            _uiState.value = AdminInternshipDetailUiState.Success(loadInternshipData(internshipId))
        }
    }

    fun resetState() {
        _uiState.value = AdminInternshipDetailUiState.Idle
    }

    private suspend fun loadInternshipData(internshipId: String): AdminInternshipDetailData = try {
        val internship = internshipRepository.getInternshipById(internshipId)
            ?: internshipRepository.getInternshipsByStudent(internshipId)
                .firstOrNull { it.status == InternshipStatus.IN_PROGRESS }
            ?: return fallbackInternshipDetail(internshipId)
        val offer = offerRepository.getOfferById(internship.offerId) ?: return fallbackInternshipDetail(internshipId)
        val institution = institutionRepository.getInstitutionById(offer.institutionId)
        val student = studentRepository.getStudentById(internship.studentId)
        val supervisor = internship.supervisorId?.let { supervisorRepository.getSupervisorById(it) }
        val applications = applicationRepository.getApplicationsByStudent(internship.studentId)
        val profiles = profileRepository.getProfiles().associateBy { it.id }
        val adminStudent = student?.let {
            listOf(it).toAdminStudents(
                profilesByUserId = profiles,
                applicationsByStudentId = mapOf(it.id to applications),
                internshipsByStudentId = mapOf(it.id to listOf(internship)),
                offersById = mapOf(offer.id to offer),
            ).first()
        } ?: sampleStudentsList.first()
        val adminMentor = supervisor?.toAdminMentor(
            profile = profiles[supervisor.userId],
            activeInternshipsBySupervisorId = mapOf(supervisor.id to listOf(internship)),
            skills = supervisorRepository.getSupervisorSkills(supervisor.id),
        )

        AdminInternshipDetailData(
            internship = internship,
            offer = offer,
            institutionName = institution?.name ?: "Instituição",
            student = adminStudent,
            mentor = adminMentor,
            applications = applications,
        )
    } catch (_: Exception) {
        fallbackInternshipDetail(internshipId)
    }
}

class AdminAccountViewModel(
    private val authRepository: AuthRepositoryInterface,
    private val studentRepository: StudentRepositoryInterface,
    private val supervisorRepository: SupervisorRepositoryInterface,
    private val institutionRepository: InstitutionRepositoryInterface,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AdminAccountUiState>(AdminAccountUiState.Idle)
    val uiState: StateFlow<AdminAccountUiState> = _uiState.asStateFlow()

    fun createStudentAccount(name: String, email: String, course: String, studentNumber: String) {
        viewModelScope.launch {
            _uiState.value = AdminAccountUiState.Loading

            try {
                val password = generateTemporaryPassword()
                val profile = authRepository.createManagedAccount(
                    SignUpInput(
                        name = name,
                        email = email,
                        password = password,
                        role = UserRole.STUDENT,
                        rgpdConsent = true,
                    )
                )

                studentRepository.createStudent(
                    CreateStudentInput(
                        userId = profile.id,
                        studentNumber = studentNumber,
                        course = course,
                    )
                )

                _uiState.value = AdminAccountUiState.Success(password)
            } catch (e: Exception) {
                _uiState.value = AdminAccountUiState.Error(e.message ?: "Erro ao criar conta de estudante.")
            }
        }
    }

    fun createMentorAccount(name: String, email: String, department: String) {
        viewModelScope.launch {
            _uiState.value = AdminAccountUiState.Loading

            try {
                val password = generateTemporaryPassword()
                val profile = authRepository.createManagedAccount(
                    SignUpInput(
                        name = name,
                        email = email,
                        password = password,
                        role = UserRole.SUPERVISOR,
                        rgpdConsent = true,
                    )
                )

                supervisorRepository.createSupervisor(
                    CreateSupervisorInput(
                        userId = profile.id,
                        department = department.ifBlank { null },
                    )
                )

                _uiState.value = AdminAccountUiState.Success(password)
            } catch (e: Exception) {
                _uiState.value = AdminAccountUiState.Error(e.message ?: "Erro ao criar conta de orientador.")
            }
        }
    }

    fun createInstitutionAccount(name: String, email: String, sector: String, address: String) {
        viewModelScope.launch {
            _uiState.value = AdminAccountUiState.Loading

            try {
                val password = generateTemporaryPassword()
                val profile = authRepository.createManagedAccount(
                    SignUpInput(
                        name = name,
                        email = email,
                        password = password,
                        role = UserRole.INSTITUTION,
                        rgpdConsent = true,
                    )
                )

                institutionRepository.createInstitution(
                    CreateInstitutionInput(
                        userId = profile.id,
                        name = name,
                        address = address.ifBlank { null },
                        sector = sector.ifBlank { null },
                    )
                )

                _uiState.value = AdminAccountUiState.Success(password)
            } catch (e: Exception) {
                _uiState.value = AdminAccountUiState.Error(e.message ?: "Erro ao criar conta de instituição.")
            }
        }
    }

    fun resetState() {
        _uiState.value = AdminAccountUiState.Idle
    }
}

private fun StudentModel.institutionFallback(): String = course.substringBefore(" ").ifBlank { "Não indicada" }
