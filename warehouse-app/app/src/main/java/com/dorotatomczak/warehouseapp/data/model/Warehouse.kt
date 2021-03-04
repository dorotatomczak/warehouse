package com.dorotatomczak.warehouseapp.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class Warehouse(

    @Json(name = "id")
    var id: Int? = null,

    @Json(name = "name")
    var name: String = ""

) : Serializable
