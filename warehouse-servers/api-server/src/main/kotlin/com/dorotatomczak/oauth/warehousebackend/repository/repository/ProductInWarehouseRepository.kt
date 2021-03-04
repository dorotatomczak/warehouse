package com.dorotatomczak.oauth.warehousebackend.repository.repository

import com.dorotatomczak.oauth.warehousebackend.repository.model.ProductInWarehouseEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductInWarehouseRepository : CrudRepository<ProductInWarehouseEntity, Int> {

    fun findByWarehouseId(warehouseId: Int): List<ProductInWarehouseEntity>
}