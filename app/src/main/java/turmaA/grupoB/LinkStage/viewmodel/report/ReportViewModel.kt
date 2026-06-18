package turmaA.grupoB.LinkStage.viewmodel.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import turmaA.grupoB.LinkStage.data.remote.model.enums.ReportStatus
import turmaA.grupoB.LinkStage.data.remote.model.report.CreateFinalReportInput
import turmaA.grupoB.LinkStage.data.remote.model.report.UpdateFinalReportInput
import turmaA.grupoB.LinkStage.data.repository.report.ReportRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.storage.StorageRepositoryInterface
import java.time.Instant

class ReportViewModel(
    private val reportRepository: ReportRepositoryInterface,
    private val storageRepository: StorageRepositoryInterface? = null,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReportUiState>(ReportUiState.Idle)
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    fun loadReports() {
        viewModelScope.launch {
            _uiState.value = ReportUiState.Loading

            try {
                val reports = reportRepository.getReports()

                _uiState.value = if (reports.isEmpty()) {
                    ReportUiState.Empty
                } else {
                    ReportUiState.SuccessList(reports)
                }
            } catch (e: Exception) {
                _uiState.value = ReportUiState.Error(
                    e.message ?: "Erro ao carregar relatórios."
                )
            }
        }
    }

    fun loadReportById(reportId: String) {
        viewModelScope.launch {
            _uiState.value = ReportUiState.Loading

            try {
                val report = reportRepository.getReportById(reportId)

                _uiState.value = if (report != null) {
                    ReportUiState.Success(report)
                } else {
                    ReportUiState.Empty
                }
            } catch (e: Exception) {
                _uiState.value = ReportUiState.Error(
                    e.message ?: "Erro ao carregar relatório."
                )
            }
        }
    }

    fun loadReportByInternship(internshipId: String) {
        viewModelScope.launch {
            _uiState.value = ReportUiState.Loading

            try {
                val report = reportRepository.getReportByInternship(internshipId)

                _uiState.value = if (report != null) {
                    ReportUiState.Success(report)
                } else {
                    ReportUiState.Empty
                }
            } catch (e: Exception) {
                _uiState.value = ReportUiState.Error(
                    e.message ?: "Erro ao carregar relatório por estágio."
                )
            }
        }
    }

    fun loadReportsByStudent(studentId: String) {
        viewModelScope.launch {
            _uiState.value = ReportUiState.Loading

            try {
                val reports = reportRepository.getReportsByStudent(studentId)

                _uiState.value = if (reports.isEmpty()) {
                    ReportUiState.Empty
                } else {
                    ReportUiState.SuccessList(reports)
                }
            } catch (e: Exception) {
                _uiState.value = ReportUiState.Error(
                    e.message ?: "Erro ao carregar relatórios por estudante."
                )
            }
        }
    }

    fun loadReportsByStatus(status: ReportStatus) {
        viewModelScope.launch {
            _uiState.value = ReportUiState.Loading

            try {
                val reports = reportRepository.getReportsByStatus(status)

                _uiState.value = if (reports.isEmpty()) {
                    ReportUiState.Empty
                } else {
                    ReportUiState.SuccessList(reports)
                }
            } catch (e: Exception) {
                _uiState.value = ReportUiState.Error(
                    e.message ?: "Erro ao carregar relatórios por estado."
                )
            }
        }
    }

    fun createReport(input: CreateFinalReportInput) {
        viewModelScope.launch {
            _uiState.value = ReportUiState.Loading

            try {
                val report = reportRepository.createReport(input)
                _uiState.value = ReportUiState.Success(report)
            } catch (e: Exception) {
                _uiState.value = ReportUiState.Error(
                    e.message ?: "Erro ao criar relatório."
                )
            }
        }
    }

    fun updateReport(reportId: String, input: UpdateFinalReportInput) {
        viewModelScope.launch {
            _uiState.value = ReportUiState.Loading

            try {
                val report = reportRepository.updateReport(reportId, input)
                _uiState.value = ReportUiState.Success(report)
            } catch (e: Exception) {
                _uiState.value = ReportUiState.Error(
                    e.message ?: "Erro ao atualizar relatório."
                )
            }
        }
    }

    fun submitReport(reportId: String) {
        viewModelScope.launch {
            _uiState.value = ReportUiState.Loading

            try {
                val report = reportRepository.submitReport(reportId)
                _uiState.value = ReportUiState.Success(report)
            } catch (e: Exception) {
                _uiState.value = ReportUiState.Error(
                    e.message ?: "Erro ao submeter relatório."
                )
            }
        }
    }

    fun uploadAndSubmitReport(
        reportId: String,
        filePath: String,
        fileBytes: ByteArray,
    ) {
        viewModelScope.launch {
            _uiState.value = ReportUiState.Loading

            try {
                val uploadedPath = requireNotNull(storageRepository).uploadFile(
                    bucket = "final-reports",
                    path = filePath,
                    bytes = fileBytes,
                )
                val report = reportRepository.updateReport(
                    reportId = reportId,
                    input = UpdateFinalReportInput(
                        fileUrl = uploadedPath,
                        status = ReportStatus.SUBMITTED,
                        updatedAt = Instant.now().toString(),
                    )
                )
                _uiState.value = ReportUiState.Success(report)
            } catch (e: Exception) {
                _uiState.value = ReportUiState.Error(
                    e.message ?: "Erro ao submeter relatÃ³rio."
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = ReportUiState.Idle
    }
}
