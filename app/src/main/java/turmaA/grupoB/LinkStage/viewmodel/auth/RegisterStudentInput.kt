package turmaA.grupoB.LinkStage.viewmodel.auth

data class RegisterStudentInput(
    val name: String,
    val email: String,
    val password: String,
    val phone: String? = null,
    val studentNumber: String,
    val course: String,
    val academicYear: String? = null,
    val rgpdConsent: Boolean
)