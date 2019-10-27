package `in`.karthiks.demo.productpriceservice.service

import `in`.karthiks.demo.productpriceservice.client.ThirdPartyPriceProvider
import `in`.karthiks.demo.productpriceservice.model.Product
import `in`.karthiks.demo.productpriceservice.model.VendorPrice
import com.google.gson.JsonObject
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import java.math.BigDecimal
import java.util.*

@Service
class PriceService(private val thirdPartyPriceProvider: ThirdPartyPriceProvider) {

    fun getMockPrices(upc: String): Product {
        return Product("upc", "Apple iPhone X",
                Arrays.asList(
                        VendorPrice("Apple", BigDecimal.valueOf(100.00).setScale(2)),
                        VendorPrice("Best Buy", BigDecimal.valueOf(99.95).setScale(2))
                )
        )
    }

    fun getPrices(upc: String): Product {
        try {
            var json: JsonObject? = thirdPartyPriceProvider.fetchPricesForUPC(upc)
        var item = json?.getAsJsonArray("items")?.get(0)?.asJsonObject
        var offers = item?.getAsJsonArray("offers")
        var prices : ArrayList<VendorPrice> = ArrayList()

        offers?.forEach {
            prices.add(VendorPrice(it.asJsonObject.get("merchant").asString,
                    it.asJsonObject.get("price").asBigDecimal.setScale(2)))
        }
        return Product(upc, (item?.get("title")?.asString ?: ""), prices)
        } catch(ex: HttpClientErrorException) {
            if (ex.statusCode == HttpStatus.BAD_REQUEST)
                throw ProductNotFoundException()
            else
                throw ex
        }
    }

    class ProductNotFoundException : Throwable("Product Not Found!")
}
