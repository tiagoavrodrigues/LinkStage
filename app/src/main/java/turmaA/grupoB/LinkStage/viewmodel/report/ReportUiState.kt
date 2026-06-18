package turmaA.grupoB.LinkStage.viewmodel.report

import turmaA.grupoB.LinkStage.data.remote.model.report.FinalReportModel

sealed class ReportUiState {
    data object Idle: ReportUiState()
    data object Loading: ReportUiState()
    data class Success(val report: FinalReportModel): ReportUiState()
    data class SuccessList(val reports: List<FinalReportModel>): ReportUiState()
    data object Empty: ReportUiState()
    data class Error(val message: String): ReportUiState()
}