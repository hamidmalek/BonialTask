import com.example.bonialtask.model.BannerContent
import com.example.bonialtask.model.BannerSlot
import com.example.bonialtask.model.BrochureContent
import com.example.bonialtask.model.ContentWrapper
import com.example.bonialtask.model.ExternalTracking
import com.example.bonialtask.model.Response
import com.squareup.moshi.*
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.*
import org.junit.Test

class ModelTest {

    private val moshi = Moshi.Builder()
        .add(
            PolymorphicJsonAdapterFactory.of(ContentWrapper::class.java, "contentType")
                .withSubtype(ContentWrapper.SuperBannerCarousel::class.java, "superBannerCarousel")
                .withSubtype(ContentWrapper.Brochure::class.java, "brochure")
        )
        .add(KotlinJsonAdapterFactory())
        .build()

    @Test
    fun testExternalTracking() {
        val json = """{"impression":["url1"],"click":["url2"]}"""
        val adapter = moshi.adapter(ExternalTracking::class.java)
        val tracking = adapter.fromJson(json)
        assertEquals(listOf("url1"), tracking?.impression)
        assertEquals(listOf("url2"), tracking?.click)
    }

    @Test
    fun testBannerContent() {
        val json = """
            {
                "id":"id123",
                "publisher":{"id":"pub1"},
                "publishedFrom":"2025-01-01",
                "publishedUntil":"2025-12-31",
                "clickUrl":"http://click.com",
                "imageUrl":"http://image.com"
            }
        """
        val adapter = moshi.adapter(BannerContent::class.java)
        val content = adapter.fromJson(json)
        assertEquals("id123", content?.id)
        assertEquals("pub1", content?.publisher?.id)
    }

    @Test
    fun testBannerSlot() {
        val json = """
            {
                "content":{
                    "id":"b1",
                    "publisher":{"id":"p1"},
                    "publishedFrom":"2025-01-01",
                    "publishedUntil":"2025-12-31",
                    "clickUrl":"http://click.com",
                    "imageUrl":"http://image.com"
                },
                "externalTracking":{
                    "impression":["i1"],
                    "click":["c1"]
                }
            }
        """
        val adapter = moshi.adapter(BannerSlot::class.java)
        val slot = adapter.fromJson(json)
        assertEquals("b1", slot?.content?.id)
        assertEquals("p1", slot?.content?.publisher?.id)
    }

    @Test
    fun testBrochureContent() {
        val json = """
            {
                "id":1,
                "contentId":"cid",
                "title":"title",
                "validFrom":"2025-08-01",
                "validUntil":"2025-08-07",
                "publishedFrom":"2025-08-01",
                "publishedUntil":"2025-08-07",
                "type":"promo",
                "brochureImage":"http://img.jpg",
                "pageCount":10,
                "publisher":{"id":"pid"},
                "contentBadges":[{"name":"Hot"}],
                "distance":2.0,
                "hideValidityDate":false,
                "closestStore":{
                    "id":"s1",
                    "latitude":1.1,
                    "longitude":2.2,
                    "street":"Main",
                    "streetNumber":"10",
                    "zip":"12345",
                    "city":"City"
                }
            }
        """
        val adapter = moshi.adapter(BrochureContent::class.java)
        val content = adapter.fromJson(json)
        assertEquals("cid", content?.contentId)
        assertEquals("pid", content?.publisher?.id)
        assertEquals("Hot", content?.contentBadges?.first()?.name)
        assertEquals("City", content?.closestStore?.city)
    }

    @Test
    fun testSuperBannerCarouselDeserialization() {
        val json = """
            {
                "placement":"top",
                "adFormat":"banner",
                "contentFormatSource":"src",
                "contentType":"superBannerCarousel",
                "content":[
                    {
                        "content":{
                            "id":"banner1",
                            "publisher":{"id":"pub1"},
                            "publishedFrom":"2025-01-01",
                            "publishedUntil":"2025-12-31",
                            "clickUrl":"http://click",
                            "imageUrl":"http://img"
                        },
                        "externalTracking":{
                            "impression":["i1"],
                            "click":["c1"]
                        }
                    }
                ],
                "externalTracking":{
                    "impression":["main_imp"],
                    "click":["main_clk"]
                }
            }
        """
        val adapter = moshi.adapter(ContentWrapper::class.java)
        val result = adapter.fromJson(json)
        assertTrue(result is ContentWrapper.SuperBannerCarousel)
        val banner = result as ContentWrapper.SuperBannerCarousel
        assertEquals("top", banner.placement)
        assertEquals("superBannerCarousel", banner.contentType)
        assertEquals("banner1", banner.content[0].content.id)
    }

    @Test
    fun testBrochureDeserialization() {
        val json = """
            {
                "placement":"middle",
                "adFormat":"brochure",
                "contentFormatSource":"src",
                "contentType":"brochure",
                "content":{
                    "id":99,
                    "contentId":"bro1",
                    "title":"Brochure Title",
                    "validFrom":"2025-08-01",
                    "validUntil":"2025-08-10",
                    "publishedFrom":"2025-08-01",
                    "publishedUntil":"2025-08-10",
                    "type":"flyer",
                    "brochureImage":"http://brochure.jpg",
                    "pageCount":5,
                    "publisher":{"id":"pubX"},
                    "contentBadges":[{"name":"New"}],
                    "distance":3.0,
                    "hideValidityDate":true,
                    "closestStore":{
                        "id":"store1",
                        "latitude":52.0,
                        "longitude":13.0,
                        "street":"Market",
                        "streetNumber":"1",
                        "zip":"10000",
                        "city":"Berlin"
                    }
                },
                "externalTracking":{
                    "impression":["i2"],
                    "click":["c2"]
                }
            }
        """
        val adapter = moshi.adapter(ContentWrapper::class.java)
        val result = adapter.fromJson(json)
        assertTrue(result is ContentWrapper.Brochure)
        val brochure = result as ContentWrapper.Brochure
        assertEquals("middle", brochure.placement)
        assertEquals("Brochure Title", brochure.content.title)
    }

    @Test
    fun testEmbeddedWrapper() {
        val json = """
            {
                "_embedded": {
                    "contents": [
                        {
                            "placement": "home",
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
        """
        val adapter = moshi.adapter(Response::class.java)
        val response = adapter.fromJson(json)
        assertEquals(1, response?.embedded?.contents?.size)
        assertTrue(response?.embedded?.contents?.first() is ContentWrapper.SuperBannerCarousel)
    }
}
