package `in`.karthiks.demo.productpriceservice.integration

import `in`.karthiks.demo.productpriceservice.ProductPriceServiceApplication
import `in`.karthiks.demo.productpriceservice.controller.ProductPriceController
import `in`.karthiks.demo.productpriceservice.model.Product
import `in`.karthiks.demo.productpriceservice.model.VendorPrice
import `in`.karthiks.demo.productpriceservice.service.PriceService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.math.BigDecimal
import java.util.*

@Tag("integration")
@ExtendWith(SpringExtension::class)
class ProductPriceIntegrationTestWithStandaloneSetup {

    lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var priceService: PriceService

    @InjectMocks
    lateinit var productPriceController: ProductPriceController

    @BeforeEach
    internal fun setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(productPriceController)
                .build()
    }

    @Test
    fun getPricesWithValidUPC() {
        var product =  Product("upc", "Apple iPhone X",
                Arrays.asList(
                        VendorPrice("Apple", BigDecimal.valueOf(100.00).setScale(2)),
                        VendorPrice("Best Buy", BigDecimal.valueOf(99.95).setScale(2))
                )
        )

        `when`(priceService.getPrices(anyString())).thenReturn(product)

        mockMvc.get("/products/12345/prices") {
        }.andExpect {
            status {isOk}
            content {contentType(MediaType.APPLICATION_JSON)}
        }
    }

    @Test
    fun throw400onInvalidUPC() {
        `when`(priceService.getPrices(anyString())).thenThrow(PriceService.ProductNotFoundException())
        mockMvc.get("/products/12345/prices") {
        }.andExpect {
            status {`is`(400)}
        }
    }
}
