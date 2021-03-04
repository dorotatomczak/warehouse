package com.dorotatomczak.oauth.warehousebackend.repository.model

import javax.persistence.*

@Entity
@Table(name = "warehouses")
data class WarehouseEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    val name: String? = null
)
