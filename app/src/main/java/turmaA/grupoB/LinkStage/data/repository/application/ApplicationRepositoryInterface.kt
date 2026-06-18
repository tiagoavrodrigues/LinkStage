package turmaA.grupoB.LinkStage.data.repository.application

import turmaA.grupoB.LinkStage.data.remote.model.application.ApplicationModel
import turmaA.grupoB.LinkStage.data.remote.model.application.CreateApplicationInput
import turmaA.grupoB.LinkStage.data.remote.model.application.UpdateApplicationDecisionInput
import turmaA.grupoB.LinkStage.data.remote.model.enums.ApplicationStatus

interface ApplicationRepositoryInterface {
    suspend fun getApplicationById(applicationId: String): ApplicationModel?
    suspend fun getApplicationsByOffer(offerId: String): List<ApplicationModel>
    suspend fun getApplicationsByStudent(studentId: String): List<ApplicationModel>
    suspend fun getApplicationsByStatus(status: ApplicationStatus): List<ApplicationModel>
    suspend fun createApplication(input: CreateApplicationInput): ApplicationModel

    suspend fun updateApplicationDecision(
        applicationId: String,
        input: UpdateApplicationDecisionInput
    ): ApplicationModel

    suspend fun acceptApplication(
        applicationId: String,
    ): ApplicationModel

    suspend fun rejectApplication(
        applicationId: String,
        rejectionReason: String,
    ): ApplicationModel
}