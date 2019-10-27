package `in`.karthiks.demo.productpriceservice.model

import java.math.BigDecimal

data class Product(
  val upc: String,
  val name: String,
  val catalog: List<VendorPrice>
)

class VendorPrice(val vendorName: String, val price: BigDecimal)
