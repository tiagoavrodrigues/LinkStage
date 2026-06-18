package turmaA.grupoB.LinkStage.viewmodel.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import turmaA.grupoB.LinkStage.data.repository.report.ReportRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.storage.StorageRepositoryInterface

class ReportViewModelFactory(
    private val reportRepository: ReportRepositoryInterface,
    private val storageRepository: StorageRepositoryInterface? = null,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ReportViewModel(reportRepository, storageRepository) as T
    }

}
