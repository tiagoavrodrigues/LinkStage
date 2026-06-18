package turmaA.grupoB.LinkStage.viewmodel.apply

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import turmaA.grupoB.LinkStage.data.remote.model.user.ProfileModel
import turmaA.grupoB.LinkStage.data.remote.model.user.StudentModel

class ApplyViewModel : ViewModel() {

    var currentStep by mutableStateOf(0)

    // Step 0
    var fullName by mutableStateOf("Tomás Silva")
    var email by mutableStateOf("tomas.silva@ipvc.pt")
    var phone by mutableStateOf("939696769")
    var course by mutableStateOf("Eng. Informática")
    var institution by mutableStateOf("IPVC")
    var gpa by mutableStateOf("14,7")

    // Step 1
    var personalStatement by mutableStateOf("")
    // Pre-filled with skills simulated from registration
    var userSkills by mutableStateOf(listOf("Python", "Kotlin", "Trabalho em grupo", "Gestão de Projetos", "IA", "Resolução de problemas"))
    var motivationFile by mutableStateOf<Uri?>(null)
    var motivationFileName by mutableStateOf("")
    var motivationFileBytes by mutableStateOf<ByteArray?>(null)

    val isCvComplete: Boolean get() = userSkills.isNotEmpty()
    val isMotivationComplete: Boolean get() = motivationFile != null

    fun saveAsDraft() {
        // Placeholder — guardar em SharedPreferences ou Room
    }

    fun submitApplication() {
        // Placeholder — chamar repositório
    }

    fun addSkill(skill: String) {
        if (skill.isNotBlank() && skill !in userSkills) {
            userSkills = userSkills + skill
        }
    }

    fun removeSkill(skill: String) {
        userSkills = userSkills - skill
    }

    fun applyProfileData(profile: ProfileModel) {
        fullName = profile.name
        email = profile.email
        phone = profile.phone ?: phone
    }

    fun applyStudentData(student: StudentModel) {
        course = student.course
        gpa = student.averageGrade?.toString()?.replace(".", ",") ?: gpa
    }
}
