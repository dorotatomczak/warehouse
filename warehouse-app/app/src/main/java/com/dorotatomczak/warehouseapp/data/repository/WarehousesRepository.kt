package com.dorotatomczak.warehouseapp.data.repository

import com.dorotatomczak.warehouseapp.data.local.dao.WarehouseDao
import com.dorotatomczak.warehouseapp.data.local.entity.WarehouseEntity
import com.dorotatomczak.warehouseapp.data.model.Warehouse
import com.dorotatomczak.warehouseapp.data.remote.api.ApiCaller.safeApiCall
import com.dorotatomczak.warehouseapp.data.remote.api.Result
import com.dorotatomczak.warehouseapp.data.remote.api.service.WarehousesService
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class WarehousesRepository @Inject constructor(
    private val warehousesService: WarehousesService,
    private val warehouseDao: WarehouseDao
) {
    suspend fun getWarehouses(sync: Boolean = false): Result<List<Warehouse>> {
        return try {
            if (sync) {
                safeApiCall(Dispatchers.IO) {
                    warehousesService.getWarehouses()
                }.also { result ->
                    if (result is Result.Success) {
                        warehouseDao.insertAll(result.data.map { WarehouseEntity.from(it) })
                    }
                }
            } else {
                Result.Success(warehouseDao.getAll().map { it.toWarehouse() })
            }
        } catch (e: Exception) {
            Result.GenericError(e)
        }
    }
}
