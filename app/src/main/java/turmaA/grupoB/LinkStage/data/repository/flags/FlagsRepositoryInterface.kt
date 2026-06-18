package turmaA.grupoB.LinkStage.data.repository.flags

import turmaA.grupoB.LinkStage.data.remote.model.Imgs

interface FlagsRepositoryInterface {
    suspend fun getFlag(country: String): Imgs?
}