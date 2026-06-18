package turmaA.grupoB.LinkStage.ui.admin

import android.content.Context
import androidx.compose.ui.graphics.Color
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.theme.DarkBlue
import turmaA.grupoB.LinkStage.ui.theme.LightBlue
import turmaA.grupoB.LinkStage.ui.theme.MediumBlue

data class AdminStudent(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val institution: String,
    val institutionCode: String,
    val course: String,
    val gpa: Float,
    val registeredAgo: String,
    val hasActiveInternship: Boolean,
    val internshipCompany: String = "",
    val applicationCount: Int = 0,
    val avatarInitials: String,
    val avatarColorIndex: Int = 0,
    val skills: List<String> = emptyList(),
    val userId: String = "",
)

data class AdminMentor(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val institution: String,
    val department: String,
    val registeredAgo: String,
    val activeStudentsCount: Int,
    val avatarInitials: String,
    val avatarColorIndex: Int = 0,
    val skills: List<String> = emptyList(),
    val supervisionAreas: List<String> = emptyList(),
    val internalNote: String = "",
    val isAvailable: Boolean = true,
    val userId: String = "",
)

enum class InstitutionStatus {
    PENDING_APPROVAL,
    APPROVED,
    REJECTED,
}

data class AdminInstitution(
    val id: String,
    val name: String,
    val code: String,
    val logoInitial: String,
    val logoColor: Color,
    val type: String,
    val location: String,
    val registeredAgo: String,
    val studentsCount: Int,
    val mentorsCount: Int,
    val activeInternshipsCount: Int,
    val email: String = "",
    val website: String = "",
    val status: InstitutionStatus = InstitutionStatus.APPROVED,
    val submittedAt: String = "",
    val userId: String = "",
)

val avatarColors = listOf(LightBlue, DarkBlue, MediumBlue)

val sampleStudentsList = listOf(
    AdminStudent(
        "s1", "Tiago Rodrigues", "tiago@estg.ipvc.pt", "912000001",
        "ESTG-IPVC", "ESTG-IPVC", "Ciências Informáticas", 14.5f,
        "há 2 horas", true, "Viana S.T.Arts", 3, "TR", 0,
    ),
    AdminStudent(
        "s2", "João Pinto", "joao@ese.ipvc.pt", "912000002",
        "ESE-IPVC", "ESE-IPVC", "Educação Básica", 13.2f,
        "há 3 horas", false, "", 1, "JP", 1,
    ),
    AdminStudent(
        "s3", "Ana Costa", "ana@estg.ipvc.pt", "912000003",
        "ESTG-IPVC", "ESTG-IPVC", "Design", 15.0f,
        "há 1 dia", true, "Pingo Doce", 2, "AC", 2,
    ),
)

val sampleMentorsList = listOf(
    AdminMentor(
        "m1", "Prof. Carvalho", "carvalho@ipvc.pt", "910000001",
        "ESTG-IPVC", "Ciências Informáticas", "há 1 dia", 3, "PC", 0,
        skills = listOf("Android", "Retrofit", "Firebase", "SQL", "Kotlin", "Room"),
        supervisionAreas = listOf("Frontend", "Mobile Development", "Databases", "IA", "Cloud Services", "Backend"),
        internalNote = "Prefere estudantes com projetos práticos e boa comunicação.",
        isAvailable = true,
    ),
    AdminMentor(
        "m2", "Prof. Santos", "santos@ese.ipvc.pt", "910000002",
        "ESE-IPVC", "Educação", "há 2 dias", 1, "PS", 1,
        skills = listOf("Pedagogia", "Investigação", "Estatística", "SPSS"),
        supervisionAreas = listOf("Educação Básica", "Didática", "Investigação Educacional"),
        internalNote = "",
        isAvailable = false,
    ),
)

val sampleInstitutionsList = listOf(
    AdminInstitution(
        "i1", "Uni. de Aveiro", "UA", "U", Color(0xFF1565C0),
        "Instituição de Ensino", "Aveiro", "há 2 horas", 45, 8, 12,
        "geral@ua.pt", "ua.pt", InstitutionStatus.APPROVED,
    ),
    AdminInstitution(
        "i2", "Pingo Doce", "PD", "P", Color(0xFF388E3C),
        "Instituição Empresarial", "Lisboa", "há 3 horas", 12, 2, 5,
        "estagios@pingodoce.pt", "pingodoce.pt", InstitutionStatus.APPROVED,
    ),
    AdminInstitution(
        "i3", "Tech Lisboa", "TL", "T", Color(0xFF7B1FA2),
        "Instituição Empresarial", "Lisboa", "há 1 hora", 0, 0, 0,
        "geral@techlisboa.pt", "techlisboa.pt", InstitutionStatus.PENDING_APPROVAL, "há 1 hora",
    ),
    AdminInstitution(
        "i4", "IPCA", "IP", "I", Color(0xFF0288D1),
        "Instituição de Ensino", "Barcelos", "há 30 minutos", 0, 0, 0,
        "geral@ipca.pt", "ipca.pt", InstitutionStatus.PENDING_APPROVAL, "há 30 minutos",
    ),
)

