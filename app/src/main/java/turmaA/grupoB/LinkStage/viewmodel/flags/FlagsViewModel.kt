package turmaA.grupoB.LinkStage.viewmodel.flags

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import turmaA.grupoB.LinkStage.data.repository.flags.FlagsRepositoryInterface

class FlagsViewModel (
    private val flagsRepository: FlagsRepositoryInterface
): ViewModel(){
    private val _uiState = MutableStateFlow<FlagsUIState>(FlagsUIState.Idle)
    val uiState: StateFlow<FlagsUIState> = _uiState.asStateFlow()

    fun getImages(names: List<String>){
        viewModelScope.launch {
            _uiState.value = FlagsUIState.Loading
            try {
                val results = names.map { name ->
                    flagsRepository.getFlag(name)
                }
                _uiState.value = if (results.isEmpty()) {
                    FlagsUIState.Empty
                } else {
                    FlagsUIState.Success(results)
                }
            }catch (e: Exception){
                _uiState.value = FlagsUIState.Error(
                    e.message ?: "Erro ao carregar imagens"
                )
            }
        }
    }
}