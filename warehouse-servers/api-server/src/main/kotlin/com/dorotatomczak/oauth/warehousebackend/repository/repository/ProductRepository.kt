package com.dorotatomczak.oauth.warehousebackend.repository.repository

import com.dorotatomczak.oauth.warehousebackend.repository.model.ProductEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : CrudRepository<ProductEntity, Int>
