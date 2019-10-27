package `in`.karthiks.demo.productpriceservice.controller

import `in`.karthiks.demo.productpriceservice.model.Product
import `in`.karthiks.demo.productpriceservice.service.PriceService
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/products")
class ProductPriceController(private val priceService: PriceService) {

    @GetMapping("{upc}/prices")
    fun getPriceForUPC(@PathVariable upc: String) : Product {
        return priceService.getPrices(upc)
    }

    @ExceptionHandler(value = [PriceService.ProductNotFoundException::class])
    fun handleException(response: HttpServletResponse) {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Invalid UPC!")
    }

}
