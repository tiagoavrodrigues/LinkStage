package turmaA.grupoB.LinkStage.viewmodel.admin

import turmaA.grupoB.LinkStage.ui.admin.AdminInstitution
import turmaA.grupoB.LinkStage.ui.admin.AdminMentor
import turmaA.grupoB.LinkStage.ui.admin.AdminStudent

sealed class AdminDashboardUiState {
    data object Idle : AdminDashboardUiState()
    data object Loading : AdminDashboardUiState()
    data class Success(val data: AdminDashboardData) : AdminDashboardUiState()
    data object Empty : AdminDashboardUiState()
    data class Error(val message: String) : AdminDashboardUiState()
}

sealed class AdminUsersUiState {
    data object Idle : AdminUsersUiState()
    data object Loading : AdminUsersUiState()
    data class Success(
        val students: List<AdminStudent>,
        val mentors: List<AdminMentor>,
    ) : AdminUsersUiState()
    data class StudentsSuccess(val students: List<AdminStudent>) : AdminUsersUiState()
    data class MentorsSuccess(val mentors: List<AdminMentor>) : AdminUsersUiState()
    data object Empty : AdminUsersUiState()
    data class Error(val message: String) : AdminUsersUiState()
}

sealed class AdminStudentDetailUiState {
    data object Idle : AdminStudentDetailUiState()
    data object Loading : AdminStudentDetailUiState()
    data class Success(val data: AdminStudentDetailData) : AdminStudentDetailUiState()
    data object Empty : AdminStudentDetailUiState()
    data class Error(val message: String) : AdminStudentDetailUiState()
}

sealed class AdminMentorDetailUiState {
    data object Idle : AdminMentorDetailUiState()
    data object Loading : AdminMentorDetailUiState()
    data class Success(val data: AdminMentorDetailData) : AdminMentorDetailUiState()
    data object Empty : AdminMentorDetailUiState()
    data class Error(val message: String) : AdminMentorDetailUiState()
}

sealed class AdminInstitutionsUiState {
    data object Idle : AdminInstitutionsUiState()
    data object Loading : AdminInstitutionsUiState()
    data class Success(
        val approvedInstitutions: List<AdminInstitution>,
        val pendingInstitutions: List<AdminInstitution>,
    ) : AdminInstitutionsUiState()
    data object Empty : AdminInstitutionsUiState()
    data class Error(val message: String) : AdminInstitutionsUiState()
}

sealed class AdminInstitutionDetailUiState {
    data object Idle : AdminInstitutionDetailUiState()
    data object Loading : AdminInstitutionDetailUiState()
    data class Success(val data: AdminInstitutionDetailData) : AdminInstitutionDetailUiState()
    data object Empty : AdminInstitutionDetailUiState()
    data class Error(val message: String) : AdminInstitutionDetailUiState()
}

sealed class AdminInternshipDetailUiState {
    data object Idle : AdminInternshipDetailUiState()
    data object Loading : AdminInternshipDetailUiState()
    data class Success(val data: AdminInternshipDetailData) : AdminInternshipDetailUiState()
    data object Empty : AdminInternshipDetailUiState()
    data class Error(val message: String) : AdminInternshipDetailUiState()
}

sealed class AdminAccountUiState {
    data object Idle : AdminAccountUiState()
    data object Loading : AdminAccountUiState()
    data class Success(val temporaryPassword: String) : AdminAccountUiState()
    data class Error(val message: String) : AdminAccountUiState()
}
