package turmaA.grupoB.LinkStage.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import turmaA.grupoB.LinkStage.data.remote.model.auth.SignInInput
import turmaA.grupoB.LinkStage.data.remote.model.auth.SignUpInput
import turmaA.grupoB.LinkStage.data.remote.model.enums.UserRole
import turmaA.grupoB.LinkStage.data.repository.auth.AuthRepository

@RunWith(AndroidJUnit4::class)
class AuthRepositoryIntegrationTest {

    private val authRepository = AuthRepository()

    @Test
    fun signUp_withoutRgpdConsent_throwsIllegalArgumentException() {
        val input = SignUpInput(
            name = "Test User",
            email = "test.user@example.com",
            password = "password123",
            phone = null,
            role = UserRole.STUDENT,
            rgpdConsent = false
        )

        assertThrows(
            "O registo sem consentimento RGPD deverial lançar IllegalArgumentException.",
            IllegalArgumentException::class.java
        ) {
            runBlocking {
                authRepository.signUp(input)
            }
        }
    }

    @Test
    fun signIn_withInvalidCredentials_throwsException(){
        val input = SignInInput(
            email = "invalid.user@example.com",
            password = "wrong-password"
        )

        assertThrows(
            "O login com credenciais inválidas deveria lançar uma exceção.",
            Exception::class.java
        ) {
            runBlocking {
                authRepository.signIn(input)
            }
        }
    }

    @Test
    fun signOut_doesNotCrash() = runBlocking {
        authRepository.signOut()

        assertTrue(
            "O logout deve executar sem crash.",
            true
        )
    }

    @Test
    fun currentUserId_canBeCheckedWithoutCrash() {
        val currentUserId = authRepository.getCurrentUserId()

        assertTrue(
            "O utilizador atual pode ser null ou um id válido",
            currentUserId == null || currentUserId.isNotBlank()
        )
    }

    @Test
    fun signUp_withValidData_createsUserAndProfile() = runBlocking {
        val timestamp = System.currentTimeMillis()

        val input = SignUpInput(
          name = "Test User $timestamp",
          email = "test+$timestamp@linkstage.test",
          password = "Password123!",
          phone = "900000000",
          role = UserRole.STUDENT,
          rgpdConsent = true
        )

        val profile = authRepository.signUp(input)

        assertNotNull(
            "O profile criado não deveria ser null.",
            profile
        )

        assertEquals(
            "O nome do profile criado deveria corresponder ao nome usado no registo.",
            input.name,
            profile.name
        )


        assertEquals(
            "O email do profile criado deveria corresponder ao email usado no registo.",
            input.email,
            profile.email
        )

        assertEquals(
            "O nome do profile criado deveria corresponder ao nome usado no registo.",
            input.name,
            profile.name
        )

        assertEquals(
            "O role do profile criado deveria ser STUDENT.",
            UserRole.STUDENT,
            profile.role
        )

        assertTrue(
            "O consentimento RGPD deveria ficar registado.",
            profile.rgpdConsent
        )

        authRepository.signOut()
    }

}