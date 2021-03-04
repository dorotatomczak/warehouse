package com.dorotatomczak.oauth.warehousebackend.service

import com.dorotatomczak.oauth.warehousebackend.repository.model.WarehouseEntity
import com.dorotatomczak.oauth.warehousebackend.repository.repository.WarehouseRepository
import org.springframework.stereotype.Service

@Service
class WarehouseService(
    private val warehouseRepository: WarehouseRepository
) {
    fun getAllWarehouses(): MutableIterable<WarehouseEntity> = warehouseRepository.findAll()
}