package turmaA.grupoB.LinkStage.viewmodel.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import turmaA.grupoB.LinkStage.R
import turmaA.grupoB.LinkStage.ui.aluno.activity.ActiveInternship
import turmaA.grupoB.LinkStage.ui.aluno.activity.ActivityLog
import turmaA.grupoB.LinkStage.ui.aluno.activity.ActivityLogStatus
import turmaA.grupoB.LinkStage.ui.aluno.activity.ApplicationItem
import turmaA.grupoB.LinkStage.ui.aluno.chat.Conversation
import turmaA.grupoB.LinkStage.ui.aluno.chat.sampleConversations
import turmaA.grupoB.LinkStage.ui.aluno.home.ApplicationStatus
import java.time.LocalDate

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _hasActiveInternship = MutableStateFlow(false)
    val hasActiveInternship: StateFlow<Boolean> = _hasActiveInternship.asStateFlow()

    private val _activeInternship = MutableStateFlow<ActiveInternship?>(null)
    val activeInternship: StateFlow<ActiveInternship?> = _activeInternship.asStateFlow()

    private val _recentApplications = MutableStateFlow(
        listOf(
            ApplicationItem("1", "Call Center Indiano", "Rasheed", application.getString(R.string.mock_time_3d_ago), ApplicationStatus.PENDING),
            ApplicationItem("2", "Intermaché dos Arcos", "Intermarché", application.getString(R.string.mock_time_2w_ago), ApplicationStatus.ACCEPTED),
            ApplicationItem("3", "UI/UX Designer", "Viana S.T.Arts", application.getString(R.string.mock_time_1w_ago), ApplicationStatus.REJECTED),
        )
    )
    val recentApplications: StateFlow<List<ApplicationItem>> = _recentApplications.asStateFlow()

    private val _activeApplications = MutableStateFlow(
        listOf(
            ApplicationItem("1", "Call Center Indiano", "Rasheed", application.getString(R.string.mock_time_3d_ago), ApplicationStatus.PENDING),
            ApplicationItem("2", "Intermaché dos Arcos", "Intermarché", application.getString(R.string.mock_time_2w_ago), ApplicationStatus.PENDING),
        )
    )
    val activeApplications: StateFlow<List<ApplicationItem>> = _activeApplications.asStateFlow()

    private val _pastApplications = MutableStateFlow(
        listOf(
            ApplicationItem("3", "Call Center Indiano", "Rasheed", application.getString(R.string.mock_time_3d_ago), ApplicationStatus.REJECTED),
        )
    )
    val pastApplications: StateFlow<List<ApplicationItem>> = _pastApplications.asStateFlow()

    private val _recentConversations = MutableStateFlow(sampleConversations.take(3))
    val recentConversations: StateFlow<List<Conversation>> = _recentConversations.asStateFlow()

    private val _hasSeenEvaluationResult = MutableStateFlow(false)
    val hasSeenEvaluationResult: StateFlow<Boolean> = _hasSeenEvaluationResult.asStateFlow()

    private val _hasDismissedEvaluationModal = MutableStateFlow(false)
    val hasDismissedEvaluationModal: StateFlow<Boolean> = _hasDismissedEvaluationModal.asStateFlow()

    private val mockActiveInternship = ActiveInternship(
        id = "mock_int_1",
        title = application.getString(R.string.mock_web_dev_internship),
        startDate = LocalDate.now().minusMonths(2),
        endDate = LocalDate.now().plusMonths(4),
        activityLogs = listOf(
            ActivityLog(
                id = "1",
                title = application.getString(R.string.mock_checkpoint_1),
                description = application.getString(R.string.mock_checkpoint_desc_mockups),
                date = LocalDate.of(2026, 1, 31),
                status = ActivityLogStatus.COMPLETED,
                company = "Viana S.T.Arts",
                companyLogoInitial = "V",
                companyLogoColor = androidx.compose.ui.graphics.Color(0xFF212121),
                requirements = listOf(
                    application.getString(R.string.mock_req_ppt),
                    application.getString(R.string.mock_req_report_updated),
                    application.getString(R.string.mock_req_additional_docs),
                ),
                hasSubmitted = true,
            ),
            ActivityLog(
                id = "2",
                title = application.getString(R.string.mock_checkpoint_2),
                description = application.getString(R.string.mock_checkpoint_desc_mockups),
                date = LocalDate.of(2026, 5, 5),
                status = ActivityLogStatus.PENDING,
                company = "Viana S.T.Arts",
                companyLogoInitial = "V",
                companyLogoColor = androidx.compose.ui.graphics.Color(0xFF212121),
                requirements = listOf(
                    application.getString(R.string.mock_req_ppt),
                    application.getString(R.string.mock_req_report_updated),
                    application.getString(R.string.mock_req_additional_docs),
                ),
                hasSubmitted = false,
            ),
        ),
    )

    fun setHasSeenEvaluationResult(seen: Boolean) {
        _hasSeenEvaluationResult.value = seen
        if (seen) _hasDismissedEvaluationModal.value = true
    }

    fun setHasDismissedEvaluationModal(dismissed: Boolean) {
        _hasDismissedEvaluationModal.value = dismissed
    }

    fun setActiveInternship(internship: ActiveInternship?) {
        _activeInternship.value = internship
        _hasActiveInternship.value = internship != null
    }

    fun toggleInternship() {
        if (_hasActiveInternship.value) {
            setActiveInternship(null)
        } else {
            setActiveInternship(mockActiveInternship)
        }
    }

    fun addActivityLog(title: String, description: String) {
        val currentInternship = _activeInternship.value ?: return
        val newLog = ActivityLog(
            id = (currentInternship.activityLogs.size + 1).toString(),
            title = title,
            description = description,
            date = LocalDate.now(),
            status = ActivityLogStatus.PENDING,
            company = currentInternship.activityLogs.firstOrNull()?.company ?: "",
            companyLogoInitial = currentInternship.activityLogs.firstOrNull()?.companyLogoInitial ?: "",
            companyLogoColor = currentInternship.activityLogs.firstOrNull()?.companyLogoColor ?: androidx.compose.ui.graphics.Color(0xFF0E1572)
        )
        val updatedLogs = currentInternship.activityLogs + newLog
        _activeInternship.value = currentInternship.copy(activityLogs = updatedLogs)
    }
}
