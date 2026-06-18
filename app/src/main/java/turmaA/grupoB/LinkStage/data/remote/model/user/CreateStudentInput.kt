package turmaA.grupoB.LinkStage.data.remote.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class CreateStudentInput(
    @SerialName("user_id")
    val userId: String,
    @SerialName("student_number")
    val studentNumber: String,
    val course: String,
    @SerialName("academic_year")
    val academicYear: String?= null,
    @SerialName("average_grade")
    val averageGrade: Double? = null,
    @SerialName("cv_data")
    val cvData: JsonObject? = null
)