fun sampleStudents(context: Context) = listOf(
    AdminStudent(
        "s1", "Tiago Rodrigues", "tiago@estg.ipvc.pt", "912000001",
        "ESTG-IPVC", "ESTG-IPVC", context.getString(R.string.mock_course_cs), 14.5f,
        context.getString(R.string.mock_time_2h_ago), true, "Viana S.T.Arts", 3, "TR", 0,
    ),
    AdminStudent(
        "s2", "João Pinto", "joao@ese.ipvc.pt", "912000002",
        "ESE-IPVC", "ESE-IPVC", context.getString(R.string.mock_course_basic_education), 13.2f,
        context.getString(R.string.mock_time_3h_ago), false, "", 1, "JP", 1,
    ),
    AdminStudent(
        "s3", "Ana Costa", "ana@estg.ipvc.pt", "912000003",
        "ESTG-IPVC", "ESTG-IPVC", "Design", 15.0f,
        context.getString(R.string.mock_time_1d_ago), true, "Pingo Doce", 2, "AC", 2,
    ),
)

fun sampleMentors(context: Context) = listOf(
    AdminMentor(
        "m1", "Prof. Carvalho", "carvalho@ipvc.pt", "910000001",
        "ESTG-IPVC", context.getString(R.string.mock_department_cs), context.getString(R.string.mock_time_1d_ago), 3, "PC", 0,
        skills = listOf("Android", "Retrofit", "Firebase", "SQL", "Kotlin", "Room"),
        supervisionAreas = listOf("Frontend", "Mobile Development", "Databases", context.getString(R.string.skill_ai), "Cloud Services", "Backend"),
        internalNote = context.getString(R.string.mock_mentor_note_carvalho),
        isAvailable = true,
    ),
    AdminMentor(
        "m2", "Prof. Santos", "santos@ese.ipvc.pt", "910000002",
        "ESE-IPVC", context.getString(R.string.mock_department_education), context.getString(R.string.mock_time_2d_ago), 1, "PS", 1,
        skills = listOf(context.getString(R.string.mock_skill_pedagogy), context.getString(R.string.mock_skill_research), context.getString(R.string.mock_skill_statistics), "SPSS"),
        supervisionAreas = listOf(context.getString(R.string.mock_area_basic_education), context.getString(R.string.mock_area_didactics), context.getString(R.string.mock_area_educational_research)),
        internalNote = "",
        isAvailable = false,
    ),
)

fun sampleInstitutions(context: Context) = listOf(
    AdminInstitution(
        "i1", "Uni. de Aveiro", "UA", "U", Color(0xFF1565C0),
        context.getString(R.string.admin_inst_filter_school), "Aveiro", context.getString(R.string.mock_time_2h_ago), 45, 8, 12,
        "geral@ua.pt", "ua.pt", InstitutionStatus.APPROVED,
    ),
    AdminInstitution(
        "i2", "Pingo Doce", "PD", "P", Color(0xFF388E3C),
        context.getString(R.string.admin_inst_filter_company), "Lisboa", context.getString(R.string.mock_time_3h_ago), 12, 2, 5,
        "estagios@pingodoce.pt", "pingodoce.pt", InstitutionStatus.APPROVED,
    ),
    AdminInstitution(
        "i3", "Tech Lisboa", "TL", "T", Color(0xFF7B1FA2),
        context.getString(R.string.admin_inst_filter_company), "Lisboa", context.getString(R.string.mock_time_1h_ago), 0, 0, 0,
        "geral@techlisboa.pt", "techlisboa.pt", InstitutionStatus.PENDING_APPROVAL, context.getString(R.string.mock_time_1h_ago),
    ),
    AdminInstitution(
        "i4", "IPCA", "IP", "I", Color(0xFF0288D1),
        context.getString(R.string.admin_inst_filter_school), "Barcelos", context.getString(R.string.mock_time_30m_ago), 0, 0, 0,
        "geral@ipca.pt", "ipca.pt", InstitutionStatus.PENDING_APPROVAL, context.getString(R.string.mock_time_30m_ago),
    ),
)
