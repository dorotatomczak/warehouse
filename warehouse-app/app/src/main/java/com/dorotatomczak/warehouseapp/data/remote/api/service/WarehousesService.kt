package com.dorotatomczak.warehouseapp.data.remote.api.service

import com.dorotatomczak.warehouseapp.data.model.Warehouse
import retrofit2.http.GET

interface WarehousesService {

    companion object {
        const val API_URL = "http://10.0.2.2:9090/"
    }

    @GET("warehouses")
    suspend fun getWarehouses(): List<Warehouse>
}
