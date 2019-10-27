package `in`.karthiks.demo.productpriceservice.integration

import `in`.karthiks.demo.productpriceservice.ProductPriceServiceApplication
import au.com.dius.pact.provider.PactVerifyProvider
import au.com.dius.pact.provider.junit.Provider
import au.com.dius.pact.provider.junit.State
import au.com.dius.pact.provider.junit.loader.PactBroker
import au.com.dius.pact.provider.junit5.HttpTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.io.BufferedReader

@Tag("integration")
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [ProductPriceServiceApplication::class])
@ActiveProfiles("test")
@Provider("ProductPriceService")
@PactBroker(host = "localhost", scheme = "http")
class ProductPriceContractIT {
    @LocalServerPort
    private var port = 0
    private lateinit var wireMockServer: WireMockServer

    @BeforeEach
    internal fun setupTestTarget(context: PactVerificationContext) {
        context.target = HttpTestTarget("localhost", port, "/")
        wireMockServer = WireMockServer(WireMockConfiguration.wireMockConfig().port(8088))
        wireMockServer.start()
    }

    @AfterEach
    internal fun tearDown() {
        wireMockServer.stop()
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider::class)
    fun pactVerificationTestTemplate(context: PactVerificationContext) {
        context.verifyInteraction()
    }

    @State("Valid UPC")
    fun pactWithValidUPC () {
        val content = this.javaClass.getResourceAsStream("/sample.json").bufferedReader().use(BufferedReader::readText)
        wireMockServer.stubFor(
                WireMock.get(WireMock.urlPathMatching("/prod/trial/lookup")
                ).willReturn(WireMock.aResponse().withHeader("Content-Type", "application/json")
                        .withStatus(200).withBody(content)))
    }

    @State("Invalid UPC")
    fun pactWithInvalidUPC () {
        wireMockServer.stubFor(
                WireMock.get(WireMock.urlPathMatching("/prod/trial/lookup")
                ).willReturn(WireMock.aResponse()
                        .withStatus(400)))
    }

}