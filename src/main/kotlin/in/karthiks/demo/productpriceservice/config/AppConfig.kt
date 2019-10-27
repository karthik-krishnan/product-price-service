package `in`.karthiks.demo.productpriceservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "thirdpartyservice")
class AppConfig {
    var baseurl: String = ""
}
