package com.dorotatomczak.warehouseapp.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable
import java.time.LocalDateTime


@JsonClass(generateAdapter = true)
data class ProductResponse(
    @Json(name = "id")
    val id: Int? = null,

    @Json(name = "name")
    var name: String = "",

    @Json(name = "publisher")
    var manufacturer: String = "",

    @Json(name = "price")
    var price: Float = 0f,

    @Json(name = "quantity")
    var quantity: Int = 0,

    @Json(name = "modified")
    var modified: LocalDateTime? = LocalDateTime.now(),

    val isOnline: Boolean = true
) : Serializable {

    fun toProduct(): Product {
        return Product(id, name, manufacturer, price, quantity, modified, isOnline)
    }
}
