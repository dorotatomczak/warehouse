package com.dorotatomczak.warehouseapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dorotatomczak.warehouseapp.data.local.entity.WarehouseEntity

@Dao
interface WarehouseDao {

    @Query("SELECT * FROM warehouses")
    suspend fun getAll(): List<WarehouseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(warehouses: List<WarehouseEntity>)
}
