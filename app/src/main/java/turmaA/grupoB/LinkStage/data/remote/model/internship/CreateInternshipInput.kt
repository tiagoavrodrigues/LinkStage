package turmaA.grupoB.LinkStage.data.remote.model.internship

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import turmaA.grupoB.LinkStage.data.remote.model.enums.InternshipStatus

@Serializable
data class CreateInternshipInput(
    @SerialName("application_id")
    val applicationId: String,
    @SerialName("offer_id")
    val offerId: String,
    @SerialName("student_id")
    val studentId: String,
    @SerialName("institution_id")
    val institutionId: String,
    @SerialName("supervisor_id")
    val supervisorId: String? = null,
    val title: String,
    @SerialName("company_supervisor_name")
    val companySupervisorName: String? = null,
    @SerialName("start_date")
    val startDate: String? = null,
    @SerialName("end_date")
    val endDate: String? = null,
    val status: InternshipStatus = InternshipStatus.PENDING_SUPERVISOR,
    @SerialName("work_plan")
    val workPlan: String? = null,
    val objectives: String? = null





)
