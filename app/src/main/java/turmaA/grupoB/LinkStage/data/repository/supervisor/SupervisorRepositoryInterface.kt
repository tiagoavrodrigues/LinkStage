package turmaA.grupoB.LinkStage.data.repository.supervisor

import turmaA.grupoB.LinkStage.data.remote.model.user.CreateSupervisorInput
import turmaA.grupoB.LinkStage.data.remote.model.user.SupervisorModel
import turmaA.grupoB.LinkStage.data.remote.model.user.SupervisorSkillModel

interface SupervisorRepositoryInterface {
    suspend fun getSupervisors(): List<SupervisorModel>
    suspend fun getSupervisorById(supervisorId: String): SupervisorModel?
    suspend fun getSupervisorByUserId(userId: String): SupervisorModel?
    suspend fun getAvailableSupervisors(): List<SupervisorModel>
    suspend fun getSupervisorsByDepartment(department: String): List<SupervisorModel>
    suspend fun getSupervisorSkills(supervisorId: String): List<SupervisorSkillModel>
    suspend fun createSupervisor(input: CreateSupervisorInput): SupervisorModel
}