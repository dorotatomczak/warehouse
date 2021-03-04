package com.dorotatomczak.warehouseapp.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class ProductInWarehouse(
    @Json(name = "quantity")
    var quantity: Int = 0,

    @Json(name = "productId")
    val productId: Int? = null,

    @Json(name = "warehouseId")
    val warehouseId: Int? = null
) : Serializable
