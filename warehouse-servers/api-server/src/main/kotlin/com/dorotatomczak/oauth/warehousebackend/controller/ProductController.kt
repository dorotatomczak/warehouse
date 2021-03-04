package com.dorotatomczak.oauth.warehousebackend.controller

import com.dorotatomczak.oauth.warehousebackend.dto.ProductDto
import com.dorotatomczak.oauth.warehousebackend.dto.ProductInWarehouseDto
import com.dorotatomczak.oauth.warehousebackend.repository.model.ProductInWarehouseEntity
import com.dorotatomczak.oauth.warehousebackend.service.ProductService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@EnableGlobalMethodSecurity(prePostEnabled = true)
class ProductController(
    val productService: ProductService
) {

    @GetMapping("/products")
    @PreAuthorize("hasAuthority('INVENTORY_VIEW')")
    fun getAllProducts(@RequestParam(defaultValue = "1") warehouseId: Int): Iterable<ProductDto> =
        productService.getAllProducts(warehouseId)

    @PostMapping("/product")
    @PreAuthorize("hasAuthority('INVENTORY_ADD')")
    fun addProduct(@RequestBody product: ProductDto): ProductDto? =
        product
            .takeIf { it.id == null }
            ?.let { productService.addProduct(product) }

    @DeleteMapping("/product/{id}")
    @PreAuthorize("hasAuthority('INVENTORY_REMOVE')")
    fun deleteProduct(
        @PathVariable id: Int,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) modified: LocalDateTime? = null
    ) = productService.deleteProduct(id, modified)

    @PutMapping("/product")
    @PreAuthorize("hasAuthority('INVENTORY_MODIFY')")
    fun editProduct(@RequestBody product: ProductDto): ProductDto? =
        product
            .takeIf { it.id != null }
            ?.let { productService.modifyProduct(it) }

    @PutMapping("/product/{id}")
    @PreAuthorize("hasAuthority('INVENTORY_MODIFY')")
    fun changeProductQuantity(
        @PathVariable id: Int,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) modified: LocalDateTime? = null,
        @RequestParam(defaultValue = "0") delta: Int,
        @RequestParam(defaultValue = "1") warehouseId: Int
    ): ProductDto? = productService.changeProductQuantity(id, modified, delta, warehouseId)

    @GetMapping("/productsInWarehouses")
    @PreAuthorize("hasAuthority('INVENTORY_VIEW')")
    fun getAllProductsInWarehouses(): Iterable<ProductInWarehouseDto> =
        productService.getAllProductsInWarehouses()
}
