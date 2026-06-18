package turmaA.grupoB.LinkStage.viewmodel.internships

import turmaA.grupoB.LinkStage.data.remote.model.internship.ActivityLogModel
import turmaA.grupoB.LinkStage.data.remote.model.internship.InternshipModel

sealed class InternshipUiState {
    data object Idle : InternshipUiState()
    data object Loading : InternshipUiState()
    data class Success(val internship: InternshipModel) : InternshipUiState()
    data class SuccessList(val internships: List<InternshipModel>) : InternshipUiState()
    data class ActiveInternshipSuccess(
        val internship: InternshipModel,
        val activityLogs: List<ActivityLogModel>,
    ) : InternshipUiState()
    data class SuccessActivity(val activityLogModel: ActivityLogModel) : InternshipUiState()
    data class SuccessActivityList(val activityLogModel: List<ActivityLogModel>) : InternshipUiState()
    data object Empty : InternshipUiState()
    data class Error(val message: String) : InternshipUiState()
}

sealed class ActivityCreationUiState {
    data object Idle : ActivityCreationUiState()
    data object Loading : ActivityCreationUiState()
    data class Success(val activityLog: ActivityLogModel) : ActivityCreationUiState()
    data class Error(val message: String) : ActivityCreationUiState()
}
