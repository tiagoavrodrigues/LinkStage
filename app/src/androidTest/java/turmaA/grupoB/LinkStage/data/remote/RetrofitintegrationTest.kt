package turmaA.grupoB.LinkStage.data.remote

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import turmaA.grupoB.LinkStage.data.remote.api.CountriesService
import java.net.HttpURLConnection

class RetrofitIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var countriesService: CountriesService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        countriesService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CountriesService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun fetchFlagShouldReturnPngUrl() = runTest {
        val mockJsonResponse = """
            [
                {
                    "flags": {
                        "png": "https://flagcdn.com/w320/pt.png"
                    }
                }
            ]
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody(mockJsonResponse)
        )

        val response = countriesService.getFlagByName("portugal")

        assertEquals("https://flagcdn.com/w320/pt.png", response.first().flags.png)
    }
}
