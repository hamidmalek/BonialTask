package com.example.bonialtask.network

import com.example.bonialtask.model.ContentWrapper
import com.example.bonialtask.model.Response
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File

class ApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService
    private lateinit var moshi: Moshi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()

        moshi = Moshi.Builder()
            .add(
                PolymorphicJsonAdapterFactory.of(ContentWrapper::class.java, "contentType")
                    .withSubtype(ContentWrapper.SuperBannerCarousel::class.java, "superBannerCarousel")
                    .withSubtype(ContentWrapper.Brochure::class.java, "brochure")
                    .withSubtype(ContentWrapper.Brochure::class.java, "brochurePremium")
            )
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/")) // Use mock server
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getShelf returns parsed response`() = runBlocking {
        val json = """
            {
              "_embedded": {
                "contents": [
                  {
                    "placement": "top",
                    "adFormat": "banner",
                    "contentFormatSource": "src",
                    "contentType": "superBannerCarousel",
                    "content": [],
                    "externalTracking": {
                      "impression": [],
                      "click": []
                    }
                  }
                ]
              }
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(json)
        )

        val response = apiService.getShelf()

        assertEquals(1, response.embedded.contents.size)
        assertEquals("top", response.embedded.contents[0].placement)
        assertEquals("superBannerCarousel", response.embedded.contents[0].contentType)
    }

    @Test
    fun `load and parse shelf_response json safely`() {
        val fileContent = File("src/test/resources/shelf.json").readText()
        val adapter = moshi.adapter(Response::class.java)
        val parsed = adapter.fromJson(fileContent)

        assertNotNull(parsed)
        val items = parsed!!.embedded.contents
        assertTrue(items.isNotEmpty())

        items.forEach { item ->
            when (item) {
                is ContentWrapper.Brochure -> {
                    assertTrue(item.contentType in listOf("brochure", "brochurePremium"))
                }
                is ContentWrapper.SuperBannerCarousel -> {
                    assertEquals("superBannerCarousel", item.contentType)
                }
                else -> fail("Unexpected content type: ${item::class.simpleName}")
            }
        }
    }
}
