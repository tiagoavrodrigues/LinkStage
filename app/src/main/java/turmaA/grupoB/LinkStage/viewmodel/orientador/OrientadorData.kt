package turmaA.grupoB.LinkStage.viewmodel.orientador

import turmaA.grupoB.LinkStage.data.remote.model.internship.InternshipModel
import turmaA.grupoB.LinkStage.ui.admin.AdminStudent
import turmaA.grupoB.LinkStage.ui.aluno.activity.ActiveInternship
import turmaA.grupoB.LinkStage.ui.aluno.activity.ActivityLog
import turmaA.grupoB.LinkStage.ui.orientador.InternshipEvaluation
import turmaA.grupoB.LinkStage.ui.orientador.MentorInternship

data class OrientadorDashboardData(
    val internships: List<MentorInternship>,
    val students: List<AdminStudent>,
    val activityLogs: List<ActivityLog>,
    val evaluation: InternshipEvaluation?,
)

data class OrientadorStudentDetailData(
    val student: AdminStudent,
    val internships: List<InternshipModel>,
    val activeInternship: ActiveInternship?,
    val activityLogs: List<ActivityLog>,
    val evaluation: InternshipEvaluation?,
)

data class OrientadorInternshipDetailData(
    val internship: MentorInternship,
    val student: AdminStudent,
    val activeInternship: ActiveInternship?,
    val activityLogs: List<ActivityLog>,
    val evaluation: InternshipEvaluation?,
)
