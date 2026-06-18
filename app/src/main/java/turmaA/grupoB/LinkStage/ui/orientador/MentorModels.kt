package turmaA.grupoB.LinkStage.ui.orientador

import android.content.Context
import androidx.compose.ui.graphics.Color
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.admin.AdminStudent
import turmaA.grupoB.LinkStage.ui.aluno.activity.ActiveInternship
import turmaA.grupoB.LinkStage.ui.aluno.activity.ActivityLog
import turmaA.grupoB.LinkStage.ui.aluno.activity.ActivityLogStatus
import turmaA.grupoB.LinkStage.ui.aluno.activity.CheckpointFile
import java.time.LocalDate

enum class CheckpointCreatedBy { STUDENT, MENTOR }

data class CheckpointViewer(
    val viewerId: String,
    val viewerName: String,
    val viewerRole: String,
    val viewedAt: String,
)

data class MentorInternship(
    val id: String,
    val offerTitle: String,
    val businessInstitutionName: String,
    val schoolInstitutionName: String,
    val logoInitial: String,
    val logoColor: Color,
    val endDate: LocalDate,
    val studentName: String,
    val studentId: String,
    val location: String = "",
    val duration: String = "",
    val type: String = "",
    val aboutCompany: String = "",
    val responsibilities: List<String> = emptyList(),
    val requirements: List<String> = emptyList(),
    val benefits: List<String> = emptyList(),
    val isBusinessInternship: Boolean = true,
)

enum class InternshipType { COMPANY_SCHOOL, SCHOOL_ONLY }

enum class EvaluationState {
    PENDING,
    PARTIAL,
    READY_FOR_FINAL,
    COMPLETED,
}

data class InternshipEvaluation(
    val internshipId: String,
    val internshipType: InternshipType = InternshipType.COMPANY_SCHOOL,
    val state: EvaluationState = EvaluationState.PENDING,

    val companyResponsibleGrade: Float? = null,
    val companyResponsibleObservation: String? = null,
    val companyResponsibleName: String = "",

    val companyMentorGrade: Float? = null,
    val companyMentorObservation: String? = null,
    val companyMentorName: String = "",

    val institutionGrade: Float? = null,
    val institutionObservation: String? = null,
    val institutionName: String = "",

    val schoolMentorGrade: Float? = null,
    val schoolMentorObservation: String? = null,
    val schoolMentorName: String = "",

    val hasSeenNotification: Boolean = false,
)

fun calculateEvaluationState(eval: InternshipEvaluation): EvaluationState {
    return when (eval.internshipType) {
        InternshipType.COMPANY_SCHOOL -> when {
            eval.schoolMentorGrade != null -> EvaluationState.COMPLETED
            eval.companyResponsibleGrade != null && eval.companyMentorGrade != null
                -> EvaluationState.READY_FOR_FINAL
            eval.companyResponsibleGrade != null || eval.companyMentorGrade != null
                -> EvaluationState.PARTIAL
            else -> EvaluationState.PENDING
        }
        InternshipType.SCHOOL_ONLY -> when {
            eval.schoolMentorGrade != null -> EvaluationState.COMPLETED
            eval.institutionGrade != null -> EvaluationState.READY_FOR_FINAL
            else -> EvaluationState.PENDING
        }
    }
}

// region Sample data

val sampleMentorInternshipsList = listOf(
    MentorInternship(
        id = "i1",
        offerTitle = "UI/UX Designer",
        businessInstitutionName = "Continente",
        schoolInstitutionName = "ESTG - IPVC",
        logoInitial = "C",
        logoColor = Color(0xFFE53935),
        endDate = LocalDate.of(2026, 5, 5),
        studentName = "Tiago Rodrigues",
        studentId = "s1",
        location = "Viana do Castelo, PT",
        duration = "6 meses",
        type = "Remoto",
        aboutCompany = "Líder nacional no retalho alimentar.",
        responsibilities = listOf(
            "Prototipar fluxos digitais e validar ideias com utilizadores.",
            "Colaborar com equipas de produto e marketing.",
            "Criar dashboards e materiais de comunicação.",
        ),
        requirements = listOf(
            "Experiência com Figma ou ferramentas de prototipagem.",
            "Portfólio com projetos de UI/UX.",
            "Inglês funcional.",
        ),
        benefits = listOf(
            "Apoio de transporte.",
            "Mentoria com equipa sénior.",
            "Remuneração competitiva.",
        ),
        isBusinessInternship = true
    ),
    MentorInternship(
        id = "i2",
        offerTitle = "Product Designer",
        businessInstitutionName = "Viana S.T.Arts",
        schoolInstitutionName = "Uni. de Aveiro",
        logoInitial = "V",
        logoColor = Color(0xFF212121),
        endDate = LocalDate.of(2026, 6, 27),
        studentName = "Francisco Fernandes",
        studentId = "s2",
        location = "Aveiro, PT",
        duration = "4 meses",
        type = "Presencial",
        aboutCompany = "Empresa criativa focada em experiências digitais.",
        responsibilities = listOf(
            "Desenhar experiências de e-learning.",
            "Aplicar design thinking em workshops.",
        ),
        requirements = listOf(
            "Abordagem centrada no utilizador.",
            "Ferramentas de prototipagem.",
        ),
        benefits = listOf(
            "Certificado de estágio.",
            "Acesso a laboratórios.",
        ),
        isBusinessInternship = false
    ),
)

