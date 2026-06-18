package turmaA.grupoB.LinkStage.viewmodel.advisorhome

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdvisorHomeViewModel : ViewModel() {
    private val _hasDismissedEvaluationModal = MutableStateFlow(false)
    val hasDismissedEvaluationModal: StateFlow<Boolean> = _hasDismissedEvaluationModal.asStateFlow()

    private val _hasSeenEvaluations = MutableStateFlow(false)
    val hasSeenEvaluations: StateFlow<Boolean> = _hasSeenEvaluations.asStateFlow()

    fun setHasDismissedEvaluationModal(dismissed: Boolean) {
        _hasDismissedEvaluationModal.value = dismissed
    }

    fun setHasSeenEvaluations(seen: Boolean) {
        _hasSeenEvaluations.value = seen
        if (seen) {
            _hasDismissedEvaluationModal.value = true
        }
    }
}
