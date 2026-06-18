package turmaA.grupoB.LinkStage.data.repository.flags

import turmaA.grupoB.LinkStage.data.remote.RetrofitInstance
import turmaA.grupoB.LinkStage.data.remote.api.CountriesService
import turmaA.grupoB.LinkStage.data.remote.model.Imgs

class FlagsRepository(
    private val api: CountriesService = RetrofitInstance.api
) : FlagsRepositoryInterface {
    private val directFlagUrls = mapOf(
        "portugal" to Imgs(png = "https://flagcdn.com/w320/pt.png"),
        "gb" to Imgs(png = "https://flagcdn.com/w320/gb.png")
    )

    override suspend fun getFlag(country: String): Imgs? {
        return try {
            api.getFlagByName(country).firstOrNull()?.flags
        } catch (e: Exception) {
            directFlagUrls[country] ?: throw e
        }
    }
}