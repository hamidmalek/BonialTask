package com.example.bonialtask.repo

import com.example.bonialtask.model.Badge
import com.example.bonialtask.model.BrochureContent
import com.example.bonialtask.model.ContentWrapper
import com.example.bonialtask.model.Embedded
import com.example.bonialtask.model.ExternalTracking
import com.example.bonialtask.model.Publisher
import com.example.bonialtask.model.Response
import com.example.bonialtask.model.Store
import com.example.bonialtask.network.ApiService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BrochureRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var apiService: ApiService
    private lateinit var repository: BrochureRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        apiService = mockk()
        repository = BrochureRepository(apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getBrochures emits Loading then Success with only brochures`() = runTest {
        // Prepare mixed contents
        val contents: List<ContentWrapper> = listOf(
            ContentWrapper.Brochure(
                placement = "mid",
                adFormat = "brochure",
                contentFormatSource = "src",
                contentType = "brochure",
                content = BrochureContent(
                    id = 1,
                    contentId = "c1",
                    title = "Test Brochure",
                    validFrom = "2025-01-01",
                    validUntil = "2025-12-31",
                    publishedFrom = "2025-01-01",
                    publishedUntil = "2025-12-31",
                    type = "promo",
                    brochureImage = "http://image.com",
                    pageCount = 12,
                    publisher = Publisher(id = "pub1"),
                    contentBadges = listOf(Badge("New")),
                    distance = 2.0,
                    hideValidityDate = false,
                    closestStore = Store(
                        id = "s1",
                        latitude = 1.1,
                        longitude = 2.2,
                        street = "Main",
                        streetNumber = "5A",
                        zip = "12345",
                        city = "Berlin"
                    )
                ),
                externalTracking = ExternalTracking(impression = emptyList(), click = emptyList())
            ),
            ContentWrapper.SuperBannerCarousel(
                placement = "top",
                adFormat = "banner",
                contentFormatSource = "src",
                contentType = "superBannerCarousel",
                content = emptyList(),
                externalTracking = ExternalTracking(impression = emptyList(), click = emptyList())
            )
        )

        coEvery { apiService.getShelf() } returns Response(Embedded(contents))

        val emissions = repository.getBrochures().toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Result.Loading)
        assertTrue(emissions[1] is Result.Success)

        val brochures = (emissions[1] as Result.Success).data
        assertEquals(1, brochures.size)
        assertEquals("Test Brochure", brochures[0].content.title)
    }

    @Test
    fun `getBrochures emits Error when API throws`() = runTest {
        coEvery { apiService.getShelf() } throws RuntimeException("Network error")

        val emissions = repository.getBrochures().toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Result.Loading)
        assertTrue(emissions[1] is Result.Error)
        assertEquals("Network error", (emissions[1] as Result.Error).exception.message)
    }
}
