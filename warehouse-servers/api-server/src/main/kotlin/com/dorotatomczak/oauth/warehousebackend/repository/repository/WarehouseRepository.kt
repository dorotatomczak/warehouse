package com.dorotatomczak.oauth.warehousebackend.repository.repository

import com.dorotatomczak.oauth.warehousebackend.repository.model.WarehouseEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface WarehouseRepository : CrudRepository<WarehouseEntity, Int>