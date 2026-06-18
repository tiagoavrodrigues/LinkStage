package turmaA.grupoB.LinkStage.data.remote.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import turmaA.grupoB.LinkStage.BuildConfig
import turmaA.grupoB.LinkStage.data.remote.model.CountryResponse

interface CountriesService{
    @GET("countries/v5")
    suspend fun getFlagByName(
        @Query("q") country: String,
        @Query("fields") fields: String = "flags",
        @Header("Authorization") authorization: String = "Bearer ${BuildConfig.RESTCOUNTRIES_API_KEY}"
    ): List<CountryResponse>
}