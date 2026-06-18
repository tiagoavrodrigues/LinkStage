package turmaA.grupoB.LinkStage.ui.instituicao

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.data.remote.model.enums.ApplicationStatus as RemoteApplicationStatus
import turmaA.grupoB.LinkStage.data.remote.model.enums.InternshipStatus as RemoteInternshipStatus
import turmaA.grupoB.LinkStage.data.remote.model.internship.InternshipModel
import turmaA.grupoB.LinkStage.data.remote.model.offer.InternshipOfferModel
import turmaA.grupoB.LinkStage.data.remote.model.user.ProfileModel
import turmaA.grupoB.LinkStage.data.remote.model.user.SupervisorModel
import turmaA.grupoB.LinkStage.ui.admin.AdminMentor
import turmaA.grupoB.LinkStage.ui.aluno.chat.avatarColors
import turmaA.grupoB.LinkStage.ui.aluno.home.ApplicationStatus
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.ui.theme.Red
import turmaA.grupoB.LinkStage.viewmodel.application.InstitutionApplicationDetails

enum class InternshipOrigin { SCHOOL, COMPANY }

data class InstitutionInternship(
    val id: String,
    val studentName: String,
    val studentAvatarInitials: String,
    val studentAvatarColorIndex: Int = 0,
    val offerTitle: String,
    val origin: InternshipOrigin = InternshipOrigin.SCHOOL,
    val schoolMentorName: String = "",
    val companyMentorName: String = "",
    val progressPercent: Int,
    val status: InternshipStatus,
) {
    val mentorName: String
        get() = when (origin) {
            InternshipOrigin.SCHOOL -> schoolMentorName
            InternshipOrigin.COMPANY -> listOfNotNull(
                schoolMentorName.ifEmpty { null },
                companyMentorName.ifEmpty { null },
            ).joinToString(" · ")
        }

    val hasMentor: Boolean
        get() = when (origin) {
            InternshipOrigin.SCHOOL -> schoolMentorName.isNotEmpty()
            InternshipOrigin.COMPANY -> schoolMentorName.isNotEmpty() && companyMentorName.isNotEmpty()
        }

    val needsSchoolMentor: Boolean get() = schoolMentorName.isEmpty()
    val needsCompanyMentor: Boolean get() = origin == InternshipOrigin.COMPANY && companyMentorName.isEmpty()
}

enum class InternshipStatus {
    IN_PROGRESS,
    PENDING_REVIEW,
    COMPLETED,
    NO_MENTOR,
}

data class InstitutionMentorItem(
    val id: String,
    val name: String,
    val avatarInitials: String,
    val avatarColorIndex: Int = 0,
    val institution: String,
    val status: MentorStatus,
)

enum class MentorStatus {
    ACTIVE,
    INACTIVE,
    NO_STUDENTS,
}

fun sampleInstitutionInternships(context: Context) = listOf(
    InstitutionInternship(
        id = "int1", studentName = "Tomás Silva", studentAvatarInitials = "TS", studentAvatarColorIndex = 0,
        offerTitle = "UI/UX Designer", origin = InternshipOrigin.SCHOOL,
        schoolMentorName = "Prof. Miguel Azevedo",
        progressPercent = 70, status = InternshipStatus.IN_PROGRESS,
    ),
    InstitutionInternship(
        id = "int2", studentName = "Francisco Fern.", studentAvatarInitials = "FF", studentAvatarColorIndex = 1,
        offerTitle = "Web Developer", origin = InternshipOrigin.COMPANY,
        schoolMentorName = "Prof. Tiago Alex.", companyMentorName = "Eng. Rui Sousa",
        progressPercent = 40, status = InternshipStatus.IN_PROGRESS,
    ),
    InstitutionInternship(
        id = "int3", studentName = "Ana Costa", studentAvatarInitials = "AC", studentAvatarColorIndex = 2,
        offerTitle = "Data Analyst", origin = InternshipOrigin.COMPANY,
        schoolMentorName = "Prof. Tiago Alex.", companyMentorName = "Dr. Marta Lopes",
        progressPercent = 100, status = InternshipStatus.PENDING_REVIEW,
    ),
    InstitutionInternship(
        id = "int4", studentName = "João Pinto", studentAvatarInitials = "JP", studentAvatarColorIndex = 1,
        offerTitle = context.getString(R.string.mock_product_designer), origin = InternshipOrigin.SCHOOL,
        progressPercent = 0, status = InternshipStatus.NO_MENTOR,
    ),
    InstitutionInternship(
        id = "int5", studentName = "Ricardo Lopes", studentAvatarInitials = "RL", studentAvatarColorIndex = 0,
        offerTitle = context.getString(R.string.mock_fullstack_developer), origin = InternshipOrigin.COMPANY,
        schoolMentorName = "Prof. Carvalho",
        progressPercent = 0, status = InternshipStatus.NO_MENTOR,
    ),
)

