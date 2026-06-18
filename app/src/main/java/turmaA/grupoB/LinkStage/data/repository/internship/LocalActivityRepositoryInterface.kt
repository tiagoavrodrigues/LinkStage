package turmaA.grupoB.LinkStage.data.repository.internship

import turmaA.grupoB.LinkStage.data.remote.model.internship.ActivityLogModel
import turmaA.grupoB.LinkStage.data.remote.model.internship.CreateActivityLogInput

interface LocalActivityRepositoryInterface {
    suspend fun getByInternship(internshipId: String): List<ActivityLogModel>
    suspend fun getById(activityLogId: String): ActivityLogModel?
    suspend fun getPending(): List<ActivityLogModel>
    suspend fun save(activityLog: ActivityLogModel, pendingSync: Boolean = false)
    suspend fun saveAll(activityLogs: List<ActivityLogModel>)
    suspend fun delete(activityLog: ActivityLogModel)
    fun toCreateInput(activityLog: ActivityLogModel): CreateActivityLogInput
}
