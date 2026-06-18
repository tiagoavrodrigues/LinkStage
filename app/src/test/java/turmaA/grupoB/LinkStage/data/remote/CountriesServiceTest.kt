package turmaA.grupoB.LinkStage.data.remote

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import turmaA.grupoB.LinkStage.data.remote.api.CountriesService
import java.net.HttpURLConnection

class CountriesServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var countriesService: CountriesService

    private val authHeader = "Bearer test-restcountries-key"

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
    fun getFlagByNameShouldParseSuccessfulResponse() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody("""[{"flags": {"png": "https://flagcdn.com/w320/pt.png"}}]""")
        )

        val result = countriesService.getFlagByName("portugal", authorization = authHeader)

        mockWebServer.takeRequest()

        assertEquals("https://flagcdn.com/w320/pt.png", result.firstOrNull()?.flags?.png)
    }

    @Test
    fun getFlagByNameShouldSendCountryAsPathParameter() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody("""[{"flags": {"png": "https://flagcdn.com/w320/de.png"}}]""")
        )

        countriesService.getFlagByName("germany", authorization = authHeader)

        val request: RecordedRequest = mockWebServer.takeRequest()
        assertEquals("/countries/v5?q=germany&fields=flags", request.path)
        assertEquals("GET", request.method)
        assertEquals(authHeader, request.getHeader("Authorization"))
    }

    @Test
    fun getFlagByNameShouldHandleEmptyPngField() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody("""[{"flags": {"png": ""}}]""")
        )

        val result = countriesService.getFlagByName("unknown", authorization = authHeader)

        mockWebServer.takeRequest()

        assertEquals("", result.firstOrNull()?.flags?.png)
    }

    @Test
    fun getFlagByNameShouldThrowOn404() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
                .setBody("""{"error": "Not found"}""")
        )

        try {
            countriesService.getFlagByName("nonexistent", authorization = authHeader)
            throw AssertionError("Expected HttpException to be thrown")
        } catch (e: retrofit2.HttpException) {
            assertEquals(404, e.code())
        }

        mockWebServer.takeRequest()
    }

    @Test
    fun getFlagByNameShouldThrowOn500() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
                .setBody("""{"error": "Internal server error"}""")
        )

        try {
            countriesService.getFlagByName("portugal", authorization = authHeader)
            throw AssertionError("Expected HttpException to be thrown")
        } catch (e: retrofit2.HttpException) {
            assertEquals(500, e.code())
        }

        mockWebServer.takeRequest()
    }

    @Test
    fun getFlagByNameShouldHandleDifferentCountryCodes() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody("""[{"flags": {"png": "https://flagcdn.com/w320/jp.png"}}]""")
        )

        val result = countriesService.getFlagByName("japan", authorization = authHeader)

        assertEquals("https://flagcdn.com/w320/jp.png", result.firstOrNull()?.flags?.png)

        val request = mockWebServer.takeRequest()
        assertEquals("/countries/v5?q=japan&fields=flags", request.path)
        assertEquals(authHeader, request.getHeader("Authorization"))
    }
}
