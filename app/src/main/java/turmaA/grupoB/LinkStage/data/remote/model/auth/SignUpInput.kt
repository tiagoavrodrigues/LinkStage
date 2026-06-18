package turmaA.grupoB.LinkStage.data.remote.model.auth

import turmaA.grupoB.LinkStage.data.remote.model.enums.UserRole

data class SignUpInput(
    val name: String,
    val email: String,
    val password: String,
    val phone: String? = null,
    val role: UserRole,
    val rgpdConsent: Boolean
)
