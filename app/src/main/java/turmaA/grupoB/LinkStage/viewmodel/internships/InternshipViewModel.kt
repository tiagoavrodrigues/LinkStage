package turmaA.grupoB.LinkStage.viewmodel.internships

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import turmaA.grupoB.LinkStage.data.remote.model.enums.InternshipStatus
import turmaA.grupoB.LinkStage.data.remote.model.internship.AssignSupervisorInput
import turmaA.grupoB.LinkStage.data.remote.model.internship.CreateActivityLogInput
import turmaA.grupoB.LinkStage.data.remote.model.internship.CreateInternshipInput
import turmaA.grupoB.LinkStage.data.repository.internship.InternshipRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.internship.LocalActivityRepositoryInterface
import turmaA.grupoB.LinkStage.data.repository.storage.StorageRepositoryInterface
import turmaA.grupoB.LinkStage.data.remote.model.internship.ActivityLogModel
import java.time.Instant
import java.util.UUID

class InternshipViewModel(
    private val internshipRepository: InternshipRepositoryInterface,
    private val localActivityRepository: LocalActivityRepositoryInterface? = null,
    private val storageRepository: StorageRepositoryInterface? = null,
) : ViewModel (){
    private val _uiState = MutableStateFlow<InternshipUiState>(InternshipUiState.Idle)
    val uiState: StateFlow<InternshipUiState> = _uiState.asStateFlow()

    private val _activityCreationUiState = MutableStateFlow<ActivityCreationUiState>(
        ActivityCreationUiState.Idle
    )
    val activityCreationUiState: StateFlow<ActivityCreationUiState> =
        _activityCreationUiState.asStateFlow()
    fun getInternships(){
        viewModelScope.launch {
            _uiState.value = InternshipUiState.Loading
            try {
                val internships = internshipRepository.getInternships()
                _uiState.value = if (internships.isEmpty()){
                    InternshipUiState.Empty
                }else{
                    InternshipUiState.SuccessList(internships)
                }
            }catch (e: Exception){
                _uiState.value = InternshipUiState.Error(
                    e.message ?: "Erro ao carregar estágios"
                )
            }
        }
    }
    fun getInternshipByStudent(studentId: String){
        viewModelScope.launch {
            _uiState.value = InternshipUiState.Loading
            try {
                val internships = internshipRepository.getInternshipsByStudent(studentId)
                _uiState.value = if (internships.isEmpty()){
                    InternshipUiState.Empty
                }else{
                    InternshipUiState.SuccessList(internships)
                }
            }catch (e: Exception){
                _uiState.value = InternshipUiState.Error(
                    e.message ?: "Erro ao carregar estágios"
                )
            }
        }
    }

    fun loadActiveInternshipByStudent(studentId: String) {
        viewModelScope.launch {
            _uiState.value = InternshipUiState.Loading

            try {
                val activeInternship = internshipRepository
                    .getInternshipsByStudent(studentId)
                    .firstOrNull { it.status == InternshipStatus.IN_PROGRESS }

                if (activeInternship == null) {
                    _uiState.value = InternshipUiState.Empty
                    return@launch
                }

                syncPendingActivities()
                val activityLogs = try {
                    internshipRepository.getActivityLogsByInternship(activeInternship.id)
                        .also { localActivityRepository?.saveAll(it) }
                } catch (_: Exception) {
                    localActivityRepository
                        ?.getByInternship(activeInternship.id)
                        .orEmpty()
                }

                _uiState.value = InternshipUiState.ActiveInternshipSuccess(
                    internship = activeInternship,
                    activityLogs = activityLogs,
                )
            } catch (e: Exception) {
                _uiState.value = InternshipUiState.Error(
                    e.message ?: "Erro ao carregar estágio ativo."
                )
            }
        }
    }
    fun getInternshipsByInstitution(institutionId: String) {
        viewModelScope.launch {
            _uiState.value = InternshipUiState.Loading
            try {
                val internships = internshipRepository.getInternshipsByInstitution(institutionId)
                _uiState.value = if (internships.isEmpty()){
                    InternshipUiState.Empty
                }else{
                    InternshipUiState.SuccessList(internships)
                }
            }catch (e: Exception){
                _uiState.value = InternshipUiState.Error(
                    e.message ?: "Erro ao carregar estágios"
                )
            }
        }
    }
    fun getInternshipsByStatus(status: InternshipStatus){
        viewModelScope.launch {
            _uiState.value = InternshipUiState.Loading
            try {
                val internships = internshipRepository.getInternshipsByStatus(status)
                _uiState.value = if (internships.isEmpty()){
                    InternshipUiState.Empty
                }else{
                    InternshipUiState.SuccessList(internships)
                }
            }catch (e: Exception){
                _uiState.value = InternshipUiState.Error(
                    e.message ?: "Erro ao carregar estágios"
                )
            }
        }
    }
    fun getInternshipById(internshipId: String){
        viewModelScope.launch {
            _uiState.value = InternshipUiState.Loading
            try {
                val internship = internshipRepository.getInternshipById(internshipId)
                _uiState.value = if (internship != null){
                    InternshipUiState.Success(internship)
                }else{
                    InternshipUiState.Empty
                }
            }catch (e: Exception){
                _uiState.value = InternshipUiState.Error(
                    e.message ?: "Erro ao carregar estágios"
                )
            }
        }
    }

    fun createInternship(input: CreateInternshipInput) {
        viewModelScope.launch {
            _uiState.value = InternshipUiState.Loading

            try {
                val internship = internshipRepository.createInternship(input)

                _uiState.value = InternshipUiState.Success(internship)
            } catch (e: Exception) {
                _uiState.value = InternshipUiState.Error(
                    e.message ?: "Erro ao criar estágio."
                )
            }
        }
    }
    fun assignSupervisor(internshipId: String, input: AssignSupervisorInput){
        viewModelScope.launch {
            _uiState.value = InternshipUiState.Loading
            try {
                val internship = internshipRepository.assignSupervisor(internshipId, input)
                _uiState.value = if (internship != null){
                    InternshipUiState.Success(internship)
                }else{
                    InternshipUiState.Empty
                }
            }catch (e: Exception){
                _uiState.value = InternshipUiState.Error(
                    e.message ?: "Erro ao carregar estágios"
                )
            }
        }
    }
    fun createActivityLog(input: CreateActivityLogInput ){
        viewModelScope.launch {
            _activityCreationUiState.value = ActivityCreationUiState.Loading
            try {
                syncPendingActivities()
                val activity = internshipRepository.createActivityLog(input)
                localActivityRepository?.save(activity)
                appendCreatedActivity(activity)
                _activityCreationUiState.value = ActivityCreationUiState.Success(activity)
            }catch (e: Exception){
                val localRepository = localActivityRepository
                if (localRepository == null) {
                    _activityCreationUiState.value = ActivityCreationUiState.Error(
                        e.message ?: "Erro ao criar atividade."
                    )
                    return@launch
                }

                val pendingActivity = input.toPendingActivityLog()
                localRepository.save(pendingActivity, pendingSync = true)
                appendCreatedActivity(pendingActivity)
                _activityCreationUiState.value = ActivityCreationUiState.Success(pendingActivity)
            }
        }
    }

    fun createActivityLog(
        input: CreateActivityLogInput,
        attachmentPath: String,
        attachmentBytes: ByteArray,
    ) {
        viewModelScope.launch {
            _activityCreationUiState.value = ActivityCreationUiState.Loading

            val uploadedInput = try {
                val uploadedPath = requireNotNull(storageRepository).uploadFile(
                    bucket = "activity-attachments",
                    path = attachmentPath,
                    bytes = attachmentBytes,
                )
                input.copy(attachmentUrl = uploadedPath)
            } catch (e: Exception) {
                _activityCreationUiState.value = ActivityCreationUiState.Error(
                    e.message ?: "Erro ao carregar anexo."
                )
                return@launch
            }

            try {
                syncPendingActivities()
                val activity = internshipRepository.createActivityLog(uploadedInput)
                localActivityRepository?.save(activity)
                appendCreatedActivity(activity)
                _activityCreationUiState.value = ActivityCreationUiState.Success(activity)
            } catch (e: Exception) {
                val localRepository = localActivityRepository
                if (localRepository == null) {
                    _activityCreationUiState.value = ActivityCreationUiState.Error(
                        e.message ?: "Erro ao criar atividade."
                    )
                    return@launch
                }

                val pendingActivity = uploadedInput.toPendingActivityLog()
                localRepository.save(pendingActivity, pendingSync = true)
                appendCreatedActivity(pendingActivity)
                _activityCreationUiState.value = ActivityCreationUiState.Success(pendingActivity)
            }
        }
    }

    fun resetActivityCreationState() {
        _activityCreationUiState.value = ActivityCreationUiState.Idle
    }

    private fun appendCreatedActivity(activity: ActivityLogModel) {
        val state = _uiState.value
        if (state is InternshipUiState.ActiveInternshipSuccess) {
            _uiState.value = state.copy(
                activityLogs = (state.activityLogs + activity).distinctBy { it.id }
            )
        }
    }
    fun getActivityLogsByInternship(internshipId : String){
        viewModelScope.launch {
            _uiState.value = InternshipUiState.Loading
            try {
                syncPendingActivities()
                val activity = internshipRepository.getActivityLogsByInternship(internshipId)
                localActivityRepository?.saveAll(activity)
                _uiState.value = if (activity.isEmpty()){
                    InternshipUiState.Empty
                }else{
                    InternshipUiState.SuccessActivityList(activity)
                }
            }catch (e: Exception){
                val cachedActivities = localActivityRepository
                    ?.getByInternship(internshipId)
                    .orEmpty()
                _uiState.value = if (cachedActivities.isEmpty()) {
                    InternshipUiState.Error(
                        e.message ?: "Erro ao carregar atividades."
                    )
                } else {
                    InternshipUiState.SuccessActivityList(cachedActivities)
                }
            }
        }
    }

    fun loadActivityLogById(activityLogId: String) {
        viewModelScope.launch {
            _uiState.value = InternshipUiState.Loading

            try {
                syncPendingActivities()
                val activityLog = internshipRepository.getActivityLogById(activityLogId)
                activityLog?.let { localActivityRepository?.save(it) }

                _uiState.value = if (activityLog != null) {
                    InternshipUiState.SuccessActivity(activityLog)
                } else {
                    InternshipUiState.Empty
                }
            } catch (e: Exception) {
                val cachedActivity = localActivityRepository?.getById(activityLogId)
                _uiState.value = if (cachedActivity != null) {
                    InternshipUiState.SuccessActivity(cachedActivity)
                } else {
                    InternshipUiState.Error(
                        e.message ?: "Erro ao carregar atividade."
                    )
                }
            }
        }
    }

    private suspend fun syncPendingActivities() {
        val localRepository = localActivityRepository ?: return
        localRepository.getPending().forEach { pendingActivity ->
            runCatching {
                internshipRepository.createActivityLog(
                    localRepository.toCreateInput(pendingActivity)
                )
            }.onSuccess { syncedActivity ->
                localRepository.delete(pendingActivity)
                localRepository.save(syncedActivity)
            }
        }
    }

    fun resetState() {
        _uiState.value = InternshipUiState.Idle
    }
}

private fun CreateActivityLogInput.toPendingActivityLog() = ActivityLogModel(
    id = UUID.randomUUID().toString(),
    internshipId = internshipId,
    studentId = studentId,
    description = description,
    activityDate = activityDate,
    hours = hours,
    type = type,
    location = location,
    attachmentUrl = attachmentUrl,
    createdAt = Instant.now().toString(),
)
