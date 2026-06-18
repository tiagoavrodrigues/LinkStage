package turmaA.grupoB.LinkStage.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import turmaA.grupoB.LinkStage.data.repository.profile.ProfileRepositoryInterface

class ProfileViewModelFactory(
    private val profileViewModelRepository: ProfileRepositoryInterface
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(profileViewModelRepository) as T
    }
}