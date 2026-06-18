package turmaA.grupoB.LinkStage.data.repository.auth

import turmaA.grupoB.LinkStage.data.remote.model.auth.SignInInput
import turmaA.grupoB.LinkStage.data.remote.model.auth.SignUpInput
import turmaA.grupoB.LinkStage.data.remote.model.user.ProfileModel
import turmaA.grupoB.LinkStage.data.remote.model.user.UpdateProfileInput

interface AuthRepositoryInterface {
    suspend fun signUp(input: SignUpInput): ProfileModel
    suspend fun signIn(input: SignInInput)
    suspend fun signOut()
    fun getCurrentUserId(): String?
    fun isUserLoggedIn(): Boolean
    suspend fun getCurrentUserProfile(): ProfileModel?

    suspend fun updateProfile(
        userId: String,
        input: UpdateProfileInput
    ): ProfileModel

    suspend fun updatePassword(newPassword: String)

    suspend fun createManagedAccount(input: SignUpInput): ProfileModel

    suspend fun setProfileActive(userId: String, active: Boolean)
}