val sampleInstitutionMentors = listOf(
    InstitutionMentorItem("m1", "Miguel Azev.", "MA", 2, "ESTG-IPVC", MentorStatus.ACTIVE),
    InstitutionMentorItem("m2", "Miguel Azev.", "MA", 2, "ESTG-IPVC", MentorStatus.INACTIVE),
    InstitutionMentorItem("m3", "Miguel Azev.", "MA", 2, "ESTG-IPVC", MentorStatus.NO_STUDENTS),
)

@Composable
fun internshipStatusLabel(status: InternshipStatus): String = when (status) {
    InternshipStatus.IN_PROGRESS -> stringResource(R.string.status_in_progress)
    InternshipStatus.PENDING_REVIEW -> stringResource(R.string.status_pending_review)
    InternshipStatus.COMPLETED -> stringResource(R.string.status_completed)
    InternshipStatus.NO_MENTOR -> stringResource(R.string.status_no_mentor)
}

fun internshipStatusColor(status: InternshipStatus): Color = when (status) {
    InternshipStatus.IN_PROGRESS -> LightBlue
    InternshipStatus.PENDING_REVIEW -> Color(0xFFF5C518)
    InternshipStatus.COMPLETED -> Color(0xFF9E9E9E)
    InternshipStatus.NO_MENTOR -> Red
}

@Composable
fun mentorStatusLabel(status: MentorStatus): String = when (status) {
    MentorStatus.ACTIVE -> stringResource(R.string.mentor_status_active)
    MentorStatus.INACTIVE -> stringResource(R.string.mentor_status_inactive)
    MentorStatus.NO_STUDENTS -> stringResource(R.string.mentor_status_no_students)
}

fun mentorStatusColor(status: MentorStatus): Color = when (status) {
    MentorStatus.ACTIVE -> LightBlue
    MentorStatus.INACTIVE -> Color(0xFF9E9E9E)
    MentorStatus.NO_STUDENTS -> Red
}

data class InstitutionApplication(
    val id: String,
    val studentName: String,
    val studentAvatarInitials: String,
    val studentAvatarColorIndex: Int = 0,
    val institution: String,
    val course: String,
    val gpa: String,
    val email: String,
    val phone: String,
    val skills: List<String>,
    val personalStatement: String,
    val motivationLetterTitle: String,
    val motivationLetterBody: String,
    val hasMotivationLetter: Boolean,
    val status: ApplicationStatus,
)

fun sampleInstitutionApplications(context: Context) = listOf(
    InstitutionApplication(
        "a1", "Miguel Azev.", "MA", 2, "ESTG-IPVC", context.getString(R.string.mock_course_cs), "14,7",
        "miguel@estg.ipvc.pt", "912000001",
        listOf("Python", context.getString(R.string.skill_teamwork), context.getString(R.string.skill_project_management), context.getString(R.string.skill_ai), "C++", "Java"),
        "Lorem ipsum dolor sit amet consectetur adipiscing elit. Quisque faucibus ex sapien vitae pellentesque sem placerat.",
        "Lorem ipsum dolor sit.",
        "Lorem ipsum dolor sit amet consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
        true, ApplicationStatus.ACCEPTED,
    ),
    InstitutionApplication(
        "a2", "Miguel Azev.", "MA", 2, "ESTG-IPVC", context.getString(R.string.mock_course_cs), "13,2",
        "miguel2@estg.ipvc.pt", "912000002",
        listOf("Python", "Java"),
        context.getString(R.string.mock_personal_statement_generic),
        context.getString(R.string.mock_motivation_letter_title), context.getString(R.string.mock_motivation_letter_body),
        true, ApplicationStatus.PENDING,
    ),
    InstitutionApplication(
        "a3", "Miguel Azev.", "MA", 2, "ESTG-IPVC", context.getString(R.string.mock_course_cs), "12,0",
        "miguel3@estg.ipvc.pt", "912000003",
        listOf("C++"),
        context.getString(R.string.mock_personal_statement_short),
        context.getString(R.string.mock_motivation_letter_title), context.getString(R.string.mock_motivation_letter_body_short),
        true, ApplicationStatus.PENDING,
    ),
)

fun RemoteApplicationStatus.toUiApplicationStatus(): ApplicationStatus = when (this) {
    RemoteApplicationStatus.PENDING -> ApplicationStatus.PENDING
    RemoteApplicationStatus.ACCEPTED -> ApplicationStatus.ACCEPTED
    RemoteApplicationStatus.REJECTED -> ApplicationStatus.REJECTED
}

