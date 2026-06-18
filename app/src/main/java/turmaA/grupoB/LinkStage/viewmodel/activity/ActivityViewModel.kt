package turmaA.grupoB.LinkStage.viewmodel.activity

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import turmaA.grupoB.LinkStage.ui.aluno.activity.ActiveInternship
import turmaA.grupoB.LinkStage.ui.aluno.activity.ApplicationItem
import turmaA.grupoB.LinkStage.ui.aluno.home.ApplicationStatus

class ActivityViewModel : ViewModel() {

    private val _hasActiveInternship = MutableStateFlow(false)
    val hasActiveInternship: StateFlow<Boolean> = _hasActiveInternship.asStateFlow()

    private val _activeInternship = MutableStateFlow<ActiveInternship?>(null)
    val activeInternship: StateFlow<ActiveInternship?> = _activeInternship.asStateFlow()

    private val _activeApplications = MutableStateFlow(sampleActiveApplications)
    val activeApplications: StateFlow<List<ApplicationItem>> = _activeApplications.asStateFlow()

    private val _pastApplications = MutableStateFlow(samplePastApplications)
    val pastApplications: StateFlow<List<ApplicationItem>> = _pastApplications.asStateFlow()

    fun setActiveInternship(internship: ActiveInternship?) {
        _activeInternship.value = internship
        _hasActiveInternship.value = internship != null
    }
}

private val sampleActiveApplications = listOf(
    ApplicationItem("1", "Call Center Indiano", "Rasheed", "3d atrás", ApplicationStatus.PENDING),
    ApplicationItem("2", "Intermaché dos Arcos", "Intermarché", "2w atrás", ApplicationStatus.PENDING),
)

private val samplePastApplications = listOf(
    ApplicationItem("3", "Call Center Indiano", "Rasheed", "3d atrás", ApplicationStatus.REJECTED),
)