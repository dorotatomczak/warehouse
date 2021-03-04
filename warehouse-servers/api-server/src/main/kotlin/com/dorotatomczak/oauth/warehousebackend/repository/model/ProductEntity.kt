package com.dorotatomczak.oauth.warehousebackend.repository.model

import com.dorotatomczak.oauth.warehousebackend.dto.ProductDto
import com.dorotatomczak.oauth.warehousebackend.repository.converter.LocalDateTimeConverter
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "products")
data class ProductEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    var name: String? = null,
    var publisher: String? = null,
    var price: Float? = null,

    @Convert(converter = LocalDateTimeConverter::class)
    var modified: LocalDateTime? = null,

    @OneToMany(
        mappedBy = "product",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    var productInWarehouses: List<ProductInWarehouseEntity>? = emptyList()
) {
    fun with(productDto: ProductDto) {
        name = productDto.name
        publisher = productDto.publisher
        price = productDto.price
    }
}