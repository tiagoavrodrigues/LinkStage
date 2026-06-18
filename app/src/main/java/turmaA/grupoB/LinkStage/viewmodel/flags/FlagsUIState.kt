package turmaA.grupoB.LinkStage.viewmodel.flags

import turmaA.grupoB.LinkStage.data.remote.model.Imgs

sealed class FlagsUIState {
    data object Idle: FlagsUIState()
    data object Loading: FlagsUIState()
    data class Success(val imgs: List<Imgs?>) : FlagsUIState()
    data object Empty: FlagsUIState()
    data class Error( val message: String): FlagsUIState()
}