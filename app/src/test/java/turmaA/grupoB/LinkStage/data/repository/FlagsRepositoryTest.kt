package turmaA.grupoB.LinkStage.data.repository

import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import turmaA.grupoB.LinkStage.data.remote.api.CountriesService
import turmaA.grupoB.LinkStage.data.remote.model.Imgs
import turmaA.grupoB.LinkStage.data.repository.flags.FlagsRepository
import java.net.HttpURLConnection

class FlagsRepositoryTest {

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    private fun createTestRepository(): FlagsRepository {
        val service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CountriesService::class.java)
        return FlagsRepository(service)
    }

    @Test
    fun getFlagShouldReturnImgsOnSuccess() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_OK)
                .setBody("""[{"flags": {"png": "https://flagcdn.com/w320/fr.png"}}]""")
        )

        val repository = createTestRepository()
        val result = repository.getFlag("france")

        assertEquals(Imgs(png = "https://flagcdn.com/w320/fr.png"), result)
    }

    @Test
    fun getFlagShouldUseDirectFallbackWhenApiFailsForKnownCountry() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
        )

        val repository = createTestRepository()
        val result = repository.getFlag("portugal")

        assertEquals(Imgs(png = "https://flagcdn.com/w320/pt.png"), result)
    }

    @Test
    fun getFlagShouldThrowHttpExceptionOn404() = runTest {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(HttpURLConnection.HTTP_NOT_FOUND)
        )

        val repository = createTestRepository()

        try {
            repository.getFlag("unknownland")
            throw AssertionError("Expected HttpException")
        } catch (e: retrofit2.HttpException) {
            assertEquals(404, e.code())
        }
    }

    @Test
    fun getFlagShouldPropagateIOExceptionOnNetworkFailure() = runTest {
        mockWebServer.shutdown()

        val repository = createTestRepository()

        try {
            repository.getFlag("unknownland")
            throw AssertionError("Expected exception on network failure")
        } catch (e: Exception) {
            assertTrue(
                "Expected IOException, ConnectException, SocketException or HttpException but got ${e::class.simpleName}",
                e is java.io.IOException ||
                e is java.net.ConnectException ||
                e is java.net.SocketException ||
                e is retrofit2.HttpException
            )
        }
    }
}
