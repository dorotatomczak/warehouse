package com.dorotatomczak.oauth.warehousebackend.controller

import com.dorotatomczak.oauth.warehousebackend.repository.model.WarehouseEntity
import com.dorotatomczak.oauth.warehousebackend.service.WarehouseService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class WarehouseController(
    val warehouseService: WarehouseService
) {
    @GetMapping("/warehouses")
    @PreAuthorize("hasAuthority('INVENTORY_VIEW')")
    fun getAllWarehouses(): Iterable<WarehouseEntity> = warehouseService.getAllWarehouses()
}