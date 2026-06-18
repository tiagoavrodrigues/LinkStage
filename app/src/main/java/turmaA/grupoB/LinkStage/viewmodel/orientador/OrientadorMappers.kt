package turmaA.grupoB.LinkStage.viewmodel.orientador

import androidx.compose.ui.graphics.Color
import turmaA.grupoB.LinkStage.data.remote.model.enums.EvaluationType
import turmaA.grupoB.LinkStage.data.remote.model.enums.InternshipStatus
import turmaA.grupoB.LinkStage.data.remote.model.evaluation.EvaluationModel
import turmaA.grupoB.LinkStage.data.remote.model.institution.InstitutionModel
import turmaA.grupoB.LinkStage.data.remote.model.internship.ActivityLogModel
import turmaA.grupoB.LinkStage.data.remote.model.internship.InternshipModel
import turmaA.grupoB.LinkStage.data.remote.model.offer.InternshipOfferModel
import turmaA.grupoB.LinkStage.data.remote.model.user.ProfileModel
import turmaA.grupoB.LinkStage.data.remote.model.user.StudentModel
import turmaA.grupoB.LinkStage.ui.admin.AdminStudent
import turmaA.grupoB.LinkStage.ui.admin.avatarColors
import turmaA.grupoB.LinkStage.ui.aluno.activity.ActiveInternship
import turmaA.grupoB.LinkStage.ui.aluno.activity.ActivityLog
import turmaA.grupoB.LinkStage.ui.aluno.activity.ActivityLogStatus
import turmaA.grupoB.LinkStage.ui.orientador.EvaluationState
import turmaA.grupoB.LinkStage.ui.orientador.InternshipEvaluation
import turmaA.grupoB.LinkStage.ui.orientador.InternshipType
import turmaA.grupoB.LinkStage.ui.orientador.MentorInternship
import turmaA.grupoB.LinkStage.ui.orientador.calculateEvaluationState
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit

internal fun List<InternshipModel>.toMentorInternships(
    studentsById: Map<String, StudentModel>,
    profilesByUserId: Map<String, ProfileModel>,
    offersById: Map<String, InternshipOfferModel>,
    institutionsById: Map<String, InstitutionModel>,
): List<MentorInternship> = map { internship ->
    internship.toMentorInternship(
        student = studentsById[internship.studentId],
        profile = profilesByUserId[studentsById[internship.studentId]?.userId.orEmpty()],
        offer = offersById[internship.offerId],
        institution = institutionsById[internship.institutionId],
    )
}

internal fun InternshipModel.toMentorInternship(
    student: StudentModel? = null,
    profile: ProfileModel? = null,
    offer: InternshipOfferModel? = null,
    institution: InstitutionModel? = null,
): MentorInternship {
    val offerTitle = offer?.title ?: title
    val companyName = institution?.name ?: "Empresa"
    val schoolName = "IPVC"

    return MentorInternship(
        id = id,
        offerTitle = offerTitle,
        businessInstitutionName = companyName,
        schoolInstitutionName = schoolName,
        logoInitial = institution?.name?.firstInitial() ?: offerTitle.firstInitial(),
        logoColor = institution?.name?.logoColor() ?: offerTitle.logoColor(),
        endDate = endDate.toLocalDateOrDefault(LocalDate.now().plusMonths(4)),
        studentName = profile?.name ?: student?.studentNumber ?: "Aluno",
        studentId = studentId,
        location = offer?.location ?: "",
        duration = "",
        type = offer?.modality ?: "",
        aboutCompany = institution?.description ?: offer?.description ?: "",
        responsibilities = offer?.requirements?.splitLines().orEmpty(),
        requirements = offer?.requirements?.splitLines().orEmpty(),
        benefits = offer?.salary?.let { listOf("Salário: ${it}€") }.orEmpty(),
        isBusinessInternship = institution?.sector != "Instituição de Ensino",
    )
}

internal fun List<StudentModel>.toMentorStudents(
    profilesByUserId: Map<String, ProfileModel>,
    internshipsByStudentId: Map<String, List<InternshipModel>>,
    offersById: Map<String, InternshipOfferModel>,
): List<AdminStudent> = map { student ->
    student.toMentorStudent(
        profile = profilesByUserId[student.userId],
        internships = internshipsByStudentId[student.id].orEmpty(),
        offersById = offersById,
    )
}

internal fun StudentModel.toMentorStudent(
    profile: ProfileModel? = null,
    internships: List<InternshipModel> = emptyList(),
    offersById: Map<String, InternshipOfferModel> = emptyMap(),
): AdminStudent {
    val activeInternship = internships.firstOrNull { it.status == InternshipStatus.IN_PROGRESS }
    val activeOffer = activeInternship?.offerId?.let { offersById[it] }

    return AdminStudent(
        id = id,
        name = profile?.name ?: studentNumber,
        email = profile?.email ?: "",
        phone = profile?.phone ?: "",
        institution = course.substringBefore(" ").ifBlank { "Não indicada" },
        institutionCode = course.substringBefore(" ").uppercase().ifBlank { "IPVC" },
        course = course,
        gpa = averageGrade?.toFloat() ?: 0f,
        registeredAgo = createdAt.orientadorRelativeTime(),
        hasActiveInternship = activeInternship != null,
        internshipCompany = activeOffer?.title ?: "",
        applicationCount = 0,
        avatarInitials = profile?.name?.initials() ?: studentNumber.take(2).uppercase().ifBlank { "AL" },
        avatarColorIndex = id.hashCode().absMod(avatarColors.size),
        skills = cvData?.keys?.toList().orEmpty(),
    )
}

