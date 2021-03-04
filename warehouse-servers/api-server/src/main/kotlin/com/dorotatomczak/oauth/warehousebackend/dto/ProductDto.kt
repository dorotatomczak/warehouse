package com.dorotatomczak.oauth.warehousebackend.dto

import com.dorotatomczak.oauth.warehousebackend.repository.model.ProductEntity
import com.dorotatomczak.oauth.warehousebackend.repository.model.ProductInWarehouseEntity
import java.time.LocalDateTime

data class ProductDto(
    val id: Int? = null,
    val name: String? = null,
    val publisher: String? = null,
    val quantity: Int = 0,
    val price: Float? = null,
    val modified: LocalDateTime? = null
) {
    companion object {
        fun from(productInWarehouse: ProductInWarehouseEntity): ProductDto {
            return ProductDto(
                productInWarehouse.product?.id,
                productInWarehouse.product?.name,
                productInWarehouse.product?.publisher,
                productInWarehouse.quantity,
                productInWarehouse.product?.price,
                productInWarehouse.product?.modified
            )
        }

        fun from(product: ProductEntity, quantity: Int = 0): ProductDto {
            return ProductDto(
                product.id,
                product.name,
                product.publisher,
                quantity,
                product.price,
                product.modified
            )
        }
    }

    fun toProductEntity(): ProductEntity {
        return ProductEntity(id, name, publisher, price, modified)
    }
}