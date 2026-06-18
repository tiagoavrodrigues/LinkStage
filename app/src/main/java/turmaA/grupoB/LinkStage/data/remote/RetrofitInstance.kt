package turmaA.grupoB.LinkStage.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import turmaA.grupoB.LinkStage.data.remote.api.CountriesService

object RetrofitInstance {
    private  const val Base_url = "https://api.restcountries.com/"
    val api: CountriesService by lazy {
        Retrofit.Builder()
            .baseUrl(Base_url)
            .addConverterFactory(
                GsonConverterFactory
                    .create()
            ).build()
            .create(CountriesService::class.java)
    }
}