val sampleMentorStudentsList = listOf(
    AdminStudent(
        "s1", "Tiago Rodrigues", "tiago@estg.ipvc.pt", "912000001",
        "ESTG-IPVC", "ESTG-IPVC", "Ciências Informáticas", 14.5f,
        "há 2 horas", true, "Continente", 2, "TR", 0,
        skills = listOf("Kotlin", "Jetpack Compose", "UI/UX Design", "Figma"),
    ),
    AdminStudent(
        "s2", "Francisco Fernandes", "francisco@ese.ipvc.pt", "912000002",
        "ESE-IPVC", "ESE-IPVC", "Educação Básica", 13.0f,
        "há 3 horas", false, "", 1, "FF", 1,
        skills = listOf("Pedagogia", "Gestão de sala de aula", "Comunicação"),
    ),
)

val sampleMentorActivityLogsList = listOf(
    ActivityLog(
        "1", "Mockups e validação inicial", "Primeira entrega de mockups para validação com a equipa.",
        LocalDate.of(2026, 1, 31), ActivityLogStatus.COMPLETED,
        "Viana S.T.Arts", "V", Color(0xFF212121),
        requirements = listOf(
            "PPT com mockups principais.",
            "Relatório atualizado.",
            "Documentos adicionais.",
        ),
        hasSubmitted = true,
        submittedAt = LocalDate.of(2026, 1, 31),
        submittedFiles = listOf(
            CheckpointFile("f1", "mockups.pptx"),
            CheckpointFile("f2", "relatorio_atualizado.pdf"),
        ),
        createdBy = "STUDENT",
        viewers = listOf(
            CheckpointViewer("m1", "Prof. Carvalho", "Orientador", "Hoje às 14:32"),
            CheckpointViewer("i1", "Viana S.T.Arts", "Instituição", "Ontem às 09:15"),
        ),
    ),
    ActivityLog(
        "2", "Relatório intercalar", "Preparação do relatório intercalar com evidências do trabalho realizado.",
        LocalDate.of(2026, 3, 15), ActivityLogStatus.PENDING,
        "Viana S.T.Arts", "V", Color(0xFF212121),
        requirements = listOf(
            "PPT com mockups principais.",
            "Relatório atualizado.",
            "Documentos adicionais.",
        ),
        hasSubmitted = false,
        submittedFiles = emptyList(),
        createdBy = "MENTOR",
        createdByName = "Prof. Carvalho",
        viewers = emptyList(),
    ),
)

val sampleStudentInternshipInstance = ActiveInternship(
    id = "int1",
    title = "Product Designer",
    startDate = LocalDate.of(2025, 10, 1),
    endDate = LocalDate.of(2026, 6, 1),
    activityLogs = sampleMentorActivityLogsList,
)

val sampleEvaluationInstance = InternshipEvaluation(
    internshipId = "int1",
    internshipType = InternshipType.COMPANY_SCHOOL,
    state = EvaluationState.READY_FOR_FINAL,
    companyResponsibleGrade = 16.5f,
    companyResponsibleObservation = "Excelente colaboração e entrega.",
    companyResponsibleName = "Ana Costa",
    companyMentorGrade = 15.0f,
    companyMentorObservation = "Bom trabalho em equipa.",
    companyMentorName = "Prof. Tiago Alexandre",
    schoolMentorName = "Prof. Carvalho",
    hasSeenNotification = false,
)

fun sampleMentorInternships(context: Context) = sampleMentorInternshipsList

fun sampleMentorStudents(context: Context) = sampleMentorStudentsList

fun sampleMentorActivityLogs(context: Context) = sampleMentorActivityLogsList

fun sampleStudentInternship(context: Context) = sampleStudentInternshipInstance

fun sampleEvaluation(context: Context) = sampleEvaluationInstance

// endregion

// region Formatting

fun formatInternshipDate(date: LocalDate, context: Context): String {
    val months = context.resources.getStringArray(R.array.months_full)
    return context.getString(R.string.date_format_full, date.dayOfMonth, months[date.monthValue - 1], date.year)
}

fun formatCheckpointDate(date: LocalDate, context: Context): String {
    val months = context.resources.getStringArray(R.array.months_short)
    return "${months[date.monthValue - 1]} ${String.format("%02d", date.dayOfMonth)}, ${date.year}"
}

fun formatCheckpointDateLong(date: LocalDate, context: Context): String {
    val months = context.resources.getStringArray(R.array.months_short)
    return "${date.dayOfMonth} ${months[date.monthValue - 1]} ${date.year}"
}

// endregion
