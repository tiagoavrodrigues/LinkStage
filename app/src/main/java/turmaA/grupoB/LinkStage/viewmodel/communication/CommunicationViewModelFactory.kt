package turmaA.grupoB.LinkStage.viewmodel.communication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import turmaA.grupoB.LinkStage.data.repository.communication.CommunicationRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepositoryInterface

class CommunicationViewModelFactory(
    private val communicationRepository: CommunicationRepositoryInterface,
    private val profileRepository: ProfileRepositoryInterface? = null,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CommunicationViewModel(communicationRepository, profileRepository) as T
    }
}
