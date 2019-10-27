package `in`.karthiks.demo.productpriceservice.integration

import `in`.karthiks.demo.productpriceservice.ProductPriceServiceApplication
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.io.BufferedReader

@Tag("integration")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [ProductPriceServiceApplication::class])
@ActiveProfiles("test")
class ProductPriceIT {

    private lateinit var wireMockServer: WireMockServer
    @Autowired
    lateinit var context: WebApplicationContext
    lateinit var mockMvc: MockMvc

    @BeforeEach
    internal fun setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .build()
        wireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().port(8088))
        wireMockServer.start()
    }

    @AfterEach
    internal fun tearDown() {
        wireMockServer.stop()
    }

    @Test
    fun getPricesWithValidUPC() {
        val content = this.javaClass.getResourceAsStream("/sample.json").bufferedReader().use(BufferedReader::readText)
        wireMockServer.stubFor(
                WireMock.get(WireMock.urlPathMatching("/prod/trial/lookup")
                ).willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(200).withBody(content)))

        mockMvc.get("/products/12345/prices") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status {isOk}
            content {contentType(MediaType.APPLICATION_JSON)}
        }
    }

    @Test
    fun throw404onInvalidUPC() {
        wireMockServer.stubFor(
                WireMock.get(WireMock.urlPathMatching("/prod/trial/lookup")
                ).willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(404)))

        mockMvc.get("/products/12345/prices") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status {isOk}
            content {contentType(MediaType.APPLICATION_JSON)}
        }
    }
}
