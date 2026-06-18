package turmaA.grupoB.LinkStage.data.repository.internship

import turmaA.grupoB.LinkStage.data.remote.model.internship.ActivityLogModel
import turmaA.grupoB.LinkStage.data.remote.model.internship.CreateActivityLogInput
import turmaA.grupoB.LinkStage.data.room.ActivityLogEntity
import turmaA.grupoB.LinkStage.data.room.AtividadeDAO

class LocalActivityRepository(
    private val atividadeDAO: AtividadeDAO,
) : LocalActivityRepositoryInterface {

    override suspend fun getByInternship(internshipId: String): List<ActivityLogModel> {
        return atividadeDAO.getAtividadesByInternship(internshipId).map { it.toModel() }
    }

    override suspend fun getById(activityLogId: String): ActivityLogModel? {
        return atividadeDAO.getAtividadeById(activityLogId)?.toModel()
    }

    override suspend fun getPending(): List<ActivityLogModel> {
        return atividadeDAO.getPendingAtividades().map { it.toModel() }
    }

    override suspend fun save(activityLog: ActivityLogModel, pendingSync: Boolean) {
        atividadeDAO.insert(activityLog.toEntity(pendingSync))
    }

    override suspend fun saveAll(activityLogs: List<ActivityLogModel>) {
        atividadeDAO.insertAll(activityLogs.map { it.toEntity(pendingSync = false) })
    }

    override suspend fun delete(activityLog: ActivityLogModel) {
        atividadeDAO.delete(activityLog.toEntity(pendingSync = true))
    }

    override fun toCreateInput(activityLog: ActivityLogModel): CreateActivityLogInput {
        return CreateActivityLogInput(
            internshipId = activityLog.internshipId,
            studentId = activityLog.studentId,
            description = activityLog.description,
            activityDate = activityLog.activityDate,
            hours = activityLog.hours,
            type = activityLog.type,
            location = activityLog.location,
            attachmentUrl = activityLog.attachmentUrl,
        )
    }
}

private fun ActivityLogEntity.toModel() = ActivityLogModel(
    id = id,
    internshipId = internshipId,
    studentId = studentId,
    description = description,
    activityDate = activityDate,
    hours = hours,
    type = type,
    location = location,
    attachmentUrl = attachmentUrl,
    createdAt = createdAt,
)

private fun ActivityLogModel.toEntity(pendingSync: Boolean) = ActivityLogEntity(
    id = id,
    internshipId = internshipId,
    studentId = studentId,
    description = description,
    activityDate = activityDate,
    hours = hours,
    type = type,
    location = location,
    attachmentUrl = attachmentUrl,
    createdAt = createdAt,
    pendingSync = pendingSync,
)
