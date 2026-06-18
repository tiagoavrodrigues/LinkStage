package turmaA.grupoB.LinkStage.data.remote

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Test
import retrofit2.Retrofit

class RetrofitInstanceTest {

    @Test
    fun apiShouldNotBeNullAfterInitialization() {
        assertNotNull(RetrofitInstance.api)
    }

    @Test
    fun apiShouldReturnSameInstanceOnMultipleCalls() {
        val first = RetrofitInstance.api
        val second = RetrofitInstance.api
        assertSame(first, second)
    }

    @Test
    fun apiShouldBeCountriesServiceImpl() {
        val api = RetrofitInstance.api
        assertNotNull(api)
        assertEquals(
            "turmaA.grupoB.LinkStage.data.remote.api.CountriesService",
            api::class.java.interfaces[0].name
        )
    }

    @Test
    fun retrofitInstanceShouldHaveCorrectBaseUrl() {
        val api = RetrofitInstance.api
        val handler = java.lang.reflect.Proxy.getInvocationHandler(api)
        val handlerClass = handler::class.java
        val retrofitField = handlerClass.declaredFields.firstOrNull { it.type == Retrofit::class.java }
            ?: error("Could not find Retrofit field in ${handlerClass.name}: ${handlerClass.declaredFields.map { "${it.type.simpleName} ${it.name}" }}")
        retrofitField.isAccessible = true
        val retrofit = retrofitField.get(handler) as Retrofit
        assertEquals("https://api.restcountries.com/", retrofit.baseUrl().toString())
    }
}
