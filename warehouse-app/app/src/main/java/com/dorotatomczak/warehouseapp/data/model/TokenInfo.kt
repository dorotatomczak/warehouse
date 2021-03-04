package com.dorotatomczak.warehouseapp.data.model

import com.squareup.moshi.Json

data class TokenInfo(
    @Json(name = "authorities")
    val authorities: List<Permission> = emptyList()
)