fun InstitutionApplicationDetails.toInstitutionApplication(): InstitutionApplication {
    val name = profile?.name ?: "Estudante"
    return InstitutionApplication(
        id = application.id,
        studentName = name,
        studentAvatarInitials = name.initials().ifBlank { "?" },
        studentAvatarColorIndex = application.studentId.hashCode().absMod(avatarColors.size),
        institution = "",
        course = student?.course.orEmpty(),
        gpa = student?.averageGrade?.let { "%.1f".format(it).replace('.', ',') } ?: "-",
        email = profile?.email.orEmpty(),
        phone = profile?.phone.orEmpty(),
        skills = student?.cvData?.keys?.toList().orEmpty(),
        personalStatement = "",
        motivationLetterTitle = "",
        motivationLetterBody = application.motivationLetter.orEmpty(),
        hasMotivationLetter = !application.motivationLetter.isNullOrBlank(),
        status = application.status.toUiApplicationStatus(),
    )
}

fun InternshipModel.toInstitutionInternship(
    studentProfile: ProfileModel?,
    offer: InternshipOfferModel?,
    supervisorProfile: ProfileModel?,
): InstitutionInternship {
    val studentName = studentProfile?.name ?: "Aluno"
    val origin = if (companySupervisorName.isNullOrBlank()) InternshipOrigin.SCHOOL else InternshipOrigin.COMPANY
    val uiStatus = when {
        supervisorId == null -> InternshipStatus.NO_MENTOR
        status == RemoteInternshipStatus.IN_PROGRESS -> InternshipStatus.IN_PROGRESS
        status == RemoteInternshipStatus.COMPLETED -> InternshipStatus.PENDING_REVIEW
        status == RemoteInternshipStatus.EVALUATED -> InternshipStatus.COMPLETED
        status == RemoteInternshipStatus.CANCELED -> InternshipStatus.COMPLETED
        else -> InternshipStatus.NO_MENTOR
    }
    val progress = when (status) {
        RemoteInternshipStatus.PENDING_SUPERVISOR -> 0
        RemoteInternshipStatus.IN_PROGRESS -> 50
        RemoteInternshipStatus.COMPLETED -> 90
        RemoteInternshipStatus.EVALUATED -> 100
        RemoteInternshipStatus.CANCELED -> 100
    }
    return InstitutionInternship(
        id = id,
        studentName = studentName,
        studentAvatarInitials = studentName.initials().ifBlank { "?" },
        studentAvatarColorIndex = studentId.hashCode().absMod(avatarColors.size),
        offerTitle = offer?.title ?: title,
        origin = origin,
        schoolMentorName = supervisorProfile?.name.orEmpty(),
        companyMentorName = companySupervisorName.orEmpty(),
        progressPercent = progress,
        status = uiStatus,
    )
}

fun SupervisorModel.toInstitutionMentorItem(
    profile: ProfileModel?,
    activeInternshipsCount: Int,
): InstitutionMentorItem {
    val name = profile?.name ?: "Mentor"
    val status = when {
        !acceptsNewInternships -> MentorStatus.INACTIVE
        activeInternshipsCount > 0 -> MentorStatus.ACTIVE
        else -> MentorStatus.NO_STUDENTS
    }
    return InstitutionMentorItem(
        id = id,
        name = name,
        avatarInitials = name.initials().ifBlank { "?" },
        avatarColorIndex = userId.hashCode().absMod(avatarColors.size),
        institution = "IPVC",
        status = status,
    )
}

fun SupervisorModel.toAdminMentor(
    profile: ProfileModel?,
    activeInternshipsCount: Int,
    skills: List<String> = emptyList(),
): AdminMentor {
    val name = profile?.name ?: "Mentor"
    return AdminMentor(
        id = id,
        name = name,
        email = profile?.email.orEmpty(),
        phone = profile?.phone.orEmpty(),
        institution = "IPVC",
        department = department.orEmpty(),
        registeredAgo = "",
        activeStudentsCount = activeInternshipsCount,
        avatarInitials = name.initials().ifBlank { "?" },
        avatarColorIndex = userId.hashCode().absMod(avatarColors.size),
        skills = skills,
        supervisionAreas = listOfNotNull(specialty),
        internalNote = "",
        isAvailable = acceptsNewInternships,
    )
}

private fun String.initials(): String = split(' ')
    .filter { it.isNotBlank() }
    .take(2)
    .joinToString("") { it.first().uppercase() }

private fun Int.absMod(modulus: Int): Int = if (modulus == 0) 0 else kotlin.math.abs(this) % modulus
