package `in`.karthiks.demo.productpriceservice.client

import `in`.karthiks.demo.productpriceservice.config.AppConfig
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class ThirdPartyPriceProvider {

    private val restTemplate = RestTemplate()

    @Autowired
    private lateinit var appConfig: AppConfig

    fun fetchPricesForUPC(upc: String): JsonObject? {
        var jsonString = restTemplate.getForObject(appConfig.baseurl + "/prod/trial/lookup?upc={upc}",String::class.java, upc)
        return Gson().fromJson(jsonString, JsonObject::class.java)
    }
}
