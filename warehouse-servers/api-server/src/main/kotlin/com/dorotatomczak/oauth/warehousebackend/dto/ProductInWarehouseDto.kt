package com.dorotatomczak.oauth.warehousebackend.dto

import com.dorotatomczak.oauth.warehousebackend.repository.model.ProductInWarehouseEntity

data class ProductInWarehouseDto(
    val productId: Int? = null,
    val warehouseId: Int? = null,
    val quantity: Int = 0
) {
    companion object {
        fun from(productInWarehouse: ProductInWarehouseEntity): ProductInWarehouseDto {
            return ProductInWarehouseDto(
                productInWarehouse.product?.id,
                productInWarehouse.warehouse?.id,
                productInWarehouse.quantity
            )
        }
    }
}