internal fun List<ActivityLogModel>.toMentorActivityLogs(
    studentsById: Map<String, StudentModel> = emptyMap(),
    profilesByUserId: Map<String, ProfileModel> = emptyMap(),
    institutionsById: Map<String, InstitutionModel> = emptyMap(),
): List<ActivityLog> = map { log ->
    log.toMentorActivityLog(
        student = studentsById[log.studentId],
        profile = profilesByUserId[studentsById[log.studentId]?.userId.orEmpty()],
        institution = institutionsById[log.internshipId],
    )
}

internal fun ActivityLogModel.toMentorActivityLog(
    student: StudentModel? = null,
    profile: ProfileModel? = null,
    institution: InstitutionModel? = null,
): ActivityLog {
    val createdByMentor = type == "MENTOR"
    val date = activityDate.toLocalDateOrDefault(LocalDate.now())

    return ActivityLog(
        id = id,
        title = type ?: "Checkpoint",
        description = description,
        date = date,
        status = if (createdByMentor) ActivityLogStatus.PENDING else ActivityLogStatus.COMPLETED,
        company = institution?.name ?: "Viana S.T.Arts",
        companyLogoInitial = institution?.name?.firstInitial() ?: "V",
        companyLogoColor = institution?.name?.logoColor() ?: Color(0xFF212121),
        requirements = emptyList(),
        hasSubmitted = !createdByMentor,
        submittedAt = if (!createdByMentor) date else null,
        submittedFiles = emptyList(),
        createdBy = if (createdByMentor) "MENTOR" else "STUDENT",
        createdByName = if (createdByMentor) profile?.name ?: student?.studentNumber ?: "Orientador" else "",
    )
}

internal fun List<EvaluationModel>.toInternshipEvaluation(
    internshipId: String? = null,
): InternshipEvaluation? {
    val supervisorEvaluation = firstOrNull { it.evaluatorType == EvaluationType.SUPERVISOR }
    val institutionEvaluation = firstOrNull { it.evaluatorType == EvaluationType.INSTITUTION }
    val resolvedInternshipId = internshipId
        ?: supervisorEvaluation?.internshipId
        ?: institutionEvaluation?.internshipId
        ?: return null

    val schoolMentorGrade = supervisorEvaluation?.grade?.toFloat()
    val institutionGrade = institutionEvaluation?.grade?.toFloat()

    val evaluation = InternshipEvaluation(
        internshipId = resolvedInternshipId,
        internshipType = if (institutionEvaluation != null) InternshipType.COMPANY_SCHOOL else InternshipType.SCHOOL_ONLY,
        state = EvaluationState.PENDING,
        companyResponsibleGrade = null,
        companyResponsibleObservation = null,
        companyResponsibleName = "",
        companyMentorGrade = schoolMentorGrade,
        companyMentorObservation = supervisorEvaluation?.comment,
        companyMentorName = supervisorEvaluation?.evaluatorUserId ?: "",
        institutionGrade = institutionGrade,
        institutionObservation = institutionEvaluation?.comment,
        institutionName = institutionEvaluation?.let { "Instituição" }.orEmpty(),
        schoolMentorGrade = schoolMentorGrade,
        schoolMentorObservation = supervisorEvaluation?.comment,
        schoolMentorName = supervisorEvaluation?.evaluatorUserId ?: "Orientador",
    )

    return evaluation.copy(state = calculateEvaluationState(evaluation))
}

internal fun InternshipModel.toActiveInternship(
    activityLogs: List<ActivityLog>,
    offer: InternshipOfferModel? = null,
): ActiveInternship = ActiveInternship(
    id = id,
    title = offer?.title ?: title,
    startDate = startDate.toLocalDateOrDefault(LocalDate.now().minusMonths(2)),
    endDate = endDate.toLocalDateOrDefault(LocalDate.now().plusMonths(4)),
    activityLogs = activityLogs,
)

private fun String?.toLocalDateOrDefault(default: LocalDate): LocalDate = this
    ?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
    ?: default

private fun String?.splitLines(): List<String> = this
    ?.split('\n')
    ?.map { it.trim() }
    ?.filter { it.isNotBlank() }
    .orEmpty()

private fun String.firstInitial(): String = firstOrNull()?.uppercaseChar()?.toString() ?: "L"

private fun String.logoColor(): Color = Color(0xFF0E1572)

private fun String.orientadorRelativeTime(): String {
    val instant = runCatching { Instant.parse(this) }.getOrNull() ?: return "recentemente"
    val minutes = ChronoUnit.MINUTES.between(instant, Instant.now()).coerceAtLeast(0)
    return when {
        minutes < 1 -> "agora"
        minutes < 60 -> "${minutes}m atrás"
        minutes < 1440 -> "${minutes / 60}h atrás"
        minutes < 43200 -> "${minutes / 1440}d atrás"
        else -> "há muito tempo"
    }
}

private fun String.initials(): String = split(' ')
    .filter { it.isNotBlank() }
    .take(2)
    .joinToString("") { it.first().uppercase() }
    .ifBlank { "AL" }

private fun Int.absMod(modulus: Int): Int = if (modulus == 0) 0 else kotlin.math.abs(this) % modulus

